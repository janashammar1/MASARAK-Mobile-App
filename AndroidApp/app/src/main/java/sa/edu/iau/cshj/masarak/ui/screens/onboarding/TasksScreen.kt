
package sa.edu.iau.cshj.masarak.ui.screens.onboarding

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
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sa.edu.iau.cshj.masarak.data.entity.TaskReviewEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskSubmissionEntity
import sa.edu.iau.cshj.masarak.ui.components.AppBottomBar
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

@Composable
fun TasksScreen(
    taskViewModel: TaskViewModel? = null,
    recordId: Long? = null,
    onTaskClick: (TaskDetails) -> Unit = {},
    onAddTask: () -> Unit = {}
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
        containerColor = Color(0xFFF7F5F3),
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = DeepNavy,
                    modifier = Modifier.size(21.dp)
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
                    EmptyTasksMessage(
                        message = "Tasks are not connected yet"
                    )
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
            .clickable { onClick() },
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFEF8)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(29.dp)
                    .background(
                        Color(0xFFE9E8F0),
                        RoundedCornerShape(9.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircleOutline,
                    contentDescription = null,
                    tint = DeepNavy,
                    modifier = Modifier.size(17.dp)
                )
            }

            Spacer(modifier = Modifier.width(9.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    fontSize = 12.sp,
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
                    fontSize = 9.sp,
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
            Color(0xFF785A00),
            Icons.Default.Autorenew
        )

        "Waiting for Field Review",
        "Waiting for Academic Review" -> Triple(
            Color(0xFFE8E6DD),
            Color(0xFF55534B),
            Icons.Default.Schedule
        )

        "Partially Relevant" -> Triple(
            Color(0xFFFFD98A),
            Color(0xFF785A00),
            Icons.Default.Contrast
        )

        "Relevant" -> Triple(
            Color(0xFFC7ECC5),
            Color(0xFF245B35),
            Icons.Default.Circle
        )

        "Not Relevant" -> Triple(
            Color(0xFFFFDAD6),
            Color(0xFF9B3434),
            Icons.Default.Circle
        )

        "Incomplete" -> Triple(
            Color(0xFFF1D5D5),
            Color(0xFF8B3030),
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
            .background(backgroundColor, CircleShape)
            .padding(horizontal = 7.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(10.dp)
        )

        Text(
            text = status,
            fontSize = 8.sp,
            fontWeight = FontWeight.SemiBold,
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

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Tasks Screen"
)
@Composable
private fun TasksScreenPreview() {
    MASARAKTheme {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                TasksScreen()
            }

            AppBottomBar(
                selectedItem = null,
                onItemSelected = {}
            )
        }
    }
}
