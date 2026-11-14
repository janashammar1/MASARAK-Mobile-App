
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sa.edu.iau.cshj.masarak.ui.components.MarkIncompleteDialog

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
    onBack: () -> Unit = {}
) {
    var feedback by remember {
        mutableStateOf(
            "Clean layout. Please add the empty state next time."
        )
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
    ) {

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "‹",
                fontSize = 28.sp,
                color = ReviewNavy,
                modifier = Modifier.clickable {
                    onBack()
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Review task",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = ReviewNavy
            )
        }

        // Task details card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
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
                    text = "Build the profile screen",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ReviewNavy
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(
                                ReviewAqua,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "JA",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ReviewNavy
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Jana Alharbi",
                        fontSize = 11.sp,
                        color = ReviewNavy
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "▣  14 Sep",
                        fontSize = 11.sp,
                        color = ReviewNavy
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "◷  5h",
                        fontSize = 11.sp,
                        color = ReviewNavy
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "♙  Individual",
                        fontSize = 11.sp,
                        color = ReviewNavy
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = Color(0xFFE2E2E2)
                )

                Text(
                    text = "Description",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Built the profile screen with the photo, name and edit button.",
                    fontSize = 12.sp,
                    color = ReviewNavy,
                    lineHeight = 17.sp
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = Color(0xFFE2E2E2)
                )

                Text(
                    text = "Evidence",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF3F3F3)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "▣",
                            color = ReviewTeal,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.width(7.dp))

                        Text(
                            text = "profile.png",
                            fontSize = 11.sp,
                            color = ReviewNavy
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
                .padding(horizontal = 18.dp)
        ) {
            HorizontalDivider(
                color = Color(0xFFE2E2E2)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "YOUR REVIEW",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Feedback",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ReviewNavy
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = feedback,
                onValueChange = { feedback = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 12.sp,
                    color = ReviewNavy,
                    lineHeight = 17.sp
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = ReviewTeal
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Jana sees this with your decision.",
                fontSize = 10.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Decision buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(
                    horizontal = 16.dp,
                    vertical = 10.dp
                )
        ) {
            Button(
                onClick = onApprove,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ReviewNavy
                )
            ) {
                Text(
                    text = "✓  Approve",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onReturn,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ReviewNavy
                    )
                ) {
                    Text(
                        text = "Return",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = {
                        showIncompleteDialog = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ReviewRed
                    )
                ) {
                    Text(
                        text = "Incomplete",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // AppBottomBar(role) will be connected here
        // by the project navigation integration.
    }

    if (showIncompleteDialog) {
        MarkIncompleteDialog(
            onDismiss = {
                showIncompleteDialog = false
            },
            onConfirm = {
                showIncompleteDialog = false
                onIncomplete()
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewTaskScreenPreview() {
    ReviewTaskScreen()
}
