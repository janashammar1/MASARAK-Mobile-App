package sa.edu.iau.cshj.masarak.notifications

import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.util.Consumer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.retryWhen
import sa.edu.iau.cshj.masarak.data.DatabaseProvider
import sa.edu.iau.cshj.masarak.data.entity.NotificationEntity
import sa.edu.iau.cshj.masarak.data.entity.UserEntity
import sa.edu.iau.cshj.masarak.data.repository.NotificationRepository

/** Install once above the NavHost, using the real signed-in user. No routing is hardcoded here. */
@Composable
fun NotificationSessionEffect(
    currentUser: UserEntity?,
    onOpenNotification: ((NotificationEntity) -> Unit)? = null
) {
    val context = LocalContext.current
    val center = remember(context.applicationContext) { SystemNotifications(context) }
    val repository = remember { NotificationRepository(DatabaseProvider.db.notificationDao()) }
    val activity = context.notificationActivity() as? ComponentActivity
    val onOpen by rememberUpdatedState(onOpenNotification)
    var pendingIntent by remember(activity) { mutableStateOf(activity?.intent) }
    SideEffect { center.setActiveRecipient(currentUser?.userId) }
    DisposableEffect(activity) {
        val listener = Consumer<Intent> { pendingIntent = it }
        activity?.addOnNewIntentListener(listener)
        onDispose { activity?.removeOnNewIntentListener(listener) }
    }
    LaunchedEffect(currentUser?.userId) {
        val user = currentUser ?: return@LaunchedEffect
        try {
            NotificationDelivery().collect(user.userId,
                repository.getNotificationsForUser(user.userId).retryWhen { cause, attempt ->
                    if (cause is CancellationException) false else (attempt < 3).also { if (it) delay(1000) }
                },
                synchronize = { center.synchronize(user.userId, it) },
                post = { center.post(it) })
        } catch (cancelled: CancellationException) { throw cancelled
        } catch (_: Exception) { Log.e("MasarakNotifications", "Notification delivery unavailable; inbox can be retried.") }
    }
    LaunchedEffect(currentUser?.userId, pendingIntent, onOpenNotification != null) {
        val user = currentUser ?: return@LaunchedEffect
        val incoming = pendingIntent ?: return@LaunchedEffect
        val request = NotificationOpenRequest.from(incoming) ?: return@LaunchedEffect
        if (request.recipientId != user.userId || onOpen == null) return@LaunchedEffect
        try {
            // Resolve from Room after checking the signed-in account, never trust target IDs from extras.
            val owned = repository.getOwnedNotification(request.notificationId, user.userId)
            if (owned != null) {
                onOpen?.invoke(owned)
                repository.markReadForUser(owned.notificationId, user.userId, System.currentTimeMillis())
            }
            incoming.removeExtra(NotificationOpenRequest.ID)
            incoming.removeExtra(NotificationOpenRequest.RECIPIENT)
            pendingIntent = null
        } catch (cancelled: CancellationException) { throw cancelled
        } catch (_: Exception) { Log.e("MasarakNotifications", "Could not open notification.") }
    }
}

