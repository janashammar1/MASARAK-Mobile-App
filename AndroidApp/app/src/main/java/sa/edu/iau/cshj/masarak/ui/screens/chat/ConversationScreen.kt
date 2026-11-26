package sa.edu.iau.cshj.masarak.ui.screens.chat

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sa.edu.iau.cshj.masarak.ui.components.AppBottomBar
import sa.edu.iau.cshj.masarak.ui.components.BottomBarItem

private val ConversationNavy = Color(0xFF0C0D45)
private val ConversationBackground = Color(0xFFF7F6F5)
private val SentBubble = Color(0xFFE3E6F5)
private val FieldGold = Color(0xFFB78B32)
private val StudentBadge = Color(0xFFBFEDEA)
private val AcademicBadge = Color(0xFFE8E8F5)

data class ConversationMessage(
    val text: String = "",
    val time: String,
    val isSent: Boolean,
    val attachmentType: String? = null,
    val attachmentName: String? = null,
    val attachmentSize: String? = null,
    val attachmentRef: String? = null,
    val attachmentMime: String? = null
)

@Composable
fun ConversationScreen(
    onBack: () -> Unit = {},
    onSendMessage: (String) -> Unit = {},
    onAttachmentClick: () -> Unit = {},
    onBottomBarItemSelected: (BottomBarItem) -> Unit = {},
    contactName: String = "Dr. Huda Alzahrani",
    role: String = "Academic",
    conversationInfo: String? = "You both supervise Sara Alqahtani and Jana Alharbi.",
    messages: List<ConversationMessage> = academicMessages(),
    thirdTabLabel: String = "My Training",
    onSendPersisted: ((String, (Boolean) -> Unit) -> Unit)? = null
) {
    var message by remember(contactName) {
        mutableStateOf("")
    }

    var sending by remember {
        mutableStateOf(false)
    }

    val messageScroll = rememberScrollState()

    LaunchedEffect(messages.size) {
        messageScroll.animateScrollTo(messageScroll.maxValue)
    }

    val roleColor = when (role) {
        "Field" -> FieldGold
        "Student" -> Color(0xFF397F82)
        else -> ConversationNavy
    }

    val roleBackground = when (role) {
        "Field" -> Color(0xFFFFE9B8)
        "Student" -> StudentBadge
        else -> AcademicBadge
    }

    val roleIcon = when (role) {
        "Field" -> Icons.Outlined.WorkOutline
        "Student" -> Icons.Outlined.School
        else -> Icons.Outlined.AccountBalance
    }

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
            .statusBarsPadding()
            .imePadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 14.dp,
                    end = 14.dp,
                    top = 9.dp,
                    bottom = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = ConversationNavy,
                    modifier = Modifier.size(19.dp)
                )
            }

            Text(
                text = contactName,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 5.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ConversationNavy,
                maxLines = 1
            )

            Row(
                modifier = Modifier
                    .background(
                        roleBackground,
                        RoundedCornerShape(5.dp)
                    )
                    .padding(
                        horizontal = 6.dp,
                        vertical = 4.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = roleIcon,
                    contentDescription = null,
                    tint = roleColor,
                    modifier = Modifier.size(10.dp)
                )

                Text(
                    text = role,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = roleColor
                )
            }
        }

        if (!conversationInfo.isNullOrBlank()) {
            Text(
                text = conversationInfo,
                fontSize = 8.sp,
                lineHeight = 11.sp,
                color = Color(0xFF55545A),
                modifier = Modifier.padding(
                    start = 17.dp,
                    end = 17.dp,
                    top = 5.dp,
                    bottom = 7.dp
                )
            )
        }

        // Messages
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(messageScroll)
                .padding(
                    horizontal = 12.dp,
                    vertical = 3.dp
                ),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            messages.forEach { item ->
                MessageBubble(item)
            }
        }

        // Composer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 9.dp,
                    vertical = 7.dp
                )
                .height(43.dp)
                .background(
                    Color.White,
                    RoundedCornerShape(12.dp)
                )
                .border(
                    0.7.dp,
                    Color(0xFFE0E0E0),
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onAttachmentClick,
                modifier = Modifier.size(31.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AttachFile,
                    contentDescription = "Attach file",
                    tint = Color(0xFF55545A),
                    modifier = Modifier.size(17.dp)
                )
            }

            BasicTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 5.dp),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 10.sp,
                    color = ConversationNavy
                ),
                decorationBox = { innerTextField ->
                    Box(
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (message.isEmpty()) {
                            Text(
                                text = "Message",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }

                        innerTextField()
                    }
                }
            )

            Button(
                onClick = {
                    if (message.isNotBlank()) {
                        if (onSendPersisted != null) {
                            sending = true

                            onSendPersisted(message) { saved ->
                                sending = false

                                if (saved) {
                                    message = ""
                                }
                            }
                        } else {
                            onSendMessage(message)
                            message = ""
                        }
                    }
                },
                enabled = !sending && message.isNotBlank(),
                modifier = Modifier.size(29.dp),
                shape = RoundedCornerShape(7.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE5E5E5)
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Send,
                    contentDescription = "Send message",
                    tint = ConversationNavy,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ConversationNavy)
                .navigationBarsPadding()
        ) {
            AppBottomBar(
                selectedItem = BottomBarItem.Chat,
                thirdTabLabel = thirdTabLabel,
                onItemSelected = onBottomBarItemSelected
            )
        }
    }
}

@Composable
private fun MessageBubble(
    message: ConversationMessage
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isSent) {
            Alignment.End
        } else {
            Alignment.Start
        }
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 265.dp)
                .background(
                    color = if (message.isSent) {
                        SentBubble
                    } else {
                        Color.White
                    },
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomEnd = if (message.isSent) 4.dp else 12.dp,
                        bottomStart = if (message.isSent) 12.dp else 4.dp
                    )
                )
                .border(
                    width = if (message.isSent) 0.dp else 0.5.dp,
                    color = Color(0xFFE5E3E0),
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomEnd = if (message.isSent) 4.dp else 12.dp,
                        bottomStart = if (message.isSent) 12.dp else 4.dp
                    )
                )
                .clickable(
                    enabled = message.attachmentRef != null
                ) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW)
                            .setDataAndType(
                                Uri.parse(message.attachmentRef),
                                message.attachmentMime
                                    ?: "application/octet-stream"
                            )
                            .addFlags(
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )

                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "No app can open this attachment.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .padding(
                    horizontal = 9.dp,
                    vertical = 7.dp
                )
        ) {
            if (message.text.isNotBlank()) {
                Text(
                    text = message.text,
                    fontSize = 8.5.sp,
                    lineHeight = 11.sp,
                    color = Color(0xFF252536)
                )
            }

            when (message.attachmentType) {
                "image" -> {
                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(65.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFFD5F0EA),
                                        Color(0xFFF5E4B9)
                                    )
                                ),
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Image,
                            contentDescription = "Image attachment",
                            tint = Color(0xFF315B57),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "${message.attachmentName.orEmpty()} · ${message.time}",
                        fontSize = 7.sp,
                        color = Color(0xFF55545A)
                    )
                }

                "pdf", "file" -> {
                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color(0xFFF7F7F5),
                                RoundedCornerShape(5.dp)
                            )
                            .padding(
                                horizontal = 6.dp,
                                vertical = 4.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = "File attachment",
                            tint = Color(0xFF438D88),
                            modifier = Modifier.size(11.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(4.dp)
                        )

                        Text(
                            text = message.attachmentName.orEmpty(),
                            fontSize = 7.sp,
                            color = Color(0xFF33333A),
                            modifier = Modifier.weight(1f),
                            maxLines = 1
                        )

                        if (!message.attachmentSize.isNullOrBlank()) {
                            Text(
                                text = message.attachmentSize,
                                fontSize = 6.5.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = message.time,
                        fontSize = 7.sp,
                        color = Color(0xFF55545A)
                    )
                }

                else -> {
                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = message.time,
                        fontSize = 7.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

internal fun fieldMessages() = listOf(
    ConversationMessage(
        text = "Morning Sara, stand-up moved to 10:00.",
        time = "08:40",
        isSent = false
    ),
    ConversationMessage(
        text = "Thanks! Sharing the login screens here.",
        time = "Sent · 08:42",
        isSent = true
    ),
    ConversationMessage(
        time = "08:44",
        isSent = true,
        attachmentType = "image",
        attachmentName = "login-flow.png"
    ),
    ConversationMessage(
        time = "08:45",
        isSent = true,
        attachmentType = "pdf",
        attachmentName = "api-notes.pdf",
        attachmentSize = "240 KB"
    ),
    ConversationMessage(
        text = "Got them. Please submit the task too so I can review it.",
        time = "08:50",
        isSent = false
    )
)

internal fun studentMessages() = listOf(
    ConversationMessage(
        text = "Some recent tasks didn't match your major. Try asking for API or testing work.",
        time = "Sent · Sun",
        isSent = true
    ),
    ConversationMessage(
        text = "Thank you, I'll ask for API tasks.",
        time = "Sun",
        isSent = false
    ),
    ConversationMessage(
        text = "See you on Thursday.",
        time = "Sent · Sun",
        isSent = true
    )
)

internal fun academicMessages() = listOf(
    ConversationMessage(
        text = "Sara's last tasks were Not Relevant. Could she get more backend work?",
        time = "08:05",
        isSent = false
    ),
    ConversationMessage(
        text = "Yes - I'll give her the API tasks from next week.",
        time = "Sent · 08:20",
        isSent = true
    ),
    ConversationMessage(
        text = "Thank you. I'll look out for them.",
        time = "08:22",
        isSent = false
    )
)

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    name = "Student"
)
@Composable
fun ConversationScreenFieldPreview() {
    ConversationScreen(
        contactName = "Eng. Khalid Alotaibi",
        role = "Field",
        conversationInfo = null,
        messages = fieldMessages(),
        thirdTabLabel = "My Training"
    )
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    name = "Academic Supervisor"
)
@Composable
fun ConversationScreenStudentPreview() {
    ConversationScreen(
        contactName = "Sara Alqahtani",
        role = "Student",
        conversationInfo = null,
        messages = studentMessages(),
        thirdTabLabel = "Students"
    )
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    name = "Field Supervisor"
)
@Composable
fun ConversationScreenAcademicPreview() {
    ConversationScreen(
        contactName = "Dr. Huda Alzahrani",
        role = "Academic",
        conversationInfo = "You both supervise Sara Alqahtani and Jana Alharbi.",
        messages = academicMessages(),
        thirdTabLabel = "Trainees"
    )
}