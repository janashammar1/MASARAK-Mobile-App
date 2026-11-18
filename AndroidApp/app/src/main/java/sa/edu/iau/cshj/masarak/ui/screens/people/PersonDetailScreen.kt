package sa.edu.iau.cshj.masarak.ui.screens.people

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
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PersonDetailScreen() = PersonDetailScreen(isAcademic = true)

@Composable
fun PersonDetailScreen(isAcademic: Boolean, attendance: Boolean = false, personName: String? = null, onBackClick: () -> Unit = {}, onReviewClick: (() -> Unit)? = null, onEvaluation: (() -> Unit)? = null, onFieldReview: (() -> Unit)? = null) {
    PersonDetailLayout(isAcademic, attendance, LocalInspectionMode.current, personName, onBackClick, onReviewClick, onEvaluation, onFieldReview)
}

private data class PersonTask(val title: String, val date: String, val status: String)

private val academicTasks = listOf(
    PersonTask("Build the login API", "6 h · Individual · 10 Sep", "Needs Review"),
    PersonTask("Connect login to the app", "4 h · Team · 9 Sep", "Partially Relevant"),
    PersonTask("Design the database", "8 h · Team · 8 Sep", "Relevant"),
    PersonTask("Organise the shared drive", "3 h · Individual · 7 Sep", "Not Relevant"),
    PersonTask("Update the team calendar", "2 h · Individual · 4 Sep", "Not Relevant")
)
private val fieldTasks = listOf(
    PersonTask("Build the profile screen", "5 h · Individual · Today", "Needs Review"),
    PersonTask("Design the settings screen", "3 h · Individual · Today", "Needs Review"),
    PersonTask("Fix the sorting bug", "2 h · Individual · 10 Sep", "Returned for Updates"),
    PersonTask("Write API tests", "4 h · Team · 11 Sep", "Waiting for Academic Review"),
    PersonTask("Set up the test database", "6 h · Team · 8 Sep", "Relevant")
)

@Composable
private fun PersonDetailLayout(academic: Boolean, attendance: Boolean, preview: Boolean, personName: String? = null, onBackClick: () -> Unit = {}, onReviewClick: (() -> Unit)? = null, onEvaluation: (() -> Unit)? = null, onFieldReview: (() -> Unit)? = null) {
    var showAttendance by rememberSaveable(attendance) { mutableStateOf(attendance) }
    PeoplePage(academic, preview, header = {
        PeopleTitle(personName ?: if (academic) "Sara Alqahtani" else "Jana Alharbi",
            "Nakheel Tech · Week 3 · ${if (academic) 50 else 54} / 240 h", onBackClick)
        PersonTabs(academic, showAttendance, onEvaluation) { showAttendance = it }
    }) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(19.dp, 11.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)) {
            if (showAttendance) {
                if (!academic) AttendanceWeek(3, "14–18 September", 5, 30, confirmed = false)
                AttendanceWeek(2, "7–11 September", 5, if (academic) 30 else 32)
                AttendanceWeek(1, "31 August–4 September", 4, if (academic) 20 else 22)
            } else {
                (if (academic) academicTasks else fieldTasks).forEach { PersonTaskCard(it, if (academic && (personName == null || personName == "Sara Alqahtani") && it.title == "Build the login API") onReviewClick else if (!academic && personName == "Jana Alharbi" && it.title == "Build the profile screen") onFieldReview else null) }
            }
        }
    }
}

@Composable
private fun PersonTabs(academic: Boolean, attendance: Boolean, onEvaluation: (() -> Unit)?, onTab: (Boolean) -> Unit) {
    val labels = if (academic) listOf("Tasks", "Attendance", "Weekly\nEvaluation") else listOf("Tasks", "Attendance")
    Row(Modifier.fillMaxWidth().height(49.dp).padding(horizontal = 19.dp)) {
        labels.forEachIndexed { index, label ->
            val selected = index == if (attendance) 1 else 0
            val count = when (index) {
                0 -> if (academic) "1" else "2"
                1 -> if (academic) null else "1"
                else -> null
            }
            Box(Modifier.weight(1f).fillMaxHeight().then(if (index < 2) Modifier.clickable { onTab(index == 1) } else if (onEvaluation != null) Modifier.clickable(onClick = onEvaluation) else Modifier), contentAlignment = Alignment.Center) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(label, color = if (selected) PeopleNavy else PeopleMuted, fontSize = 14.sp,
                        lineHeight = 16.sp, textAlign = TextAlign.Center,
                        fontWeight = if (selected) androidx.compose.ui.text.font.FontWeight.Bold
                        else androidx.compose.ui.text.font.FontWeight.Medium)
                    if (count != null) {
                        Box(Modifier.background(PeopleMint, CircleShape).padding(horizontal = 7.dp)) {
                            PeopleText(count, 10, PeopleTeal, bold = true)
                        }
                    }
                }
                if (selected) Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth(.9f).height(3.dp)
                    .background(PeopleNavy, RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)))
            }
        }
    }
    HorizontalDivider(color = PeopleLine)
}

@Composable
private fun PersonTaskCard(task: PersonTask, onClick: (() -> Unit)? = null) {
    PeopleCard {
        Row(Modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier).padding(14.dp), verticalAlignment = Alignment.Top) {
            PeopleTile(Icons.Outlined.TaskAlt)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                PeopleText(task.title, 15, bold = true)
                PeopleText(task.date, 12, PeopleMuted)
                val background = when (task.status) {
                    "Relevant" -> Color(0xFFCAEDC8)
                    "Not Relevant" -> Color(0xFFFFDAD7)
                    "Partially Relevant", "Returned for Updates" -> Color(0xFFFFCD82)
                    else -> Color(0xFFE4E2DB)
                }
                val icon = when (task.status) {
                    "Relevant", "Partially Relevant" -> Icons.Outlined.Circle
                    "Not Relevant" -> Icons.Outlined.RadioButtonUnchecked
                    "Returned for Updates" -> Icons.Outlined.AssignmentReturn
                    else -> Icons.Outlined.Schedule
                }
                PersonChip(task.status, icon, background)
            }
            Icon(Icons.Outlined.ChevronRight, null, Modifier.align(Alignment.CenterVertically).size(19.dp),
                tint = PeopleInk)
        }
    }
}

@Composable
private fun AttendanceWeek(week: Int, dates: String, days: Int, hours: Int, confirmed: Boolean = true) {
    PeopleCard {
        Row(Modifier.fillMaxWidth().padding(14.dp)) {
            PeopleTile(Icons.Outlined.EventAvailable, PeopleMint, PeopleTeal)
            Spacer(Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                PeopleText("Week $week", 16, bold = true)
                PeopleText(dates, 13, PeopleMuted)
                Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                    AttendanceMetric(Icons.Outlined.HowToReg, "$days days")
                    AttendanceMetric(Icons.Outlined.Schedule, "$hours h")
                }
                PersonChip("week-$week-report.pdf", Icons.Outlined.Description, Color(0xFFF0EFEB),
                    iconColor = PeopleTeal)
                if (confirmed) {
                    PersonChip("Confirmed by Field Supervisor", Icons.Outlined.Verified, Color(0xFFCAEDC8))
                } else {
                    Row(Modifier.background(PeopleNavy, RoundedCornerShape(13.dp)).padding(15.dp, 12.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Outlined.Check, null, Modifier.size(15.dp), tint = Color.White)
                        PeopleText("Confirm", 13, Color.White, bold = true)
                    }
                }
            }
        }
    }
}

@Composable
private fun AttendanceMetric(icon: ImageVector, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, null, Modifier.size(15.dp), tint = PeopleTeal)
        PeopleText(value, 12)
    }
}

@Composable
internal fun PersonChip(value: String, icon: ImageVector, bg: Color, iconColor: Color = PeopleInk) {
    Row(Modifier.background(bg, RoundedCornerShape(7.dp)).padding(8.dp, 5.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Icon(icon, null, Modifier.size(13.dp), tint = iconColor)
        PeopleText(value, 11, bold = true)
    }
}

@Preview(name = "Academic · Tasks", widthDp = 390, heightDp = 844)
@Composable
private fun AcademicTasksPreview() = MaterialTheme { PersonDetailLayout(true, false, true) }

@Preview(name = "Academic · Attendance", widthDp = 390, heightDp = 844)
@Composable
private fun AcademicAttendancePreview() = MaterialTheme { PersonDetailLayout(true, true, true) }

@Preview(name = "Field · Tasks", widthDp = 390, heightDp = 844)
@Composable
private fun FieldTasksPreview() = MaterialTheme { PersonDetailLayout(false, false, true) }

@Preview(name = "Field · Attendance", widthDp = 390, heightDp = 844)
@Composable
private fun FieldAttendancePreview() = MaterialTheme { PersonDetailLayout(false, true, true) }
