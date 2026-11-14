
package sa.edu.iau.cshj.masarak.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF0C0D45)
private val Teal = Color(0xFF65D6D0)
private val ScreenBackground = Color(0xFFF7F6F5)

// Conversation data
data class ChatConversation(
    val conversationId: String,
    val initials: String,
    val name: String,
    val role: String,
    val lastMessage: String,
    val time: String,
    val unreadCount: Int = 0,
    val isAcademic: Boolean = false
)

@Composable
fun ChatScreen(
    conversations: List<ChatConversation> = emptyList(),
    onConversationClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE2ECF9),
                        ScreenBackground,
                        ScreenBackground
                    )
                )
            )
    ) {
        // Screen title
        Text(
            text = "Chat",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Navy,
            modifier = Modifier.padding(
                start = 18.dp,
                top = 20.dp,
                bottom = 22.dp
            )
        )

        // Conversations card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFFE2E2E2),
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    Color.White,
                    RoundedCornerShape(16.dp)
                )
        ) {
            conversations.forEachIndexed { index, conversation ->

                ConversationItem(
                    conversation = conversation,
                    onClick = {
                        onConversationClick(conversation.conversationId)
                    }
                )

                if (index < conversations.lastIndex) {
                    HorizontalDivider(
                        color = Color(0xFFE5E5E5),
                        thickness = 1.dp
                    )
                }
            }
        }

        // Information text
        Text(
            text = "Chat is for quick questions. Task feedback and attendance stay in their own sections.",
            fontSize = 10.sp,
            color = Color.DarkGray,
            lineHeight = 14.sp,
            modifier = Modifier.padding(
                horizontal = 18.dp,
                vertical = 12.dp
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        // AppBottomBar(role) will be connected here
        // by the project navigation integration.
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
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with role indicator
        Box(
            modifier = Modifier.size(42.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = if (conversation.isAcademic)
                            Color(0xFFE8E8F5)
                        else
                            Color(0xFFDDF4F1),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = conversation.initials,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (conversation.isAcademic)
                        Color(0xFF55557A)
                    else
                        Teal
                )
            }

            Box(
                modifier = Modifier
                    .size(15.dp)
                    .align(Alignment.BottomEnd)
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (conversation.isAcademic) "♜" else "♙",
                    fontSize = 8.sp,
                    color = Navy
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Name, role and last message
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = conversation.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF20202A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row {
                Text(
                    text = "${conversation.role} · ",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Teal,
                    maxLines = 1
                )

                Text(
                    text = conversation.lastMessage,
                    fontSize = 10.sp,
                    color = Color.DarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Time, unread count and arrow
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = conversation.time,
                fontSize = 9.sp,
                color = Color.Gray
            )

            if (conversation.unreadCount > 0) {
                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .size(16.dp)
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

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "›",
            fontSize = 23.sp,
            color = Color.DarkGray
        )
    }
}

// Preview with Figma sample data
@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen(
        conversations = listOf(
            ChatConversation(
                conversationId = "1",
                initials = "SA",
                name = "Sara Alqahtani",
                role = "Student",
                lastMessage = "You: Got them. Please submit the task too so I can...",
                time = "08:50"
            ),
            ChatConversation(
                conversationId = "2",
                initials = "HZ",
                name = "Dr. Huda Alzahrani",
                role = "Academic Supervisor",
                lastMessage = "Thank you. I'll look out for them.",
                time = "08:22",
                unreadCount = 1,
                isAcademic = true
            ),
            ChatConversation(
                conversationId = "3",
                initials = "JA",
                name = "Jana Alharbi",
                role = "Student",
                lastMessage = "I'll send the form today.",
                time = "Sun"
            )
        )
    )
}
