
package sa.edu.iau.cshj.masarak.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sa.edu.iau.cshj.masarak.ui.components.AppBottomBar
import sa.edu.iau.cshj.masarak.ui.components.BottomBarItem

private val ConversationNavy = Color(0xFF0C0D45)
private val ConversationTeal = Color(0xFF65D6D0)
private val ConversationBackground = Color(0xFFF7F6F5)

@Composable
fun ConversationScreen(
    onBack: () -> Unit = {},
    onSendMessage: (String) -> Unit = {},
    onAttachmentClick: () -> Unit = {},
    onBottomBarItemSelected: (BottomBarItem) -> Unit = {}
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
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = ConversationNavy
                )
            }

            Text(
                text = "Dr. Huda Alzahrani",
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ConversationNavy
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalance,
                    contentDescription = "Academic",
                    tint = ConversationNavy,
                    modifier = Modifier.size(14.dp)
                )

                Text(
                    text = "Academic",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = ConversationNavy
                )
            }
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
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

        // Message input - one outer box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 10.dp
                )
                .height(68.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(22.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Attachment
            IconButton(
                onClick = onAttachmentClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AttachFile,
                    contentDescription = "Attach file",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Message field
            BasicTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = ConversationNavy
                ),
                decorationBox = { innerTextField ->
                    Box(
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (message.isEmpty()) {
                            Text(
                                text = "Message",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                        innerTextField()
                    }
                }
            )

            // Send button
            Button(
                onClick = {
                    if (message.isNotBlank()) {
                        onSendMessage(message)
                        message = ""
                    }
                },
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(13.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE5E5E5)
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Send,
                    contentDescription = "Send message",
                    tint = ConversationNavy,
                    modifier = Modifier.size(21.dp)
                )
            }
        }

        // Shared bottom navigation
        AppBottomBar(
            selectedItem = BottomBarItem.Chat,
            onItemSelected = onBottomBarItemSelected
        )
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
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(
                    horizontal = 12.dp,
                    vertical = 10.dp
                )
        ) {
            Text(
                text = text,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = Color(0xFF252536)
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

@Preview(showBackground = true)
@Composable
fun ConversationScreenPreview() {
    ConversationScreen()
}
