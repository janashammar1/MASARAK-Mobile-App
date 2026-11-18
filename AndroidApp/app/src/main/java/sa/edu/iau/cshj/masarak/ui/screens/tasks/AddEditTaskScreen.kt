package sa.edu.iau.cshj.masarak.ui.screens.tasks

import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import sa.edu.iau.cshj.masarak.ui.components.DatePickerDialog
import sa.edu.iau.cshj.masarak.ui.components.AddAttachmentSheet
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Send
import sa.edu.iau.cshj.masarak.ui.components.AppBottomBar
import sa.edu.iau.cshj.masarak.ui.components.BottomBarItem
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val TaskNavy = Color(0xFF0C0D45)
private val TaskPaper = Color(0xFFF7F6F5)
private val TaskLine = Color(0xFFD9DADF)

@Composable
fun AddEditTaskScreen(onBackClick: () -> Unit = {}) =
    StudentTaskForm(onBackClick = onBackClick)

@Composable
internal fun StudentTaskForm(filled: Boolean = true, scrolled: Boolean = false, dateFocused: Boolean = false, showEvidenceRow: Boolean = true, onBackClick: () -> Unit = {}) {
    var overlay by rememberSaveable { mutableStateOf("") }
    when (overlay) {
        "date" -> DatePickerDialog(onDismiss = { overlay = "" })
        "attachment" -> AddAttachmentSheet(onDismiss = { overlay = "" })
    }
    Column(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(0f to Color(0xFFE2ECF9), .24f to TaskPaper, 1f to TaskPaper)
        ).statusBarsPadding()
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.ArrowBack, "Back", tint = TaskNavy, modifier = Modifier.size(21.dp).clickable(onClick = onBackClick))
            Spacer(Modifier.width(17.dp))
            Text("Add task", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TaskNavy)
        }
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            if (!scrolled) {
                Text("Add a task after you complete it.", fontSize = 13.sp, lineHeight = 17.sp, color = Color(0xFF55555F))
                TaskField("Task title",
                    if (filled) "Build the sign-up form" else "e.g. Build the sign-up form", filled)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.weight(1f)) {
                    TaskField("Date completed", "Mon, 14 Sep", true, icon = Icons.Outlined.CalendarToday, focused = dateFocused || overlay == "date", onClick = { overlay = "date" })
                }
                Box(Modifier.weight(1f)) {
                    TaskField("Time spent", if (filled) "5" else "0", filled, suffix = "h")
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Done as", color = TaskNavy, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(25.dp)) {
                    TaskType("Individual", true)
                    TaskType("Team", false)
                }
            }
            TaskField("Description", if (filled) "Built the form with field validation and clear error messages."
            else "What did you do, and how?", filled, 98)
            TaskField("Challenges", "Anything that made it harder?", false, 78, optional = true)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                TaskLabel("Evidence", optional = true)
                if (showEvidenceRow) Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(onClick = { overlay = "attachment" }, shape = RoundedCornerShape(14.dp), color = Color.White, border = BorderStroke(1.dp, TaskLine)) {
                        Row(Modifier.padding(horizontal = 12.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.AttachFile, null, Modifier.size(15.dp), tint = TaskNavy)
                            Spacer(Modifier.width(6.dp))
                            Text("Add attachment", color = TaskNavy, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    if (filled) Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(13.dp), color = Color.Transparent, border = BorderStroke(1.dp, Color(0xFFE1DFDB))) {
                        Row(Modifier.padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Image, null, Modifier.size(14.dp), tint = Color(0xFF00696E))
                            Spacer(Modifier.width(5.dp))
                            Text("signup-form.png", Modifier.weight(1f), color = TaskNavy, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Spacer(Modifier.width(7.dp))
                            Text("×", color = TaskNavy, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
        Surface(color = Color.White) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 9.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (filled) "Ready to send. Eng. Khalid will review it next." else "Add a title to continue",
                    color = Color(0xFF55555F), fontSize = 11.sp)
                Spacer(Modifier.height(6.dp))
                Button(onClick = {}, enabled = filled, modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TaskNavy)) {
                    Icon(Icons.Outlined.Send, null, Modifier.size(15.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Submit task", fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Box(Modifier.fillMaxWidth().background(TaskNavy).navigationBarsPadding()) {
            AppBottomBar(selectedItem = BottomBarItem.Home, thirdTabLabel = "My Training", chatUnreadCount = 2)
        }
    }
}

@Composable
private fun TaskField(label: String, value: String, filled: Boolean, minHeight: Int = 52, icon: ImageVector? = null, suffix: String? = null, focused: Boolean = false, optional: Boolean = false, onClick: (() -> Unit)? = null) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        TaskLabel(label, optional, if (focused) Color(0xFF00696E) else TaskNavy)
        Surface(
            modifier = Modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier), shape = RoundedCornerShape(15.dp),
            color = Color.White, border = BorderStroke(if (focused) 2.dp else 1.dp, if (focused) Color(0xFF00696E) else Color(0xFFA3A2A8))
        ) {
            Row(
                Modifier.fillMaxWidth().heightIn(min = minHeight.dp).padding(horizontal = 11.dp, vertical = 10.dp),
                verticalAlignment = if (minHeight > 52) Alignment.Top else Alignment.CenterVertically
            ) {
                Text(value, Modifier.weight(1f), color = if (filled) TaskNavy else Color(0xFF63636D), fontSize = 14.sp)
                if (suffix != null) Text(suffix, color = Color(0xFF63636D), fontSize = 13.sp)
                if (icon != null) {
                    Spacer(Modifier.width(4.dp))
                    Icon(icon, null, tint = TaskNavy, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun TaskLabel(label: String, optional: Boolean = false, color: Color = TaskNavy) {
    Text(
        text = buildAnnotatedString {
            append(label)
            if (optional) {
                withStyle(SpanStyle(color = Color(0xFF74747E), fontWeight = FontWeight.Normal)) {
                    append(" (optional)")
                }
            }
        },
        color = color, fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun TaskType(label: String, selected: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(18.dp).border(2.dp, if (selected) TaskNavy else Color(0xFF64646C), CircleShape),
            contentAlignment = Alignment.Center) {
            if (selected) Box(Modifier.size(9.dp).background(TaskNavy, CircleShape))
        }
        Spacer(Modifier.width(10.dp))
        Text(label, color = TaskNavy, fontSize = 14.sp)
    }
}

@Preview(name = "Add task", widthDp = 390, heightDp = 844)
@Composable
private fun AddTaskPreview() = TaskPreviewFrame { StudentTaskForm(showEvidenceRow = false) }

@Preview(name = "Add task · scrolled", widthDp = 390, heightDp = 844)
@Composable
private fun AddTaskScrolledPreview() = TaskPreviewFrame { StudentTaskForm(scrolled = true) }

@Composable
private fun TaskPreviewFrame(content: @Composable () -> Unit) {
    MaterialTheme {
        Column(Modifier.fillMaxSize().background(TaskNavy)) {
            TaskPreviewStatusBar()
            Box(Modifier.weight(1f).fillMaxWidth()) {
                content()
            }
            Box(Modifier.fillMaxWidth().height(17.dp), contentAlignment = Alignment.Center) {
                Box(Modifier.width(100.dp).height(4.dp)
                    .background(Color.White.copy(alpha = .45f), CircleShape))
            }
        }
    }
}

@Composable
private fun TaskPreviewStatusBar() {
    Box(Modifier.fillMaxWidth().height(28.dp).background(Color(0xFFE2ECF9)).padding(horizontal = 20.dp)) {
        Text("9:41", modifier = Modifier.align(Alignment.CenterStart),
            color = TaskNavy, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Canvas(Modifier.align(Alignment.CenterEnd).size(50.dp, 14.dp)) {
            val u = size.height / 14f
            repeat(4) { i ->
                val h = (3 + i * 2) * u
                drawRect(TaskNavy, Offset(i * 3 * u, 12 * u - h), Size(2 * u, h))
            }
            listOf(12f, 8f, 4f).forEach { d ->
                drawArc(TaskNavy, 225f, 90f, false,
                    topLeft = Offset((23f - d / 2) * u, (11f - d / 2) * u),
                    size = Size(d * u, d * u), style = Stroke(1.2f * u))
            }
            drawCircle(TaskNavy, u, Offset(23 * u, 11 * u))
            drawRect(TaskNavy, Offset(36 * u, 3 * u), Size(7 * u, 10 * u))
            drawRect(TaskNavy, Offset(38 * u, 1.5f * u), Size(3 * u, 2 * u))
        }
    }
}
