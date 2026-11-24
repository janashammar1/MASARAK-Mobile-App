
package sa.edu.iau.cshj.masarak.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import sa.edu.iau.cshj.masarak.data.DatabaseProvider
import sa.edu.iau.cshj.masarak.data.entity.SupervisorLinkEntity
import sa.edu.iau.cshj.masarak.ui.screens.people.*
import androidx.compose.runtime.saveable.rememberSaveable
@Composable
fun LinkPersonSheet() {
    LinkPersonSheet(
        isAcademic = true,
        currentUserId = null
    )
}

@Composable
fun LinkPersonSheet(
    isAcademic: Boolean,
    currentUserId: Long?,
    onDismiss: () -> Unit = {}
) {
    LinkPersonLayout(
        academic = isAcademic,
        currentUserId = currentUserId,
        preview = LocalInspectionMode.current,
        onDismiss = onDismiss
    )
}

@Composable
private fun LinkPersonLayout(
    academic: Boolean,
    currentUserId: Long?,
    preview: Boolean,
    onDismiss: () -> Unit = {}
) {
    var email by rememberSaveable { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var requestSent by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {

        // Existing screen remains behind the sheet.
        PeopleListLayout(
            academic = academic,
            preview = preview
        )

        Box(
            Modifier
                .matchParentSize()
                .background(PeopleNavy.copy(alpha = .32f))
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = null,
                    onClick = onDismiss
                )
        )

        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Color.White,
                    RoundedCornerShape(
                        topStart = 23.dp,
                        topEnd = 23.dp
                    )
                )
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
            ) {

                Box(
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 12.dp, bottom = 15.dp)
                        .width(34.dp)
                        .height(3.dp)
                        .background(
                            Color(0xFFD5D4CE),
                            CircleShape
                        )
                )

                PeopleText(
                    if (academic) "Link a student" else "Link a trainee",
                    17,
                    bold = true
                )

                Spacer(Modifier.height(8.dp))

                PeopleText(
                    "They’ll get your request in their Profile. Once they accept, you’ll see their training record.",
                    13,
                    PeopleMuted
                )

                Spacer(Modifier.height(15.dp))

                PeopleText(
                    "University email",
                    12,
                    bold = true
                )

                Spacer(Modifier.height(6.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        message = null
                        isError = false
                        requestSent = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Enter student's email")
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.MailOutline,
                            contentDescription = null
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    enabled = !isLoading && !requestSent
                )

                if (message != null) {
                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = message!!,
                        color = if (isError) {
                            Color(0xFFB3261E)
                        } else {
                            Color(0xFF18794E)
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(15.dp))

                Button(
                    onClick = {
                        scope.launch {

                            val cleanEmail = email.trim().lowercase()

                            if (!android.util.Patterns.EMAIL_ADDRESS
                                    .matcher(cleanEmail)
                                    .matches()
                            ) {
                                message = "Please enter a valid email address."
                                isError = true
                                return@launch
                            }

                            if (currentUserId == null || currentUserId <= 0L) {
                                message =
                                    "Unable to identify the current supervisor."
                                isError = true
                                return@launch
                            }

                            isLoading = true
                            message = null
                            isError = false

                            try {
                                val db = DatabaseProvider.db

                                // Retrieve the signed-in supervisor.
                                val supervisor = db.userDao()
                                    .getUserById(currentUserId)

                                if (supervisor == null) {
                                    message = "Supervisor account was not found."
                                    isError = true
                                    return@launch
                                }

                                // Verify the supervisor's actual role.
                                val actualRole = supervisor.role
                                    .trim()
                                    .lowercase()

                                val expectedRole = if (academic) {
                                    "academic"
                                } else {
                                    "field"
                                }

                                val roleMatches = when (expectedRole) {
                                    "academic" ->
                                        actualRole == "academic" ||
                                                actualRole == "academic supervisor"

                                    else ->
                                        actualRole == "field" ||
                                                actualRole == "field supervisor"
                                }

                                if (!roleMatches) {
                                    message =
                                        "You are not authorized to send this request."
                                    isError = true
                                    return@launch
                                }

                                // Find the account using the entered email.
                                val student = db.userDao()
                                    .getUserByEmail(cleanEmail)

                                if (student == null) {
                                    message = "Student account was not found."
                                    isError = true
                                    return@launch
                                }

                                // Only student accounts can receive this request.
                                if (!student.role.trim().equals(
                                        "student",
                                        ignoreCase = true
                                    )
                                ) {
                                    message =
                                        "This email does not belong to a student."
                                    isError = true
                                    return@launch
                                }

                                // Retrieve the student's training records.
                                val records = db.trainingDao()
                                    .getTrainingRecordsForStudent(
                                        student.userId
                                    )
                                    .first()

                                val record = records.firstOrNull()

                                if (record == null) {
                                    message =
                                        "Student has no training record yet."
                                    isError = true
                                    return@launch
                                }

                                // Check whether a request already exists.
                                val existingRequest = db.trainingDao()
                                    .getLinkRequest(
                                        record.recordId,
                                        currentUserId
                                    )

                                if (existingRequest != null) {
                                    message = when (
                                        existingRequest.status.lowercase()
                                    ) {
                                        "pending" ->
                                            "A pending request already exists."

                                        "accepted" ->
                                            "You are already linked to this student."

                                        "rejected" ->
                                            "A previous request was rejected."

                                        else ->
                                            "A link request already exists."
                                    }

                                    isError = true
                                    return@launch
                                }

                                // Save the request in Room.
                                db.trainingDao().insertSupervisorLink(
                                    SupervisorLinkEntity(
                                        recordId = record.recordId,
                                        supervisorId = currentUserId,
                                        supervisorRole = expectedRole,
                                        status = "pending",
                                        requestedAt = System.currentTimeMillis()
                                    )
                                )

                                message = "Request sent successfully."
                                isError = false
                                requestSent = true

                            } catch (cancelled: CancellationException) {
                                throw cancelled
                            } catch (exception: Exception) {
                                message =
                                    "Something went wrong. Please try again."
                                isError = true
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    enabled = !isLoading && !requestSent,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PeopleNavy
                    ),
                    shape = RoundedCornerShape(13.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.Outlined.Send,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )

                        Spacer(Modifier.width(7.dp))

                        Text(
                            if (requestSent) "Request sent" else "Send request"
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))
            }

            if (preview) {
                PeopleGestureBar(dark = false)
            } else {
                Spacer(Modifier.navigationBarsPadding())
            }
        }
    }
}

@Preview(
    name = "Academic · Link a student",
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun LinkStudentPreview() {
    MaterialTheme {
        LinkPersonLayout(
            academic = true,
            currentUserId = null,
            preview = true
        )
    }
}

@Preview(
    name = "Field · Link a trainee",
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun LinkTraineePreview() {
    MaterialTheme {
        LinkPersonLayout(
            academic = false,
            currentUserId = null,
            preview = true
        )
    }
}
