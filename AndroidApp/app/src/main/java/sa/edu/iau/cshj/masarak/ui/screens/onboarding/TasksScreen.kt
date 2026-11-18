package sa.edu.iau.cshj.masarak.ui.screens.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.SignalCellularAlt

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sa.edu.iau.cshj.masarak.data.entity.TaskReviewEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskSubmissionEntity
import sa.edu.iau.cshj.masarak.ui.components.AppBottomBar
import sa.edu.iau.cshj.masarak.ui.components.BottomBarItem
import sa.edu.iau.cshj.masarak.ui.theme.DeepNavy
import sa.edu.iau.cshj.masarak.ui.theme.MASARAKTheme
import sa.edu.iau.cshj.masarak.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class TaskItem(
    val title: String,
    val hours: String,
    val type: String,
    val date: String,
    val status: String
)

private val demoTaskRows = listOf(
    sampleTask.copy(hours = "2 h") to "Returned for Updates",
    TaskDetails("Build the sign-up form", "Today", "5 h", "Individual", "Built the sign-up form.", TaskStatus.WAITING_REVIEW) to "Waiting for Field Review",
    TaskDetails("Build the login API", "10 Sep", "6 h", "Individual", "Built the login endpoint.", TaskStatus.WAITING_REVIEW) to "Waiting for Academic Review",
    TaskDetails("Connect login to the app", "9 Sep", "4 h", "Team", "Connected the login UI to the app.", TaskStatus.PARTIALLY_RELEVANT) to "Partially Relevant",
    TaskDetails("Design the database", "8 Sep", "8 h", "Team", "Designed the database schema.", TaskStatus.RELEVANT) to "Relevant",
    TaskDetails("Organise the shared drive", "7 Sep", "3 h", "Individual", "Organised the shared files.", TaskStatus.NOT_RELEVANT) to "Not Relevant",
    TaskDetails("Update the team calendar", "4 Sep", "2 h", "Individual", "Updated the team calendar.", TaskStatus.NOT_RELEVANT) to "Not Relevant",
    TaskDetails("Set up CI pipeline", "10 Sep", "3 h", "Team", "Started setting up the CI pipeline.", TaskStatus.INCOMPLETE) to "Incomplete"
)

@Composable
fun TasksScreen(
    taskViewModel: TaskViewModel? = null,
    recordId: Long? = null,
    onTaskClick: (TaskDetails) -> Unit = {},
    onAddTask: () -> Unit = {},
    onBackClick: () -> Unit = {},
    previewFrame: Boolean = false,
    previewScrolled: Boolean = false
) {
    val tasks by (
            taskViewModel?.tasks?.collectAsState()
                ?: remember { mutableStateOf(emptyList()) }
            )

    val submissions by (
            taskViewModel?.submissions?.collectAsState()
                ?: remember { mutableStateOf(emptyMap()) }
            )

    val reviews by (
            taskViewModel?.reviews?.collectAsState()
                ?: remember { mutableStateOf(emptyMap()) }
            )

    LaunchedEffect(taskViewModel, recordId) {
        if (taskViewModel != null && recordId != null) {
            taskViewModel.loadTasks(recordId)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(0f to Color(0xFFDCE7F5), .23f to Color(0xFFF7F5F3))
        ),
        bottomBar = {
            Column(Modifier.fillMaxWidth().background(DeepNavy)
                .then(if (previewFrame) Modifier else Modifier.navigationBarsPadding())) {
                AppBottomBar(
                    selectedItem = BottomBarItem.Home,
                    thirdTabLabel = "My Training",
                    chatUnreadCount = 2
                )
                if (previewFrame) {
                    Box(Modifier.fillMaxWidth().height(18.dp), contentAlignment = Alignment.Center) {
                        Box(Modifier.width(104.dp).height(3.dp)
                            .background(Color.White.copy(alpha = .45f), CircleShape))
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTask,
                modifier = Modifier.padding(bottom = 2.dp),
                containerColor = DeepNavy,
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp),
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 3.dp
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add task",
                        modifier = Modifier.size(19.dp)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        "Add task",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {

            if (previewFrame) {
                Box(Modifier.fillMaxWidth().height(32.dp), contentAlignment = Alignment.Center) {
                    Text("9:41", Modifier.align(Alignment.CenterStart).padding(start = 4.dp),
                        color = DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Box(Modifier.size(15.dp).background(DeepNavy, CircleShape))
                    Row(Modifier.align(Alignment.CenterEnd), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        listOf(Icons.Default.SignalCellularAlt, Icons.Default.Wifi, Icons.Default.BatteryFull).forEach {
                            Icon(it, null, Modifier.size(15.dp), tint = DeepNavy)
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 19.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = DeepNavy,
                    modifier = Modifier.size(21.dp).clickable(onClick = onBackClick)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Tasks",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepNavy
                )
            }

            when {
                taskViewModel == null || recordId == null -> {
                    val previousCardOffset = with(LocalDensity.current) { 94.dp.roundToPx() }
                    LazyColumn(
                        state = rememberLazyListState(
                            initialFirstVisibleItemIndex = if (previewScrolled) 2 else 0,
                            initialFirstVisibleItemScrollOffset = if (previewScrolled) previousCardOffset else 0
                        ),
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
                    ) {
                        items(demoTaskRows, key = { it.first.title }) { (task, statusLabel) ->
                            TaskCard(
                                task = TaskItem(task.title, task.hours, task.type, task.date, statusLabel),
                                onClick = { onTaskClick(task) }
                            )
                        }
                    }
                }

                tasks.isEmpty() -> {
                    EmptyTasksMessage(
                        message = "No tasks available yet"
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(
                            items = tasks,
                            key = { it.taskId }
                        ) { task ->

                            LaunchedEffect(task.taskId) {
                                taskViewModel.loadSubmissions(task.taskId)
                            }

                            val taskSubmissions =
                                submissions[task.taskId].orEmpty()

                            val latestSubmission =
                                taskSubmissions.maxByOrNull {
                                    it.attemptNo
                                }

                            if (latestSubmission != null) {

                                LaunchedEffect(
                                    latestSubmission.submissionId
                                ) {
                                    taskViewModel.loadReviews(
                                        latestSubmission.submissionId
                                    )
                                }

                                val taskReviews =
                                    reviews[latestSubmission.submissionId]
                                        .orEmpty()

                                val latestReview =
                                    taskReviews.maxByOrNull {
                                        it.reviewedAt
                                    }

                                val taskItem =
                                    latestSubmission.toTaskItem(latestReview)

                                TaskCard(
                                    task = taskItem,
                                    onClick = {
                                        onTaskClick(
                                            latestSubmission.toTaskDetails(
                                                taskReviews
                                            )
                                        )
                                    }
                                )

                            } else {
                                TaskCard(
                                    task = TaskItem(
                                        title = "Task #${task.taskId}",
                                        hours = "Not submitted",
                                        type = "Task",
                                        date = "",
                                        status = "Waiting for Field Review"
                                    ),
                                    onClick = {}
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyTasksMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Color.Gray,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TaskCard(
    task: TaskItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = if (task.status == "Incomplete") 99.dp else 105.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFFE0DEDA)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .align(Alignment.Top)
                    .background(
                        Color(0xFFE9E8F0),
                        RoundedCornerShape(9.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.TaskAlt,
                    contentDescription = null,
                    tint = DeepNavy,
                    modifier = Modifier.size(21.dp)
                )
            }

            Spacer(modifier = Modifier.width(11.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepNavy
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = listOf(
                        task.hours,
                        task.type,
                        task.date
                    ).filter { it.isNotBlank() }.joinToString(" · "),
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )

                Spacer(modifier = Modifier.height(5.dp))

                StatusChip(status = task.status)
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Task details",
                tint = DeepNavy,
                modifier = Modifier.size(19.dp)
            )
        }
    }
}

@Composable
private fun StatusChip(status: String) {

    val (backgroundColor, textColor, icon) = when (status) {

        "Returned for Updates" -> Triple(
            Color(0xFFFFD087),
            Color(0xFF382400),
            Icons.Default.Autorenew
        )

        "Waiting for Field Review",
        "Waiting for Academic Review" -> Triple(
            Color(0xFFE8E6DD),
            Color(0xFF33343B),
            Icons.Default.Schedule
        )

        "Partially Relevant" -> Triple(
            Color(0xFFFFD98A),
            Color(0xFF382400),
            Icons.Default.Contrast
        )

        "Relevant" -> Triple(
            Color(0xFFC7ECC5),
            Color(0xFF103C20),
            Icons.Default.Circle
        )

        "Not Relevant" -> Triple(
            Color(0xFFFFDAD6),
            Color(0xFF59191D),
            Icons.Outlined.RadioButtonUnchecked
        )

        "Incomplete" -> Triple(
            Color(0xFFFFDAD6),
            Color(0xFF59191D),
            Icons.Default.Cancel
        )

        else -> Triple(
            Color.LightGray,
            DeepNavy,
            Icons.Default.Schedule
        )
    }

    Row(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(13.dp)
        )

        Text(
            text = status,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

private fun TaskSubmissionEntity.toTaskItem(
    review: TaskReviewEntity?
): TaskItem {

    val status = when (
        review?.decision?.uppercase()
    ) {
        "RETURNED",
        "RETURNED_FOR_UPDATES" -> "Returned for Updates"

        "APPROVED",
        "RELEVANT" -> "Relevant"

        "PARTIALLY_RELEVANT" -> "Partially Relevant"

        "NOT_RELEVANT",
        "REJECTED" -> "Not Relevant"

        "INCOMPLETE" -> "Incomplete"

        else -> "Waiting for Field Review"
    }

    val hoursValue = durationMinutes / 60.0
    val hoursText = if (durationMinutes % 60 == 0) {
        "${durationMinutes / 60}h"
    } else {
        "${String.format(Locale.getDefault(), "%.1f", hoursValue)}h"
    }

    return TaskItem(
        title = title,
        hours = hoursText,
        type = taskType,
        date = SimpleDateFormat(
            "dd MMM",
            Locale.getDefault()
        ).format(Date(taskDate)),
        status = status
    )
}

private fun TaskSubmissionEntity.toTaskDetails(
    reviews: List<TaskReviewEntity>
): TaskDetails {

    val latestReview = reviews.maxByOrNull {
        it.reviewedAt
    }

    val status = when (
        latestReview?.decision?.uppercase()
    ) {
        "RETURNED",
        "RETURNED_FOR_UPDATES" -> TaskStatus.RETURNED

        "APPROVED",
        "RELEVANT" -> TaskStatus.RELEVANT

        "PARTIALLY_RELEVANT" -> TaskStatus.PARTIALLY_RELEVANT

        "NOT_RELEVANT",
        "REJECTED" -> TaskStatus.NOT_RELEVANT

        "INCOMPLETE" -> TaskStatus.INCOMPLETE

        else -> TaskStatus.WAITING_REVIEW
    }

    val hoursValue = durationMinutes / 60.0
    val hoursText = if (durationMinutes % 60 == 0) {
        "${durationMinutes / 60}h"
    } else {
        "${String.format(Locale.getDefault(), "%.1f", hoursValue)}h"
    }

    return TaskDetails(
        title = title,
        date = SimpleDateFormat(
            "dd MMM",
            Locale.getDefault()
        ).format(Date(taskDate)),
        hours = hoursText,
        type = taskType,
        description = description,
        status = status,
        reviewer = latestReview?.let {
            "Reviewer #${it.reviewerId}"
        } ?: "",
        reviewComment = latestReview?.comment.orEmpty(),
        challenges = challenges.orEmpty()
    )
}

@Preview(name = "Tasks · Start", widthDp = 390, heightDp = 870)
@Composable
private fun TasksScreenPreview() {
    MASARAKTheme { TasksScreen(previewFrame = true) }
}

@Preview(name = "Tasks · Scrolled", widthDp = 390, heightDp = 870)
@Composable
private fun TasksScrolledPreview() {
    MASARAKTheme { TasksScreen(previewFrame = true, previewScrolled = true) }
}
