
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import sa.edu.iau.cshj.masarak.data.DatabaseProvider
import sa.edu.iau.cshj.masarak.data.entity.SupervisorLinkEntity
import sa.edu.iau.cshj.masarak.ui.screens.people.*

@Composable
fun LinkPersonSheet() = LinkPersonSheet(isAcademic = true)

@Composable
fun LinkPersonSheet(
    isAcademic: Boolean,
    onDismiss: () -> Unit = {}
) {
    LinkPersonLayout(isAcademic, LocalInspectionMode.current, onDismiss)
}

@Composable
private fun LinkPersonLayout(
    academic: Boolean,
    preview: Boolean,
    onDismiss: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {

        PeopleListLayout(academic, preview)

        Box(
            Modifier.matchParentSize()
                .background(PeopleNavy.copy(alpha = .32f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
        )

        Column(
            Modifier.align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Color.White,
                    RoundedCornerShape(topStart = 23.dp, topEnd = 23.dp)
                )
        ) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 22.dp)) {

                Box(
                    Modifier.align(Alignment.CenterHorizontally)
                        .padding(top = 12.dp, bottom = 15.dp)
                        .width(34.dp).height(3.dp)
                        .background(Color(0xFFD5D4CE), CircleShape)
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

                PeopleText("University email", 12, bold = true)

                Spacer(Modifier.height(6.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        message = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("name@uni.edu.sa") },
                    leadingIcon = {
                        Icon(Icons.Outlined.MailOutline, contentDescription = null)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    )
                )

                if (message != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = message!!,
                        color = if (isError) Color(0xFFB3261E)
                        else Color(0xFF18794E),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(15.dp))

                Button(
                    onClick = {
                        scope.launch {
                            val cleanEmail = email.trim().lowercase()

                            if (!android.util.Patterns.EMAIL_ADDRESS
                                    .matcher(cleanEmail).matches()
                            ) {
                                message = "Please enter a valid email address."
                                isError = true
                                return@launch
                            }

                            if (cleanEmail != "student@iau.edu.sa") {
                                message = "No student found with this email."
                                isError = true
                                return@launch
                            }

                            isLoading = true

                            try {
                                val db = DatabaseProvider.db

                                val student = db.userDao()
                                    .getUserByEmail(cleanEmail)

                                if (student == null) {
                                    message = "Student account was not found."
                                    isError = true
                                    return@launch
                                }

                                val supervisorEmail = if (academic) {
                                    "supervisor.academic@iau.edu.sa"
                                } else {
                                    "supervisor.field@iau.edu.sa"
                                }

                                val supervisor = db.userDao()
                                    .getUserByEmail(supervisorEmail)

                                if (supervisor == null) {
                                    message = "Supervisor account was not found."
                                    isError = true
                                    return@launch
                                }

                                val records = db.trainingDao()
                                    .getTrainingRecordsForStudent(student.userId)
                                    .first()

                                val record = records.firstOrNull()

                                if (record == null) {
                                    message = "Student has no training record yet."
                                    isError = true
                                    return@launch
                                }

                                val existingRequest = db.trainingDao()
                                    .getLinkRequest(
                                        record.recordId,
                                        supervisor.userId
                                    )

                                if (existingRequest != null) {
                                    message = "A link request already exists."
                                    isError = true
                                    return@launch
                                }

                                db.trainingDao().insertSupervisorLink(
                                    SupervisorLinkEntity(
                                        recordId = record.recordId,
                                        supervisorId = supervisor.userId,
                                        supervisorRole = if (academic) "academic" else "field",
                                        status = "pending",
                                        requestedAt = System.currentTimeMillis()
                                    )
                                )

                                message = "Request sent successfully."
                                isError = false

                            } catch (e: Exception) {
                                message = "Something went wrong. Please try again."
                                isError = true
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    enabled = !isLoading,
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
                        Text("Send request")
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

@Preview(name = "Academic · Link a student", widthDp = 390, heightDp = 844)
@Composable
private fun LinkStudentPreview() {
    MaterialTheme {
        LinkPersonLayout(true, true)
    }
}

@Preview(name = "Field · Link a trainee", widthDp = 390, heightDp = 844)
@Composable
private fun LinkTraineePreview() {
    MaterialTheme {
        LinkPersonLayout(false, true)
    }
}
