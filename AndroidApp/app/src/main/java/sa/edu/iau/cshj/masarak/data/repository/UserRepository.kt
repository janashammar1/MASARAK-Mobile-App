package sa.edu.iau.cshj.masarak.data.repository

import kotlinx.coroutines.flow.Flow
import sa.edu.iau.cshj.masarak.data.dao.UserDao
import sa.edu.iau.cshj.masarak.data.entity.UserEntity

class UserRepository(private val userDao: UserDao) {
    suspend fun getUserByEmail(email: String): UserEntity? = userDao.getUserByEmail(email)
    suspend fun getUserById(userId: Long): UserEntity? = userDao.getUserById(userId)
    suspend fun insertUser(user: UserEntity): Long = userDao.insertUser(user)
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()
}
