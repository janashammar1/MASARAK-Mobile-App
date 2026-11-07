
package sa.edu.iau.cshj.masarak.ui.screens

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
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sa.edu.iau.cshj.masarak.ui.components.AppBottomBar
import sa.edu.iau.cshj.masarak.ui.theme.DeepNavy
import sa.edu.iau.cshj.masarak.ui.theme.MASARAKTheme
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.RadioButtonUnchecked

private data class TaskItem(
    val title: String,
    val hours: String,
    val type: String,
    val date: String,
    val status: String
)

private val sampleTasks = listOf(
    TaskItem("Fix the navigation bug", "2h", "Individual", "11 Sep", "Returned for Updates"),
    TaskItem("Build the sign-up form", "5h", "Individual", "Today", "Waiting for Field Review"),
    TaskItem("Build the login API", "6h", "Individual", "10 Sep", "Waiting for Academic Review"),
    TaskItem("Connect login to the app", "4h", "Team", "9 Sep", "Partially Relevant"),
    TaskItem("Design the database", "8h", "Team", "8 Sep", "Relevant"),
    TaskItem("Organise the shared drive", "3h", "Individual", "7 Sep", "Not Relevant"),
    TaskItem("Update the team calendar", "2h", "Individual", "4 Sep", "Not Relevant"),
    TaskItem("Set up CI pipeline", "3h", "Team", "10 Sep", "Incomplete")
)

@Composable
fun TasksScreen(
    onTaskClick: (TaskDetails) -> Unit = {}
) {
    Scaffold(
        containerColor = Color(0xFFF7F5F3),

        bottomBar = {
            // No destination selected
            AppBottomBar(
                selectedItem = null,
                onItemSelected = {}
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(sampleTasks) { task ->
                    TaskCard(
                        task = task,
                        onClick = {
                            onTaskClick(task.toTaskDetails())
                        }
                    )
                }
            }
        }
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
                    text = "${task.hours} · ${task.type} · ${task.date}",
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
            Icons.Default.RadioButtonUnchecked
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

private fun TaskItem.toTaskDetails(): TaskDetails {
    val taskStatus = when (status) {
        "Returned for Updates" -> TaskStatus.RETURNED

        "Waiting for Field Review",
        "Waiting for Academic Review" -> TaskStatus.WAITING_REVIEW

        "Partially Relevant" -> TaskStatus.PARTIALLY_RELEVANT
        "Relevant" -> TaskStatus.RELEVANT
        "Not Relevant" -> TaskStatus.NOT_RELEVANT
        "Incomplete" -> TaskStatus.INCOMPLETE
        else -> TaskStatus.RELEVANT
    }

    return TaskDetails(
        title = title,
        date = date,
        hours = hours,
        type = type,
        description = "",
        status = taskStatus
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
        TasksScreen()
    }
}
