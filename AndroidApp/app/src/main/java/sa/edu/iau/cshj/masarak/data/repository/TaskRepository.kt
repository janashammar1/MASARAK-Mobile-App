package sa.edu.iau.cshj.masarak.data.repository

import kotlinx.coroutines.flow.Flow
import sa.edu.iau.cshj.masarak.data.dao.TaskDao
import sa.edu.iau.cshj.masarak.data.entity.TaskAttachmentEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskReviewEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskSubmissionEntity
import androidx.room.withTransaction
import sa.edu.iau.cshj.masarak.data.DatabaseProvider
import kotlinx.coroutines.flow.first
class TaskRepository(private val taskDao: TaskDao) {

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

    fun getTasksForRecord(recordId: Long): Flow<List<TaskEntity>> =
        taskDao.getTasksForRecord(recordId)

    suspend fun insertTask(task: TaskEntity): Long =
        taskDao.insertTask(task)

    fun getSubmissionsForTask(taskId: Long): Flow<List<TaskSubmissionEntity>> =
        taskDao.getSubmissionsForTask(taskId)

    suspend fun insertSubmission(submission: TaskSubmissionEntity): Long =
        taskDao.insertSubmission(submission)

    suspend fun getSubmissionById(submissionId: Long): TaskSubmissionEntity? =
        taskDao.getSubmissionById(submissionId)

    fun getAttachmentsForSubmission(submissionId: Long): Flow<List<TaskAttachmentEntity>> =
        taskDao.getAttachmentsForSubmission(submissionId)

    suspend fun insertAttachment(attachment: TaskAttachmentEntity): Long =
        taskDao.insertAttachment(attachment)

    suspend fun deleteAttachmentById(attachmentId: Long) =
        taskDao.deleteAttachmentById(attachmentId)

    fun getReviewsForSubmission(submissionId: Long): Flow<List<TaskReviewEntity>> =
        taskDao.getReviewsForSubmission(submissionId)

    suspend fun insertReview(review: TaskReviewEntity): Long =
        taskDao.insertReview(review)
}