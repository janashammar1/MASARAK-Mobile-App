
package sa.edu.iau.cshj.masarak.data.repository

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import sa.edu.iau.cshj.masarak.data.DatabaseProvider
import sa.edu.iau.cshj.masarak.data.dao.TaskDao
import sa.edu.iau.cshj.masarak.data.entity.TaskAttachmentEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskReviewEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskSubmissionEntity

class TaskRepository(
    private val taskDao: TaskDao
) {

    suspend fun saveNewTask(
        recordId: Long,
        submission: TaskSubmissionEntity,
        attachment: TaskAttachmentEntity? = null
    ): Long = DatabaseProvider.db.withTransaction {

        val taskId = taskDao.insertTask(
            TaskEntity(recordId = recordId)
        )

        val submissionId = taskDao.insertSubmission(
            submission.copy(taskId = taskId)
        )

        if (attachment != null) {
            taskDao.insertAttachment(
                attachment.copy(submissionId = submissionId)
            )
        }

        submissionId
    }

    suspend fun resubmitTask(
        existingSubmissionId: Long,
        updatedSubmission: TaskSubmissionEntity,
        removedAttachmentIds: Set<Long>,
        newAttachment: TaskAttachmentEntity? = null
    ): Long = DatabaseProvider.db.withTransaction {

        val oldSubmission = taskDao.getSubmissionById(existingSubmissionId)
            ?: error("Original submission not found")

        val submissions = taskDao
            .getSubmissionsForTask(oldSubmission.taskId)
            .first()

        val nextAttemptNo =
            (submissions.maxOfOrNull { it.attemptNo } ?: 0) + 1

        val newSubmissionId = taskDao.insertSubmission(
            updatedSubmission.copy(
                submissionId = 0L,
                taskId = oldSubmission.taskId,
                attemptNo = nextAttemptNo,
                submittedAt = System.currentTimeMillis()
            )
        )

        val oldAttachments = taskDao
            .getAttachmentsForSubmission(existingSubmissionId)
            .first()

        oldAttachments
            .filterNot { it.attachmentId in removedAttachmentIds }
            .forEach { attachment ->
                taskDao.insertAttachment(
                    attachment.copy(
                        attachmentId = 0L,
                        submissionId = newSubmissionId
                    )
                )
            }

        if (newAttachment != null) {
            taskDao.insertAttachment(
                newAttachment.copy(
                    attachmentId = 0L,
                    submissionId = newSubmissionId
                )
            )
        }

        newSubmissionId
    }

    // Tasks

    fun getTasksForRecord(recordId: Long): Flow<List<TaskEntity>> =
        taskDao.getTasksForRecord(recordId)

    suspend fun insertTask(task: TaskEntity): Long =
        taskDao.insertTask(task)


    // Submissions

    fun getSubmissionsForTask(
        taskId: Long
    ): Flow<List<TaskSubmissionEntity>> =
        taskDao.getSubmissionsForTask(taskId)

    suspend fun insertSubmission(
        submission: TaskSubmissionEntity
    ): Long = taskDao.insertSubmission(submission)

    suspend fun getSubmissionById(
        submissionId: Long
    ): TaskSubmissionEntity? =
        taskDao.getSubmissionById(submissionId)


    // Attachments

    fun getAttachmentsForSubmission(
        submissionId: Long
    ): Flow<List<TaskAttachmentEntity>> =
        taskDao.getAttachmentsForSubmission(submissionId)

    suspend fun insertAttachment(
        attachment: TaskAttachmentEntity
    ): Long = taskDao.insertAttachment(attachment)

    suspend fun deleteAttachmentById(attachmentId: Long) =
        taskDao.deleteAttachmentById(attachmentId)


    // Reviews

    fun getReviewsForSubmission(
        submissionId: Long
    ): Flow<List<TaskReviewEntity>> =
        taskDao.getReviewsForSubmission(submissionId)

    // General review save path.
    suspend fun insertReview(
        review: TaskReviewEntity
    ): Long = DatabaseProvider.db.withTransaction {
        upsertReview(review)
    }

    // Academic reviews require an accepted academic supervisor.
    suspend fun saveAcademicReview(
        review: TaskReviewEntity
    ): Long = DatabaseProvider.db.withTransaction {

        if (!review.reviewStage.equals("Academic", ignoreCase = true)) {
            throw IllegalArgumentException(
                "Invalid academic review stage."
            )
        }

        val isAuthorized =
            taskDao.isAcceptedAcademicSupervisorForSubmission(
                submissionId = review.submissionId,
                reviewerId = review.reviewerId
            )

        if (!isAuthorized) {
            throw SecurityException(
                "You are not authorized to review this submission."
            )
        }

        upsertReview(review)
    }

    // Insert a new review or update the existing one.
    private suspend fun upsertReview(
        review: TaskReviewEntity
    ): Long {

        val existingReview = taskDao.getReviewByReviewerAndStage(
            submissionId = review.submissionId,
            reviewerId = review.reviewerId,
            reviewStage = review.reviewStage
        )

        return if (existingReview != null) {
            taskDao.updateReview(
                review.copy(
                    reviewId = existingReview.reviewId
                )
            )
            existingReview.reviewId
        } else {
            taskDao.insertReview(review)
        }
    }
}
