package sa.edu.iau.cshj.masarak.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import sa.edu.iau.cshj.masarak.data.dao.ProfileDao
import sa.edu.iau.cshj.masarak.data.dao.ProfileSupervisorRow
import sa.edu.iau.cshj.masarak.data.entity.TrainingRecordEntity
import sa.edu.iau.cshj.masarak.data.entity.UserEntity

enum class ProfileLinkResult {
    SUCCESS,
    INVALID_REQUEST,
    REQUEST_NOT_UPDATED
}

class ProfileRepository(
    private val profileDao: ProfileDao,
    private val userRepository: UserRepository,
    private val trainingRepository: TrainingRepository
) {

    fun observeUser(userId: Long): Flow<UserEntity?> =
        userRepository.observeUser(userId)

    fun observeTrainingRecords(
        studentId: Long
    ): Flow<List<TrainingRecordEntity>> =
        trainingRepository.getTrainingRecordsForStudent(studentId)

    fun observeSupervisorLinks(
        studentId: Long,
        recordId: Long
    ): Flow<List<ProfileSupervisorRow>> =
        profileDao.observeSupervisorLinks(
            studentId = studentId,
            recordId = recordId
        )

    fun observeLinkedStudentsCount(
        supervisorId: Long
    ): Flow<Int> =
        profileDao.observeLinkedStudentsCount(supervisorId)

    fun observeLinkedSupervisorsCount(
        supervisorId: Long,
        oppositeRole: String
    ): Flow<Int> =
        profileDao.observeLinkedSupervisorsCount(
            supervisorId = supervisorId,
            oppositeRole = oppositeRole
        )

    suspend fun acceptLinkRequest(
        studentId: Long,
        recordId: Long,
        linkId: Long
    ): ProfileLinkResult {
        if (studentId <= 0L || recordId <= 0L || linkId <= 0L) {
            return ProfileLinkResult.INVALID_REQUEST
        }

        val updatedRows = withContext(Dispatchers.IO) {
            profileDao.acceptLinkRequest(
                studentId = studentId,
                recordId = recordId,
                linkId = linkId,
                respondedAt = System.currentTimeMillis()
            )
        }

        return if (updatedRows == 1) {
            ProfileLinkResult.SUCCESS
        } else {
            ProfileLinkResult.REQUEST_NOT_UPDATED
        }
    }

    suspend fun declineLinkRequest(
        studentId: Long,
        recordId: Long,
        linkId: Long
    ): ProfileLinkResult {
        if (studentId <= 0L || recordId <= 0L || linkId <= 0L) {
            return ProfileLinkResult.INVALID_REQUEST
        }

        val updatedRows = withContext(Dispatchers.IO) {
            profileDao.declineLinkRequest(
                studentId = studentId,
                recordId = recordId,
                linkId = linkId,
                respondedAt = System.currentTimeMillis()
            )
        }

        return if (updatedRows == 1) {
            ProfileLinkResult.SUCCESS
        } else {
            ProfileLinkResult.REQUEST_NOT_UPDATED
        }
    }

    suspend fun changePassword(
        userId: Long,
        currentPassword: String,
        newPassword: String
    ): ChangePasswordResult =
        userRepository.changePassword(
            userId = userId,
            currentPassword = currentPassword,
            newPassword = newPassword
        )
}