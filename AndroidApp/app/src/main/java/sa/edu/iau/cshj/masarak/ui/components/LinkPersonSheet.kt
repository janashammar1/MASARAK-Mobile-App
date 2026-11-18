package sa.edu.iau.cshj.masarak.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sa.edu.iau.cshj.masarak.ui.screens.people.*

@Composable
fun LinkPersonSheet() = LinkPersonSheet(isAcademic = true)

@Composable
fun LinkPersonSheet(isAcademic: Boolean, onDismiss: () -> Unit = {}) {
    LinkPersonLayout(isAcademic, LocalInspectionMode.current, onDismiss)
}

@Composable
private fun LinkPersonLayout(academic: Boolean, preview: Boolean, onDismiss: () -> Unit = {}) {
    Box(Modifier.fillMaxSize()) {
        PeopleListLayout(academic, preview)
        Box(Modifier.matchParentSize().background(PeopleNavy.copy(alpha = .32f))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDismiss))
        Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth()
            .background(Color.White, RoundedCornerShape(topStart = 23.dp, topEnd = 23.dp))) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 22.dp)) {
                Box(Modifier.align(Alignment.CenterHorizontally).padding(top = 12.dp, bottom = 15.dp)
                    .width(34.dp).height(3.dp).background(Color(0xFFD5D4CE), CircleShape))
                PeopleText(if (academic) "Link a student" else "Link a trainee", 17, bold = true)
                Spacer(Modifier.height(8.dp))
                PeopleText("They’ll get your request in their Profile. Once they accept, you’ll see their training record.",
                    13, PeopleMuted)
                Spacer(Modifier.height(15.dp))
                PeopleText("University email", 12, bold = true)
                Spacer(Modifier.height(6.dp))
                PeopleInput(Icons.Outlined.MailOutline, "name@uni.edu.sa")
                Spacer(Modifier.height(15.dp))
                Row(Modifier.fillMaxWidth().height(44.dp)
                    .background(Color(0xFFE9E7E2), RoundedCornerShape(13.dp)),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Icon(Icons.Outlined.Send, null, Modifier.size(14.dp), tint = PeopleMuted)
                    Spacer(Modifier.width(7.dp))
                    PeopleText("Send request", 13, PeopleMuted, bold = true)
                }
                Spacer(Modifier.height(10.dp))
            }
            if (preview) PeopleGestureBar(dark = false) else Spacer(Modifier.navigationBarsPadding())
        }
    }
}

@Preview(name = "Academic · Link a student", widthDp = 390, heightDp = 844)
@Composable
private fun LinkStudentPreview() = MaterialTheme { LinkPersonLayout(true, true) }

@Preview(name = "Field · Link a trainee", widthDp = 390, heightDp = 844)
@Composable
private fun LinkTraineePreview() = MaterialTheme { LinkPersonLayout(false, true) }
