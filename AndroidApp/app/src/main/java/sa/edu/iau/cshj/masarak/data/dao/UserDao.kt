package sa.edu.iau.cshj.masarak.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import sa.edu.iau.cshj.masarak.data.entity.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    fun observeUser(userId: Long): Flow<UserEntity?>

    @Upsert
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query(
        """
        UPDATE users
        SET passwordHash = :newPasswordHash,
            passwordSalt = :newPasswordSalt
        WHERE userId = :userId
          AND passwordHash = :expectedPasswordHash
          AND (
              passwordSalt = :expectedPasswordSalt
              OR (
                  passwordSalt IS NULL
                  AND :expectedPasswordSalt IS NULL
              )
          )
        """
    )
    suspend fun updatePasswordIfUnchanged(
        userId: Long,
        expectedPasswordHash: String,
        expectedPasswordSalt: String?,
        newPasswordHash: String,
        newPasswordSalt: String
    ): Int
}