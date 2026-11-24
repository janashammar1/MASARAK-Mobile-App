package sa.edu.iau.cshj.masarak.ui.screens.training

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import sa.edu.iau.cshj.masarak.ui.components.BottomBarItem
import sa.edu.iau.cshj.masarak.ui.viewmodel.AcademicEvaluationUiState
import sa.edu.iau.cshj.masarak.ui.viewmodel.DEMO_ACADEMIC_EMAIL
import sa.edu.iau.cshj.masarak.ui.viewmodel.EvaluationWeekUi
import sa.edu.iau.cshj.masarak.ui.viewmodel.MasarakViewModelFactory
import sa.edu.iau.cshj.masarak.ui.viewmodel.TrainingViewModel

@Composable
fun AcademicWeeklyEvaluationScreen(
    recordId: Long,
    onBack: () -> Unit = {},
    onTabSelected: (BottomBarItem) -> Unit = {},
    academicEmail: String = DEMO_ACADEMIC_EMAIL // replace with the logged-in supervisor's email after login is connected
) {
    val trainingViewModel: TrainingViewModel = viewModel(factory = MasarakViewModelFactory())
    val state by trainingViewModel.academicEvaluation.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(recordId) {
        trainingViewModel.loadAcademicEvaluation(recordId)
    }

    AcademicEvaluationLayout(
        state = state,
        onBack = onBack,
        onTabSelected = onTabSelected,
        onRetry = { trainingViewModel.loadAcademicEvaluation(recordId) },
        onSave = { weekId, grade, comment, done ->
            scope.launch {
                done(trainingViewModel.saveEvaluation(weekId, grade, comment, academicEmail))
            }
        }
    )
}

@Composable
private fun AcademicEvaluationLayout(
    state: AcademicEvaluationUiState,
    previewFrame: Boolean = false,
    onBack: () -> Unit = {},
    onTabSelected: (BottomBarItem) -> Unit = {},
    onRetry: () -> Unit = {},
    onSave: (Long, String, String, (String?) -> Unit) -> Unit = { _, _, _, _ -> }
) {
    TrainingPage(
        BottomBarItem.MyTraining, academic = true, previewFrame = previewFrame, onTabSelected = onTabSelected,
        header = {
            Column {
                TrainingTitle(
                    state.studentName.ifBlank { "Weekly Evaluation" }, back = true,
                    subtitle = state.subtitle.ifBlank { null }, onBack = onBack
                )
                // Tasks and Attendance live on the person details screen
                AcademicEvaluationTabs(onOtherTab = onBack)
            }
        }
    ) {
        when {
            state.isLoading -> TrainingMessage("Loading weeks…", loading = true)
            state.error != null -> TrainingMessage(state.error, onRetry = onRetry)
            state.weeks.isEmpty() -> TrainingMessage("${state.studentName} hasn't submitted any training weeks yet.")
            else -> state.weeks.forEach { week ->
                AcademicWeekItem(week, onSave)
            }
        }
    }
}

@Composable
private fun AcademicEvaluationTabs(onOtherTab: () -> Unit = {}) {
    Row(Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 10.dp)) {
        listOf("Tasks", "Attendance", "Weekly\nEvaluation").forEachIndexed { index, label ->
            val selected = index == 2
            Box(
                Modifier.weight(1f).fillMaxHeight()
                    .then(if (selected) Modifier else Modifier.clickable(onClick = onOtherTab)),
                contentAlignment = Alignment.Center
            ) {
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

/** One week: shows the saved grade, or the form when it isn't graded yet (or while editing). */
@Composable
private fun AcademicWeekItem(
    week: EvaluationWeekUi,
    onSave: (Long, String, String, (String?) -> Unit) -> Unit
) {
    var editing by remember(week.weekId) { mutableStateOf(false) }
    val showForm = week.grade == null || editing

    EvaluationWeek(week, supervisorName = "You", showComment = !showForm) {
        if (!week.confirmed) {
            Spacer(Modifier.height(10.dp))
            TrainingText("Attendance not confirmed by the field supervisor yet.", 12, TrainingMuted)
        }
        if (showForm && week.weekId != null) {
            AcademicGradeForm(
                weekId = week.weekId,
                initialGrade = week.gradeText.orEmpty(),
                initialComment = week.comment.orEmpty(),
                isEdit = week.grade != null,
                onCancel = if (week.grade != null) ({ editing = false }) else null,
                onSave = { grade, comment, done ->
                    onSave(week.weekId, grade, comment) { result ->
                        if (result == null) editing = false
                        done(result)
                    }
                }
            )
        } else if (week.grade != null) {
            Spacer(Modifier.height(4.dp))
            TextButton(onClick = { editing = true }, contentPadding = PaddingValues(0.dp)) {
                Text("Edit evaluation", color = TrainingNavy, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun AcademicGradeForm(
    weekId: Long,
    initialGrade: String,
    initialComment: String,
    isEdit: Boolean,
    onCancel: (() -> Unit)?,
    onSave: (String, String, (String?) -> Unit) -> Unit
) {
    var grade by remember(weekId, initialGrade) { mutableStateOf(initialGrade) }
    var comment by remember(weekId, initialComment) { mutableStateOf(initialComment) }
    var error by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }

    HorizontalDivider(Modifier.padding(vertical = 6.dp), color = TrainingLine)
    TrainingText("YOUR WEEKLY EVALUATION", 12, TrainingMuted, FontWeight.SemiBold)
    Spacer(Modifier.height(6.dp))
    TrainingText("Grade", 13, weight = FontWeight.SemiBold)
    Spacer(Modifier.height(4.dp))
    TrainingInput(
        grade, { grade = it.filter { c -> c.isDigit() || c == '.' }.take(4); error = null },
        suffix = "/ 10", placeholder = "0–10", keyboardType = KeyboardType.Decimal,
        minHeight = 44, isError = error != null
    )
    Spacer(Modifier.height(6.dp))
    Text(
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append("Comment") }
            withStyle(SpanStyle(color = TrainingMuted)) { append(" (optional)") }
        },
        color = TrainingInk, fontSize = 13.sp
    )
    Spacer(Modifier.height(4.dp))
    TrainingInput(
        comment, { comment = it.take(500); error = null },
        placeholder = "What went well, and what to improve", multiline = true, minHeight = 62
    )
    if (error != null) {
        Spacer(Modifier.height(6.dp))
        TrainingText(error!!, 13, TrainingError, FontWeight.Medium)
    }
    Spacer(Modifier.height(8.dp))
    TrainingAction(
        label = if (isEdit) "Update evaluation" else "Save evaluation",
        loading = saving,
        onClick = {
            saving = true
            onSave(grade, comment) { result ->
                saving = false
                error = result
            }
        }
    )
    if (onCancel != null) {
        TextButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
            Text("Cancel", color = TrainingMuted, fontSize = 13.sp)
        }
    }
}

// ---------- Preview (sample data only, no database) ----------

private val previewAcademic = AcademicEvaluationUiState(
    isLoading = false, studentName = "Noura Almutairi", subtitle = "Nakheel Tech · Week 3 · 88 / 240 h",
    weeks = listOf(
        EvaluationWeekUi(3, 3, "14–18 September", listOf("Write unit tests for the cart"),
            null, null, null, "Not evaluated yet", true),
        EvaluationWeekUi(2, 2, "7–11 September", listOf("Review pull requests"),
            8f, "8", "Good start. Ask questions early when you’re stuck.", null, true)
    )
)

@Preview(name = "Academic Weekly Evaluation", widthDp = 390, heightDp = 844)
@Composable
private fun AcademicWeeklyEvaluationPreview() = MaterialTheme { AcademicEvaluationLayout(previewAcademic, previewFrame = true) }