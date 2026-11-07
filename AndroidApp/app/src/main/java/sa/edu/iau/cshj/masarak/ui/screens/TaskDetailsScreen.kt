
package sa.edu.iau.cshj.masarak.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sa.edu.iau.cshj.masarak.ui.components.AppBottomBar
import sa.edu.iau.cshj.masarak.ui.theme.DeepNavy
import sa.edu.iau.cshj.masarak.ui.theme.MASARAKTheme
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Contrast

enum class TaskStatus {
    RETURNED,
    WAITING_REVIEW,
    PARTIALLY_RELEVANT,
    NOT_RELEVANT,
    INCOMPLETE,
    RELEVANT
}

data class TaskDetails(
    val title: String,
    val date: String,
    val hours: String,
    val type: String,
    val description: String,
    val status: TaskStatus,
    val reviewer: String = "",
    val reviewComment: String = "",
    val supervisorComment: String = "",
    val supervisorTime: String = "",
    val challenges: String = "",
    val evidence: List<String> = emptyList()
)

private val sampleTask = TaskDetails(
    title = "Fix the navigation bug",
    date = "11 Sep",
    hours = "2h",
    type = "Individual",
    description = "Fixed the back button closing the app on the profile screen.",
    status = TaskStatus.RETURNED,
    reviewer = "Eng. Khalid",
    reviewComment = "Please add screenshots of the bug and of the fix. Update the details and resubmit."
)

@Composable
fun TaskDetailsScreen(
    task: TaskDetails = sampleTask,
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = Color(0xFFF7F7F3),
        bottomBar = {
            AppBottomBar(
                selectedItem = null,
                onItemSelected = {}
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DeepNavy,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onBackClick() }
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = "Task details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepNavy
                    )
                }
            }

            item {
                TaskInformationCard(task)
            }

            item {
                Text(
                    text = "REVIEW",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepNavy,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            item {
                ReviewSection(
                    task = task,
                    onEditClick = onEditClick
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun TaskInformationCard(task: TaskDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFEF8)
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = task.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DeepNavy
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MetadataItem(Icons.Default.CalendarToday, task.date)
                MetadataItem(Icons.Default.AccessTime, task.hours)
                MetadataItem(Icons.Default.PersonOutline, task.type)
            }

            Spacer(modifier = Modifier.height(10.dp))
            DividerLine()
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Description",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepNavy
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = task.description,
                fontSize = 12.sp,
                color = Color(0xFF242424),
                lineHeight = 17.sp
            )

            if (task.challenges.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                DividerLine()
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Challenges",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepNavy
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = task.challenges,
                    fontSize = 13.sp,
                    color = Color(0xFF333333)
                )
            }

            if (task.evidence.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                DividerLine()
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Evidence",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepNavy
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    task.evidence.forEach { file ->

                        val fileIcon = when {
                            file.endsWith(".png", ignoreCase = true) ||
                                    file.endsWith(".jpg", ignoreCase = true) ||
                                    file.endsWith(".jpeg", ignoreCase = true) ->
                                Icons.Default.Image

                            file.endsWith(".pdf", ignoreCase = true) ->
                                Icons.Default.PictureAsPdf

                            else -> Icons.Default.AttachFile
                        }

                        Row(
                            modifier = Modifier
                                .background(
                                    Color(0xFFF0EFE7),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(
                                    horizontal = 9.dp,
                                    vertical = 7.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = fileIcon,
                                contentDescription = null,
                                tint = DeepNavy,
                                modifier = Modifier.size(14.dp)
                            )

                            Text(
                                text = file,
                                fontSize = 10.sp,
                                color = DeepNavy,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetadataItem(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF555555),
            modifier = Modifier.size(13.dp)
        )

        Text(
            text = text,
            fontSize = 11.sp,
            color = Color(0xFF242424)
        )
    }
}

@Composable
private fun DividerLine() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFE6E3D9))
    )
}

@Composable
private fun ReviewSection(
    task: TaskDetails,
    onEditClick: () -> Unit
) {
    when (task.status) {

        TaskStatus.RETURNED -> {
            ReviewCard(
                color = Color(0xFFFFD98A),
                icon = Icons.Default.Assignment,
                title = "Returned for Updates · ${task.reviewer}",
                message = task.reviewComment
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onEditClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFBDE8E5),
                    contentColor = DeepNavy
                )
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    "Edit and resubmit",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        TaskStatus.WAITING_REVIEW -> {
            ReviewCard(
                color = Color(0xFFE5E3EF),
                icon = Icons.Default.Schedule,
                title = "Waiting for Academic Review",
                message = "Eng. Khalid approved it. Dr. Huda will review it next."
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (task.supervisorComment.isNotBlank()) {
                ReviewCard(
                    color = Color(0xFFFFE4A6),
                    icon = Icons.Default.Badge,
                    title = "Eng. Khalid · Field Supervisor",
                    message = task.supervisorComment,
                    time = task.supervisorTime
                )
            }
        }

        TaskStatus.PARTIALLY_RELEVANT -> {
            ReviewCard(
                color = Color(0xFFFFD98A),
                icon = Icons.Default.Contrast,
                title = "Partially Relevant · Dr. Huda",
                message = task.reviewComment
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (task.supervisorComment.isNotBlank()) {
                ReviewCard(
                    color = Color(0xFFFFE4A6),
                    icon = Icons.Default.Badge,
                    title = "Eng. Khalid · Field Supervisor",
                    message = task.supervisorComment,
                    time = task.supervisorTime
                )
            }
        }

        TaskStatus.NOT_RELEVANT -> {
            ReviewCard(
                color = Color(0xFFFFDAD6),
                icon = Icons.Default.Cancel,
                title = "Not Relevant · Dr. Huda",
                message = task.reviewComment
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (task.supervisorComment.isNotBlank()) {
                ReviewCard(
                    color = Color(0xFFFFE4A6),
                    icon = Icons.Default.Badge,
                    title = "Eng. Khalid · Field Supervisor",
                    message = task.supervisorComment,
                    time = task.supervisorTime
                )
            }
        }

        TaskStatus.INCOMPLETE -> {
            ReviewCard(
                color = Color(0xFFFFDAD6),
                icon = Icons.Default.Cancel,
                title = "Incomplete · Eng. Khalid",
                message = task.reviewComment
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "This task won't count this time. Use the feedback for your next one.",
                fontSize = 12.sp,
                color = Color(0xFF444444),
                lineHeight = 16.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        TaskStatus.RELEVANT -> {
            ReviewCard(
                color = Color(0xFFC7ECC5),
                icon = Icons.Default.CheckCircle,
                title = "Relevant",
                message = ""
            )
        }
    }
}

@Composable
private fun ReviewCard(
    color: Color,
    icon: ImageVector,
    title: String,
    message: String,
    time: String = ""
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = DeepNavy,
                    modifier = Modifier.size(15.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DeepNavy,
                    modifier = Modifier.weight(1f)
                )

                if (time.isNotBlank()) {
                    Text(
                        text = time,
                        fontSize = 13.sp,
                        color = DeepNavy
                    )
                }
            }

            if (message.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = message,
                    fontSize = 12.sp,
                    color = Color(0xFF333333),
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(start = 23.dp)
                )
            }
        }
    }
}

// Preview data for all task statuses

private val waitingTask = sampleTask.copy(
    title = "Build the login API",
    date = "10 Sep",
    hours = "6h",
    description = "Built the login endpoint with token sign-in, then tested it with the mobile app.",
    status = TaskStatus.WAITING_REVIEW,
    challenges = "Handling expired tokens.",
    evidence = listOf("login-flow.png", "api-notes.pdf"),
    supervisorComment = "Clean structure and clear validation. Add screenshots of the login flow next time.",
    supervisorTime = "09:30"
)

private val partiallyRelevantTask = sampleTask.copy(
    title = "Connect login to the app",
    date = "9 Sep",
    hours = "4h",
    type = "Team",
    description = "Connected the login screen to the new API with the team and handled loading states.",
    status = TaskStatus.PARTIALLY_RELEVANT,
    reviewer = "Dr. Huda",
    reviewComment = "Good work. Ask for tasks with more backend logic so they match your major.",
    supervisorComment = "Good teamwork and tidy code.",
    supervisorTime = "Thu"
)

private val notRelevantTask = sampleTask.copy(
    title = "Organise the shared drive",
    date = "7 Sep",
    hours = "3h",
    description = "Sorted the team files into folders and renamed them.",
    status = TaskStatus.NOT_RELEVANT,
    reviewer = "Dr. Huda",
    reviewComment = "Try asking for tasks with coding or testing, so they fit your major.",
    supervisorComment = "Thanks for sorting the files quickly.",
    supervisorTime = "Tue"
)

private val incompleteTask = sampleTask.copy(
    title = "Set up CI pipeline",
    date = "10 Sep",
    hours = "3h",
    type = "Team",
    description = "Started the build pipeline for the app with the team.",
    status = TaskStatus.INCOMPLETE,
    reviewer = "Eng. Khalid",
    reviewComment = "The pipeline isn't finished yet, so this task won't count this time."
)

private val relevantTask = sampleTask.copy(
    title = "Design the database",
    date = "8 Sep",
    hours = "8h",
    type = "Team",
    description = "Designed the database structure for the application.",
    status = TaskStatus.RELEVANT
)

@Preview(showBackground = true, name = "1 - Returned for Updates")
@Composable
private fun ReturnedPreview() {
    MASARAKTheme {
        TaskDetailsScreen(task = sampleTask)
    }
}

@Preview(showBackground = true, name = "2 - Waiting for Review")
@Composable
private fun WaitingPreview() {
    MASARAKTheme {
        TaskDetailsScreen(task = waitingTask)
    }
}

@Preview(showBackground = true, name = "3 - Partially Relevant")
@Composable
private fun PartiallyRelevantPreview() {
    MASARAKTheme {
        TaskDetailsScreen(task = partiallyRelevantTask)
    }
}

@Preview(showBackground = true, name = "4 - Not Relevant")
@Composable
private fun NotRelevantPreview() {
    MASARAKTheme {
        TaskDetailsScreen(task = notRelevantTask)
    }
}

@Preview(showBackground = true, name = "5 - Incomplete")
@Composable
private fun IncompletePreview() {
    MASARAKTheme {
        TaskDetailsScreen(task = incompleteTask)
    }
}

@Preview(showBackground = true, name = "6 - Relevant")
@Composable
private fun RelevantPreview() {
    MASARAKTheme {
        TaskDetailsScreen(task = relevantTask)
    }
}
