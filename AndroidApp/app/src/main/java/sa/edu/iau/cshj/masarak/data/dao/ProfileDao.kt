package sa.edu.iau.cshj.masarak.data.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class ProfileSupervisorRow(
    val linkId: Long,
    val recordId: Long,
    val supervisorId: Long,
    val supervisorName: String,
    val supervisorRole: String,
    val organization: String?,
    val status: String,
    val requestedAt: Long,
    val respondedAt: Long?
)

@Dao
interface ProfileDao {

    @Query(
        """
        SELECT
            links.linkId AS linkId,
            links.recordId AS recordId,
            links.supervisorId AS supervisorId,
            users.fullName AS supervisorName,
            links.supervisorRole AS supervisorRole,
            users.workOrganization AS organization,
            links.status AS status,
            links.requestedAt AS requestedAt,
            links.respondedAt AS respondedAt
        FROM supervisor_links AS links
        INNER JOIN users
            ON users.userId = links.supervisorId
        INNER JOIN training_records AS records
            ON records.recordId = links.recordId
        WHERE records.studentId = :studentId
          AND records.recordId = :recordId
        ORDER BY links.requestedAt DESC, links.linkId DESC
        """
    )
    fun observeSupervisorLinks(
        studentId: Long,
        recordId: Long
    ): Flow<List<ProfileSupervisorRow>>

    @Query(
        """
        SELECT COUNT(DISTINCT records.studentId)
        FROM supervisor_links AS links
        INNER JOIN training_records AS records
            ON records.recordId = links.recordId
        WHERE links.supervisorId = :supervisorId
          AND LOWER(TRIM(links.status)) = 'accepted'
        """
    )
    fun observeLinkedStudentsCount(
        supervisorId: Long
    ): Flow<Int>

    @Query(
        """
        SELECT COUNT(DISTINCT otherLinks.supervisorId)
        FROM supervisor_links AS myLinks
        INNER JOIN supervisor_links AS otherLinks
            ON otherLinks.recordId = myLinks.recordId
        INNER JOIN users AS otherUsers
            ON otherUsers.userId = otherLinks.supervisorId
        WHERE myLinks.supervisorId = :supervisorId
          AND LOWER(TRIM(myLinks.status)) = 'accepted'
          AND LOWER(TRIM(otherLinks.status)) = 'accepted'
          AND otherLinks.supervisorId != :supervisorId
          AND (
              (
                  :oppositeRole = 'academic'
                  AND LOWER(TRIM(otherLinks.supervisorRole))
                      IN ('academic', 'academic supervisor')
                  AND LOWER(TRIM(otherUsers.role))
                      IN ('academic', 'academic supervisor')
              )
              OR
              (
                  :oppositeRole = 'field'
                  AND LOWER(TRIM(otherLinks.supervisorRole))
                      IN ('field', 'field supervisor')
                  AND LOWER(TRIM(otherUsers.role))
                      IN ('field', 'field supervisor')
              )
          )
        """
    )
    fun observeLinkedSupervisorsCount(
        supervisorId: Long,
        oppositeRole: String
    ): Flow<Int>

    // يقبل الطلب المعلّق الخاص بالطالب وسجل التدريب المحددين.
    // يمنع قبول أكثر من مشرف من الدور نفسه في السجل.
    @Query(
        """
        UPDATE supervisor_links
        SET status = 'accepted',
            respondedAt = :respondedAt
        WHERE linkId = :linkId
          AND recordId = :recordId
          AND LOWER(TRIM(status)) = 'pending'

          AND EXISTS (
              SELECT 1
              FROM training_records AS records
              INNER JOIN users AS student
                  ON student.userId = records.studentId
              WHERE records.recordId = supervisor_links.recordId
                AND records.studentId = :studentId
                AND LOWER(TRIM(student.role)) = 'student'
          )

          AND EXISTS (
              SELECT 1
              FROM users AS supervisor
              WHERE supervisor.userId = supervisor_links.supervisorId
                AND (
                    (
                        LOWER(TRIM(supervisor_links.supervisorRole))
                            IN ('academic', 'academic supervisor')
                        AND LOWER(TRIM(supervisor.role))
                            IN ('academic', 'academic supervisor')
                    )
                    OR
                    (
                        LOWER(TRIM(supervisor_links.supervisorRole))
                            IN ('field', 'field supervisor')
                        AND LOWER(TRIM(supervisor.role))
                            IN ('field', 'field supervisor')
                    )
                )
          )

          AND NOT EXISTS (
              SELECT 1
              FROM supervisor_links AS accepted
              WHERE accepted.recordId = supervisor_links.recordId
                AND LOWER(TRIM(accepted.status)) = 'accepted'
                AND (
                    (
                        LOWER(TRIM(supervisor_links.supervisorRole))
                            IN ('academic', 'academic supervisor')
                        AND LOWER(TRIM(accepted.supervisorRole))
                            IN ('academic', 'academic supervisor')
                    )
                    OR
                    (
                        LOWER(TRIM(supervisor_links.supervisorRole))
                            IN ('field', 'field supervisor')
                        AND LOWER(TRIM(accepted.supervisorRole))
                            IN ('field', 'field supervisor')
                    )
                )
          )
        """
    )
    suspend fun acceptLinkRequest(
        studentId: Long,
        recordId: Long,
        linkId: Long,
        respondedAt: Long
    ): Int

    // يرفض الطلب فقط إذا كان ما زال معلقًا ويخص الطالب.
    @Query(
        """
        UPDATE supervisor_links
        SET status = 'declined',
            respondedAt = :respondedAt
        WHERE linkId = :linkId
          AND recordId = :recordId
          AND LOWER(TRIM(status)) = 'pending'

          AND EXISTS (
              SELECT 1
              FROM training_records AS records
              INNER JOIN users AS student
                  ON student.userId = records.studentId
              WHERE records.recordId = supervisor_links.recordId
                AND records.studentId = :studentId
                AND LOWER(TRIM(student.role)) = 'student'
          )
        """
    )
    suspend fun declineLinkRequest(
        studentId: Long,
        recordId: Long,
        linkId: Long,
        respondedAt: Long
    ): Int
}