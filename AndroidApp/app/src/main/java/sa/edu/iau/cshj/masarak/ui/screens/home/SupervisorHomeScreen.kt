package sa.edu.iau.cshj.masarak.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import sa.edu.iau.cshj.masarak.data.entity.UserEntity
import sa.edu.iau.cshj.masarak.ui.viewmodel.SupervisorHomeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sa.edu.iau.cshj.masarak.ui.components.AppBottomBar
import sa.edu.iau.cshj.masarak.ui.components.BottomBarItem

@Composable
fun SupervisorHomeScreen() = SupervisorHomeScreen(isAcademic = false)

@Composable
fun SupervisorHomeScreen(
    isAcademic: Boolean,
    onPeopleClick: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onTabSelected: (BottomBarItem) -> Unit = {},
    currentUser: UserEntity? = null,
    onPersonClick: (String, Long) -> Unit = { _, _ -> },
    chatUnreadCount: Int? = null
) {
    if (LocalInspectionMode.current) {
        SupervisorHomeContent(isAcademic, inPreview = true)
    } else {
        val homeViewModel: SupervisorHomeViewModel = viewModel(factory = SupervisorHomeViewModel.Factory)
        val state by homeViewModel.state.collectAsState()
        LaunchedEffect(currentUser?.userId, currentUser?.role, isAcademic) { homeViewModel.load(currentUser, isAcademic) }
        val role = if (isAcademic) "academic" else "field"
        val visible = if (state.userId == currentUser?.userId && state.role == role) state
        else SupervisorHomeViewModel.State(isLoading = currentUser != null)
        SupervisorHomeLayout(
            isAcademic, false, onPeopleClick, onNotifications, onTabSelected,
            currentUser, visible, onPersonClick, chatUnreadCount ?: visible.unreadChats,
            onRetry = { homeViewModel.load(currentUser, isAcademic) }
        )
    }
}

@Composable
private fun SupervisorHomeContent(isAcademic: Boolean, inPreview: Boolean) {
    if (inPreview) {
        SupervisorPreviewFrame { SupervisorHomeLayout(isAcademic, previewStatusBar = true) }
    }
}

@Composable
private fun SupervisorHomeLayout(
    isAcademic: Boolean,
    previewStatusBar: Boolean,
    onPeopleClick: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onTabSelected: (BottomBarItem) -> Unit = {},
    currentUser: UserEntity? = null,
    state: SupervisorHomeViewModel.State = SupervisorHomeViewModel.State(),
    onPersonClick: (String, Long) -> Unit = { _, _ -> },
    chatUnreadCount: Int? = null,
    onRetry: () -> Unit = {}
) {
    val role = if (isAcademic) MahaRole.Academic else MahaRole.Field
    val name = if (previewStatusBar) {
        if (isAcademic) "Dr. Huda Alzahrani" else "Eng. Khalid Alotaibi"
    } else currentUser?.fullName.orEmpty()
    val words = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    val nameWords = words.filterNot { it.trimEnd('.').lowercase(Locale.ROOT) in setOf("dr", "eng", "prof") }
    val firstName = nameWords.firstOrNull() ?: "Supervisor"
    val initials = nameWords.take(2).mapNotNull { it.firstOrNull() }.joinToString("").uppercase()
    val roleLabel = if (isAcademic) "Academic Supervisor" else "Field Supervisor"
    val organization = if (previewStatusBar && !isAcademic) "Nakheel Tech" else currentUser?.workOrganization
    val subtitle = roleLabel + if (!isAcademic && !organization.isNullOrBlank()) " · $organization" else ""
    val ready = previewStatusBar || (currentUser != null && !state.isLoading && state.error == null)
    val people = if (previewStatusBar) 6 else state.peopleCount
    val tasks = if (previewStatusBar) { if (isAcademic) 2 else 4 } else state.pendingTasks
    val weeks = if (previewStatusBar && !isAcademic) 1 else state.pendingWeeks
    val needsReview = tasks + if (isAcademic) 0 else weeks
    SupervisorScreenFrame(role, previewStatusBar, onPeopleClick, onTabSelected, chatUnreadCount) {
        MahaHomeHeader(initials.ifBlank { "?" }, name.ifBlank { "Supervisor" }, subtitle, firstName,
            if (previewStatusBar) { if (isAcademic) 3 else 4 } else state.unreadNotifications,
            if (isAcademic) MahaLilac else MahaGold, onNotifications)
        if (!previewStatusBar) {
            if (currentUser == null) {
                Text("Please sign in to view your supervisor home.", color = MahaMuted)
                Spacer(Modifier.height(12.dp))
            }
            if (state.isLoading) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
            }
            state.error?.let { error ->
                Text(error, color = MahaRed)
                TextButton(onClick = onRetry) { Text("Retry") }
            }
        }
        Box(Modifier.fillMaxWidth().clickable(enabled = ready, onClick = onPeopleClick)) {
            MahaSummary {
                MahaSummaryLabel(
                    if (isAcademic) Icons.Outlined.School else Icons.Outlined.Groups,
                    if (isAcademic) "Your students" else "Your trainees", arrow = true
                )
                Spacer(Modifier.height(12.dp))
                Row(Modifier.height(IntrinsicSize.Min)) {
                    Column(Modifier.weight(1f)) {
                        SupervisorStat(if (ready) people.toString() else "—", if (isAcademic) "Students" else "Total trainees")
                    }
                    Box(Modifier.fillMaxHeight().width(1.dp).background(Color.White.copy(alpha = .2f)))
                    Column(Modifier.weight(1f).padding(start = 14.dp)) {
                        SupervisorStat(if (ready) needsReview.toString() else "—", "Needs Review")
                        Text(
                            when {
                                !ready -> "Data is not available yet"
                                isAcademic -> "$tasks approved ${if (tasks == 1) "task awaits" else "tasks await"} your review"
                                else -> "$tasks tasks · $weeks ${if (weeks == 1) "week" else "weeks"} to confirm"
                            },
                            color = Color(0xFFC2C5DA), fontSize = 12.sp, lineHeight = 15.sp
                        )
                    }
                }
            }
        }
        if (isAcademic && ready) {
            val followUp = if (previewStatusBar) SupervisorHomeViewModel.FollowUp("Sara Alqahtani", 0L, 2)
            else state.followUps.firstOrNull()
            if (followUp != null) {
                Spacer(Modifier.height(12.dp))
                AcademicHomeAlert(followUp.name, followUp.count) {
                    onPersonClick(followUp.name, followUp.recordId)
                }
                if (!previewStatusBar && state.followUps.size > 1) {
                    TextButton(onClick = onPeopleClick) { Text("View all students (${state.followUps.size} with relevance follow-up)") }
                }
            }
        }
        Spacer(Modifier.height(22.dp))
        Text("Recent activity", color = MahaInk, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(10.dp))
        MahaList {
            if (previewStatusBar) {
                if (isAcademic) {
                    Activity("SA", "Sara Alqahtani", "Task approved by Field Supervisor", "09:30")
                    HorizontalDivider(color = MahaLine)
                    Activity("NM", "Noura Almutairi", "Week 3 confirmed by Field Supervisor", "09:15")
                    HorizontalDivider(color = MahaLine)
                    Activity("JA", "Jana Alharbi", "Task approved by Field Supervisor", "Yesterday")
                } else {
                    Activity("JA", "Jana Alharbi", "Submitted a task", "09:32")
                    HorizontalDivider(color = MahaLine)
                    Activity("SA", "Sara Alqahtani", "Submitted a task", "09:10")
                    HorizontalDivider(color = MahaLine)
                    Activity("JA", "Jana Alharbi", "Recorded Week 3 attendance", "08:15")
                    HorizontalDivider(color = MahaLine)
                    Activity("NM", "Noura Almutairi", "Resubmitted a task", "Yesterday")
                }
            } else if (ready) {
                if (state.activities.isEmpty()) {
                    Text(if (state.peopleCount == 0) "No linked ${if (isAcademic) "students" else "trainees"} yet."
                    else "No recent activity.", Modifier.padding(16.dp), color = MahaMuted)
                } else {
                    state.activities.forEachIndexed { index, item ->
                        if (index > 0) HorizontalDivider(color = MahaLine)
                        Box(Modifier.fillMaxWidth().clickable { onPersonClick(item.name, item.recordId) }) {
                            val letters = item.name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
                                .take(2).mapNotNull { it.firstOrNull() }.joinToString("").uppercase()
                            Activity(letters, item.name, item.details, supervisorActivityTime(item.at))
                        }
                    }
                }
            } else {
                Text(if (state.isLoading) "Loading activity..." else "Activity is unavailable.",
                    Modifier.padding(16.dp), color = MahaMuted)
            }
        }
    }
}

private fun supervisorActivityTime(at: Long): String {
    val today = java.util.Calendar.getInstance()
    val date = java.util.Calendar.getInstance().apply { timeInMillis = at }
    fun sameDay(a: java.util.Calendar, b: java.util.Calendar): Boolean =
        a.get(java.util.Calendar.YEAR) == b.get(java.util.Calendar.YEAR) &&
                a.get(java.util.Calendar.DAY_OF_YEAR) == b.get(java.util.Calendar.DAY_OF_YEAR)
    if (sameDay(today, date)) return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(at))
    today.add(java.util.Calendar.DAY_OF_YEAR, -1)
    if (sameDay(today, date)) return "Yesterday"
    return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(at))
}

@Composable
private fun SupervisorStat(number: String, title: String) {
    Text(number, color = Color.White, fontSize = 32.sp, lineHeight = 37.sp, fontWeight = FontWeight.Bold)
    Text(title, color = Color.White, fontSize = 13.sp, lineHeight = 17.sp, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun AcademicHomeAlert(name: String, count: Int, onOpen: () -> Unit) {
    val ink = Color(0xFF511B1D)
    Row(
        Modifier.fillMaxWidth().background(Color(0xFFFFDAD7), RoundedCornerShape(17.dp)).padding(15.dp)
    ) {
        Icon(Icons.Outlined.Warning, null, Modifier.size(18.dp), tint = ink)
        Spacer(Modifier.width(9.dp))
        Column(Modifier.weight(1f)) {
            Text("Relevance follow-up · $name", color = ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("$count ${if (count == 1) "task is" else "tasks are"} marked Not Relevant in the latest academic reviews.",
                color = ink, fontSize = 13.sp, lineHeight = 18.sp)
            Spacer(Modifier.height(12.dp))
            Text("Open student", color = ink, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline, modifier = Modifier.clickable(onClick = onOpen))
        }
    }
}

@Composable
private fun Activity(initials: String, name: String, details: String, time: String) {
    MahaListRow(name, details, time) { MahaAvatar(initials) }
}

@Preview(name = "Field Supervisor · Home", widthDp = 390, heightDp = 844)
@Composable
private fun FieldHomePreview() {
    SupervisorHomeContent(isAcademic = false, inPreview = true)
}

@Preview(name = "Academic Supervisor · Home", widthDp = 390, heightDp = 844)
@Composable
private fun AcademicHomePreview() {
    SupervisorHomeContent(isAcademic = true, inPreview = true)
}

@Composable
private fun SupervisorScreenFrame(
    role: MahaRole,
    previewStatusBar: Boolean,
    onPeopleClick: () -> Unit,
    onTabSelected: (BottomBarItem) -> Unit,
    chatUnreadCount: Int?,
    content: @Composable ColumnScope.() -> Unit
) {
    val background = Brush.verticalGradient(
        0f to Color(0xFFDCE7F5),
        0.24f to MahaPaper,
        1f to MahaPaper
    )
    Column(Modifier.fillMaxSize().background(MahaNavy)) {
        Column(Modifier.weight(1f).fillMaxWidth().background(background)) {
            if (previewStatusBar) SupervisorPreviewStatusBar()
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth()
                    .then(if (previewStatusBar) Modifier else Modifier.statusBarsPadding())
                    .verticalScroll(rememberScrollState())
                    .padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 20.dp),
                content = content
            )
        }
        Box(Modifier.fillMaxWidth().background(MahaNavy).navigationBarsPadding()) {
            AppBottomBar(
                selectedItem = BottomBarItem.Home,
                thirdTabLabel = role.thirdTab,
                chatUnreadCount = if (previewStatusBar) role.unreadChat else chatUnreadCount ?: 0,
                onItemSelected = { if (it == BottomBarItem.MyTraining) onPeopleClick() else onTabSelected(it) }
            )
        }
    }
}

@Composable
private fun SupervisorPreviewFrame(content: @Composable () -> Unit) {
    Column(Modifier.fillMaxSize().background(MahaNavy)) {
        Box(Modifier.weight(1f).fillMaxWidth()) {
            content()
        }
        Box(Modifier.fillMaxWidth().height(17.dp), contentAlignment = Alignment.Center) {
            Box(Modifier.width(100.dp).height(4.dp)
                .background(Color.White.copy(alpha = .45f), CircleShape))
        }
    }
}

@Composable
private fun SupervisorPreviewStatusBar() {
    Box(Modifier.fillMaxWidth().height(28.dp).padding(horizontal = 20.dp)) {
        Text("9:41", modifier = Modifier.align(Alignment.CenterStart),
            color = MahaInk, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Canvas(Modifier.align(Alignment.CenterEnd).size(50.dp, 14.dp)) {
            val u = size.height / 14f
            repeat(4) { i ->
                val h = (3 + i * 2) * u
                drawRect(MahaInk, Offset(i * 3 * u, 12 * u - h), Size(2 * u, h))
            }
            listOf(12f, 8f, 4f).forEach { d ->
                drawArc(MahaInk, 225f, 90f, false,
                    topLeft = Offset((23f - d / 2) * u, (11f - d / 2) * u),
                    size = Size(d * u, d * u), style = Stroke(1.2f * u))
            }
            drawCircle(MahaInk, u, Offset(23 * u, 11 * u))
            drawRect(MahaInk, Offset(36 * u, 3 * u), Size(7 * u, 10 * u))
            drawRect(MahaInk, Offset(38 * u, 1.5f * u), Size(3 * u, 2 * u))
        }
    }
}
