
package sa.edu.iau.cshj.masarak.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

private val ChatNavy = Color(0xFF0C0D45)
private val ChatTeal = Color(0xFF00696E)
private val ChatBackground = Color(0xFFF7F6F5)

// Message data
data class ConversationMessage(
    val messageId: String,
    val text: String,
    val time: String,
    val isSent: Boolean,
    val attachmentName: String? = null
)

@Composable
fun ConversationScreen(
    conversationId: String = "",
    participantName: String = "Eng. Khalid Alotaibi",
    participantRole: String = "Field Supervisor",
    messages: List<ConversationMessage> = emptyList(),
    onBack: () -> Unit = {},
    onEdit: () -> Unit = {},
    onSendMessage: (String) -> Unit = {},
    onAttachmentClick: (String) -> Unit = {}
) {
    var message by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChatBackground)
    ) {

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text(
                    text = "‹",
                    fontSize = 28.sp,
                    color = ChatNavy
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = participantName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChatNavy
                )

                Text(
                    text = participantRole,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }

            TextButton(onClick = onEdit) {
                Text(
                    text = "Edit",
                    color = ChatTeal,
                    fontSize = 11.sp
                )
            }
        }

        // Messages
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            items(
                items = messages,
                key = { it.messageId }
            ) { item ->

                if (item.attachmentName != null) {
                    AttachmentBubble(
                        fileName = item.attachmentName,
                        time = item.time,
                        onClick = {
                            onAttachmentClick(item.messageId)
                        }
                    )
                } else {
                    MessageBubble(
                        text = item.text,
                        time = item.time,
                        isSent = item.isSent
                    )
                }
            }
        }

        // Message input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "♧",
                fontSize = 20.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                placeholder = {
                    Text(
                        "Message",
                        fontSize = 12.sp
                    )
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = ChatTeal
                )
            )

            Spacer(modifier = Modifier.width(6.dp))

            Button(
                onClick = {
                    val text = message.trim()

                    if (text.isNotEmpty()) {
                        onSendMessage(text)
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
                    text = "▶",
                    color = ChatNavy,
                    fontSize = 14.sp
                )
            }
        }
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
        horizontalAlignment = if (isSent) Alignment.End
        else Alignment.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 270.dp)
                .background(
                    color = if (isSent) Color(0xFFE5E9F7)
                    else Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(10.dp)
        ) {
            Text(
                text = text,
                fontSize = 11.sp,
                color = ChatNavy,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = time,
                fontSize = 9.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun AttachmentBubble(
    fileName: String,
    time: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 220.dp)
                .background(
                    Color(0xFFE5E9F7),
                    RoundedCornerShape(12.dp)
                )
                .clickable { onClick() }
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            Color(0xFFDDE8DF),
                            RoundedCornerShape(6.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "▣",
                        color = ChatTeal
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = fileName,
                    fontSize = 10.sp,
                    color = ChatNavy
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = time,
                fontSize = 9.sp,
                color = Color.Gray
            )
        }
    }
}

// Preview
@Preview(showBackground = true)
@Composable
fun ConversationScreenPreview() {
    ConversationScreen(
        messages = listOf(
            ConversationMessage(
                messageId = "1",
                text = "Morning Sara, stand-up moved to 10:00.",
                time = "08:40",
                isSent = false
            ),
            ConversationMessage(
                messageId = "2",
                text = "Thanks! Sharing the login screens here.",
                time = "Sent · 08:42",
                isSent = true
            ),
            ConversationMessage(
                messageId = "3",
                text = "",
                time = "08:44",
                isSent = true,
                attachmentName = "login-flow.png"
            ),
            ConversationMessage(
                messageId = "4",
                text = "",
                time = "08:45",
                isSent = true,
                attachmentName = "api-notes.pdf"
            ),
            ConversationMessage(
                messageId = "5",
                text = "Got them. Please submit the task too so I can review it.",
                time = "08:50",
                isSent = false
            )
        )
    )
}
