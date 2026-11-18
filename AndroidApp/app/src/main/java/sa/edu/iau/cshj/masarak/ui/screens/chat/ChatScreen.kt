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
import androidx.compose.material.icons.outlined.WorkOutline
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
private val AcademicBackground = Color(0xFFF6F6F4)
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
    val isAcademic: Boolean = false,
    val isFieldSupervisor: Boolean = false
)

@Composable
fun ChatScreen(
    conversations: List<ChatConversation> = emptyList(),
    onConversationClick: (String) -> Unit = {},
    onBottomBarItemSelected: (BottomBarItem) -> Unit = {}
) {
    ChatScreen(
        conversations = conversations,
        onConversationClick = onConversationClick,
        thirdTabLabel = "My Training",
        onBottomBarItemSelected = onBottomBarItemSelected
    )
}

@Composable
fun ChatScreen(
    conversations: List<ChatConversation> = emptyList(),
    onConversationClick: (String) -> Unit = {},
    thirdTabLabel: String,
    usePlainBackground: Boolean = false,
    onBottomBarItemSelected: (BottomBarItem) -> Unit = {}
) {
    val background = if (usePlainBackground) {
        Modifier.background(AcademicBackground)
    } else {
        Modifier.background(
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFDCE9F7),
                    Color(0xFFF4F4F3),
                    ScreenBackground
                )
            )
        )
    }

    Column(modifier = Modifier.fillMaxSize().then(background).statusBarsPadding()) {
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

        Box(Modifier.fillMaxWidth().background(Navy).navigationBarsPadding()) {
            AppBottomBar(
                selectedItem = BottomBarItem.Chat,
                thirdTabLabel = thirdTabLabel,
                onItemSelected = onBottomBarItemSelected
            )
        }
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
            .defaultMinSize(minHeight = 48.dp)
            .padding(horizontal = 12.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(38.dp)) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        color = when {
                            conversation.isAcademic -> Color(0xFFE8E8F5)
                            conversation.isFieldSupervisor -> Color(0xFFFFF0CC)
                            else -> Color(0xFFD9F2EF)
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = conversation.initials,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        conversation.isAcademic -> AcademicInitialsColor
                        conversation.isFieldSupervisor -> Color(0xFF80621F)
                        else -> StudentInitialsColor
                    }
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
                        imageVector = when {
                            conversation.isAcademic -> Icons.Outlined.AccountBalance
                            conversation.isFieldSupervisor -> Icons.Outlined.WorkOutline
                            else -> Icons.Outlined.School
                        },
                        contentDescription = null,
                        tint = when {
                            conversation.isAcademic -> Navy
                            conversation.isFieldSupervisor -> Color(0xFF80621F)
                            else -> StudentTeal
                        },
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
                            color = when {
                                conversation.isAcademic -> Color(0xFF64648A)
                                conversation.isFieldSupervisor -> Color(0xFF9A7624)
                                else -> StudentTeal
                            },
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

@Preview(name = "Student - Chat", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun ChatScreenStudentPreview() {
    ChatScreen(
        thirdTabLabel = "My Training",
        conversations = listOf(
            ChatConversation(
                conversationId = "student-field-supervisor",
                initials = "KA",
                name = "Eng. Khalid Alotaibi",
                role = "Field Supervisor",
                lastMessage = "Got them. Please submit the task too so I can review it.",
                time = "08:50",
                unreadCount = 2,
                isFieldSupervisor = true
            ),
            ChatConversation(
                conversationId = "student-academic-supervisor",
                initials = "HZ",
                name = "Dr. Huda Alzahrani",
                role = "Academic Supervisor",
                lastMessage = "See you on Thursday.",
                time = "Sun",
                isAcademic = true
            )
        )
    )
}

@Preview(name = "Academic Supervisor - Chat", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun ChatScreenAcademicSupervisorPreview() {
    ChatScreen(
        thirdTabLabel = "Students",
        usePlainBackground = true,
        conversations = listOf(
            ChatConversation(
                conversationId = "academic-field-supervisor",
                initials = "KA",
                name = "Eng. Khalid Alotaibi",
                role = "Field Supervisor",
                lastMessage = "You: Thank you. I'll look out for them.",
                time = "08:22",
                isFieldSupervisor = true
            ),
            ChatConversation(
                conversationId = "academic-student-sara",
                initials = "SA",
                name = "Sara Alqahtani",
                role = "Student",
                lastMessage = "You: See you on Thursday.",
                time = "Sun"
            ),
            ChatConversation(
                conversationId = "academic-student-jana",
                initials = "JA",
                name = "Jana Alharbi",
                role = "Student",
                lastMessage = "Week 3 went well so far.",
                time = "Sat"
            )
        )
    )
}

@Preview(name = "Field Supervisor - Chat", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun ChatScreenFieldSupervisorPreview() {
    ChatScreen(
        thirdTabLabel = "Trainees",
        conversations = listOf(
            ChatConversation(
                conversationId = "field-student-sara",
                initials = "SA",
                name = "Sara Alqahtani",
                role = "Student",
                lastMessage = "You: Got them. Please submit the task too so I can review it.",
                time = "08:50"
            ),
            ChatConversation(
                conversationId = "field-academic-supervisor",
                initials = "HZ",
                name = "Dr. Huda Alzahrani",
                role = "Academic Supervisor",
                lastMessage = "Thank you. I'll look out for them.",
                time = "08:22",
                unreadCount = 1,
                isAcademic = true
            ),
            ChatConversation(
                conversationId = "field-student-jana",
                initials = "JA",
                name = "Jana Alharbi",
                role = "Student",
                lastMessage = "I'll send the form today.",
                time = "Sun"
            )
        )
    )
}

internal fun demoConversations(role: String): List<ChatConversation> = when (role) {
    "student" -> listOf(
        ChatConversation(
            conversationId = "student-field-supervisor",
            initials = "KA",
            name = "Eng. Khalid Alotaibi",
            role = "Field Supervisor",
            lastMessage = "Got them. Please submit the task too so I can review it.",
            time = "08:50",
            unreadCount = 2,
            isFieldSupervisor = true
        ),
        ChatConversation(
            conversationId = "student-academic-supervisor",
            initials = "HZ",
            name = "Dr. Huda Alzahrani",
            role = "Academic Supervisor",
            lastMessage = "See you on Thursday.",
            time = "Sun",
            isAcademic = true
        )
    )
    "academic" -> listOf(
        ChatConversation(
            conversationId = "academic-field-supervisor",
            initials = "KA",
            name = "Eng. Khalid Alotaibi",
            role = "Field Supervisor",
            lastMessage = "You: Thank you. I'll look out for them.",
            time = "08:22",
            isFieldSupervisor = true
        ),
        ChatConversation(
            conversationId = "academic-student-sara",
            initials = "SA",
            name = "Sara Alqahtani",
            role = "Student",
            lastMessage = "You: See you on Thursday.",
            time = "Sun"
        ),
        ChatConversation(
            conversationId = "academic-student-jana",
            initials = "JA",
            name = "Jana Alharbi",
            role = "Student",
            lastMessage = "Week 3 went well so far.",
            time = "Sat"
        )
    )
    "field" -> listOf(
        ChatConversation(
            conversationId = "field-student-sara",
            initials = "SA",
            name = "Sara Alqahtani",
            role = "Student",
            lastMessage = "You: Got them. Please submit the task too so I can review it.",
            time = "08:50"
        ),
        ChatConversation(
            conversationId = "field-academic-supervisor",
            initials = "HZ",
            name = "Dr. Huda Alzahrani",
            role = "Academic Supervisor",
            lastMessage = "Thank you. I'll look out for them.",
            time = "08:22",
            unreadCount = 1,
            isAcademic = true
        ),
        ChatConversation(
            conversationId = "field-student-jana",
            initials = "JA",
            name = "Jana Alharbi",
            role = "Student",
            lastMessage = "I'll send the form today.",
            time = "Sun"
        )
    )
    else -> emptyList()
}
