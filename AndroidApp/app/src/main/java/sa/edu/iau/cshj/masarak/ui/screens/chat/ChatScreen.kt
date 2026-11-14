
package sa.edu.iau.cshj.masarak.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF0C0D45)
private val Teal = Color(0xFF00696E)
private val ScreenBackground = Color(0xFFF7F6F5)

// Conversation data
data class ChatConversation(
    val conversationId: String,
    val initials: String,
    val name: String,
    val role: String,
    val lastMessage: String,
    val time: String,
    val unreadCount: Int = 0
)

@Composable
fun ChatScreen(
    conversations: List<ChatConversation> = emptyList(),
    onConversationClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {

        // Screen title
        Text(
            text = "Chat",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Navy,
            modifier = Modifier.padding(
                start = 18.dp,
                top = 20.dp,
                bottom = 18.dp
            )
        )

        if (conversations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No conversations yet",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        } else {
            Column {
                conversations.forEach { conversation ->
                    ConversationItem(
                        conversation = conversation,
                        onClick = {
                            onConversationClick(
                                conversation.conversationId
                            )
                        }
                    )
                }
            }
        }

        Text(
            text = "Chat is for quick questions. Task feedback and attendance stay in their own sections.",
            fontSize = 11.sp,
            color = Color.DarkGray,
            lineHeight = 16.sp,
            modifier = Modifier.padding(
                horizontal = 18.dp,
                vertical = 14.dp
            )
        )
    }
}

@Composable
private fun ConversationItem(
    conversation: ChatConversation,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 5.dp)
            .background(
                Color.White,
                RoundedCornerShape(12.dp)
            )
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Avatar
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    Color(0xFFFFE8C5),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = conversation.initials,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Navy
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Conversation details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = conversation.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Navy,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${conversation.role} · ${conversation.lastMessage}",
                fontSize = 10.sp,
                color = Color.DarkGray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 13.sp
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Time and unread count
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = conversation.time,
                fontSize = 9.sp,
                color = Color.Gray
            )

            if (conversation.unreadCount > 0) {
                Spacer(modifier = Modifier.height(5.dp))

                Box(
                    modifier = Modifier
                        .size(17.dp)
                        .background(
                            Color(0xFFC62828),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = conversation.unreadCount.toString(),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// Preview
@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen(
        conversations = listOf(
            ChatConversation(
                conversationId = "1",
                initials = "KA",
                name = "Eng. Khalid Alotaibi",
                role = "Field Supervisor",
                lastMessage = "Please submit the task so I can review it.",
                time = "9:05",
                unreadCount = 5
            ),
            ChatConversation(
                conversationId = "2",
                initials = "HA",
                name = "Dr. Huda Alzahrani",
                role = "Academic Supervisor",
                lastMessage = "Good morning. See you on Thursday.",
                time = "Sun"
            )
        )
    )
}
