package sa.edu.iau.cshj.masarak.ui.screens.notifications

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import sa.edu.iau.cshj.masarak.data.entity.NotificationEntity
import sa.edu.iau.cshj.masarak.data.entity.UserEntity
import sa.edu.iau.cshj.masarak.notifications.*
import sa.edu.iau.cshj.masarak.ui.components.AppBottomBar
import sa.edu.iau.cshj.masarak.ui.components.BottomBarItem
import sa.edu.iau.cshj.masarak.ui.screens.home.*
import sa.edu.iau.cshj.masarak.ui.viewmodel.NotificationViewModel

@Composable
fun NotificationsScreen(
    role: String = "student",
    onBack: () -> Unit = {},
    onTabSelected: (BottomBarItem) -> Unit = {},
    currentUser: UserEntity? = null,
    onNotificationClick: (NotificationEntity) -> Unit = {},
    chatUnreadCount: Int = 0
) {
    if (LocalInspectionMode.current) {
        NotificationsPreview(role)
        return
    }
    val selectedRole = role.trim().lowercase(Locale.ROOT)
    val vm: NotificationViewModel = viewModel(factory = NotificationViewModel.Factory)
    val state by vm.state.collectAsState()
    LaunchedEffect(currentUser?.userId, currentUser?.role, selectedRole) { vm.load(currentUser, selectedRole) }
    val visible = if (state.recipientId == currentUser?.userId && state.role == selectedRole) state
    else NotificationViewModel.State(isLoading = currentUser != null)
    val context = LocalContext.current
    val activity = context.notificationActivity()
    DisposableEffect(activity) {
        val controller = activity?.window?.let { WindowCompat.getInsetsController(it, it.decorView) }
        val previous = controller?.isAppearanceLightStatusBars
        val previousNavigation = controller?.isAppearanceLightNavigationBars
        controller?.isAppearanceLightStatusBars = true
        controller?.isAppearanceLightNavigationBars = false
        onDispose {
            if (previous != null) controller?.isAppearanceLightStatusBars = previous
            if (previousNavigation != null) controller?.isAppearanceLightNavigationBars = previousNavigation
        }
    }
    val center = remember(context.applicationContext) { SystemNotifications(context) }
    var settings by remember { mutableStateOf(center.status(context.notificationActivity())) }
    var showSettings by remember { mutableStateOf(false) }
    val refresh = { settings = center.status(context.notificationActivity()) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { refresh() }
    val owner = LocalLifecycleOwner.current
    DisposableEffect(owner, center) {
        val observer = LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_RESUME) refresh() }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer) }
    }
    val requestPermission = {
        if (settings.canRequest && Build.VERSION.SDK_INT >= 33) {
            center.recordPermissionRequest()
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else center.openSettings()
    }
    NotificationsContent(selectedRole, visible, currentUser != null, settings, chatUnreadCount,
        onBack = onBack, onTabSelected = onTabSelected,
        onEnable = requestPermission,
        onDefer = { center.defer(); refresh() }, onSettings = { showSettings = true },
        onCategory = vm::selectCategory, onUnread = vm::showUnread,
        onMarkAll = vm::markAllAsRead, onRetry = { vm.load(currentUser, selectedRole) },
        onNotification = { vm.markAsRead(it.notificationId); onNotificationClick(it) })
    if (showSettings) NotificationSettingsDialog(settings, onClose = { showSettings = false },
        onEnable = requestPermission, onCategory = { center.openSettings(it) })
}

@Composable
private fun NotificationsContent(
    role: String, state: NotificationViewModel.State, signedIn: Boolean,
    settings: SystemNotifications.Status, chatUnreadCount: Int = 0,
    onBack: () -> Unit = {}, onTabSelected: (BottomBarItem) -> Unit = {},
    onEnable: () -> Unit = {}, onDefer: () -> Unit = {}, onSettings: () -> Unit = {},
    onCategory: (NotificationCategory?) -> Unit = {}, onUnread: (Boolean) -> Unit = {},
    onMarkAll: () -> Unit = {}, onRetry: () -> Unit = {},
    onNotification: (NotificationEntity) -> Unit = {}
) {
    val thirdTab = when (role) { "academic" -> "Students"; "field" -> "Trainees"; else -> "My Training" }
    val background = if (role == "academic") Brush.verticalGradient(listOf(MahaPaper, MahaPaper))
    else Brush.verticalGradient(0f to Color(0xFFDCE7F5), .24f to MahaPaper, 1f to MahaPaper)
    Column(Modifier.fillMaxSize().background(background)) {
        LazyColumn(Modifier.weight(1f).fillMaxWidth().statusBarsPadding(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back", tint = MahaInk) }
                    Text("Notifications", Modifier.weight(1f), color = MahaInk, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onSettings) { Icon(Icons.Outlined.Settings, "Notification settings", tint = MahaInk) }
                }
            }
            item {
                if (settings.enabled) {
                    Text("Device notifications are on", color = MahaMuted, fontSize = 13.sp)
                } else {
                    Surface(color = MahaLilac, shape = RoundedCornerShape(16.dp)) {
                        Column(Modifier.fillMaxWidth().padding(16.dp)) {
                            Text("Choose your notifications", color = MahaNavy, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(Modifier.height(6.dp))
                            Text("Allow device alerts for tasks, attendance and updates. Your inbox stays available if you decline.", color = MahaInk, fontSize = 14.sp)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(onClick = onEnable) { Text(if (settings.canRequest) "Allow notifications" else "Open settings", color = MahaNavy) }
                                if (!settings.deferred) TextButton(onClick = onDefer) { Text("Not now", color = MahaMuted) }
                            }
                        }
                    }
                }
            }
            if (!signedIn) {
                item { Text("Please sign in to view your notifications.", Modifier.padding(vertical = 16.dp), color = MahaMuted) }
            } else {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${state.unreadCount} unread", Modifier.weight(1f), color = MahaInk, fontSize = 14.sp)
                        TextButton(onClick = onMarkAll, colors = ButtonDefaults.textButtonColors(contentColor = MahaNavy), enabled = !state.isLoading && state.error == null && !state.isSaving && state.unreadCount > 0) { Text("Mark all read") }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = !state.unreadOnly, onClick = { onUnread(false) }, label = { Text("All") }, colors = FilterChipDefaults.filterChipColors(labelColor = MahaInk, selectedLabelColor = MahaNavy, selectedContainerColor = MahaMint))
                        FilterChip(selected = state.unreadOnly, onClick = { onUnread(true) }, label = { Text("Unread") }, colors = FilterChipDefaults.filterChipColors(labelColor = MahaInk, selectedLabelColor = MahaNavy, selectedContainerColor = MahaMint))
                    }
                    CategoryPicker(state.category, onCategory)
                }
                if (state.isLoading) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
                if (state.error != null) item {
                    Text(state.error, color = MahaRed)
                    TextButton(onClick = onRetry) { Text("Retry") }
                }
                if (state.actionError != null) item { Text(state.actionError, color = MahaRed) }
                if (!state.isLoading && state.error == null && state.visible.isEmpty()) item {
                    Text(if (state.notifications.isEmpty()) "You’re all caught up. No notifications yet." else "No notifications match this filter.",
                        Modifier.padding(vertical = 24.dp), color = MahaMuted, fontSize = 14.sp)
                }
                items(state.visible, key = { it.notificationId }) { notification ->
                    val event = NotificationEvent.from(notification.eventType)
                    Surface(onClick = { onNotification(notification) }, enabled = !state.isSaving,
                        modifier = Modifier.fillMaxWidth().semantics { stateDescription = if (notification.readAt == null) "Unread" else "Read" },
                        color = Color.White, border = BorderStroke(1.dp, MahaLine), shape = RoundedCornerShape(16.dp)) {
                        Column {
                            Text(event.category.label, Modifier.padding(start = 14.dp, top = 10.dp), color = MahaMuted, fontSize = 12.sp)
                            MahaListRow(event.title, notification.body, notificationTime(notification.createdAt), unread = notification.readAt == null) {
                                MahaTileIcon(categoryIcon(event.category))
                            }
                        }
                    }
                }
            }
        }
        // Let the shared bar grow at large font sizes without changing the shared component.
        Box(Modifier.fillMaxWidth().background(MahaNavy).navigationBarsPadding()
            .heightIn(min = 76.dp * LocalDensity.current.fontScale.coerceAtLeast(1f)), propagateMinConstraints = true) {
            AppBottomBar(selectedItem = BottomBarItem.Home, thirdTabLabel = thirdTab, chatUnreadCount = chatUnreadCount, onItemSelected = onTabSelected)
        }
    }
}

@Composable
private fun CategoryPicker(selected: NotificationCategory?, onCategory: (NotificationCategory?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }, colors = ButtonDefaults.outlinedButtonColors(contentColor = MahaNavy), modifier = Modifier.fillMaxWidth()) {
            Text(selected?.label ?: "All categories", Modifier.weight(1f))
            Icon(Icons.Outlined.ExpandMore, null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("All categories") }, onClick = { onCategory(null); expanded = false })
            NotificationCategory.entries.forEach { category ->
                DropdownMenuItem(text = { Text(category.label) }, onClick = { onCategory(category); expanded = false })
            }
        }
    }
}

@Composable
private fun NotificationSettingsDialog(
    state: SystemNotifications.Status, onClose: () -> Unit,
    onEnable: () -> Unit, onCategory: (NotificationCategory) -> Unit
) {
    AlertDialog(onDismissRequest = onClose, title = { Text("Notification settings") },
        text = {
            Column(Modifier.heightIn(max = 480.dp).verticalScroll(rememberScrollState())) {
                Text("Choose which device alerts you receive in Android settings. Turning alerts off keeps your in-app inbox available.")
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = onEnable) { Text(if (state.enabled) "App notification settings" else if (state.canRequest) "Allow notifications" else "Open app settings") }
                NotificationCategory.entries.forEach { category ->
                    TextButton(onClick = { onCategory(category) }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                        Text(category.label, Modifier.weight(1f))
                        Text(if (state.channels[category] == true) "On" else "Off")
                    }
                }
            }
        }, confirmButton = { TextButton(onClick = onClose) { Text("Done") } })
}

private fun categoryIcon(category: NotificationCategory): ImageVector = when (category) {
    NotificationCategory.TASKS -> Icons.Outlined.TaskAlt
    NotificationCategory.ATTENDANCE -> Icons.Outlined.EventAvailable
    NotificationCategory.EVALUATIONS -> Icons.Outlined.Assessment
    NotificationCategory.LINKS -> Icons.Outlined.Link
    NotificationCategory.MESSAGES -> Icons.Outlined.ChatBubbleOutline
    NotificationCategory.GENERAL -> Icons.Outlined.Notifications
}

private fun notificationTime(at: Long): String {
    val zone = ZoneId.systemDefault()
    val date = Instant.ofEpochMilli(at).atZone(zone)
    val today = java.time.LocalDate.now(zone)
    return when (date.toLocalDate()) {
        today -> date.format(DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()))
        today.minusDays(1) -> "Yesterday"
        else -> date.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault()))
    }
}

@Composable
private fun NotificationsPreview(role: String) {
    val sample = listOf(NotificationEntity(1, 1, if (role == "student") "TASK_RETURNED" else "TASK_SUBMITTED", "Training task updated", 0))
    NotificationsContent(role, NotificationViewModel.State(recipientId = 1, role = role, notifications = sample), true,
        SystemNotifications.Status(true, false, false, NotificationCategory.entries.associateWith { true }))
}

@Preview(name = "Student · Notifications", widthDp = 390, heightDp = 844, showBackground = true)
@Composable private fun StudentNotificationsPreview() = NotificationsPreview("student")
@Preview(name = "Field Supervisor · Notifications", widthDp = 390, heightDp = 844, showBackground = true)
@Composable private fun FieldNotificationsPreview() = NotificationsPreview("field")
@Preview(name = "Academic Supervisor · Notifications", widthDp = 390, heightDp = 844, showBackground = true)
@Composable private fun AcademicNotificationsPreview() = NotificationsPreview("academic")
