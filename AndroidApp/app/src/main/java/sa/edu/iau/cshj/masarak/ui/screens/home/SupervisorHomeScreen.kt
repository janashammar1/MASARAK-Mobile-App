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
import androidx.compose.runtime.Composable
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
fun SupervisorHomeScreen(isAcademic: Boolean, onPeopleClick: () -> Unit = {}, onNotifications: () -> Unit = {}, onTabSelected: (BottomBarItem) -> Unit = {}) {
    SupervisorHomeContent(isAcademic = isAcademic, inPreview = LocalInspectionMode.current, onPeopleClick = onPeopleClick, onNotifications = onNotifications, onTabSelected = onTabSelected)
}

@Composable
private fun SupervisorHomeContent(isAcademic: Boolean, inPreview: Boolean, onPeopleClick: () -> Unit = {}, onNotifications: () -> Unit = {}, onTabSelected: (BottomBarItem) -> Unit = {}) {
    if (inPreview) {
        SupervisorPreviewFrame {
            SupervisorHomeLayout(isAcademic = isAcademic, previewStatusBar = true)
        }
    } else {
        SupervisorHomeLayout(isAcademic = isAcademic, previewStatusBar = false, onPeopleClick = onPeopleClick, onNotifications = onNotifications, onTabSelected = onTabSelected)
    }
}

@Composable
private fun SupervisorHomeLayout(isAcademic: Boolean, previewStatusBar: Boolean, onPeopleClick: () -> Unit = {}, onNotifications: () -> Unit = {}, onTabSelected: (BottomBarItem) -> Unit = {}) {
    val role = if (isAcademic) MahaRole.Academic else MahaRole.Field
    SupervisorScreenFrame(role, previewStatusBar, onPeopleClick, onTabSelected) {
        if (isAcademic) {
            MahaHomeHeader("HZ", "Dr. Huda Alzahrani", "Academic Supervisor", "Huda", 3, MahaLilac, onNotifications)
        } else {
            MahaHomeHeader("KA", "Eng. Khalid Alotaibi", "Field Supervisor · Nakheel Tech", "Khalid", 4, MahaGold, onNotifications)
        }
        Box(Modifier.fillMaxWidth().clickable(onClick = onPeopleClick)) {
            MahaSummary {
                MahaSummaryLabel(
                    if (isAcademic) Icons.Outlined.School else Icons.Outlined.Groups,
                    if (isAcademic) "Your students" else "Your trainees", arrow = true
                )
                Spacer(Modifier.height(12.dp))
                Row(Modifier.height(IntrinsicSize.Min)) {
                    Column(Modifier.weight(1f)) {
                        SupervisorStat("6", if (isAcademic) "Students" else "Total trainees")
                    }
                    Box(Modifier.fillMaxHeight().width(1.dp).background(Color.White.copy(alpha = .2f)))
                    Column(Modifier.weight(1f).padding(start = 14.dp)) {
                        SupervisorStat(if (isAcademic) "2" else "5", "Needs Review")
                        Text(
                            if (isAcademic) "2 tasks approved by Field Supervisor" else "4 tasks · 1 week to confirm",
                            color = Color(0xFFC2C5DA), fontSize = 12.sp, lineHeight = 15.sp
                        )
                    }
                }
            }
        }
        if (isAcademic) {
            Spacer(Modifier.height(12.dp))
            AcademicHomeAlert()
        }
        Spacer(Modifier.height(22.dp))
        Text("Recent activity", color = MahaInk, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(10.dp))
        MahaList {
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
        }
    }
}

@Composable
private fun SupervisorStat(number: String, title: String) {
    Text(number, color = Color.White, fontSize = 32.sp, lineHeight = 37.sp, fontWeight = FontWeight.Bold)
    Text(title, color = Color.White, fontSize = 13.sp, lineHeight = 17.sp, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun AcademicHomeAlert() {
    val ink = Color(0xFF511B1D)
    Row(
        Modifier.fillMaxWidth().background(Color(0xFFFFDAD7), RoundedCornerShape(17.dp)).padding(15.dp)
    ) {
        Icon(Icons.Outlined.Warning, null, Modifier.size(18.dp), tint = ink)
        Spacer(Modifier.width(9.dp))
        Column(Modifier.weight(1f)) {
            Text("Academic Alert · Sara Alqahtani", color = ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Several recent tasks didn’t match the major. A quick check-in may help.",
                color = ink, fontSize = 13.sp, lineHeight = 18.sp)
            Spacer(Modifier.height(12.dp))
            Text("Open student", color = ink, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline)
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
                chatUnreadCount = role.unreadChat,
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
