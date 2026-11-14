
package sa.edu.iau.cshj.masarak.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import sa.edu.iau.cshj.masarak.ui.components.MarkIncompleteDialog
import sa.edu.iau.cshj.masarak.ui.theme.DeepNavy
import sa.edu.iau.cshj.masarak.ui.theme.PearlAqua
import sa.edu.iau.cshj.masarak.ui.theme.Teal
import sa.edu.iau.cshj.masarak.ui.theme.MASARAKTheme

@Composable
fun ReviewTaskScreen(
    taskTitle: String = "Build the profile screen",
    studentName: String = "Jana Alshahr",
    taskDate: String = "14 Sep",
    duration: String = "5h",
    taskType: String = "Individual",
    description: String = "Build the profile screen with the photo, name and edit button.",
    evidenceFileName: String = "profile.png",
    onApprove: (String) -> Unit = {},
    onReturn: (String) -> Unit = {},
    onIncomplete: (String) -> Unit = {},
    onBack: () -> Unit = {},
    onEvidenceClick: () -> Unit = {}
) {
    var feedback by remember {
        mutableStateOf("Clean layout. Please add the empty state next time.")
    }

    var showIncompleteDialog by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F6F5))
            .padding(horizontal = 16.dp)
    ) {

        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "‹",
                fontSize = 30.sp,
                color = DeepNavy,
                modifier = Modifier.clickable {
                    onBack()
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Review task",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DeepNavy
            )
        }

        // Task Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                Text(
                    text = taskTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepNavy
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(PearlAqua, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = studentName.firstOrNull()?.uppercase() ?: "?",
                            color = DeepNavy,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = studentName,
                        fontSize = 12.sp,
                        color = DeepNavy,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "▣ $taskDate     ◷ $duration     ♙ $taskType",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Description",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepNavy
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Evidence",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepNavy
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    modifier = Modifier.clickable {
                        onEvidenceClick()
                    },
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFEAF5F5)
                ) {
                    Text(
                        text = "▣  $evidenceFileName",
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        ),
                        fontSize = 11.sp,
                        color = Teal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Your Review
        Text(
            text = "YOUR REVIEW",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = DeepNavy
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = feedback,
            onValueChange = { feedback = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(105.dp),
            placeholder = {
                Text(
                    "Write your feedback...",
                    fontSize = 12.sp
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PearlAqua,
                unfocusedBorderColor = Color.LightGray,
                focusedTextColor = DeepNavy,
                unfocusedTextColor = DeepNavy,
                cursorColor = Teal
            ),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 12.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Jana sees this with your decision.",
            fontSize = 10.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.weight(1f))

        // Approve Button
        Button(
            onClick = {
                onApprove(feedback)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DeepNavy
            )
        ) {
            Text(
                text = "✓  Approve",
                color = Color.White,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Return and Incomplete Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    onReturn(feedback)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DeepNavy
                )
            ) {
                Text(
                    "Return",
                    fontSize = 12.sp
                )
            }

            OutlinedButton(
                onClick = {
                    showIncompleteDialog = true
                },
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFC62828)
                )
            ) {
                Text(
                    "Incomplete",
                    fontSize = 12.sp
                )
            }
        }
    }

    // Mark Incomplete Dialog
    if (showIncompleteDialog) {
        MarkIncompleteDialog(
            onDismiss = {
                showIncompleteDialog = false
            },
            onConfirm = {
                showIncompleteDialog = false
                onIncomplete(feedback)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewTaskScreenPreview() {
    MASARAKTheme {
        ReviewTaskScreen()
    }
}
