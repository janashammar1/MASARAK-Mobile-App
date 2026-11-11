package sa.edu.iau.cshj.masarak.data.repository

import kotlinx.coroutines.flow.Flow
import sa.edu.iau.cshj.masarak.data.dao.TaskDao
import sa.edu.iau.cshj.masarak.data.entity.TaskAttachmentEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskReviewEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskSubmissionEntity

class TaskRepository(private val taskDao: TaskDao) {
    fun getTasksForRecord(recordId: Long): Flow<List<TaskEntity>> =
        taskDao.getTasksForRecord(recordId)

    suspend fun insertTask(task: TaskEntity): Long =
        taskDao.insertTask(task)

    fun getSubmissionsForTask(taskId: Long): Flow<List<TaskSubmissionEntity>> =
        taskDao.getSubmissionsForTask(taskId)

    suspend fun insertSubmission(submission: TaskSubmissionEntity): Long =
        taskDao.insertSubmission(submission)

    fun getAttachmentsForSubmission(submissionId: Long): Flow<List<TaskAttachmentEntity>> =
        taskDao.getAttachmentsForSubmission(submissionId)

    suspend fun insertAttachment(attachment: TaskAttachmentEntity): Long =
        taskDao.insertAttachment(attachment)

    fun getReviewsForSubmission(submissionId: Long): Flow<List<TaskReviewEntity>> =
        taskDao.getReviewsForSubmission(submissionId)

    suspend fun insertReview(review: TaskReviewEntity): Long =
        taskDao.insertReview(review)
}
