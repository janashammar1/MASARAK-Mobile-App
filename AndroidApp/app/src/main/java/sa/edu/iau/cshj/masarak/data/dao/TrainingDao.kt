
package sa.edu.iau.cshj.masarak.data.dao

import androidx.room.Dao
import androidx.room.Upsert
import androidx.room.Query
import sa.edu.iau.cshj.masarak.data.entity.TrainingRecordEntity
import sa.edu.iau.cshj.masarak.data.entity.TrainingWeekEntity
import sa.edu.iau.cshj.masarak.data.entity.WeeklyEvaluationEntity
import sa.edu.iau.cshj.masarak.data.entity.SupervisorLinkEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskSubmissionEntity
import sa.edu.iau.cshj.masarak.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.ColumnInfo

data class SupervisorPersonRow(
    @ColumnInfo(name = "userId")
    val userId: Long,

    @ColumnInfo(name = "fullName")
    val fullName: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "recordId")
    val recordId: Long,

    @ColumnInfo(name = "trainingOrganization")
    val trainingOrganization: String,

    @ColumnInfo(name = "status")
    val linkStatus: String,

    @ColumnInfo(name = "tasksNeedingReview")
    val tasksNeedingReview: Int,

    @ColumnInfo(name = "weeksWaitingConfirmation")
    val weeksWaitingConfirmation: Int
)

// One row per supervisor linked to a training record
// (used by My Training & Weekly Evaluation)
data class RecordSupervisorRow(
    @ColumnInfo(name = "supervisorId")
    val supervisorId: Long,

    @ColumnInfo(name = "fullName")
    val fullName: String,

    @ColumnInfo(name = "supervisorRole")
    val supervisorRole: String,

    @ColumnInfo(name = "status")
    val status: String
)

@Dao
interface TrainingDao {

    @Query("""
        SELECT
            users.userId AS userId,
            users.fullName AS fullName,
            users.email AS email,
            training_records.recordId AS recordId,
            training_records.trainingOrganization AS trainingOrganization,
            supervisor_links.status AS status,

            (
                SELECT COUNT(*)
                FROM tasks
                INNER JOIN task_submissions
                    ON task_submissions.taskId = tasks.taskId
                WHERE tasks.recordId = training_records.recordId
                  AND task_submissions.submissionId = (
                      SELECT latest.submissionId
                      FROM task_submissions AS latest
                      WHERE latest.taskId = tasks.taskId
                      ORDER BY
                          latest.attemptNo DESC,
                          latest.submittedAt DESC,
                          latest.submissionId DESC
                      LIMIT 1
                  )
                  AND NOT EXISTS (
                      SELECT 1
                      FROM task_reviews
                      WHERE task_reviews.submissionId =
                            task_submissions.submissionId
                        AND task_reviews.reviewStage = :reviewStage
                  )
            ) AS tasksNeedingReview,

            (
                SELECT COUNT(*)
                FROM training_weeks
                WHERE training_weeks.recordId = training_records.recordId
                  AND training_weeks.confirmedAt IS NULL
            ) AS weeksWaitingConfirmation

        FROM supervisor_links

        INNER JOIN users AS supervisors
            ON supervisors.userId = supervisor_links.supervisorId

        INNER JOIN training_records
            ON training_records.recordId = supervisor_links.recordId

        INNER JOIN users
            ON users.userId = training_records.studentId

        WHERE supervisors.userId = :supervisorId
          AND supervisor_links.status = 'accepted'

        ORDER BY users.fullName COLLATE NOCASE
    """)
    fun getPeopleForSupervisor(
        supervisorId: Long,
        reviewStage: String
    ): Flow<List<SupervisorPersonRow>>

    @Query("SELECT * FROM training_records WHERE studentId = :studentId")
    fun getTrainingRecordsForStudent(studentId: Long): Flow<List<TrainingRecordEntity>>

    @Upsert
    suspend fun insertTrainingRecord(record: TrainingRecordEntity): Long

    @Query("SELECT * FROM training_weeks WHERE recordId = :recordId")
    fun getTrainingWeeksForRecord(recordId: Long): Flow<List<TrainingWeekEntity>>

    @Upsert
    suspend fun insertTrainingWeek(week: TrainingWeekEntity): Long

    @Query("SELECT * FROM weekly_evaluations WHERE weekId = :weekId LIMIT 1")
    suspend fun getEvaluationForWeek(weekId: Long): WeeklyEvaluationEntity?

    @Upsert
    suspend fun insertWeeklyEvaluation(evaluation: WeeklyEvaluationEntity): Long

    @Query("SELECT * FROM supervisor_links WHERE recordId = :recordId")
    fun getSupervisorLinksForRecord(recordId: Long): Flow<List<SupervisorLinkEntity>>

    @Upsert
    suspend fun insertSupervisorLink(link: SupervisorLinkEntity): Long


    // Return the latest request regardless of role or status.
// Used for duplicate prevention and demo-data setup.
    @Query("""
    SELECT * FROM supervisor_links
    WHERE recordId = :recordId
      AND supervisorId = :supervisorId
    ORDER BY requestedAt DESC, linkId DESC
    LIMIT 1
""")
    suspend fun getLinkRequest(
        recordId: Long,
        supervisorId: Long
    ): SupervisorLinkEntity?

    // Return only an accepted field supervisor link.
    @Query("""
    SELECT * FROM supervisor_links
    WHERE recordId = :recordId
      AND supervisorId = :supervisorId
      AND LOWER(TRIM(status)) = 'accepted'
      AND LOWER(TRIM(supervisorRole)) = 'field'
    ORDER BY respondedAt DESC, requestedAt DESC, linkId DESC
    LIMIT 1
""")
    suspend fun getAcceptedFieldLink(
        recordId: Long,
        supervisorId: Long
    ): SupervisorLinkEntity?


    // ---------- My Training & Weekly Evaluation screens ----------

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    fun observeUser(userId: Long): Flow<UserEntity?>

    @Query("SELECT * FROM training_records WHERE recordId = :recordId LIMIT 1")
    fun observeTrainingRecord(recordId: Long): Flow<TrainingRecordEntity?>

    @Query("""
        SELECT weekly_evaluations.* FROM weekly_evaluations
        INNER JOIN training_weeks
            ON training_weeks.weekId = weekly_evaluations.weekId
        WHERE training_weeks.recordId = :recordId
    """)
    fun observeEvaluationsForRecord(recordId: Long): Flow<List<WeeklyEvaluationEntity>>

    @Query("""
        SELECT task_submissions.* FROM task_submissions
        INNER JOIN tasks
            ON tasks.taskId = task_submissions.taskId
        WHERE tasks.recordId = :recordId
    """)
    fun observeSubmissionsForRecord(recordId: Long): Flow<List<TaskSubmissionEntity>>

    @Query("""
        SELECT users.userId AS supervisorId,
               users.fullName AS fullName,
               supervisor_links.supervisorRole AS supervisorRole,
               supervisor_links.status AS status
        FROM supervisor_links
        INNER JOIN users
            ON users.userId = supervisor_links.supervisorId
        WHERE supervisor_links.recordId = :recordId
        ORDER BY supervisor_links.requestedAt DESC
    """)
    fun observeSupervisorsForRecord(recordId: Long): Flow<List<RecordSupervisorRow>>
}
