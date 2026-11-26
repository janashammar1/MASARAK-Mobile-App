package sa.edu.iau.cshj.masarak.ui.screens.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.withTransaction
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import sa.edu.iau.cshj.masarak.data.DatabaseProvider
import sa.edu.iau.cshj.masarak.data.entity.TaskReviewEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskSubmissionEntity
import sa.edu.iau.cshj.masarak.ui.components.BottomBarItem

@Composable
fun RoomFieldReviewRoute(
    submissionId: Long,
    reviewerId: Long,
    onBack: () -> Unit,
    onTab: (BottomBarItem) -> Unit
) {
    val db = DatabaseProvider.db
    val dao = db.taskDao()
    val scope = rememberCoroutineScope()

    var submission by remember(submissionId, reviewerId) {
        mutableStateOf<TaskSubmissionEntity?>(null)
    }

    var name by remember(submissionId, reviewerId) {
        mutableStateOf("")
    }

    var evidence by remember(submissionId, reviewerId) {
        mutableStateOf("")
    }

    var feedback by remember(submissionId, reviewerId) {
        mutableStateOf("")
    }

    var loading by remember(submissionId, reviewerId) {
        mutableStateOf(true)
    }

    var saving by remember(submissionId, reviewerId) {
        mutableStateOf(false)
    }

    var error by remember(submissionId, reviewerId) {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(submissionId, reviewerId) {
        try {
            check(
                dao.isAcceptedFieldReviewer(
                    submissionId,
                    reviewerId
                )
            ) {
                "You are not linked to this student's training."
            }

            val data = dao.getSubmissionById(submissionId)
                ?: throw IllegalStateException(
                    "Task submission not found."
                )

            check(
                dao.latestSubmissionId(submissionId) == submissionId
            ) {
                "The student has submitted a newer attempt. Reopen the task."
            }

            name = dao.submissionStudentName(
                submissionId
            ).orEmpty()

            evidence = dao.getAttachmentsForSubmission(
                submissionId
            ).first().joinToString("\n") {
                it.fileName
            }

            feedback = dao.getReviewsForSubmission(
                submissionId
            ).first()
                .filter {
                    it.reviewerId == reviewerId &&
                            it.reviewStage.equals(
                                "Field",
                                ignoreCase = true
                            )
                }
                .maxByOrNull { it.reviewedAt }
                ?.comment
                .orEmpty()

            submission = data
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            error = e.message ?: "Unable to load task."
        } finally {
            loading = false
        }
    }

    val task = submission

    if (task == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(20.dp)
        ) {
            Text(
                text = if (loading) {
                    "Loading task..."
                } else {
                    error ?: "Task not found."
                }
            )

            TextButton(onClick = onBack) {
                Text("Back")
            }
        }

        return
    }

    ReviewTaskScreen(
        submission = task,
        studentName = name,
        evidenceName = evidence,
        initialFeedback = feedback,
        saving = saving,
        errorMessage = error,
        chatUnreadCount = 0,
        onBack = onBack,
        onBottomBarItemSelected = onTab,
        onSaveReview = { decision, comment ->
            if (!saving) {
                if (
                    decision != "Approved" &&
                    comment.isBlank()
                ) {
                    error =
                        "Add feedback before returning or marking the task incomplete."
                } else {
                    saving = true
                    error = null

                    scope.launch {
                        try {
                            db.withTransaction {
                                check(
                                    dao.isAcceptedFieldReviewer(
                                        submissionId,
                                        reviewerId
                                    )
                                ) {
                                    "You are no longer linked to this training."
                                }

                                check(
                                    dao.latestSubmissionId(
                                        submissionId
                                    ) == submissionId
                                ) {
                                    "A newer attempt is available. Reopen the task."
                                }

                                val prior =
                                    dao.getReviewByReviewerAndStage(
                                        submissionId = submissionId,
                                        reviewerId = reviewerId,
                                        reviewStage = "Field"
                                    )

                                val review = TaskReviewEntity(
                                    reviewId = prior?.reviewId ?: 0L,
                                    submissionId = submissionId,
                                    reviewerId = reviewerId,
                                    reviewStage = "Field",
                                    decision = decision,
                                    comment = comment.trim().takeIf {
                                        it.isNotEmpty()
                                    },
                                    reviewedAt =
                                        System.currentTimeMillis()
                                )

                                if (prior == null) {
                                    dao.insertReview(review)
                                } else {
                                    dao.updateReview(review)
                                }
                            }

                            onBack()
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            error = e.message
                                ?: "Review was not saved. Try again."
                        } finally {
                            saving = false
                        }
                    }
                }
            }
        }
    )
}