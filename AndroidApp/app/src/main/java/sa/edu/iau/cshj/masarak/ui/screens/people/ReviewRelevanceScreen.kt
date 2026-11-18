package sa.edu.iau.cshj.masarak.ui.screens.tasks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sa.edu.iau.cshj.masarak.ui.screens.people.*

@Composable
fun ReviewRelevanceScreen(onBackClick: () -> Unit = {}) {
    ReviewRelevanceLayout(LocalInspectionMode.current, scrolled = false, onBackClick = onBackClick)
}

@Composable
private fun ReviewRelevanceLayout(preview: Boolean, scrolled: Boolean, onBackClick: () -> Unit = {}) {
    PeoplePage(academic = true, preview = preview, header = { PeopleTitle("Review relevance", onBackClick = onBackClick) }, action = {
        Column(Modifier.fillMaxWidth().background(Color.White)) {
            HorizontalDivider(color = PeopleLine)
            Box(Modifier.fillMaxWidth().padding(horizontal = 19.dp, vertical = 11.dp).height(45.dp)
                .background(PeopleNavy, RoundedCornerShape(15.dp)), contentAlignment = Alignment.Center) {
                PeopleText("Save", 14, Color.White, bold = true)
            }
        }
    }) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState(if (scrolled) Int.MAX_VALUE else 0))
                .padding(horizontal = 19.dp).padding(top = 14.dp, bottom = 23.dp)
        ) {
            RelevanceTask()
            Spacer(Modifier.height(23.dp))
            PeopleText("REVIEW", 12, PeopleMuted, bold = true)
            Spacer(Modifier.height(14.dp))
            Column(Modifier.fillMaxWidth().background(Color(0xFFF8E3B6), RoundedCornerShape(17.dp))
                .padding(15.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Badge, null, Modifier.size(17.dp), tint = Color(0xFF3D2A0B))
                    Spacer(Modifier.width(8.dp))
                    PeopleText("Eng. Khalid · approved", 13, Color(0xFF3D2A0B),
                        bold = true, modifier = Modifier.weight(1f))
                    PeopleText("09:30", 11, Color(0xFF685431))
                }
                Spacer(Modifier.height(9.dp))
                PeopleText("Clean structure and clear validation. Add screenshots of the login flow next time.",
                    14, Color(0xFF3D2A0B))
            }
            Spacer(Modifier.height(22.dp))
            HorizontalDivider(color = PeopleLine)
            Spacer(Modifier.height(15.dp))
            PeopleText("YOUR REVIEW", 12, PeopleMuted, bold = true)
            Spacer(Modifier.height(14.dp))
            PeopleText("How well does it fit the major?", 15, bold = true)
            Spacer(Modifier.height(5.dp))
            listOf("Relevant", "Partially Relevant", "Not Relevant").forEachIndexed { index, label ->
                Row(Modifier.fillMaxWidth().height(45.dp).padding(start = 3.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    val selected = index == 1
                    Icon(if (selected) Icons.Outlined.RadioButtonChecked else Icons.Outlined.RadioButtonUnchecked,
                        null, Modifier.size(22.dp), tint = if (selected) PeopleNavy else PeopleMuted)
                    Spacer(Modifier.width(12.dp))
                    PeopleText(label, 14)
                }
            }
            Spacer(Modifier.height(10.dp))
            PeopleText("Note for Sara", 12, bold = true)
            Spacer(Modifier.height(7.dp))
            Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(13.dp), color = Color.White,
                border = BorderStroke(1.dp, Color(0xFF9B9AA0))) {
                Box(Modifier.heightIn(min = 95.dp).padding(14.dp)) {
                    PeopleText("Good work. Ask for tasks with more backend logic so they match your major.",
                        14, Color(0xFF3A365D))
                }
            }
            Spacer(Modifier.height(8.dp))
            PeopleText("Required for Partially Relevant and Not Relevant.", 11, PeopleMuted)
        }
    }
}

@Composable
private fun RelevanceTask() {
    PeopleCard {
        Column(Modifier.fillMaxWidth().padding(15.dp)) {
            PeopleText("Build the login API", 16, bold = true)
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                PeopleAvatar("SA", small = true)
                Spacer(Modifier.width(9.dp))
                PeopleText("Sara Alqahtani", 13)
            }
            Spacer(Modifier.height(11.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                RelevanceMetric(Icons.Outlined.Event, "10 Sep")
                RelevanceMetric(Icons.Outlined.Schedule, "6 h")
                RelevanceMetric(Icons.Outlined.PersonOutline, "Individual")
            }
            RelevanceDivider()
            PeopleText("Description", 12, PeopleMuted, bold = true)
            Spacer(Modifier.height(5.dp))
            PeopleText("Built the login endpoint with token sign-in, then tested it with the mobile app.", 14)
            RelevanceDivider()
            PeopleText("Challenges", 12, PeopleMuted, bold = true)
            Spacer(Modifier.height(5.dp))
            PeopleText("Handling expired tokens.", 14)
            RelevanceDivider()
            PeopleText("Evidence", 12, PeopleMuted, bold = true)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PersonChip("login-flow.png", Icons.Outlined.Image, Color(0xFFF0EFEB), PeopleTeal)
                PersonChip("api-notes.pdf", Icons.Outlined.Description, Color(0xFFF0EFEB), PeopleTeal)
            }
        }
    }
}

@Composable
private fun RelevanceMetric(icon: ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, null, Modifier.size(15.dp), tint = PeopleMuted)
        PeopleText(label, 12)
    }
}

@Composable
private fun RelevanceDivider() {
    HorizontalDivider(Modifier.padding(vertical = 10.dp), color = PeopleLine)
}

@Preview(name = "Review relevance", widthDp = 390, heightDp = 844)
@Composable
private fun ReviewRelevancePreview() = MaterialTheme { ReviewRelevanceLayout(true, false) }

@Preview(name = "Review relevance · scrolled", widthDp = 390, heightDp = 844)
@Composable
private fun ReviewRelevanceScrolledPreview() = MaterialTheme { ReviewRelevanceLayout(true, true) }
