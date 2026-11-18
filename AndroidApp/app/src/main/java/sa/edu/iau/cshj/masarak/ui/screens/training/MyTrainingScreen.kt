package sa.edu.iau.cshj.masarak.ui.screens.training

import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sa.edu.iau.cshj.masarak.ui.components.AppBottomBar
import sa.edu.iau.cshj.masarak.ui.components.BottomBarItem

internal val TrainingNavy = Color(0xFF0C0D45)
internal val TrainingInk = Color(0xFF191925)
internal val TrainingMuted = Color(0xFF55555D)
internal val TrainingLine = Color(0xFFDADAD7)
internal val TrainingTeal = Color(0xFF00696E)
internal val TrainingAqua = Color(0xFF75CBD1)
private val TrainingPaper = Color(0xFFF7F7F4)

@Composable
internal fun TrainingPage(
    selected: BottomBarItem,
    academic: Boolean = false,
    scrolled: Boolean = false,
    previewFrame: Boolean = false,
    onTabSelected: (BottomBarItem) -> Unit = {},
    header: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val background = if (academic) {
        Brush.verticalGradient(0f to TrainingPaper, .28f to TrainingPaper, 1f to Color(0xFFDCEAF3))
    } else {
        Brush.verticalGradient(0f to Color(0xFFDCE8F4), .24f to TrainingPaper, 1f to TrainingPaper)
    }
    Column(Modifier.fillMaxSize().background(background).then(
        if (previewFrame) Modifier else Modifier.statusBarsPadding()
    )) {
        if (previewFrame) TrainingPreviewStatusBar()
        header()
        Column(
            Modifier.weight(1f).fillMaxWidth()
                .verticalScroll(rememberScrollState(if (scrolled) Int.MAX_VALUE else 0))
                .padding(start = 18.dp, end = 18.dp, top = if (previewFrame) 5.dp else 12.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
        Column(
            Modifier.fillMaxWidth()
                .then(if (previewFrame) Modifier.clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)) else Modifier)
                .background(TrainingNavy)
                .then(if (previewFrame) Modifier else Modifier.navigationBarsPadding())
        ) {
            AppBottomBar(
                selectedItem = selected,
                thirdTabLabel = if (academic) "Students" else "My Training",
                chatUnreadCount = if (academic) 0 else 2,
                onItemSelected = onTabSelected
            )
            if (previewFrame) {
                Box(Modifier.fillMaxWidth().height(17.dp), contentAlignment = Alignment.Center) {
                    Box(Modifier.width(100.dp).height(4.dp)
                        .background(Color.White.copy(alpha = .45f), CircleShape))
                }
            }
        }
    }
}

@Composable
private fun TrainingPreviewStatusBar() {
    Box(Modifier.fillMaxWidth().height(28.dp).padding(horizontal = 20.dp)) {
        TrainingText("9:41", 12, weight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterStart))
        Canvas(Modifier.align(Alignment.CenterEnd).size(50.dp, 14.dp)) {
            val u = size.height / 14f
            repeat(4) { i ->
                val h = (3 + i * 2) * u
                drawRect(TrainingInk, Offset(i * 3 * u, 12 * u - h), Size(2 * u, h))
            }
            listOf(12f, 8f, 4f).forEach { d ->
                drawArc(TrainingInk, 225f, 90f, false,
                    topLeft = Offset((23f - d / 2) * u, (11f - d / 2) * u),
                    size = Size(d * u, d * u), style = Stroke(1.2f * u))
            }
            drawCircle(TrainingInk, u, Offset(23 * u, 11 * u))
            drawRect(TrainingInk, Offset(36 * u, 3 * u), Size(7 * u, 10 * u))
            drawRect(TrainingInk, Offset(38 * u, 1.5f * u), Size(3 * u, 2 * u))
        }
    }
}

@Composable
internal fun TrainingTitle(title: String, back: Boolean = false, subtitle: String? = null, onBack: () -> Unit = {}) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (back) {
            Icon(Icons.Outlined.ArrowBack, "Back", Modifier.size(21.dp).clickable(onClick = onBack), tint = TrainingInk)
            Spacer(Modifier.width(18.dp))
        }
        Column {
            Text(title, color = TrainingInk, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 24.sp)
            if (subtitle != null) Text(subtitle, color = TrainingMuted, fontSize = 12.sp, lineHeight = 16.sp)
        }
    }
}

@Composable
internal fun TrainingCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
        color = Color.White, border = BorderStroke(1.dp, TrainingLine)
    ) {
        Column(Modifier.fillMaxWidth().padding(14.dp), content = content)
    }
}

@Composable
internal fun TrainingText(
    value: String, size: Int = 13, color: Color = TrainingInk,
    weight: FontWeight = FontWeight.Normal, modifier: Modifier = Modifier
) {
    Text(value, modifier, color = color, fontSize = size.sp, lineHeight = (size + 4).sp, fontWeight = weight)
}

@Composable
internal fun TrainingField(value: String, modifier: Modifier = Modifier, suffix: String? = null, multiline: Boolean = false) {
    Surface(
        modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(15.dp), color = Color.White,
        border = BorderStroke(1.dp, Color(0xFF8A898F))
    ) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = if (multiline) 75.dp else 52.dp)
                .padding(horizontal = 15.dp, vertical = 12.dp),
            verticalAlignment = if (multiline) Alignment.Top else Alignment.CenterVertically
        ) {
            TrainingText(value, 15, Color(0xFF3A365D), modifier = Modifier.weight(1f))
            if (suffix != null) {
                Spacer(Modifier.width(8.dp))
                TrainingText(suffix, 14, TrainingMuted)
            }
        }
    }
}

@Composable
internal fun TrainingAction(label: String) {
    Button(
        onClick = {}, modifier = Modifier.fillMaxWidth().height(46.dp),
        shape = RoundedCornerShape(15.dp),
        colors = ButtonDefaults.buttonColors(containerColor = TrainingNavy, contentColor = Color.White)
    ) {
        Text(label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Composable
private fun TrainingGlyph(icon: ImageVector, tint: Color = TrainingTeal, size: Int = 16) {
    Icon(icon, null, Modifier.size(size.dp), tint = tint)
}

@Composable
fun MyTrainingScreen(onTabSelected: (BottomBarItem) -> Unit = {}) = MyTrainingLayout(onTabSelected = onTabSelected)

@Composable
private fun MyTrainingLayout(previewFrame: Boolean = false, showTrainingRecord: Boolean = true, onTabSelected: (BottomBarItem) -> Unit = {}) {
    TrainingPage(BottomBarItem.MyTraining, previewFrame = previewFrame, onTabSelected = onTabSelected, header = { TrainingTitle("My Training") }) {
        if (showTrainingRecord) TrainingRecord()
        CurrentTrainingWeek()
        ConfirmedTrainingWeek(2, "7–11 September", 5, 30)
        ConfirmedTrainingWeek(1, "31 August–4 September", 4, 20)
    }
}

@Composable
private fun TrainingRecord() {
    TrainingCard {
        TrainingText("TRAINING RECORD", 11, TrainingMuted, FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        TrainingText("Nakheel Tech · Software development", 15, weight = FontWeight.Bold)
        TrainingText("31 August – 20 November · 50 of 240 h confirmed", 12, TrainingMuted)
        HorizontalDivider(Modifier.padding(vertical = 10.dp), color = TrainingLine)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(31.dp).background(Color(0xFFF8E5B9), CircleShape), contentAlignment = Alignment.Center) {
                TrainingText("KA", 11, weight = FontWeight.Bold)
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                TrainingText("Eng. Khalid Alotaibi", 13, weight = FontWeight.Bold)
                TrainingText("Field Supervisor · reviews tasks and confirms attendance", 12, TrainingMuted)
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(31.dp).background(Color(0xFFF1F1EE), CircleShape), contentAlignment = Alignment.Center) {
                TrainingGlyph(Icons.Outlined.AccountBalance, TrainingMuted)
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                TrainingText("Academic Supervisor", 13, weight = FontWeight.Bold)
                TrainingText("Linking request waiting in Profile", 12, TrainingTeal, FontWeight.SemiBold)
            }
            TrainingGlyph(Icons.Outlined.ChevronRight, TrainingMuted)
        }
    }
}

@Composable
private fun CurrentTrainingWeek() {
    TrainingCard {
        TrainingText("Week 3", 17, weight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        TrainingText("14–18 September", 13, TrainingMuted)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                TrainingText("Days attended", 12, weight = FontWeight.SemiBold)
                TrainingField("5", suffix = "days")
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                TrainingText("Training hours", 12, weight = FontWeight.SemiBold)
                TrainingField("30", suffix = "h")
            }
        }
        Spacer(Modifier.height(12.dp))
        TrainingText("Weekly report", 12, weight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        TrainingReport(3, removable = true)
        Spacer(Modifier.height(12.dp))
        TrainingText("Ready to send. Eng. Khalid will confirm it.", 13, TrainingMuted)
        Spacer(Modifier.height(10.dp))
        TrainingAction("Submit week")
    }
}

@Composable
private fun TrainingReport(week: Int, removable: Boolean = false) {
    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF1F1EB), border = BorderStroke(1.dp, TrainingLine)) {
        Row(Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TrainingGlyph(Icons.Outlined.Description)
            TrainingText("week-$week-report.pdf", 12, weight = FontWeight.Medium)
            if (removable) TrainingGlyph(Icons.Outlined.Close, TrainingMuted, 15)
        }
    }
}

@Composable
private fun ConfirmedTrainingWeek(week: Int, date: String, days: Int, hours: Int) {
    TrainingCard {
        Row {
            Box(Modifier.size(38.dp).background(Color(0xFFCEF0EF), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center) {
                TrainingGlyph(Icons.Outlined.EventAvailable, size = 21)
            }
            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                TrainingText("Week $week", 17, weight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                TrainingText(date, 13, TrainingMuted)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TrainingGlyph(Icons.Outlined.PersonOutline)
                    TrainingText("$days days")
                    Spacer(Modifier.width(6.dp))
                    TrainingGlyph(Icons.Outlined.AccessTime)
                    TrainingText("$hours h")
                }
                Spacer(Modifier.height(10.dp))
                TrainingReport(week)
                Spacer(Modifier.height(10.dp))
                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFC9EAC6)) {
                    Row(Modifier.padding(horizontal = 8.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                        TrainingGlyph(Icons.Outlined.CheckCircle, Color(0xFF143B22), 14)
                        Spacer(Modifier.width(4.dp))
                        TrainingText("Confirmed by Field Supervisor", 12, Color(0xFF143B22), FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Preview(name = "My Training", widthDp = 390, heightDp = 844)
@Composable
private fun MyTrainingPreview() = MaterialTheme { MyTrainingLayout(previewFrame = true) }

@Preview(name = "My Training · scrolled", widthDp = 390, heightDp = 844)
@Composable
private fun MyTrainingScrolledPreview() = MaterialTheme { MyTrainingLayout(previewFrame = true, showTrainingRecord = false) }
