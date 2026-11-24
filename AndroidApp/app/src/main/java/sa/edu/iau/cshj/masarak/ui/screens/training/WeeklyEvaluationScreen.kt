package sa.edu.iau.cshj.masarak.ui.screens.training

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import sa.edu.iau.cshj.masarak.ui.components.BottomBarItem
import sa.edu.iau.cshj.masarak.ui.viewmodel.DEMO_STUDENT_EMAIL
import sa.edu.iau.cshj.masarak.ui.viewmodel.EvaluationWeekUi
import sa.edu.iau.cshj.masarak.ui.viewmodel.MasarakViewModelFactory
import sa.edu.iau.cshj.masarak.ui.viewmodel.TrainingLogic
import sa.edu.iau.cshj.masarak.ui.viewmodel.TrainingViewModel
import sa.edu.iau.cshj.masarak.ui.viewmodel.WeeklyEvaluationUiState

@Composable
fun WeeklyEvaluationScreen(
    onBack: () -> Unit = {},
    onTabSelected: (BottomBarItem) -> Unit = {},
    studentEmail: String = DEMO_STUDENT_EMAIL // replace with the logged-in user's email after login is connected
) {
    val trainingViewModel: TrainingViewModel = viewModel(factory = MasarakViewModelFactory())
    val state by trainingViewModel.weeklyEvaluation.collectAsState()

    LaunchedEffect(studentEmail) {
        trainingViewModel.loadStudentTraining(studentEmail)
    }

    StudentEvaluationLayout(
        state = state,
        onBack = onBack,
        onTabSelected = onTabSelected,
        onRetry = { trainingViewModel.loadStudentTraining(studentEmail) }
    )
}

@Composable
private fun StudentEvaluationLayout(
    state: WeeklyEvaluationUiState,
    scrolled: Boolean = false,
    previewFrame: Boolean = false,
    onBack: () -> Unit = {},
    onTabSelected: (BottomBarItem) -> Unit = {},
    onRetry: () -> Unit = {}
) {
    TrainingPage(BottomBarItem.Home, scrolled = scrolled, previewFrame = previewFrame, onTabSelected = onTabSelected, header = { TrainingTitle("Weekly Evaluation", back = true, onBack = onBack) }) {
        when {
            state.isLoading -> TrainingMessage("Loading your evaluations…", loading = true)
            state.error != null -> TrainingMessage(state.error, onRetry = onRetry)
            state.weeks.isEmpty() -> TrainingMessage("No training weeks yet. Your grades will appear here.")
            else -> {
                EvaluationProgress(state)
                state.weeks.forEach { week ->
                    EvaluationWeek(week, supervisorName = state.academicName)
                }
            }
        }
    }
}

@Composable
private fun EvaluationProgress(state: WeeklyEvaluationUiState) {
    TrainingCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TrainingText("YOUR PROGRESS", 12, TrainingMuted, FontWeight.SemiBold)
            TrainingText("${state.gradedCount} of ${state.totalWeeks} weeks graded", 12, TrainingNavy, FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        TrainingText("Weekly grades from ${state.academicName ?: "your academic supervisor"},\nout of 10.", 13, TrainingMuted)
        Spacer(Modifier.height(16.dp))
        if (state.bars.isEmpty()) {
            TrainingText("No grades yet.", 13, TrainingMuted)
        } else {
            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                state.bars.forEach { (week, grade) -> ProgressBar(grade, week) }
            }
        }
    }
}

@Composable
private fun ProgressBar(grade: Float, week: String) {
    // Bar height follows the grade: 10/10 = 50dp
    val barHeight = (grade / 10f * 50f).coerceAtLeast(4f)
    Column(Modifier.width(68.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        TrainingText(TrainingLogic.gradeText(grade), 12, TrainingNavy, FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Box(Modifier.fillMaxWidth().height(barHeight.dp).background(TrainingAqua, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 3.dp, bottomEnd = 3.dp)))
        Spacer(Modifier.height(3.dp))
        TrainingText(week, 12, TrainingMuted)
    }
}

/** Week card used by both the student and the academic evaluation screens. */
@Composable
internal fun EvaluationWeek(
    week: EvaluationWeekUi,
    supervisorName: String? = null,
    showComment: Boolean = true,
    extra: (@Composable ColumnScope.() -> Unit)? = null
) {
    TrainingCard {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                TrainingText("Week ${week.weekNo}", 17, weight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                TrainingText(week.dateRange, 13, TrainingMuted)
            }
            if (week.gradeText != null) {
                Row(verticalAlignment = Alignment.Bottom) {
                    TrainingText(week.gradeText, 25, TrainingNavy, FontWeight.Bold)
                    TrainingText(" / 10", 12, TrainingMuted, FontWeight.SemiBold, Modifier.padding(bottom = 3.dp))
                }
            } else if (week.status != null) {
                TrainingText(week.status, 13, TrainingMuted)
            }
        }
        HorizontalDivider(Modifier.padding(vertical = 10.dp), color = TrainingLine)
        if (week.tasks.isEmpty()) {
            TrainingText("No tasks recorded this week.", 13, TrainingMuted)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                week.tasks.forEach { task ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.TaskAlt, null, Modifier.size(21.dp), tint = TrainingTeal)
                        Spacer(Modifier.width(8.dp))
                        TrainingText(task, 14)
                    }
                }
            }
        }
        if (showComment && week.comment != null) {
            Spacer(Modifier.height(12.dp))
            EvaluationComment(week.comment, supervisorName)
        }
        extra?.invoke(this)
    }
}

@Composable
private fun EvaluationComment(comment: String, supervisorName: String?) {
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), color = Color(0xFFE2E3F0)) {
        Column(Modifier.padding(13.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.AccountBalance, null, Modifier.size(16.dp), tint = TrainingNavy)
                Spacer(Modifier.width(7.dp))
                TrainingText("${supervisorName ?: "Your supervisor"} · Academic Supervisor", 12, TrainingNavy, FontWeight.Bold)
            }
            Spacer(Modifier.height(5.dp))
            TrainingText(comment, 14, TrainingNavy)
        }
    }
}

// ---------- Previews (sample data only, no database) ----------

private val previewEvaluation = WeeklyEvaluationUiState(
    isLoading = false, academicName = "Dr. Huda", gradedCount = 2, totalWeeks = 12,
    bars = listOf("W1" to 8f, "W2" to 9f),
    weeks = listOf(
        EvaluationWeekUi(3, 3, "14–18 September", listOf("Fix the navigation bug", "Build the sign-up form"),
            null, null, null, "Week in progress", false),
        EvaluationWeekUi(2, 2, "7–11 September", listOf("Design the database", "Build the login API", "Connect login to the app"),
            9f, "9", "Strong week. Your work was clear and well organised.", null, true),
        EvaluationWeekUi(1, 1, "31 August–4 September", listOf("Set up the development environment", "Build the sign-up screen", "Write validation tests"),
            8f, "8", "Good start. Ask questions early when you’re stuck.", null, true)
    )
)

@Preview(name = "Weekly Evaluation", widthDp = 390, heightDp = 844)
@Composable
private fun WeeklyEvaluationPreview() = MaterialTheme { StudentEvaluationLayout(previewEvaluation, previewFrame = true) }

@Preview(name = "Weekly Evaluation · scrolled", widthDp = 390, heightDp = 844)
@Composable
private fun WeeklyEvaluationScrolledPreview() = MaterialTheme { StudentEvaluationLayout(previewEvaluation, scrolled = true, previewFrame = true) }