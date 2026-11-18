package sa.edu.iau.cshj.masarak.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import sa.edu.iau.cshj.masarak.ui.screens.tasks.StudentTaskForm

private val DateNavy = Color(0xFF0C0D45)

@Composable
fun DatePickerDialog(onDismiss: () -> Unit = {}) {
    Dialog(onDismissRequest = onDismiss) { DatePickerLayout(onDismiss) }
}

@Composable
private fun DatePickerLayout(onDismiss: () -> Unit = {}) {
    Surface(
        modifier = Modifier.widthIn(max = 340.dp).fillMaxWidth(),
        shape = RoundedCornerShape(22.dp), color = Color.White
    ) {
        Column(Modifier.padding(18.dp)) {
            Text("Select date", color = Color(0xFF55555F), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(16.dp))
            Text("Mon, 14 Sep", color = DateNavy, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFE3E3E8))
            Row(Modifier.fillMaxWidth().height(44.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.ChevronLeft, "Previous month", tint = DateNavy)
                Text("September 2026", Modifier.weight(1f).padding(horizontal = 10.dp),
                    color = DateNavy, fontWeight = FontWeight.SemiBold)
                Icon(Icons.Outlined.ChevronRight, "Next month", tint = DateNavy)
            }
            Row(Modifier.fillMaxWidth()) {
                listOf("S", "M", "T", "W", "T", "F", "S").forEach {
                    Box(Modifier.weight(1f).height(30.dp), contentAlignment = Alignment.Center) {
                        Text(it, color = Color(0xFF636575), fontSize = 12.sp)
                    }
                }
            }
            val days = List(2) { "" } + (1..30).map { it.toString() } + List(3) { "" }
            days.chunked(7).forEach { week ->
                Row(Modifier.fillMaxWidth()) {
                    week.forEach { day ->
                        Box(Modifier.weight(1f).height(36.dp), contentAlignment = Alignment.Center) {
                            Box(Modifier.size(32.dp).background(
                                if (day == "14") DateNavy else Color.Transparent, CircleShape
                            ), contentAlignment = Alignment.Center) {
                                Text(day, color = if (day == "14") Color.White else if ((day.toIntOrNull() ?: 0) > 14) Color(0xFF9C9CA5) else DateNavy, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("Choose the day you completed the task. Future days aren’t available.", color = Color(0xFF55555F), fontSize = 12.sp, lineHeight = 16.sp)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Cancel", color = Color(0xFF00696E)) }
                TextButton(onClick = onDismiss) { Text("OK", color = Color(0xFF00696E), fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Preview(name = "Date Picker Dialog", widthDp = 390, heightDp = 844)
@Composable
private fun DatePickerPreview() {
    MaterialTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(Modifier.fillMaxSize().background(DateNavy)) {
                DatePreviewStatusBar()
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    StudentTaskForm(filled = false, dateFocused = true)
                }
                Box(Modifier.fillMaxWidth().height(17.dp), contentAlignment = Alignment.Center) {
                    Box(Modifier.width(100.dp).height(4.dp)
                        .background(Color.White.copy(alpha = .45f), CircleShape))
                }
            }
            Box(Modifier.matchParentSize().background(DateNavy.copy(alpha = .32f)))
            Box(Modifier.fillMaxWidth().padding(horizontal = 40.dp), contentAlignment = Alignment.Center) { DatePickerLayout() }
        }
    }
}

@Composable
private fun DatePreviewStatusBar() {
    Box(Modifier.fillMaxWidth().height(28.dp).background(Color(0xFFE2ECF9)).padding(horizontal = 20.dp)) {
        Text("9:41", modifier = Modifier.align(Alignment.CenterStart),
            color = DateNavy, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Canvas(Modifier.align(Alignment.CenterEnd).size(50.dp, 14.dp)) {
            val u = size.height / 14f
            repeat(4) { i ->
                val h = (3 + i * 2) * u
                drawRect(DateNavy, Offset(i * 3 * u, 12 * u - h), Size(2 * u, h))
            }
            listOf(12f, 8f, 4f).forEach { d ->
                drawArc(DateNavy, 225f, 90f, false,
                    topLeft = Offset((23f - d / 2) * u, (11f - d / 2) * u),
                    size = Size(d * u, d * u), style = Stroke(1.2f * u))
            }
            drawCircle(DateNavy, u, Offset(23 * u, 11 * u))
            drawRect(DateNavy, Offset(36 * u, 3 * u), Size(7 * u, 10 * u))
            drawRect(DateNavy, Offset(38 * u, 1.5f * u), Size(3 * u, 2 * u))
        }
    }
}
