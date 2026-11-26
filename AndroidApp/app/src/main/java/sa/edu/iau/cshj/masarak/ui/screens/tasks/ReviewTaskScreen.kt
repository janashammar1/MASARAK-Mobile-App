package sa.edu.iau.cshj.masarak.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sa.edu.iau.cshj.masarak.data.entity.TaskSubmissionEntity
import sa.edu.iau.cshj.masarak.ui.components.AppBottomBar
import sa.edu.iau.cshj.masarak.ui.components.BottomBarItem
import sa.edu.iau.cshj.masarak.ui.components.MarkIncompleteDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ReviewNavy = Color(0xFF0C0D45)
private val ReviewTeal = Color(0xFF00696E)
private val ReviewAqua = Color(0xFFBFEDEA)
private val ReviewBackground = Color(0xFFF7F6F5)
private val ReviewRed = Color(0xFFC62828)

@Composable
fun ReviewTaskScreen(
    onApprove: () -> Unit = {},
    onReturn: () -> Unit = {},
    onIncomplete: () -> Unit = {},
    onBack: () -> Unit = {},
    onBottomBarItemSelected: (BottomBarItem) -> Unit = {}
) {
    ReviewTaskScreen(
        onApprove = onApprove,
        onReturn = onReturn,
        onIncomplete = onIncomplete,
        onBack = onBack,
        chatUnreadCount = 0,
        onBottomBarItemSelected = onBottomBarItemSelected
    )
}

@Composable
fun ReviewTaskScreen(
    onApprove: () -> Unit = {},
    onReturn: () -> Unit = {},
    onIncomplete: () -> Unit = {},
    onBack: () -> Unit = {},
    chatUnreadCount: Int,
    submission: TaskSubmissionEntity? = null,
    studentName: String = "Jana Alharbi",
    evidenceName: String = "profile.png",
    initialFeedback: String =
        "Clean layout. Please add the empty state next time.",
    saving: Boolean = false,
    errorMessage: String? = null,
    onSaveReview: ((String, String) -> Unit)? = null,
    onBottomBarItemSelected: (BottomBarItem) -> Unit = {}
) {
    var feedback by remember(
        submission?.submissionId,
        initialFeedback
    ) {
        mutableStateOf(initialFeedback)
    }

    var showIncompleteDialog by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE2ECF9),
                        ReviewBackground,
                        ReviewBackground
                    )
                )
            )
            .statusBarsPadding()
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 20.dp,
                    bottom = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = ReviewNavy,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Review task",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ReviewNavy
            )
        }

        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Text(
                        text = submission?.title
                            ?: "Build the profile screen",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF202020)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    ReviewAqua,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = studentName.trim()
                                    .split(Regex("\\s+"))
                                    .take(2)
                                    .mapNotNull { it.firstOrNull() }
                                    .joinToString(""),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ReviewNavy
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = studentName,
                            fontSize = 14.sp,
                            color = Color(0xFF333333)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement =
                            Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement =
                                Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarToday,
                                contentDescription = "Date",
                                tint = Color(0xFF555555),
                                modifier = Modifier.size(17.dp)
                            )

                            Text(
                                text = submission?.let {
                                    SimpleDateFormat(
                                        "dd MMM",
                                        Locale.ENGLISH
                                    ).format(Date(it.taskDate))
                                } ?: "14 Sep",
                                fontSize = 14.sp,
                                color = Color(0xFF333333)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement =
                                Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccessTime,
                                contentDescription = "Duration",
                                tint = Color(0xFF555555),
                                modifier = Modifier.size(17.dp)
                            )

                            Text(
                                text = submission?.let {
                                    "${it.durationMinutes / 60f} h"
                                } ?: "5h",
                                fontSize = 14.sp,
                                color = Color(0xFF333333)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement =
                                Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PersonOutline,
                                contentDescription = "Task type",
                                tint = Color(0xFF555555),
                                modifier = Modifier.size(17.dp)
                            )

                            Text(
                                text = submission?.taskType
                                    ?: "Individual",
                                fontSize = 14.sp,
                                color = Color(0xFF333333)
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = Color(0xFFE2E2E2)
                    )

                    Text(
                        text = "Description",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = submission?.description
                            ?: "Built the profile screen with the photo, name and edit button.",
                        fontSize = 16.sp,
                        color = Color(0xFF252536),
                        lineHeight = 22.sp
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = Color(0xFFE2E2E2)
                    )

                    Text(
                        text = "Evidence",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF3F3F3)
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Image,
                                contentDescription = "Attachment",
                                tint = ReviewTeal,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = evidenceName.ifBlank {
                                    "No attachment"
                                },
                                fontSize = 14.sp,
                                color = Color(0xFF333333)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Your review
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                HorizontalDivider(
                    color = Color(0xFFE2E2E2)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "YOUR REVIEW",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Feedback",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = ReviewNavy
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = feedback,
                    onValueChange = { feedback = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(92.dp),
                    shape = RoundedCornerShape(14.dp),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        color = Color(0xFF555577),
                        lineHeight = 20.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFBDBDBD),
                        unfocusedBorderColor = Color(0xFFBDBDBD),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = ReviewTeal
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "$studentName sees this with your decision.",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = ReviewRed,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        // Fixed decision buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    top = 10.dp,
                    bottom = 16.dp
                )
        ) {
            Button(
                onClick = {
                    if (onSaveReview != null) {
                        onSaveReview("Approved", feedback)
                    } else {
                        onApprove()
                    }
                },
                enabled = !saving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ReviewNavy
                )
            ) {
                Text(
                    text = "✓  Approve",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(9.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (onSaveReview != null) {
                            onSaveReview("Returned", feedback)
                        } else {
                            onReturn()
                        }
                    },
                    enabled = !saving,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ReviewNavy
                    )
                ) {
                    Text(
                        text = "Return",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = {
                        showIncompleteDialog = true
                    },
                    enabled = !saving,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ReviewRed
                    )
                ) {
                    Text(
                        text = "Incomplete",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Shared bottom navigation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ReviewNavy)
                .navigationBarsPadding()
        ) {
            AppBottomBar(
                selectedItem = BottomBarItem.MyTraining,
                thirdTabLabel = "Trainees",
                chatUnreadCount = chatUnreadCount,
                onItemSelected = onBottomBarItemSelected
            )
        }
    }

    if (showIncompleteDialog) {
        MarkIncompleteDialog(
            onDismiss = {
                showIncompleteDialog = false
            },
            onConfirm = {
                showIncompleteDialog = false

                if (onSaveReview != null) {
                    onSaveReview("Incomplete", feedback)
                } else {
                    onIncomplete()
                }
            }
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ReviewTaskScreenPreview() {
    ReviewTaskScreen(chatUnreadCount = 1)
}