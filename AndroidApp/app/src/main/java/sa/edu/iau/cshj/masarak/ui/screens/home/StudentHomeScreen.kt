package sa.edu.iau.cshj.masarak.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sa.edu.iau.cshj.masarak.ui.components.AppBottomBar
import sa.edu.iau.cshj.masarak.ui.components.BottomBarItem

internal val MahaNavy = Color(0xFF0C0D45)
internal val MahaPaper = Color(0xFFF6F6F3)
internal val MahaInk = Color(0xFF191928)
internal val MahaMuted = Color(0xFF505058)
internal val MahaAqua = Color(0xFF75CBD1)
internal val MahaMint = Color(0xFFCEF0EF)
internal val MahaLilac = Color(0xFFE2E3F3)
internal val MahaGold = Color(0xFFF8E3B8)
internal val MahaLine = Color(0xFFDFDEDA)
internal val MahaRed = Color(0xFFB81320)

internal enum class MahaRole(val thirdTab: String, val unreadChat: Int) {
    Student("My Training", 2),
    Academic("Students", 0),
    Field("Trainees", 1)
}

@Composable
internal fun MahaScreen(role: MahaRole, onTabSelected: (BottomBarItem) -> Unit = {}, content: @Composable ColumnScope.() -> Unit) {
    MahaScreenLayout(role, previewStatusBar = false, onTabSelected = onTabSelected, content = content)
}

@Composable
private fun MahaScreenLayout(
    role: MahaRole,
    previewStatusBar: Boolean,
    onTabSelected: (BottomBarItem) -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    val background = if (role == MahaRole.Academic) {
        Brush.verticalGradient(listOf(MahaPaper, MahaPaper))
    } else {
        Brush.verticalGradient(
            0f to Color(0xFFDCE7F5),
            0.24f to MahaPaper,
            1f to MahaPaper
        )
    }
    Column(Modifier.fillMaxSize().background(MahaNavy)) {
        Column(Modifier.weight(1f).fillMaxWidth().background(background)) {
            if (previewStatusBar) MahaPreviewStatusBar()
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
                onItemSelected = onTabSelected
            )
        }
    }
}

@Composable
internal fun MahaAvatar(initials: String, color: Color = MahaMint) {
    Box(Modifier.size(36.dp).background(color, CircleShape), contentAlignment = Alignment.Center) {
        Text(initials, color = MahaNavy, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
internal fun MahaHomeHeader(
    initials: String, fullName: String, subtitle: String,
    firstName: String, count: Int, avatar: Color, onNotifications: () -> Unit = {}
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        MahaAvatar(initials, avatar)
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(fullName, color = MahaInk, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 17.sp)
            Text(subtitle, color = MahaMuted, fontSize = 12.sp, lineHeight = 15.sp)
        }
        BadgedBox(badge = {
            Badge(containerColor = MahaRed, contentColor = Color.White) { Text(count.toString()) }
        }) {
            Surface(onClick = onNotifications, shape = RoundedCornerShape(15.dp), color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, MahaLine)) {
                Box(Modifier.size(38.dp), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Notifications",
                        tint = MahaInk, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
    Spacer(Modifier.height(14.dp))
    Text(buildAnnotatedString {
        append("Good morning, ")
        withStyle(SpanStyle(color = Color(0xFF5D5491), fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold)) { append(firstName) }
    }, color = MahaInk, fontSize = 25.sp, lineHeight = 30.sp, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(14.dp))
}

@Composable
internal fun MahaSummary(content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(25.dp)
    Surface(shape = shape, shadowElevation = 8.dp, color = MahaNavy) {
        Box(Modifier.fillMaxWidth().clip(shape).background(
            Brush.linearGradient(listOf(MahaNavy, Color(0xFF202F57), MahaNavy),
                start = Offset(0f, 380f), end = Offset(1000f, 0f)))) {
            Canvas(Modifier.align(Alignment.TopEnd).offset(x = 18.dp, y = (-21).dp).size(107.dp)) {
                val w = size.width
                val h = size.height
                val star = Path().apply {
                    moveTo(w / 2f, 0f)
                    quadraticBezierTo(w * .56f, h * .43f, w, h / 2f)
                    quadraticBezierTo(w * .56f, h * .57f, w / 2f, h)
                    quadraticBezierTo(w * .44f, h * .57f, 0f, h / 2f)
                    quadraticBezierTo(w * .44f, h * .43f, w / 2f, 0f)
                    close()
                }
                drawPath(star, Color.White.copy(alpha = .13f))
            }
            Column(Modifier.fillMaxWidth().padding(18.dp), content = content)
        }
    }
}

@Composable
internal fun MahaSummaryLabel(icon: ImageVector, text: String, arrow: Boolean = false) {
    val tint = Color(0xFFC2C5DA)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(16.dp), tint = tint)
        Spacer(Modifier.width(8.dp))
        Text(text, Modifier.weight(1f), color = tint, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        if (arrow) Icon(Icons.Outlined.ChevronRight, null, Modifier.size(18.dp), tint = tint)
    }
}

@Composable
internal fun MahaList(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(), color = Color.White,
        shape = RoundedCornerShape(17.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MahaLine)
    ) { Column(content = content) }
}

@Composable
internal fun MahaListRow(
    title: String, subtitle: String, time: String? = null, unread: Boolean = false,
    leading: @Composable () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = 64.dp).padding(horizontal = 13.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leading()
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = MahaInk, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 17.sp)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, color = MahaMuted, fontSize = 12.sp, lineHeight = 15.sp)
        }
        if (time != null) {
            Spacer(Modifier.width(7.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(time, color = MahaMuted, fontSize = 11.sp)
                if (unread) {
                    Spacer(Modifier.height(4.dp))
                    Box(Modifier.size(7.dp).background(Color(0xFF00696E), CircleShape))
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        Icon(Icons.Outlined.ChevronRight, null, Modifier.size(17.dp), tint = MahaMuted)
    }
}

@Composable
internal fun MahaTileIcon(icon: ImageVector, color: Color = MahaLilac, ink: Color = MahaNavy) {
    Box(Modifier.size(36.dp).background(color, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
        Icon(icon, null, Modifier.size(20.dp), tint = ink)
    }
}

@Composable
fun StudentHomeScreen(onWeeklyEvaluation: () -> Unit = {}, onTasksClick: () -> Unit = {}, onAddTask: () -> Unit = {}, onNotifications: () -> Unit = {}, onTabSelected: (BottomBarItem) -> Unit = {}) =
    StudentHomeLayout(onWeeklyEvaluation = onWeeklyEvaluation, onTasksClick = onTasksClick, onAddTask = onAddTask, onNotifications = onNotifications, onTabSelected = onTabSelected)

@Composable
private fun StudentHomeLayout(onWeeklyEvaluation: () -> Unit = {}, previewStatusBar: Boolean = false, onTasksClick: () -> Unit = {}, onAddTask: () -> Unit = {}, onNotifications: () -> Unit = {}, onTabSelected: (BottomBarItem) -> Unit = {}) {
    MahaScreenLayout(MahaRole.Student, previewStatusBar = previewStatusBar, onTabSelected = onTabSelected) {
        MahaHomeHeader("SA", "Sara Alqahtani", "Student · Week 3", "Sara", 2, MahaMint, onNotifications)
        MahaSummary {
            MahaSummaryLabel(Icons.Outlined.AccessTime, "Confirmed training hours")
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text("50", color = Color.White, fontSize = 52.sp, lineHeight = 55.sp, fontWeight = FontWeight.Bold)
                Text(" / 240 h", Modifier.padding(bottom = 5.dp), color = Color(0xFFC2C5DA),
                    fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth().height(7.dp).background(MahaLilac.copy(alpha = .24f), CircleShape)) {
                Box(Modifier.fillMaxWidth(50f / 240f).fillMaxHeight().background(MahaAqua, CircleShape))
            }
            Spacer(Modifier.height(12.dp))
            Surface(onClick = onAddTask, shape = RoundedCornerShape(15.dp), color = MahaAqua) {
                Text("+   Add task", Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    color = MahaNavy, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
        Spacer(Modifier.height(12.dp))
        MahaList {
            Box(Modifier.fillMaxWidth().clickable { onTasksClick() }) {
                MahaListRow("Tasks", "1 task returned for updates") { MahaTileIcon(Icons.Outlined.TaskAlt) }
            }
            HorizontalDivider(color = MahaLine)
            Box(Modifier.fillMaxWidth().clickable { onTabSelected(BottomBarItem.MyTraining) }) {
                MahaListRow("My Training", "Add this week’s attendance and hours") {
                    MahaTileIcon(Icons.Outlined.WorkOutline, MahaMint, Color(0xFF00696E))
                }
            }
            HorizontalDivider(color = MahaLine)
            Box(Modifier.fillMaxWidth().clickable(onClick = onWeeklyEvaluation)) {
                MahaListRow("Weekly Evaluation", "Week 2 · 9/10") {
                    MahaTileIcon(Icons.Outlined.FactCheck, MahaGold, Color(0xFF795A14))
                }
            }
        }
    }
}

@Preview(name = "Student · Home", widthDp = 390, heightDp = 844)
@Composable
private fun StudentHomePreview() {
    Column(Modifier.fillMaxSize().background(MahaNavy)) {
        Box(Modifier.weight(1f).fillMaxWidth()) {
            StudentHomeLayout(previewStatusBar = true)
        }
        Box(Modifier.fillMaxWidth().height(17.dp), contentAlignment = Alignment.Center) {
            Box(Modifier.width(100.dp).height(4.dp)
                .background(Color.White.copy(alpha = .45f), CircleShape))
        }
    }
}

@Composable
private fun MahaPreviewStatusBar() {
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
