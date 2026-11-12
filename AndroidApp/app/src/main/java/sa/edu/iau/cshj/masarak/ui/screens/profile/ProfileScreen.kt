package sa.edu.iau.cshj.masarak.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sa.edu.iau.cshj.masarak.ui.theme.MASARAKTheme

private val Cream = Color(0xFFF7F6F3)
private val Navy = Color(0xFF100D46)
private val Ink = Color(0xFF1D1E2F)
private val Muted = Color(0xFF4D4E5E)
private val Teal = Color(0xFF006970)
private val Aqua = Color(0xFF79CCD2)
private val Line = Color(0xFFD5D1C8)
private val Tile = Color(0xFFF0EFEB)
private val Gold = Color(0xFFF6DFB2)

enum class ProfileRole(val label: String) {
    STUDENT("Student"),
    FIELD("Field Supervisor"),
    ACADEMIC("Academic Supervisor")
}

data class ProfilePerson(
    val name: String,
    val email: String,
    val initials: String,
    val role: ProfileRole,
    val organization: String? = null,
    val linkedPeople: Int = 0,
    val linkedSupervisors: Int = 0,
    val fieldSupervisor: String? = null,
    val fieldOrganization: String? = null,
    val academicSupervisor: String? = null,
    val pendingAcademicRequest: String? = null
)

@Composable
fun ProfileScreen(
    person: ProfilePerson,
    onLogout: () -> Unit = {},
    onChangePassword: () -> Unit = {},
    onAcceptRequest: () -> Unit = {},
    onDeclineRequest: () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    val isStudent = person.role == ProfileRole.STUDENT

    Column(
        Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(Cream)
                if (size.width > 0f) {
                    drawRect(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFFCDDDEA), Color.Transparent),
                            center = Offset(size.width * 0.95f, 0f),
                            radius = size.width * 0.82f
                        )
                    )
                    drawRect(
                        Brush.radialGradient(
                            colors = listOf(Color(0x99DDDDF1), Color.Transparent),
                            center = Offset(size.width * 0.08f, 0f),
                            radius = size.width * 0.72f
                        )
                    )
                }
            }
            .statusBarsPadding()
    ) {
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
                .padding(top = 20.dp, bottom = 8.dp)
        ) {
            Text("Profile", color = Ink, fontSize = 23.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))

            ProfileCard {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val avatarColor = when (person.role) {
                        ProfileRole.STUDENT -> Color(0xFFC9ECEE)
                        ProfileRole.FIELD -> Gold
                        ProfileRole.ACADEMIC -> Color(0xFFDFE3F2)
                    }
                    Initials(person.initials, avatarColor, 52)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(person.name, color = Ink, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Row(
                            Modifier
                                .background(Tile, RoundedCornerShape(8.dp))
                                .padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(roleIcon(person.role), null, Modifier.size(14.dp), tint = Muted)
                            Spacer(Modifier.width(4.dp))
                            Text(person.role.label, color = Muted, fontSize = 12.sp)
                        }
                    }
                    TextButton(
                        onClick = onLogout,
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text("Log out", color = Teal, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            if (isStudent) {
                SectionTitle("Your supervisors")
                ProfileCard {
                    ProfileRow(
                        title = person.fieldSupervisor ?: "Field Supervisor",
                        subtitle = if (person.fieldSupervisor == null) "Not linked yet"
                        else listOfNotNull("Field Supervisor", person.fieldOrganization)
                            .joinToString(" · "),
                        icon = Icons.Outlined.Badge,
                        initials = person.fieldSupervisor?.let { initialsOf(it) },
                        linked = person.fieldSupervisor != null
                    )
                    HorizontalDivider(color = Line)
                    ProfileRow(
                        title = person.academicSupervisor ?: "Academic Supervisor",
                        subtitle = if (person.academicSupervisor == null) "Not linked yet"
                        else "Academic Supervisor",
                        icon = Icons.Outlined.AccountBalance,
                        linked = person.academicSupervisor != null
                    )
                }

                person.pendingAcademicRequest?.let { requester ->
                    SectionTitle("Linking requests")
                    ProfileCard {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                            IconTile(Icons.Outlined.Link, Gold)
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(requester, color = Ink, fontWeight = FontWeight.Bold)
                                Text(
                                    "Academic Supervisor · wants to link with you",
                                    color = Muted,
                                    fontSize = 12.sp
                                )
                                Spacer(Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = onDeclineRequest,
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(14.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp)
                                    ) {
                                        Text("Decline", color = Navy)
                                    }
                                    Button(
                                        onClick = onAcceptRequest,
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Navy,
                                            contentColor = Color.White
                                        ),
                                        contentPadding = PaddingValues(horizontal = 6.dp)
                                    ) {
                                        Text("Accept")
                                    }
                                }
                            }
                        }
                    }
                    Text(
                        "Supervisors can see your training only after you accept.",
                        modifier = Modifier.padding(top = 10.dp, start = 4.dp),
                        color = Muted,
                        fontSize = 12.sp
                    )
                }
            } else {
                Spacer(Modifier.height(12.dp))
                val field = person.role == ProfileRole.FIELD
                ProfileCard {
                    Row(
                        Modifier.fillMaxWidth().height(IntrinsicSize.Min).padding(14.dp)
                    ) {
                        ProfileStat(
                            person.linkedPeople,
                            if (field) "Linked trainees" else "Linked students",
                            Modifier.weight(1f)
                        )
                        VerticalDivider(Modifier.fillMaxHeight(), color = Line)
                        ProfileStat(
                            person.linkedSupervisors,
                            if (field) "Academic Supervisor" else "Field Supervisor",
                            Modifier.weight(1f).padding(start = 14.dp)
                        )
                    }
                }
            }

            SectionTitle("Account")
            ProfileCard {
                ProfileRow(
                    person.email,
                    if (person.role == ProfileRole.FIELD) "Work email" else "University email",
                    Icons.Outlined.Email
                )
                if (person.role == ProfileRole.FIELD && person.organization != null) {
                    HorizontalDivider(color = Line)
                    ProfileRow(
                        person.organization,
                        "Training organization",
                        Icons.Outlined.Business
                    )
                }
                HorizontalDivider(color = Line)
                Surface(onClick = onChangePassword, color = Color.Transparent) {
                    ProfileRow("Change password", null, Icons.Outlined.Lock, arrow = true)
                }
            }
        }

        bottomBar()
    }
}

@Composable
private fun ProfileCard(content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color.White)
            .border(1.dp, Line, shape),
        content = content
    )
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        title,
        modifier = Modifier.padding(top = 20.dp, bottom = 10.dp),
        color = Ink,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun ProfileStat(number: Int, label: String, modifier: Modifier) {
    Column(modifier) {
        Text("$number", color = Navy, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Muted, fontSize = 13.sp)
    }
}

@Composable
private fun Initials(text: String, background: Color, size: Int) {
    Box(
        Modifier.size(size.dp).background(background, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Navy, fontSize = if (size >= 50) 19.sp else 13.sp,
            fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun IconTile(icon: ImageVector, background: Color = Tile) {
    Box(
        Modifier.size(36.dp).background(background, RoundedCornerShape(11.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, Modifier.size(18.dp), tint = Muted)
    }
}

@Composable
private fun ProfileRow(
    title: String,
    subtitle: String?,
    icon: ImageVector,
    initials: String? = null,
    linked: Boolean = false,
    arrow: Boolean = false
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (initials != null) Initials(initials, Gold, 36) else IconTile(icon)
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            if (subtitle != null) {
                Spacer(Modifier.height(3.dp))
                Text(subtitle, color = Muted, fontSize = 12.sp)
            }
        }
        if (linked) {
            Spacer(Modifier.width(6.dp))
            Row(
                Modifier
                    .background(Color(0xFFC3EAC7), RoundedCornerShape(8.dp))
                    .padding(horizontal = 7.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Link, null, Modifier.size(13.dp), tint = Color(0xFF173F23))
                Spacer(Modifier.width(3.dp))
                Text("Linked", color = Color(0xFF173F23), fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold)
            }
        }
        if (arrow) {
            Icon(Icons.Outlined.ChevronRight, null, Modifier.size(20.dp), tint = Muted)
        }
    }
}

private fun initialsOf(name: String): String =
    name.trim().split(Regex("\\s+"))
        .filter { it.isNotBlank() && !it.endsWith(".") }
        .take(2).joinToString("") { it.first().uppercaseChar().toString() }

private fun roleIcon(role: ProfileRole): ImageVector = when (role) {
    ProfileRole.STUDENT -> Icons.Outlined.School
    ProfileRole.FIELD -> Icons.Outlined.Badge
    ProfileRole.ACADEMIC -> Icons.Outlined.AccountBalance
}

@Composable
private fun PreviewProfileBar(role: ProfileRole, unread: Int) {
    val thirdLabel = when (role) {
        ProfileRole.STUDENT -> "My Training"
        ProfileRole.FIELD -> "Trainees"
        ProfileRole.ACADEMIC -> "Students"
    }
    val thirdIcon = when (role) {
        ProfileRole.STUDENT -> Icons.Outlined.WorkOutline
        ProfileRole.FIELD -> Icons.Outlined.Groups
        ProfileRole.ACADEMIC -> Icons.Outlined.School
    }

    Row(
        Modifier
            .fillMaxWidth()
            .background(Navy, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .navigationBarsPadding()
            .padding(top = 8.dp, bottom = 8.dp)
    ) {
        PreviewTab("Home", Icons.Outlined.Home, Modifier.weight(1f))
        PreviewTab("Chat", Icons.Outlined.ChatBubbleOutline, Modifier.weight(1f), unread = unread)
        PreviewTab(thirdLabel, thirdIcon, Modifier.weight(1f))
        PreviewTab("Profile", Icons.Outlined.Person, Modifier.weight(1f), selected = true)
    }
}

@Composable
private fun PreviewTab(
    label: String,
    icon: ImageVector,
    modifier: Modifier,
    selected: Boolean = false,
    unread: Int = 0
) {
    val tint = if (selected) Color.White else Color(0xFFB7B8D1)
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(width = 46.dp, height = 26.dp)
                .background(if (selected) Aqua else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, Modifier.size(18.dp), tint = if (selected) Navy else tint)
            if (unread > 0) {
                Box(
                    Modifier.align(Alignment.TopEnd).size(15.dp)
                        .background(Color(0xFFB81922), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$unread", color = Color.White, fontSize = 9.sp, lineHeight = 11.sp)
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(label, color = tint, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Preview(name = "Student", showSystemUi = true)
@Composable
private fun StudentProfilePreview() {
    MASARAKTheme {
        ProfileScreen(
            person = ProfilePerson(
                name = "Sara Alqahtani",
                email = "s.alqahtani@uni.edu.sa",
                initials = "SA",
                role = ProfileRole.STUDENT,
                fieldSupervisor = "Eng. Khalid Alotaibi",
                fieldOrganization = "Nakheel Tech",
                pendingAcademicRequest = "Dr. Huda Alzahrani"
            ),
            bottomBar = { PreviewProfileBar(ProfileRole.STUDENT, unread = 2) }
        )
    }
}

@Preview(name = "Field Supervisor", showSystemUi = true)
@Composable
private fun FieldProfilePreview() {
    MASARAKTheme {
        ProfileScreen(
            person = ProfilePerson(
                name = "Eng. Khalid Alotaibi",
                email = "k.alotaibi@nakheeltech.sa",
                initials = "KA",
                role = ProfileRole.FIELD,
                organization = "Nakheel Tech",
                linkedPeople = 6,
                linkedSupervisors = 1
            ),
            bottomBar = { PreviewProfileBar(ProfileRole.FIELD, unread = 1) }
        )
    }
}

@Preview(name = "Academic Supervisor", showSystemUi = true)
@Composable
private fun AcademicProfilePreview() {
    MASARAKTheme {
        ProfileScreen(
            person = ProfilePerson(
                name = "Dr. Huda Alzahrani",
                email = "h.alzahrani@uni.edu.sa",
                initials = "HZ",
                role = ProfileRole.ACADEMIC,
                linkedPeople = 6,
                linkedSupervisors = 1
            ),
            bottomBar = { PreviewProfileBar(ProfileRole.ACADEMIC, unread = 0) }
        )
    }
}