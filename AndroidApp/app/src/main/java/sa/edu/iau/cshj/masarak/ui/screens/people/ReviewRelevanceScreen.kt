
package sa.edu.iau.cshj.masarak.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import sa.edu.iau.cshj.masarak.data.entity.TaskAttachmentEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskReviewEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskSubmissionEntity
import sa.edu.iau.cshj.masarak.ui.screens.people.*
import sa.edu.iau.cshj.masarak.ui.viewmodel.MasarakViewModelFactory
import sa.edu.iau.cshj.masarak.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReviewRelevanceScreen(
    submissionId: Long = 0L,
    reviewerId: Long? = null,
    onBackClick: () -> Unit = {}
) {
    val preview = LocalInspectionMode.current

    if (preview) {
        ReviewRelevanceLayout(
            preview = true,
            scrolled = false,
            onBackClick = onBackClick
        )
        return
    }

    val taskViewModel: TaskViewModel =
        viewModel(factory = MasarakViewModelFactory())

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val selectedSubmission by
    taskViewModel.selectedSubmission.collectAsState()

    val attachmentsMap by
    taskViewModel.attachments.collectAsState()

    val reviewsMap by
    taskViewModel.reviews.collectAsState()

    val submission = selectedSubmission
        ?.takeIf { it.submissionId == submissionId }

    val attachments = attachmentsMap[submissionId].orEmpty()

    val academicReview = reviewsMap[submissionId]
        .orEmpty()
        .filter {
            it.reviewStage.equals("Academic", ignoreCase = true)
        }
        .maxByOrNull { it.reviewedAt }

    var selectedDecision by remember(submissionId) {
        mutableStateOf("Partially Relevant")
    }

    var comment by remember(submissionId) {
        mutableStateOf("")
    }

    var isSaving by remember(submissionId) {
        mutableStateOf(false)
    }

    LaunchedEffect(submissionId) {
        if (submissionId > 0L) {
            taskViewModel.loadSubmission(submissionId)
            taskViewModel.loadAttachments(submissionId)
            taskViewModel.loadReviews(submissionId)
        }
    }

    LaunchedEffect(academicReview) {
        selectedDecision =
            academicReview?.decision ?: "Partially Relevant"

        comment = academicReview?.comment.orEmpty()
    }

    ReviewRelevanceLayout(
        preview = false,
        scrolled = false,
        onBackClick = onBackClick,
        submission = submission,
        attachments = attachments,
        selectedDecision = selectedDecision,
        onDecisionChange = {
            selectedDecision = it
        },
        comment = comment,
        onCommentChange = {
            comment = it
        },
        isSaving = isSaving,
        snackbarHostState = snackbarHostState,
        onSave = {
            when {
                submissionId <= 0L -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            "Submission ID is missing."
                        )
                    }
                }

                submission == null -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            "The selected submission could not be loaded."
                        )
                    }
                }

                reviewerId == null || reviewerId <= 0L -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            "Unable to identify the current supervisor."
                        )
                    }
                }

                selectedDecision !in listOf(
                    "Relevant",
                    "Partially Relevant",
                    "Not Relevant"
                ) -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            "Please select a valid review decision."
                        )
                    }
                }

                selectedDecision != "Relevant" && comment.isBlank() -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            "Please add a note for this decision."
                        )
                    }
                }

                isSaving -> Unit

                else -> {
                    scope.launch {
                        isSaving = true

                        try {
                            taskViewModel.saveAcademicReview(
                                TaskReviewEntity(
                                    submissionId = submissionId,
                                    reviewerId = reviewerId,
                                    reviewStage = "Academic",
                                    decision = selectedDecision,
                                    comment = comment.trim()
                                        .ifBlank { null },
                                    reviewedAt = System.currentTimeMillis()
                                )
                            )

                            taskViewModel.loadReviews(submissionId)

                            snackbarHostState.showSnackbar(
                                "Review saved successfully."
                            )

                        } catch (cancelled: CancellationException) {
                            throw cancelled

                        } catch (exception: Exception) {
                            snackbarHostState.showSnackbar(
                                "Could not save review. Please try again."
                            )

                        } finally {
                            isSaving = false
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun ReviewRelevanceLayout(
    preview: Boolean,
    scrolled: Boolean,
    onBackClick: () -> Unit = {},
    submission: TaskSubmissionEntity? = null,
    attachments: List<TaskAttachmentEntity> = emptyList(),
    selectedDecision: String = "Partially Relevant",
    onDecisionChange: (String) -> Unit = {},
    comment: String = "",
    onCommentChange: (String) -> Unit = {},
    isSaving: Boolean = false,
    snackbarHostState: SnackbarHostState? = null,
    onSave: () -> Unit = {}
) {
    PeoplePage(
        academic = true,
        preview = preview,
        header = {
            PeopleTitle(
                "Review relevance",
                onBackClick = onBackClick
            )
        },
        action = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                HorizontalDivider(color = PeopleLine)

                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 19.dp,
                            vertical = 11.dp
                        )
                        .height(45.dp)
                        .background(
                            if (isSaving) PeopleMuted else PeopleNavy,
                            RoundedCornerShape(15.dp)
                        )
                        .clickable(
                            enabled = !isSaving && !preview,
                            onClick = onSave
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    PeopleText(
                        if (isSaving) "Saving..." else "Save",
                        14,
                        Color.White,
                        bold = true
                    )
                }
            }
        }
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState(
                            if (scrolled) Int.MAX_VALUE else 0
                        )
                    )
                    .padding(horizontal = 19.dp)
                    .padding(
                        top = 14.dp,
                        bottom = 23.dp
                    )
            ) {
                RelevanceTask(
                    submission = submission,
                    attachments = attachments,
                    preview = preview
                )

                Spacer(Modifier.height(23.dp))

                PeopleText(
                    "REVIEW",
                    12,
                    PeopleMuted,
                    bold = true
                )

                Spacer(Modifier.height(14.dp))

                PeopleText(
                    "How well does it fit the major?",
                    15,
                    bold = true
                )

                Spacer(Modifier.height(5.dp))

                listOf(
                    "Relevant",
                    "Partially Relevant",
                    "Not Relevant"
                ).forEach { label ->

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                            .clickable(
                                enabled = !preview,
                                onClick = {
                                    onDecisionChange(label)
                                }
                            )
                            .padding(start = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedDecision == label,
                            onClick = {
                                onDecisionChange(label)
                            },
                            enabled = !preview,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = PeopleNavy,
                                unselectedColor = PeopleMuted
                            )
                        )

                        Spacer(Modifier.width(8.dp))

                        PeopleText(label, 14)
                    }
                }

                Spacer(Modifier.height(10.dp))

                PeopleText(
                    "Review note",
                    12,
                    bold = true
                )

                Spacer(Modifier.height(7.dp))

                OutlinedTextField(
                    value = comment,
                    onValueChange = onCommentChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 95.dp),
                    placeholder = {
                        Text("Write your feedback here...")
                    },
                    enabled = !preview,
                    shape = RoundedCornerShape(13.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PeopleNavy,
                        unfocusedBorderColor = Color(0xFF9B9AA0)
                    )
                )

                Spacer(Modifier.height(8.dp))

                PeopleText(
                    "Required for Partially Relevant and Not Relevant.",
                    11,
                    PeopleMuted
                )

                Spacer(Modifier.height(20.dp))
            }

            if (snackbarHostState != null) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun RelevanceTask(
    submission: TaskSubmissionEntity? = null,
    attachments: List<TaskAttachmentEntity> = emptyList(),
    preview: Boolean = false
) {
    PeopleCard {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            PeopleText(
                submission?.title
                    ?: if (preview) " " else "Loading submission...",
                16,
                bold = true
            )

            Spacer(Modifier.height(11.dp))

            val formattedDate = submission?.let {
                SimpleDateFormat(
                    "dd MMM",
                    Locale.getDefault()
                ).format(Date(it.taskDate))
            } ?: "--"

            val duration = submission?.let {
                if (it.durationMinutes % 60 == 0) {
                    "${it.durationMinutes / 60} h"
                } else {
                    "${it.durationMinutes / 60} h ${it.durationMinutes % 60} min"
                }
            } ?: "--"

            val taskType = submission?.taskType ?: "--"

            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                RelevanceMetric(
                    Icons.Outlined.Event,
                    formattedDate
                )

                RelevanceMetric(
                    Icons.Outlined.Schedule,
                    duration
                )

                RelevanceMetric(
                    Icons.Outlined.PersonOutline,
                    taskType
                )
            }

            RelevanceDivider()

            PeopleText(
                "Description",
                12,
                PeopleMuted,
                bold = true
            )

            Spacer(Modifier.height(5.dp))

            PeopleText(
                submission?.description
                    ?.takeIf { it.isNotBlank() }
                    ?: "No description available.",
                14
            )

            RelevanceDivider()

            PeopleText(
                "Challenges",
                12,
                PeopleMuted,
                bold = true
            )

            Spacer(Modifier.height(5.dp))

            PeopleText(
                submission?.challenges
                    ?.takeIf { it.isNotBlank() }
                    ?: "No challenges provided.",
                14
            )

            RelevanceDivider()

            PeopleText(
                "Evidence",
                12,
                PeopleMuted,
                bold = true
            )

            Spacer(Modifier.height(8.dp))

            if (attachments.isEmpty()) {
                PeopleText(
                    "No attachments available.",
                    12,
                    PeopleMuted
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    attachments.forEach { attachment ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (
                                    attachment.mimeType.startsWith("image")
                                ) {
                                    Icons.Outlined.Image
                                } else {
                                    Icons.Outlined.Description
                                },
                                contentDescription = null,
                                modifier = Modifier.size(17.dp),
                                tint = PeopleTeal
                            )

                            Spacer(Modifier.width(8.dp))

                            PeopleText(
                                attachment.fileName,
                                12
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RelevanceMetric(
    icon: ImageVector,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(15.dp),
            tint = PeopleMuted
        )

        PeopleText(label, 12)
    }
}

@Composable
private fun RelevanceDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 10.dp),
        color = PeopleLine
    )
}

@Preview(
    name = "Review relevance",
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun ReviewRelevancePreview() {
    MaterialTheme {
        ReviewRelevanceLayout(
            preview = true,
            scrolled = false
        )
    }
}

@Preview(
    name = "Review relevance - scrolled",
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun ReviewRelevanceScrolledPreview() {
    MaterialTheme {
        ReviewRelevanceLayout(
            preview = true,
            scrolled = true
        )
    }
}
