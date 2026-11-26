package sa.edu.iau.cshj.masarak.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Locale
import sa.edu.iau.cshj.masarak.data.dao.ProfileSupervisorRow
import sa.edu.iau.cshj.masarak.ui.components.AppBottomBar
import sa.edu.iau.cshj.masarak.ui.components.BottomBarItem
import sa.edu.iau.cshj.masarak.ui.theme.MASARAKTheme
import sa.edu.iau.cshj.masarak.ui.viewmodel.MasarakViewModelFactory
import sa.edu.iau.cshj.masarak.ui.viewmodel.ProfileViewModel

private val Cream = Color(0xFFF7F6F3)
private val Navy = Color(0xFF100D46)
private val Ink = Color(0xFF1D1E2F)
private val Muted = Color(0xFF4D4E5E)
private val Teal = Color(0xFF006970)
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

// نقطة الدخول التي سنستدعيها من AppNavGraph.
// userId يجب أن يكون معرّف الحساب المسجّل دخوله.
@Composable
fun ProfileRoute(
    userId: Long,
    onLogout: () -> Unit,
    bottomBar: @Composable () -> Unit = {},
    profileViewModel: ProfileViewModel = viewModel(
        factory = MasarakViewModelFactory()
    )
) {
    val state by profileViewModel.uiState.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    var showPasswordDialog by rememberSaveable(userId) {
        mutableStateOf(false)
    }

    LaunchedEffect(userId) {
        profileViewModel.loadProfile(userId)
    }

    LaunchedEffect(state.actionMessage) {
        val message = state.actionMessage ?: return@LaunchedEffect
        snackbar.showSnackbar(message)
        profileViewModel.clearActionMessage()
    }

    LaunchedEffect(state.passwordChanged) {
        if (state.passwordChanged) {
            showPasswordDialog = false
            snackbar.showSnackbar("Password changed successfully.")
            profileViewModel.clearPasswordFeedback()
        }
    }

    Scaffold(
        containerColor = Cream,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = bottomBar
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Teal)
                    }
                }

                state.error != null -> {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.error
                                ?: "Unable to load your profile.",
                            color = MaterialTheme.colorScheme.error
                        )

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                profileViewModel.loadProfile(userId)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Navy
                            )
                        ) {
                            Text("Retry")
                        }

                        TextButton(
                            onClick = {
                                showPasswordDialog = false
                                profileViewModel.clearProfile()
                                onLogout()
                            }
                        ) {
                            Text("Log out", color = Teal)
                        }
                    }
                }

                state.person != null -> {
                    ProfileScreen(
                        person = state.person!!,
                        requests = state.pendingRequests,
                        processingLinkId = state.processingLinkId,
                        onLogout = {
                            showPasswordDialog = false
                            profileViewModel.clearProfile()
                            onLogout()
                        },
                        onChangePassword = {
                            profileViewModel.clearPasswordFeedback()
                            showPasswordDialog = true
                        },
                        onAcceptLink = profileViewModel::acceptRequest,
                        onDeclineLink = profileViewModel::declineRequest
                    )
                }
            }
        }
    }

    if (showPasswordDialog && state.person != null) {
        ChangePasswordDialog(
            isSaving = state.isChangingPassword,
            errorMessage = state.passwordError,
            onDismiss = {
                showPasswordDialog = false
                profileViewModel.clearPasswordFeedback()
            },
            onSubmit = { current, new, confirm ->
                profileViewModel.changePassword(
                    currentPassword = current,
                    newPassword = new,
                    confirmPassword = confirm
                )
            }
        )
    }
}

// واجهة العرض، وتبقى قابلة للمعاينة بدون قاعدة بيانات.
@Composable
fun ProfileScreen(
    person: ProfilePerson,
    onLogout: () -> Unit = {},
    onChangePassword: () -> Unit = {},
    onAcceptRequest: () -> Unit = {},
    onDeclineRequest: () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    requests: List<ProfileSupervisorRow> = emptyList(),
    processingLinkId: Long? = null,
    onAcceptLink: (Long) -> Unit = {},
    onDeclineLink: (Long) -> Unit = {}
) {
    Column(
        Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(Cream)

                if (size.width > 0f) {
                    drawRect(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFCDDDEA),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.95f, 0f),
                            radius = size.width * 0.82f
                        )
                    )

                    drawRect(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0x99DDDDF1),
                                Color.Transparent
                            ),
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
                .padding(top = 20.dp, bottom = 24.dp)
        ) {
            Text(
                "Profile",
                color = Ink,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(24.dp))

            ProfileCard {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
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
                        Text(
                            person.name,
                            color = Ink,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(4.dp))

                        Row(
                            Modifier
                                .background(
                                    Tile,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(
                                    horizontal = 7.dp,
                                    vertical = 4.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                roleIcon(person.role),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Muted
                            )

                            Spacer(Modifier.width(4.dp))

                            Text(
                                person.role.label,
                                color = Muted,
                                fontSize = 12.sp
                            )
                        }
                    }

                    TextButton(
                        onClick = onLogout,
                        contentPadding = PaddingValues(
                            horizontal = 4.dp
                        )
                    ) {
                        Text(
                            "Log out",
                            color = Teal,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (person.role == ProfileRole.STUDENT) {
                SectionTitle("Your supervisors")

                ProfileCard {
                    ProfileRow(
                        title = person.fieldSupervisor
                            ?: "Field Supervisor",
                        subtitle = if (
                            person.fieldSupervisor == null
                        ) {
                            "Not linked yet"
                        } else {
                            listOfNotNull(
                                "Field Supervisor",
                                person.fieldOrganization
                            ).joinToString(" · ")
                        },
                        icon = Icons.Outlined.Badge,
                        initials = person.fieldSupervisor
                            ?.let(::initialsOf),
                        linked = person.fieldSupervisor != null
                    )

                    HorizontalDivider(color = Line)

                    ProfileRow(
                        title = person.academicSupervisor
                            ?: "Academic Supervisor",
                        subtitle = if (
                            person.academicSupervisor == null
                        ) {
                            "Not linked yet"
                        } else {
                            "Academic Supervisor"
                        },
                        icon = Icons.Outlined.AccountBalance,
                        initials = person.academicSupervisor
                            ?.let(::initialsOf),
                        linked = person.academicSupervisor != null
                    )
                }

                if (requests.isNotEmpty()) {
                    SectionTitle("Linking requests")

                    requests.forEach { request ->
                        key(request.linkId) {
                            LinkRequestCard(
                                name = request.supervisorName,
                                roleLabel = supervisorLabel(
                                    request.supervisorRole
                                ),
                                enabled = processingLinkId == null,
                                isProcessing =
                                    processingLinkId == request.linkId,
                                onAccept = {
                                    onAcceptLink(request.linkId)
                                },
                                onDecline = {
                                    onDeclineLink(request.linkId)
                                }
                            )

                            Spacer(Modifier.height(10.dp))
                        }
                    }

                    LinkingNote()

                } else {
                    // بيانات معاينة قديمة، وليست طلبات Room.
                    person.pendingAcademicRequest?.let { name ->
                        SectionTitle("Linking requests")

                        LinkRequestCard(
                            name = name,
                            roleLabel = "Academic Supervisor",
                            enabled = processingLinkId == null,
                            isProcessing = false,
                            onAccept = onAcceptRequest,
                            onDecline = onDeclineRequest
                        )

                        Spacer(Modifier.height(10.dp))
                        LinkingNote()
                    }
                }

            } else {
                Spacer(Modifier.height(12.dp))

                ProfileCard {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .padding(14.dp)
                    ) {
                        ProfileStat(
                            number = person.linkedPeople,
                            label = if (
                                person.role == ProfileRole.FIELD
                            ) {
                                "Linked trainees"
                            } else {
                                "Linked students"
                            },
                            modifier = Modifier.weight(1f)
                        )

                        VerticalDivider(
                            modifier = Modifier.fillMaxHeight(),
                            color = Line
                        )

                        ProfileStat(
                            number = person.linkedSupervisors,
                            label = if (
                                person.role == ProfileRole.FIELD
                            ) {
                                "Academic Supervisor"
                            } else {
                                "Field Supervisor"
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 14.dp)
                        )
                    }
                }
            }

            SectionTitle("Account")

            ProfileCard {
                ProfileRow(
                    title = person.email,
                    subtitle = if (
                        person.role == ProfileRole.FIELD
                    ) {
                        "Work email"
                    } else {
                        "University email"
                    },
                    icon = Icons.Outlined.Email
                )

                if (
                    person.role == ProfileRole.FIELD &&
                    !person.organization.isNullOrBlank()
                ) {
                    HorizontalDivider(color = Line)

                    ProfileRow(
                        title = person.organization.orEmpty(),
                        subtitle = "Training organization",
                        icon = Icons.Outlined.Business
                    )
                }

                HorizontalDivider(color = Line)

                Surface(
                    onClick = onChangePassword,
                    color = Color.Transparent
                ) {
                    ProfileRow(
                        title = "Change password",
                        subtitle = null,
                        icon = Icons.Outlined.Lock,
                        arrow = true
                    )
                }
            }
        }

        bottomBar()
    }
}

@Composable
private fun LinkRequestCard(
    name: String,
    roleLabel: String,
    enabled: Boolean,
    isProcessing: Boolean,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    ProfileCard {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            IconTile(Icons.Outlined.Link, Gold)

            Spacer(Modifier.width(10.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    name,
                    color = Ink,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "$roleLabel · wants to link with you",
                    color = Muted,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDecline,
                        enabled = enabled,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(
                            horizontal = 6.dp
                        )
                    ) {
                        Text("Decline")
                    }

                    Button(
                        onClick = onAccept,
                        enabled = enabled,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Navy,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(
                            horizontal = 6.dp
                        )
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = Muted
                            )
                        } else {
                            Text("Accept")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LinkingNote() {
    Text(
        "Supervisors can see your training only after you accept.",
        modifier = Modifier.padding(start = 4.dp),
        color = Muted,
        fontSize = 12.sp
    )
}

@Composable
private fun ChangePasswordDialog(
    isSaving: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String) -> Unit
) {
    // لا نحفظ كلمات المرور في rememberSaveable.
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {
            if (!isSaving) onDismiss()
        },
        containerColor = Color.White,
        title = {
            Text(
                "Change password",
                color = Ink,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Use at least 8 characters and choose a password different from your current one.",
                    color = Muted
                )

                PasswordField(
                    label = "Current password",
                    value = currentPassword,
                    enabled = !isSaving,
                    onValueChange = { currentPassword = it }
                )

                PasswordField(
                    label = "New password",
                    value = newPassword,
                    enabled = !isSaving,
                    onValueChange = { newPassword = it }
                )

                PasswordField(
                    label = "Confirm new password",
                    value = confirmPassword,
                    enabled = !isSaving,
                    onValueChange = { confirmPassword = it }
                )

                if (!errorMessage.isNullOrBlank()) {
                    Text(
                        errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(
                        currentPassword,
                        newPassword,
                        confirmPassword
                    )
                },
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Navy
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Save")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSaving
            ) {
                Text("Cancel", color = Teal)
            }
        }
    )
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        visualTransformation = if (visible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            IconButton(
                onClick = { visible = !visible },
                enabled = enabled
            ) {
                Icon(
                    imageVector = if (visible) {
                        Icons.Outlined.VisibilityOff
                    } else {
                        Icons.Outlined.Visibility
                    },
                    contentDescription = if (visible) {
                        "Hide password"
                    } else {
                        "Show password"
                    }
                )
            }
        }
    )
}

@Composable
private fun ProfileCard(
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(18.dp)

    Column(
        modifier = Modifier
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
        modifier = Modifier.padding(
            top = 20.dp,
            bottom = 10.dp
        ),
        color = Ink,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun ProfileStat(
    number: Int,
    label: String,
    modifier: Modifier
) {
    Column(modifier) {
        Text(
            "$number",
            color = Navy,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Text(label, color = Muted, fontSize = 13.sp)
    }
}

@Composable
private fun Initials(
    text: String,
    background: Color,
    size: Int
) {
    Box(
        Modifier
            .size(size.dp)
            .background(background, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = Navy,
            fontSize = if (size >= 50) 19.sp else 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun IconTile(
    icon: ImageVector,
    background: Color = Tile
) {
    Box(
        Modifier
            .size(36.dp)
            .background(background, RoundedCornerShape(11.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Muted
        )
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
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (initials != null) {
            Initials(initials, Gold, 36)
        } else {
            IconTile(icon)
        }

        Spacer(Modifier.width(10.dp))

        Column(Modifier.weight(1f)) {
            Text(
                title,
                color = Ink,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            if (subtitle != null) {
                Spacer(Modifier.height(3.dp))
                Text(subtitle, color = Muted, fontSize = 12.sp)
            }
        }

        if (linked) {
            Spacer(Modifier.width(6.dp))

            Row(
                Modifier
                    .background(
                        Color(0xFFC3EAC7),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 7.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Link,
                    contentDescription = null,
                    modifier = Modifier.size(13.dp),
                    tint = Color(0xFF173F23)
                )

                Spacer(Modifier.width(3.dp))

                Text(
                    "Linked",
                    color = Color(0xFF173F23),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (arrow) {
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Muted
            )
        }
    }
}

private fun initialsOf(name: String): String =
    name.trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() && !it.endsWith(".") }
        .take(2)
        .joinToString("") {
            it.first().uppercaseChar().toString()
        }

private fun roleIcon(role: ProfileRole): ImageVector =
    when (role) {
        ProfileRole.STUDENT -> Icons.Outlined.School
        ProfileRole.FIELD -> Icons.Outlined.Badge
        ProfileRole.ACADEMIC -> Icons.Outlined.AccountBalance
    }

private fun supervisorLabel(role: String): String =
    when (role.trim().lowercase(Locale.ROOT)) {
        "field", "field supervisor" -> "Field Supervisor"
        "academic", "academic supervisor" -> "Academic Supervisor"
        else -> "Supervisor"
    }

// بيانات المعاينة فقط؛ ProfileRoute يستخدم بيانات Room.
internal fun demoProfile(role: String): ProfilePerson =
    when (role) {
        "field" -> ProfilePerson(
            name = "Eng. Khalid Alotaibi",
            email = "k.alotaibi@nakheeltech.sa",
            initials = "KA",
            role = ProfileRole.FIELD,
            organization = "Nakheel Tech",
            linkedPeople = 6,
            linkedSupervisors = 1
        )

        "academic" -> ProfilePerson(
            name = "Dr. Huda Alzahrani",
            email = "h.alzahrani@uni.edu.sa",
            initials = "HZ",
            role = ProfileRole.ACADEMIC,
            linkedPeople = 6,
            linkedSupervisors = 1
        )

        else -> ProfilePerson(
            name = "Sara Alqahtani",
            email = "s.alqahtani@uni.edu.sa",
            initials = "SA",
            role = ProfileRole.STUDENT,
            fieldSupervisor = "Eng. Khalid Alotaibi",
            fieldOrganization = "Nakheel Tech",
            pendingAcademicRequest = "Dr. Huda Alzahrani"
        )
    }

@Composable
private fun PreviewProfile(role: String) {
    MASARAKTheme {
        Column(Modifier.fillMaxSize().background(Color(0xFF0C0D45))) {
            Box(Modifier.weight(1f).fillMaxWidth()) {
                ProfileScreen(
                    person = demoProfile(role),
                    bottomBar = {
                        Box(Modifier.fillMaxWidth()
                            .background(Color(0xFF0C0D45)).navigationBarsPadding()) {
                            AppBottomBar(
                                selectedItem = BottomBarItem.Profile,
                                thirdTabLabel = when (role) {
                                    "field" -> "Trainees"
                                    "academic" -> "Students"
                                    else -> "My Training"
                                },
                                chatUnreadCount = 0,
                                onItemSelected = {}
                            )
                        }
                    }
                )
            }
            Box(Modifier.fillMaxWidth().height(17.dp), contentAlignment = Alignment.Center) {
                Box(Modifier.width(100.dp).height(4.dp)
                    .background(Color.White.copy(alpha = .45f), CircleShape))
            }
        }
    }
}

@Preview(name = "Student", widthDp = 390, heightDp = 844, showSystemUi = false)
@Composable
private fun StudentProfilePreview() {
    PreviewProfile("student")
}

@Preview(name = "Field Supervisor", widthDp = 390, heightDp = 844, showSystemUi = false)
@Composable
private fun FieldProfilePreview() {
    PreviewProfile("field")
}

@Preview(name = "Academic Supervisor", widthDp = 390, heightDp = 844, showSystemUi = false)
@Composable
private fun AcademicProfilePreview() {
    PreviewProfile("academic")
}



