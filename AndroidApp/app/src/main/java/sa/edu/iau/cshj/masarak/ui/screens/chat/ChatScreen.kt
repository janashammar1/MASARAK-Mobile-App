package sa.edu.iau.cshj.masarak.ui.screens.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sa.edu.iau.cshj.masarak.ui.components.AppBottomBar
import sa.edu.iau.cshj.masarak.ui.components.BottomBarItem

private val Navy = Color(0xFF0C0D45)
private val StudentTeal = Color(0xFF398D90)
private val ScreenBackground = Color(0xFFF8F7F4)
private val StudentInitialsColor = Color(0xFF15565A)
private val AcademicInitialsColor = Color(0xFF303052)

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
    onConversationClick: (String) -> Unit = {},
    onBottomBarItemSelected: (BottomBarItem) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFDCE9F7),
                        Color(0xFFF4F4F3),
                        ScreenBackground
                    )
                )
            )
    ) {
        Text(
            text = "Chat",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Navy,
            modifier = Modifier.padding(start = 18.dp, top = 20.dp, bottom = 28.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .border(
                    BorderStroke(1.dp, Color(0xFFE1E1E1)),
                    RoundedCornerShape(16.dp)
                )
                .background(Color.White, RoundedCornerShape(16.dp))
        ) {
            conversations.forEachIndexed { index, conversation ->
                ConversationItem(conversation) {
                    onConversationClick(conversation.conversationId)
                }

                if (index < conversations.lastIndex) {
                    HorizontalDivider(
                        color = Color(0xFFE5E5E5),
                        thickness = 1.dp
                    )
                }
            }
        }

        Text(
            text = "Chat is for quick questions. Task feedback and attendance stay in their own sections.",
            fontSize = 10.sp,
            color = Color(0xFF55545A),
            lineHeight = 14.sp,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        AppBottomBar(
            selectedItem = BottomBarItem.Chat,
            onItemSelected = onBottomBarItemSelected
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
            .clickable(onClick = onClick)
            .defaultMinSize(minHeight = 68.dp)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(38.dp)) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        color = if (conversation.isAcademic) Color(0xFFE8E8F5)
                        else Color(0xFFD9F2EF),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = conversation.initials,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (conversation.isAcademic) AcademicInitialsColor
                    else StudentInitialsColor
                )
            }

            Surface(
                modifier = Modifier
                    .size(15.dp)
                    .align(Alignment.BottomEnd),
                shape = CircleShape,
                color = Color.White,
                border = BorderStroke(0.5.dp, Color(0xFFE7E7E7))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (conversation.isAcademic) {
                            Icons.Outlined.AccountBalance
                        } else {
                            Icons.Outlined.School
                        },
                        contentDescription = null,
                        tint = if (conversation.isAcademic) Navy else StudentTeal,
                        modifier = Modifier.size(9.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(9.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
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

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = if (conversation.isAcademic) Color(0xFF64648A)
                            else StudentTeal,
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append("${conversation.role} · ")
                    }
                    withStyle(SpanStyle(color = Color(0xFF5E5D63))) {
                        append(conversation.lastMessage)
                    }
                },
                fontSize = 9.5.sp,
                lineHeight = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(5.dp))

        Column(
            modifier = Modifier.widthIn(min = 27.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = conversation.time,
                fontSize = 9.sp,
                color = Color.Gray,
                maxLines = 1
            )

            if (conversation.unreadCount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(Color(0xFFC62828), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = conversation.unreadCount.toString(),
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = "Open conversation",
            tint = Color(0xFF4E4E55),
            modifier = Modifier.size(19.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun ChatScreenPreview() {
    ChatScreen(
        conversations = listOf(
            ChatConversation(
                "1", "SA", "Sara Alqahtani", "Student",
                "You: Got them. Please submit the task too so I can...", "08:50"
            ),
            ChatConversation(
                "2", "HZ", "Dr. Huda Alzahrani", "Academic Supervisor",
                "Thank you. I'll look out for them.", "08:22", 1, true
            ),
            ChatConversation(
                "3", "JA", "Jana Alharbi", "Student",
                "I'll send the form today.", "Sun"
            )
        )
    )
}
