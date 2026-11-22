package sa.edu.iau.cshj.masarak.data.dao


import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import sa.edu.iau.cshj.masarak.data.entity.*

/** Read-only Home queries. No schema change or mutation of teammates' data. */
@Dao
interface HomeDao {
    // Room invalidates this Flow for updates as well as inserts/deletes, even if counts stay equal.
    @Query("""SELECT
        (SELECT COUNT(*) FROM training_records) + (SELECT COUNT(*) FROM supervisor_links) +
        (SELECT COUNT(*) FROM training_weeks) + (SELECT COUNT(*) FROM weekly_evaluations) +
        (SELECT COUNT(*) FROM tasks) + (SELECT COUNT(*) FROM task_submissions) +
        (SELECT COUNT(*) FROM task_reviews) + (SELECT COUNT(*) FROM users) +
        (SELECT COUNT(*) FROM notifications) + (SELECT COUNT(*) FROM messages) +
        (SELECT COUNT(*) FROM conversation_members)""")
    fun changes(): Flow<Long>

    @Query("""SELECT * FROM training_records WHERE
        (:role = 'student' AND studentId = :userId) OR
        (:role IN ('academic', 'field') AND recordId IN (
            SELECT recordId FROM supervisor_links WHERE supervisorId = :userId
            AND LOWER(TRIM(status)) = 'accepted' AND LOWER(TRIM(supervisorRole)) = :role))""")
    suspend fun records(userId: Long, role: String): List<TrainingRecordEntity>

    @Query("SELECT * FROM users WHERE userId IN (:ids)")
    suspend fun students(ids: List<Long>): List<UserEntity>

    @Query("SELECT * FROM training_weeks WHERE recordId IN (:ids)")
    suspend fun weeks(ids: List<Long>): List<TrainingWeekEntity>

    @Query("""SELECT * FROM weekly_evaluations WHERE weekId IN
        (SELECT weekId FROM training_weeks WHERE recordId IN (:ids))""")
    suspend fun evaluations(ids: List<Long>): List<WeeklyEvaluationEntity>

    @Query("""SELECT s.* FROM task_submissions s JOIN tasks t ON t.taskId = s.taskId
        WHERE t.recordId IN (:ids)""")
    suspend fun submissions(ids: List<Long>): List<TaskSubmissionEntity>

    @Query("SELECT * FROM tasks WHERE recordId IN (:ids)")
    suspend fun tasks(ids: List<Long>): List<TaskEntity>

    @Query("""SELECT r.* FROM task_reviews r JOIN task_submissions s ON s.submissionId = r.submissionId
        JOIN tasks t ON t.taskId = s.taskId WHERE t.recordId IN (:ids)""")
    suspend fun reviews(ids: List<Long>): List<TaskReviewEntity>

    @Query("SELECT COUNT(*) FROM notifications WHERE recipientId = :userId AND readAt IS NULL")
    suspend fun unreadNotifications(userId: Long): Int

    @Query("""SELECT COUNT(*) FROM messages m JOIN conversation_members cm
        ON cm.conversationId = m.conversationId WHERE cm.userId = :userId
        AND m.senderUserId != :userId AND (cm.lastReadAt IS NULL OR m.sentAt > cm.lastReadAt)""")
    suspend fun unreadChats(userId: Long): Int
}

