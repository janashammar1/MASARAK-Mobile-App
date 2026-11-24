package sa.edu.iau.cshj.masarak.ui.screens.people

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import sa.edu.iau.cshj.masarak.ui.viewmodel.TrainingViewModel
import sa.edu.iau.cshj.masarak.ui.viewmodel.TaskViewModel
import sa.edu.iau.cshj.masarak.ui.viewmodel.MasarakViewModelFactory
import sa.edu.iau.cshj.masarak.data.entity.TrainingWeekEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskSubmissionEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskReviewEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PersonDetailScreen() =
    PersonDetailScreen(isAcademic = true)

@Composable
fun PersonDetailScreen(
    isAcademic: Boolean,
    attendance: Boolean = false,
    personName: String? = null,
    recordId: Long = 0L,
    currentUserId: Long? = null,
    onBackClick: () -> Unit = {},
    onReviewClick: ((Long) -> Unit)? = null,
    onEvaluation: (() -> Unit)? = null,
    onFieldReview: ((Long) -> Unit)? = null,
    onReportClick: ((String) -> Unit)? = null
) {
    val preview = LocalInspectionMode.current
    val scope = rememberCoroutineScope()

    val trainingViewModel: TrainingViewModel? =
        if (preview) null
        else viewModel(factory = MasarakViewModelFactory())

    val taskViewModel: TaskViewModel? =
        if (preview) null
        else viewModel(factory = MasarakViewModelFactory())

    val trainingWeeks by (
            trainingViewModel?.trainingWeeks
                ?: remember {
                    MutableStateFlow(emptyList<TrainingWeekEntity>())
                }
            ).collectAsState()

    val tasks by (
            taskViewModel?.tasks
                ?: remember {
                    MutableStateFlow(emptyList<TaskEntity>())
                }
            ).collectAsState()

    val submissions by (
            taskViewModel?.submissions
                ?: remember {
                    MutableStateFlow(emptyMap<Long, List<TaskSubmissionEntity>>())
                }
            ).collectAsState()

    val reviews by (
            taskViewModel?.reviews
                ?: remember {
                    MutableStateFlow(emptyMap<Long, List<TaskReviewEntity>>())
                }
            ).collectAsState()

    var loadingRecord by remember { mutableStateOf(false) }
    var loadError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(recordId) {
        if (!preview) {
            if (recordId <= 0L) {
                loadError = "Invalid training record."
            } else {
                loadingRecord = true
                loadError = null

                try {
                    trainingViewModel?.loadTrainingWeeks(recordId)
                    taskViewModel?.loadTasks(recordId)
                } catch (exception: Exception) {
                    loadError = exception.message
                        ?: "Unable to load training details."
                }

                loadingRecord = false
            }
        }
    }

    LaunchedEffect(tasks) {
        if (!preview) {
            tasks.forEach { task ->
                taskViewModel?.loadSubmissions(task.taskId)
            }
        }
    }

    LaunchedEffect(submissions) {
        if (!preview) {
            submissions.values.flatten().forEach { submission ->
                taskViewModel?.loadReviews(submission.submissionId)
            }
        }
    }

    PersonDetailLayout(
        academic = isAcademic,
        attendance = attendance,
        preview = preview,
        personName = personName,
        recordId = recordId,
        currentUserId = currentUserId,
        trainingWeeks = trainingWeeks,
        tasks = tasks,
        submissions = submissions,
        reviews = reviews,
        isLoading = loadingRecord,
        error = loadError,
        onConfirmAttendance = { week ->
            if (
                !isAcademic &&
                currentUserId != null &&
                currentUserId > 0L
            ) {
                scope.launch {
                    trainingViewModel?.confirmAttendance(
                        week,
                        currentUserId
                    )
                }
            }
        },
        onBackClick = onBackClick,
        onReviewClick = onReviewClick,
        onEvaluation = onEvaluation,
        onFieldReview = onFieldReview,
        onReportClick = onReportClick
    )
}

private data class PersonTask(
    val title: String,
    val date: String,
    val status: String,
    val submissionId: Long
)

@Composable
private fun PersonDetailLayout(
    academic: Boolean,
    attendance: Boolean,
    preview: Boolean,
    personName: String? = null,
    recordId: Long = 0L,
    currentUserId: Long? = null,
    trainingWeeks: List<TrainingWeekEntity> = emptyList(),
    tasks: List<TaskEntity> = emptyList(),
    submissions: Map<Long, List<TaskSubmissionEntity>> = emptyMap(),
    reviews: Map<Long, List<TaskReviewEntity>> = emptyMap(),
    isLoading: Boolean = false,
    error: String? = null,
    onConfirmAttendance: (TrainingWeekEntity) -> Unit = {},
    onBackClick: () -> Unit = {},
    onReviewClick: ((Long) -> Unit)? = null,
    onEvaluation: (() -> Unit)? = null,
    onFieldReview: ((Long) -> Unit)? = null,
    onReportClick: ((String) -> Unit)? = null
) {
    var showAttendance by rememberSaveable(attendance) {
        mutableStateOf(attendance)
    }

    val displayedTasks = if (preview) {
        emptyList()
    } else {
        tasks.mapNotNull { task ->

            val latestSubmission = submissions[task.taskId]
                .orEmpty()
                .maxWithOrNull(
                    compareBy<TaskSubmissionEntity> { it.attemptNo }
                        .thenBy { it.submittedAt }
                        .thenBy { it.submissionId }
                )

            latestSubmission?.let { submission ->

                val latestReview = reviews[submission.submissionId]
                    .orEmpty()
                    .maxByOrNull { it.reviewedAt }

                val formattedDate = SimpleDateFormat(
                    "dd MMM yyyy",
                    Locale.getDefault()
                ).format(Date(submission.taskDate))

                PersonTask(
                    title = submission.title,
                    date = "${submission.durationMinutes} min · ${submission.taskType} · $formattedDate",
                    status = latestReview?.decision ?: "Needs Review",
                    submissionId = submission.submissionId
                )
            }
        }
    }

    val totalMinutes = trainingWeeks.sumOf { it.durationMinutes }
    val totalHours = totalMinutes / 60
    val latestWeek = trainingWeeks.maxOfOrNull { it.weekNo }

    val subtitle = if (preview) {
        ""
    } else {
        buildString {
            if (latestWeek != null) {
                append("Week $latestWeek")
            }
            if (totalHours > 0) {
                if (isNotEmpty()) append(" · ")
                append("$totalHours h")
            }
        }
    }

    PeoplePage(
        academic,
        preview,
        header = {
            PeopleTitle(
                personName ?: "",
                subtitle.ifBlank { null },
                onBackClick
            )

            PersonTabs(
                academic = academic,
                attendance = showAttendance,
                taskCount = displayedTasks.size,
                attendanceCount = trainingWeeks.size,
                onEvaluation = onEvaluation
            ) {
                showAttendance = it
            }
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(19.dp, 11.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            when {
                preview -> {
                    PeopleText(
                        "No data available in preview.",
                        14,
                        PeopleMuted
                    )
                }

                recordId <= 0L -> {
                    PeopleText(
                        "Invalid training record.",
                        14,
                        PeopleMuted
                    )
                }

                isLoading -> {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PeopleTeal)
                    }
                }

                error != null -> {
                    PeopleText(
                        error,
                        14,
                        Color(0xFFB3261E)
                    )
                }

                showAttendance -> {
                    if (trainingWeeks.isEmpty()) {
                        PeopleText(
                            "No attendance records available.",
                            14,
                            PeopleMuted
                        )
                    } else {
                        trainingWeeks
                            .sortedByDescending { it.weekNo }
                            .forEach { week ->
                                AttendanceWeek(
                                    week = week,
                                    academic = academic,
                                    onConfirm = {
                                        onConfirmAttendance(week)
                                    },
                                    onReportClick = onReportClick
                                )
                            }
                    }
                }

                displayedTasks.isEmpty() -> {
                    PeopleText(
                        "No submitted tasks available.",
                        14,
                        PeopleMuted
                    )
                }

                else -> {
                    displayedTasks.forEach { task ->

                        val taskClick: (() -> Unit)? =
                            if (academic) {
                                {
                                    onReviewClick?.invoke(
                                        task.submissionId
                                    )
                                }
                            } else {
                                {
                                    onFieldReview?.invoke(
                                        task.submissionId
                                    )
                                }
                            }

                        PersonTaskCard(
                            task = task,
                            onClick = taskClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonTabs(
    academic: Boolean,
    attendance: Boolean,
    taskCount: Int,
    attendanceCount: Int,
    onEvaluation: (() -> Unit)?,
    onTab: (Boolean) -> Unit
) {
    val labels = if (academic) {
        listOf("Tasks", "Attendance", "Weekly\nEvaluation")
    } else {
        listOf("Tasks", "Attendance")
    }

    Row(
        Modifier
            .fillMaxWidth()
            .height(49.dp)
            .padding(horizontal = 19.dp)
    ) {
        labels.forEachIndexed { index, label ->

            val selected = index == if (attendance) 1 else 0

            val count = when (index) {
                0 -> taskCount.toString()
                1 -> attendanceCount.toString()
                else -> null
            }

            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .then(
                        if (index < 2) {
                            Modifier.clickable {
                                onTab(index == 1)
                            }
                        } else if (onEvaluation != null) {
                            Modifier.clickable(onClick = onEvaluation)
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        label,
                        color = if (selected) PeopleNavy else PeopleMuted,
                        fontSize = 14.sp,
                        lineHeight = 16.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = if (selected) {
                            androidx.compose.ui.text.font.FontWeight.Bold
                        } else {
                            androidx.compose.ui.text.font.FontWeight.Medium
                        }
                    )

                    if (count != null) {
                        Box(
                            Modifier
                                .background(PeopleMint, CircleShape)
                                .padding(horizontal = 7.dp)
                        ) {
                            PeopleText(
                                count,
                                10,
                                PeopleTeal,
                                bold = true
                            )
                        }
                    }
                }

                if (selected) {
                    Box(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(.9f)
                            .height(3.dp)
                            .background(
                                PeopleNavy,
                                RoundedCornerShape(
                                    topStart = 3.dp,
                                    topEnd = 3.dp
                                )
                            )
                    )
                }
            }
        }
    }

    HorizontalDivider(color = PeopleLine)
}

@Composable
private fun PersonTaskCard(
    task: PersonTask,
    onClick: (() -> Unit)? = null
) {
    PeopleCard {
        Row(
            Modifier
                .fillMaxWidth()
                .then(
                    if (onClick != null) {
                        Modifier.clickable(onClick = onClick)
                    } else {
                        Modifier
                    }
                )
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            PeopleTile(Icons.Outlined.TaskAlt)

            Spacer(Modifier.width(12.dp))

            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PeopleText(task.title, 15, bold = true)
                PeopleText(task.date, 12, PeopleMuted)

                val background = when (task.status) {
                    "Relevant" -> Color(0xFFCAEDC8)
                    "Not Relevant" -> Color(0xFFFFDAD7)
                    "Partially Relevant",
                    "Returned for Updates" -> Color(0xFFFFCD82)
                    else -> Color(0xFFE4E2DB)
                }

                val icon = when (task.status) {
                    "Relevant",
                    "Partially Relevant" -> Icons.Outlined.Circle

                    "Not Relevant" ->
                        Icons.Outlined.RadioButtonUnchecked

                    "Returned for Updates" ->
                        Icons.Outlined.AssignmentReturn

                    else -> Icons.Outlined.Schedule
                }

                PersonChip(
                    task.status,
                    icon,
                    background
                )
            }

            Icon(
                Icons.Outlined.ChevronRight,
                null,
                Modifier
                    .align(Alignment.CenterVertically)
                    .size(19.dp),
                tint = PeopleInk
            )
        }
    }
}

@Composable
private fun AttendanceWeek(
    week: TrainingWeekEntity,
    academic: Boolean,
    onConfirm: () -> Unit,
    onReportClick: ((String) -> Unit)?
) {
    val minutes = week.durationMinutes
    val hours = minutes / 60.0
    val confirmed = week.confirmedBy != null

    PeopleCard {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PeopleTile(
                Icons.Outlined.EventAvailable,
                PeopleMint,
                PeopleTeal
            )

            Spacer(Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PeopleText(
                    "Week ${week.weekNo}",
                    16,
                    bold = true
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    AttendanceMetric(
                        Icons.Outlined.HowToReg,
                        "${week.daysAttended} days"
                    )

                    AttendanceMetric(
                        Icons.Outlined.Schedule,
                        if (minutes % 60 == 0) {
                            "${minutes / 60} h"
                        } else {
                            String.format(
                                Locale.getDefault(),
                                "%.1f h",
                                hours
                            )
                        }
                    )
                }

                if (week.reportFileName.isNotBlank()) {
                    Row(
                        Modifier
                            .clickable {
                                onReportClick?.invoke(week.reportFileName)
                            }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PersonChip(
                            week.reportFileName,
                            Icons.Outlined.Description,
                            Color(0xFFF0EFEB),
                            iconColor = PeopleTeal
                        )
                    }
                }

                if (confirmed) {
                    PersonChip(
                        "Confirmed by Field Supervisor",
                        Icons.Outlined.Verified,
                        Color(0xFFCAEDC8)
                    )
                } else if (!academic) {
                    Row(
                        Modifier
                            .background(
                                PeopleNavy,
                                RoundedCornerShape(13.dp)
                            )
                            .clickable(onClick = onConfirm)
                            .padding(15.dp, 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Check,
                            null,
                            Modifier.size(15.dp),
                            tint = Color.White
                        )

                        PeopleText(
                            "Confirm",
                            13,
                            Color.White,
                            bold = true
                        )
                    }
                } else {
                    PersonChip(
                        "Waiting for field supervisor confirmation",
                        Icons.Outlined.Schedule,
                        Color(0xFFF0EFEB)
                    )
                }
            }
        }
    }
}

@Composable
private fun AttendanceMetric(
    icon: ImageVector,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon,
            null,
            Modifier.size(15.dp),
            tint = PeopleTeal
        )

        PeopleText(value, 12)
    }
}

@Composable
internal fun PersonChip(
    value: String,
    icon: ImageVector,
    bg: Color,
    iconColor: Color = PeopleInk
) {
    Row(
        Modifier
            .background(
                bg,
                RoundedCornerShape(7.dp)
            )
            .padding(8.dp, 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            icon,
            null,
            Modifier.size(13.dp),
            tint = iconColor
        )

        PeopleText(value, 11, bold = true)
    }
}

@Preview(name = "Academic · Tasks", widthDp = 390, heightDp = 844)
@Composable
private fun AcademicTasksPreview() =
    MaterialTheme {
        PersonDetailLayout(
            academic = true,
            attendance = false,
            preview = true
        )
    }

@Preview(name = "Academic · Attendance", widthDp = 390, heightDp = 844)
@Composable
private fun AcademicAttendancePreview() =
    MaterialTheme {
        PersonDetailLayout(
            academic = true,
            attendance = true,
            preview = true
        )
    }

@Preview(name = "Field · Tasks", widthDp = 390, heightDp = 844)
@Composable
private fun FieldTasksPreview() =
    MaterialTheme {
        PersonDetailLayout(
            academic = false,
            attendance = false,
            preview = true
        )
    }

@Preview(name = "Field · Attendance", widthDp = 390, heightDp = 844)
@Composable
private fun FieldAttendancePreview() =
    MaterialTheme {
        PersonDetailLayout(
            academic = false,
            attendance = true,
            preview = true
        )
    }