package sa.edu.iau.cshj.masarak.ui.screens.training

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sa.edu.iau.cshj.masarak.ui.components.BottomBarItem

@Composable
fun WeeklyEvaluationScreen(onBack: () -> Unit = {}, onTabSelected: (BottomBarItem) -> Unit = {}) =
    StudentEvaluationLayout(onBack = onBack, onTabSelected = onTabSelected)

@Composable
private fun StudentEvaluationLayout(scrolled: Boolean = false, previewFrame: Boolean = false, onBack: () -> Unit = {}, onTabSelected: (BottomBarItem) -> Unit = {}) {
    TrainingPage(BottomBarItem.Home, scrolled = scrolled, previewFrame = previewFrame, onTabSelected = onTabSelected, header = { TrainingTitle("Weekly Evaluation", back = true, onBack = onBack) }) {
        EvaluationProgress()
        EvaluationWeek("Week 3", "14–18 September", status = "Week in progress",
            tasks = listOf("Fix the navigation bug", "Build the sign-up form"))
        EvaluationWeek("Week 2", "7–11 September", grade = "9",
            tasks = listOf("Design the database", "Build the login API", "Connect login to the app"),
            comment = "Strong week. Your work was clear and well organised.")
        EvaluationWeek("Week 1", "31 August–4 September", grade = "8",
            tasks = listOf("Set up the development environment", "Build the sign-up screen", "Write validation tests"),
            comment = "Good start. Ask questions early when you’re stuck.")
    }
}

@Composable
private fun EvaluationProgress() {
    TrainingCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TrainingText("YOUR PROGRESS", 12, TrainingMuted, FontWeight.SemiBold)
            TrainingText("2 of 12 weeks graded", 12, TrainingNavy, FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        TrainingText("Weekly grades from Dr. Huda,\nout of 10.", 13, TrainingMuted)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ProgressBar("8", "W1")
            ProgressBar("9", "W2")
        }
    }
}

@Composable
private fun ProgressBar(grade: String, week: String) {
    Column(Modifier.width(68.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        TrainingText(grade, 12, TrainingNavy, FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Box(Modifier.fillMaxWidth().height(45.dp).background(TrainingAqua, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 3.dp, bottomEnd = 3.dp)))
        Spacer(Modifier.height(3.dp))
        TrainingText(week, 12, TrainingMuted)
    }
}

@Composable
internal fun EvaluationWeek(
    title: String, date: String, tasks: List<String>, grade: String? = null, status: String? = null,
    comment: String? = null, extra: (@Composable ColumnScope.() -> Unit)? = null
) {
    TrainingCard {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                TrainingText(title, 17, weight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                TrainingText(date, 13, TrainingMuted)
            }
            if (grade != null) {
                Row(verticalAlignment = Alignment.Bottom) {
                    TrainingText(grade, 25, TrainingNavy, FontWeight.Bold)
                    TrainingText(" / 10", 12, TrainingMuted, FontWeight.SemiBold, Modifier.padding(bottom = 3.dp))
                }
            } else if (status != null) {
                TrainingText(status, 13, TrainingMuted)
            }
        }
        HorizontalDivider(Modifier.padding(vertical = 10.dp), color = TrainingLine)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            tasks.forEach { task ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.TaskAlt, null, Modifier.size(21.dp), tint = TrainingTeal)
                    Spacer(Modifier.width(8.dp))
                    TrainingText(task, 14)
                }
            }
        }
        if (comment != null) {
            Spacer(Modifier.height(12.dp))
            EvaluationComment(comment)
        }
        extra?.invoke(this)
    }
}

@Composable
private fun EvaluationComment(comment: String) {
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), color = Color(0xFFE2E3F0)) {
        Column(Modifier.padding(13.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.AccountBalance, null, Modifier.size(16.dp), tint = TrainingNavy)
                Spacer(Modifier.width(7.dp))
                TrainingText("Dr. Huda · Academic Supervisor", 12, TrainingNavy, FontWeight.Bold)
            }
            Spacer(Modifier.height(5.dp))
            TrainingText(comment, 14, TrainingNavy)
        }
    }
}

@Preview(name = "Weekly Evaluation", widthDp = 390, heightDp = 844)
@Composable
private fun WeeklyEvaluationPreview() = MaterialTheme { StudentEvaluationLayout(previewFrame = true) }

@Preview(name = "Weekly Evaluation · scrolled", widthDp = 390, heightDp = 844)
@Composable
private fun WeeklyEvaluationScrolledPreview() = MaterialTheme { StudentEvaluationLayout(scrolled = true, previewFrame = true) }
