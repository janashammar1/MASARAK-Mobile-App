
package sa.edu.iau.cshj.masarak.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

private val ConversationNavy = Color(0xFF0C0D45)
private val ConversationTeal = Color(0xFF65D6D0)
private val ConversationBackground = Color(0xFFF7F6F5)

@Composable
fun ConversationScreen(
    onBack: () -> Unit = {},
    onSendMessage: (String) -> Unit = {},
    onAttachmentClick: () -> Unit = {}
) {
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE2ECF9),
                        ConversationBackground,
                        ConversationBackground
                    )
                )
            )
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onBack,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "‹",
                    fontSize = 28.sp,
                    color = ConversationNavy
                )
            }

            Text(
                text = "Dr. Huda Alzahrani",
                modifier = Modifier.weight(1f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ConversationNavy
            )

            Text(
                text = "♜ Academic",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = ConversationNavy
            )
        }

        // Conversation information
        Text(
            text = "You both supervise Sara Alqahtani and Jana Alharbi.",
            fontSize = 10.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(
                start = 18.dp,
                end = 18.dp,
                top = 8.dp,
                bottom = 10.dp
            )
        )

        // Messages
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MessageBubble(
                text = "Sara’s last tasks were Not Relevant. Could she get more backend work?",
                time = "08:05",
                isSent = false
            )

            MessageBubble(
                text = "Yes — I’ll give her the API tasks from next week.",
                time = "Sent · 08:20",
                isSent = true
            )

            MessageBubble(
                text = "Thank you. I’ll look out for them.",
                time = "08:22",
                isSent = false
            )
        }

        // Message input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(
                    horizontal = 10.dp,
                    vertical = 7.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onAttachmentClick,
                modifier = Modifier.size(34.dp)
            ) {
                Text(
                    text = "♧",
                    fontSize = 19.sp,
                    color = Color.DarkGray
                )
            }

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                placeholder = {
                    Text(
                        text = "Message",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 11.sp,
                    color = ConversationNavy
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE5E5E5),
                    focusedBorderColor = ConversationTeal,
                    cursorColor = ConversationTeal
                )
            )

            Spacer(modifier = Modifier.width(5.dp))

            Button(
                onClick = {
                    if (message.isNotBlank()) {
                        onSendMessage(message)
                        message = ""
                    }
                },
                modifier = Modifier.size(38.dp),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE5E5E5)
                )
            ) {
                Text(
                    text = "▷",
                    fontSize = 17.sp,
                    color = ConversationNavy
                )
            }
        }

        // AppBottomBar(role) will be connected here
        // by the project navigation integration.
    }
}

@Composable
private fun MessageBubble(
    text: String,
    time: String,
    isSent: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isSent) {
            Alignment.End
        } else {
            Alignment.Start
        }
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 265.dp)
                .background(
                    color = if (isSent) {
                        Color(0xFFE3E6F5)
                    } else {
                        Color.White
                    },
                    shape = RoundedCornerShape(2.dp)
                )
                .padding(
                    horizontal = 11.dp,
                    vertical = 9.dp
                )
        ) {
            Text(
                text = text,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = Color(0xFF252536)
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = time,
                fontSize = 9.sp,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConversationScreenPreview() {
    ConversationScreen()
}
