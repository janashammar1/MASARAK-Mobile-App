package sa.edu.iau.cshj.masarak.data.dao

import androidx.room.Dao
import androidx.room.Upsert
import androidx.room.Query
import sa.edu.iau.cshj.masarak.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: Long): UserEntity?

    @Upsert
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>
}
