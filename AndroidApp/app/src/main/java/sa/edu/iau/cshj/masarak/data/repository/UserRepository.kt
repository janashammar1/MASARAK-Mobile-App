package sa.edu.iau.cshj.masarak.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import sa.edu.iau.cshj.masarak.data.dao.UserDao
import sa.edu.iau.cshj.masarak.data.entity.UserEntity
import sa.edu.iau.cshj.masarak.data.util.PasswordHasher

enum class ChangePasswordResult {
    SUCCESS,
    USER_NOT_FOUND,
    INCORRECT_CURRENT_PASSWORD,
    INVALID_NEW_PASSWORD,
    SAME_PASSWORD,
    ACCOUNT_CHANGED
}

class UserRepository(
    private val userDao: UserDao
) {

    suspend fun getUserByEmail(email: String): UserEntity? =
        userDao.getUserByEmail(email)

    suspend fun getUserById(userId: Long): UserEntity? =
        userDao.getUserById(userId)

    fun observeUser(userId: Long): Flow<UserEntity?> =
        userDao.observeUser(userId)

    suspend fun insertUser(user: UserEntity): Long =
        userDao.insertUser(user)

    val allUsers: Flow<List<UserEntity>> =
        userDao.getAllUsers()

    suspend fun changePassword(
        userId: Long,
        currentPassword: String,
        newPassword: String
    ): ChangePasswordResult {

        if (userId <= 0L) {
            return ChangePasswordResult.USER_NOT_FOUND
        }

        if (newPassword.length < 8 || newPassword.isBlank()) {
            return ChangePasswordResult.INVALID_NEW_PASSWORD
        }

        val user = withContext(Dispatchers.IO) {
            userDao.getUserById(userId)
        } ?: return ChangePasswordResult.USER_NOT_FOUND

        val currentPasswordIsCorrect =
            withContext(Dispatchers.Default) {
                PasswordHasher.verifyPassword(
                    password = currentPassword,
                    storedHash = user.passwordHash,
                    storedSalt = user.passwordSalt
                )
            }

        if (!currentPasswordIsCorrect) {
            return ChangePasswordResult.INCORRECT_CURRENT_PASSWORD
        }

        if (newPassword == currentPassword) {
            return ChangePasswordResult.SAME_PASSWORD
        }

        val newHash = withContext(Dispatchers.Default) {
            PasswordHasher.hashPassword(newPassword)
        }

        val updatedRows = withContext(Dispatchers.IO) {
            userDao.updatePasswordIfUnchanged(
                userId = user.userId,
                expectedPasswordHash = user.passwordHash,
                expectedPasswordSalt = user.passwordSalt,
                newPasswordHash = newHash.hash,
                newPasswordSalt = newHash.salt
            )
        }

        return if (updatedRows == 1) {
            ChangePasswordResult.SUCCESS
        } else {
            ChangePasswordResult.ACCOUNT_CHANGED
        }
    }
}