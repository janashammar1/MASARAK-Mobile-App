package sa.edu.iau.cshj.masarak.notifications

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import sa.edu.iau.cshj.masarak.MainActivity
import sa.edu.iau.cshj.masarak.R
import sa.edu.iau.cshj.masarak.data.entity.NotificationEntity

/** Uses Android's actual permission and channel settings as the source of truth. */
class SystemNotifications(context: Context) {
    private val app = context.applicationContext
    private val manager = app.getSystemService(NotificationManager::class.java)
    private val preferences = app.getSharedPreferences("masarak_notification_controls", Context.MODE_PRIVATE)
    data class Status(
        val enabled: Boolean, val canRequest: Boolean, val deferred: Boolean,
        val channels: Map<NotificationCategory, Boolean>
    )
    init { createChannels() }
    fun createChannels() {
        manager.createNotificationChannels(NotificationCategory.entries.map {
            NotificationChannel(it.channelId, it.label, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = it.description
                lockscreenVisibility = android.app.Notification.VISIBILITY_PRIVATE
            }
        })
    }
    fun status(activity: Activity?): Status {
        val granted = Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(app, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        val enabled = granted && NotificationManagerCompat.from(app).areNotificationsEnabled()
        val asked = preferences.getBoolean("permission_requested", false)
        val canRequest = !enabled && Build.VERSION.SDK_INT >= 33 && !granted &&
                (!asked || activity?.let { ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.POST_NOTIFICATIONS) } == true)
        return Status(enabled, canRequest, preferences.getBoolean("deferred", false),
            NotificationCategory.entries.associateWith { enabled && (manager.getNotificationChannel(it.channelId)?.importance ?: NotificationManager.IMPORTANCE_NONE) > NotificationManager.IMPORTANCE_NONE })
    }
    fun recordPermissionRequest() { preferences.edit().putBoolean("permission_requested", true).apply() }
    fun defer() { preferences.edit().putBoolean("deferred", true).apply() }
    fun openSettings(category: NotificationCategory? = null) {
        val intent = if (category == null) Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, app.packageName)
        else Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, app.packageName)
            .putExtra(Settings.EXTRA_CHANNEL_ID, category.channelId)
        try { app.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
        catch (_: ActivityNotFoundException) {
            app.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${app.packageName}")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
    fun setActiveRecipient(recipientId: Long?) {
        val next = recipientId ?: -1L
        val previous = preferences.getLong("active_recipient", -1L)
        if (previous != next) {
            manager.activeNotifications.filter { it.tag?.startsWith("masarak:") == true }.forEach { manager.cancel(it.tag, it.id) }
            preferences.edit().putLong("active_recipient", next).apply()
        }
    }
    fun synchronize(recipientId: Long, unreadIds: Set<Long>) {
        val prefix = "masarak:$recipientId:"
        manager.activeNotifications.filter { it.tag?.startsWith(prefix) == true && it.tag?.removePrefix(prefix)?.toLongOrNull() !in unreadIds }
            .forEach { manager.cancel(it.tag, it.id) }
    }
    fun post(notification: NotificationEntity): Boolean {
        if (preferences.getLong("active_recipient", -1L) != notification.recipientId || notification.readAt != null) return false
        val event = NotificationEvent.from(notification.eventType)
        if (!status(null).channels.getValue(event.category)) return false
        val intent = Intent(app, MainActivity::class.java)
            .setAction("sa.edu.iau.cshj.masarak.OPEN_NOTIFICATION")
            .setData(Uri.Builder().scheme("masarak-notification").authority(notification.recipientId.toString()).appendPath(notification.notificationId.toString()).build())
            .putExtra(NotificationOpenRequest.RECIPIENT, notification.recipientId)
            .putExtra(NotificationOpenRequest.ID, notification.notificationId)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pending = PendingIntent.getActivity(app, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val publicVersion = NotificationCompat.Builder(app, event.category.channelId)
            .setSmallIcon(R.drawable.ic_masarak_notification).setContentTitle("MASARAK").setContentText("You have a new update").build()
        val alert = NotificationCompat.Builder(app, event.category.channelId)
            .setSmallIcon(R.drawable.ic_masarak_notification)
            .setContentTitle(event.title).setContentText(notification.body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notification.body))
            .setContentIntent(pending).setAutoCancel(true).setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE).setPublicVersion(publicVersion)
            .setCategory(if (event.category == NotificationCategory.MESSAGES) NotificationCompat.CATEGORY_MESSAGE else NotificationCompat.CATEGORY_STATUS)
            .build()
        return try { NotificationManagerCompat.from(app).notify("masarak:${notification.recipientId}:${notification.notificationId}", 0, alert); true }
        catch (_: SecurityException) { false } // Permission may be revoked between checking and posting.
    }
}

data class NotificationOpenRequest(val recipientId: Long, val notificationId: Long) {
    companion object {
        const val RECIPIENT = "masarak_notification_recipient"
        const val ID = "masarak_notification_id"
        fun from(intent: Intent?): NotificationOpenRequest? {
            val recipient = intent?.getLongExtra(RECIPIENT, -1L) ?: return null
            val id = intent.getLongExtra(ID, -1L)
            return if (recipient > 0L && id > 0L) NotificationOpenRequest(recipient, id) else null
        }
    }
}

internal tailrec fun Context.notificationActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.notificationActivity()
    else -> null
}

