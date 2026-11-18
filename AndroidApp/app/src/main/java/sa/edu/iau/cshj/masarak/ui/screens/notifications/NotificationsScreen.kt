package sa.edu.iau.cshj.masarak.ui.screens.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sa.edu.iau.cshj.masarak.ui.components.BottomBarItem
import sa.edu.iau.cshj.masarak.ui.screens.home.*

private data class MahaNotice(val title: String, val details: String, val time: String, val icon: ImageVector)

@Composable
fun NotificationsScreen(
    role: String = "student",
    onBack: () -> Unit = {},
    onTabSelected: (BottomBarItem) -> Unit = {}
) = NotificationsLayout(
    when (role) {
        "academic" -> MahaRole.Academic
        "field" -> MahaRole.Field
        else -> MahaRole.Student
    },
    onBack, onTabSelected
)

@Composable
private fun NotificationsLayout(
    role: MahaRole,
    onBack: () -> Unit = {},
    onTabSelected: (BottomBarItem) -> Unit = {}
) {
    val notices = when (role) {
        MahaRole.Student -> listOf(
            MahaNotice("Returned for Updates", "Eng. Khalid · Fix the navigation bug", "Fri", Icons.Outlined.AssignmentReturn),
            MahaNotice("Linking request", "Dr. Huda Alzahrani wants to link with you", "Sun", Icons.Outlined.Link)
        )
        MahaRole.Academic -> listOf(
            MahaNotice("Academic Alert", "Sara Alqahtani · recent tasks were Not Relevant", "09:30", Icons.Outlined.Warning),
            MahaNotice("Needs Review", "Sara Alqahtani · Build the login API", "09:30", Icons.Outlined.TaskAlt),
            MahaNotice("Needs Review", "Jana Alharbi · Write API tests", "Yesterday", Icons.Outlined.TaskAlt)
        )
        MahaRole.Field -> listOf(
            MahaNotice("Needs Review", "Jana Alharbi · Build the profile screen", "09:32", Icons.Outlined.TaskAlt),
            MahaNotice("Needs Review", "Sara Alqahtani · Build the sign-up form", "09:10", Icons.Outlined.TaskAlt),
            MahaNotice("Week 3 ready to confirm", "Jana Alharbi recorded her attendance", "08:15", Icons.Outlined.EventAvailable),
            MahaNotice("New message", "Dr. Huda Alzahrani", "08:22", Icons.Outlined.ChatBubbleOutline)
        )
    }
    MahaScreen(role, onTabSelected = onTabSelected) {
        Row(Modifier.heightIn(min = 42.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(42.dp)) {
                Icon(Icons.Outlined.ArrowBack, "Back", modifier = Modifier.size(22.dp), tint = MahaInk)
            }
            Spacer(Modifier.width(8.dp))
            Text("Notifications", color = MahaInk, fontSize = 19.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
        MahaList {
            notices.forEachIndexed { index, notice ->
                if (index > 0) HorizontalDivider(color = MahaLine)
                MahaListRow(notice.title, notice.details, notice.time, unread = true) { MahaTileIcon(notice.icon) }
            }
        }
    }
}

@Preview(name = "Student · Notifications", widthDp = 390, heightDp = 844, showBackground = true)
@Composable
private fun StudentNotificationsPreview() = NotificationsLayout(MahaRole.Student)

@Preview(name = "Field Supervisor · Notifications", widthDp = 390, heightDp = 844, showBackground = true)
@Composable
private fun FieldNotificationsPreview() = NotificationsLayout(MahaRole.Field)

@Preview(name = "Academic Supervisor · Notifications", widthDp = 390, heightDp = 844, showBackground = true)
@Composable
private fun AcademicNotificationsPreview() = NotificationsLayout(MahaRole.Academic)
