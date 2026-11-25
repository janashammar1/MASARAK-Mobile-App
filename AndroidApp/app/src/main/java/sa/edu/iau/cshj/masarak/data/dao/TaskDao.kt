
package sa.edu.iau.cshj.masarak.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import sa.edu.iau.cshj.masarak.data.entity.TaskAttachmentEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskReviewEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskSubmissionEntity

@Dao
interface TaskDao {

    // Tasks

    @Query("SELECT * FROM tasks WHERE recordId = :recordId")
    fun getTasksForRecord(recordId: Long): Flow<List<TaskEntity>>

    @Upsert
    suspend fun insertTask(task: TaskEntity): Long


    // Submissions

    @Query("SELECT * FROM task_submissions WHERE taskId = :taskId")
    fun getSubmissionsForTask(taskId: Long): Flow<List<TaskSubmissionEntity>>

    @Query(
        "SELECT * FROM task_submissions " +
                "WHERE submissionId = :submissionId LIMIT 1"
    )
    suspend fun getSubmissionById(
        submissionId: Long
    ): TaskSubmissionEntity?

    @Upsert
    suspend fun insertSubmission(
        submission: TaskSubmissionEntity
    ): Long


    // Attachments

    @Query(
        "SELECT * FROM task_attachments " +
                "WHERE submissionId = :submissionId"
    )
    fun getAttachmentsForSubmission(
        submissionId: Long
    ): Flow<List<TaskAttachmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachment(
        attachment: TaskAttachmentEntity
    ): Long

    @Query(
        "DELETE FROM task_attachments " +
                "WHERE attachmentId = :attachmentId"
    )
    suspend fun deleteAttachmentById(
        attachmentId: Long
    )


    // Reviews

    @Query(
        "SELECT * FROM task_reviews " +
                "WHERE submissionId = :submissionId"
    )
    fun getReviewsForSubmission(
        submissionId: Long
    ): Flow<List<TaskReviewEntity>>

    @Query(
        """
        SELECT * FROM task_reviews
        WHERE submissionId = :submissionId
        AND reviewerId = :reviewerId
        AND reviewStage = :reviewStage
        ORDER BY reviewedAt DESC
        LIMIT 1
        """
    )
    suspend fun getReviewByReviewerAndStage(
        submissionId: Long,
        reviewerId: Long,
        reviewStage: String
    ): TaskReviewEntity?

    // Verify that the reviewer is an accepted academic supervisor
    // linked to the training record of this submission.
    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM task_submissions
            INNER JOIN tasks
                ON tasks.taskId = task_submissions.taskId
            INNER JOIN supervisor_links
                ON supervisor_links.recordId = tasks.recordId
            WHERE task_submissions.submissionId = :submissionId
                AND supervisor_links.supervisorId = :reviewerId
                AND LOWER(TRIM(supervisor_links.supervisorRole)) = 'academic'
                AND LOWER(TRIM(supervisor_links.status)) = 'accepted'
        )
        """
    )
    suspend fun isAcceptedAcademicSupervisorForSubmission(
        submissionId: Long,
        reviewerId: Long
    ): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(
        review: TaskReviewEntity
    ): Long

    @Update
    suspend fun updateReview(
        review: TaskReviewEntity
    ): Int
}
