package sa.edu.iau.cshj.masarak.data.dao

import androidx.room.Dao
import androidx.room.Upsert
import androidx.room.Query
import sa.edu.iau.cshj.masarak.data.entity.TaskEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskSubmissionEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskAttachmentEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskReviewEntity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE recordId = :recordId")
    fun getTasksForRecord(recordId: Long): Flow<List<TaskEntity>>

    @Upsert
    suspend fun insertTask(task: TaskEntity): Long

    @Query("SELECT * FROM task_submissions WHERE taskId = :taskId")
    fun getSubmissionsForTask(taskId: Long): Flow<List<TaskSubmissionEntity>>

    @Upsert
    suspend fun insertSubmission(submission: TaskSubmissionEntity): Long

    @Query("SELECT * FROM task_attachments WHERE submissionId = :submissionId")
    fun getAttachmentsForSubmission(submissionId: Long): Flow<List<TaskAttachmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachment(attachment: TaskAttachmentEntity): Long

    @Query("SELECT * FROM task_reviews WHERE submissionId = :submissionId")
    fun getReviewsForSubmission(submissionId: Long): Flow<List<TaskReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: TaskReviewEntity): Long
}
