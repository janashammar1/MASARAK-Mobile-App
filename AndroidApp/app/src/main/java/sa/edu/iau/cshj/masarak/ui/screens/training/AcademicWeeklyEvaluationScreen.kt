package sa.edu.iau.cshj.masarak.ui.screens.training

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sa.edu.iau.cshj.masarak.ui.components.BottomBarItem

@Composable
fun AcademicWeeklyEvaluationScreen(onBack: () -> Unit = {}, onTabSelected: (BottomBarItem) -> Unit = {}) =
    AcademicEvaluationLayout(onBack = onBack, onTabSelected = onTabSelected)

@Composable
private fun AcademicEvaluationLayout(previewFrame: Boolean = false, onBack: () -> Unit = {}, onTabSelected: (BottomBarItem) -> Unit = {}) {
    TrainingPage(
        BottomBarItem.MyTraining, academic = true, previewFrame = previewFrame, onTabSelected = onTabSelected,
        header = {
            Column {
                TrainingTitle("Noura Almutairi", back = true, subtitle = "Nakheel Tech · Week 3 · 88 / 240 h", onBack = onBack)
                AcademicEvaluationTabs()
            }
        }
    ) {
        EvaluationWeek(
            "Week 3", "14–18 September", status = "Not evaluated yet",
            tasks = listOf("Write unit tests for the cart"),
            extra = { AcademicGradeForm() }
        )
        EvaluationWeek(
            "Week 2", "7–11 September", grade = "8",
            tasks = listOf("Review pull requests"),
            comment = "Good start. Ask questions early when you’re stuck."
        )
    }
}

@Composable
private fun AcademicEvaluationTabs() {
    Row(Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 10.dp)) {
        listOf("Tasks", "Attendance", "Weekly\nEvaluation").forEachIndexed { index, label ->
            val selected = index == 2
            Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                Text(label, color = if (selected) TrainingNavy else TrainingMuted, fontSize = 14.sp,
                    lineHeight = 16.sp, textAlign = TextAlign.Center,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
                if (selected) {
                    Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth(.8f).height(3.dp)
                        .background(TrainingNavy, RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)))
                }
            }
        }
    }
    HorizontalDivider(color = TrainingLine)
}

@Composable
private fun AcademicGradeForm() {
    HorizontalDivider(Modifier.padding(vertical = 6.dp), color = TrainingLine)
    TrainingText("YOUR WEEKLY EVALUATION", 12, TrainingMuted, FontWeight.SemiBold)
    Spacer(Modifier.height(6.dp))
    TrainingText("Grade", 13, weight = FontWeight.SemiBold)
    Spacer(Modifier.height(4.dp))
    AcademicField("8", suffix = "/ 10")
    Spacer(Modifier.height(6.dp))
    Text(
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append("Comment") }
            withStyle(SpanStyle(color = TrainingMuted)) { append(" (optional)") }
        },
        color = TrainingInk, fontSize = 13.sp
    )
    Spacer(Modifier.height(4.dp))
    AcademicField("Solid testing work. Try to share progress earlier in the week.", multiline = true)
    Spacer(Modifier.height(8.dp))
    TrainingAction("Save evaluation")
}

@Composable
private fun AcademicField(value: String, suffix: String? = null, multiline: Boolean = false) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFF8A898F))
    ) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = if (multiline) 62.dp else 44.dp)
                .padding(horizontal = 14.dp, vertical = 9.dp),
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

@Preview(name = "Academic Weekly Evaluation", widthDp = 390, heightDp = 844)
@Composable
private fun AcademicWeeklyEvaluationPreview() = MaterialTheme { AcademicEvaluationLayout(previewFrame = true) }
