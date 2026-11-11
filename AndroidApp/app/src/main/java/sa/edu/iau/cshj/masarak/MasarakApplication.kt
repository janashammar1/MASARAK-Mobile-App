package sa.edu.iau.cshj.masarak

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import sa.edu.iau.cshj.masarak.data.DatabaseProvider
import sa.edu.iau.cshj.masarak.data.entity.UserEntity
import sa.edu.iau.cshj.masarak.data.util.PasswordHasher

class MasarakApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DatabaseProvider.initialize(this)

        // Pre-create demo accounts synchronously with salted hashes to guarantee completion before login queries
        runBlocking(Dispatchers.IO) {
            try {
                val userDao = DatabaseProvider.db.userDao()
                val demoHash = PasswordHasher.hashPassword("password123")
                val demoUsers = listOf(
                    UserEntity(
                        email = "student@iau.edu.sa",
                        passwordHash = demoHash.hash,
                        passwordSalt = demoHash.salt,
                        fullName = "Student Demo",
                        role = "Student",
                        workOrganization = "IAU University"
                    ),
                    UserEntity(
                        email = "supervisor.field@iau.edu.sa",
                        passwordHash = demoHash.hash,
                        passwordSalt = demoHash.salt,
                        fullName = "Field Supervisor Demo",
                        role = "Field Supervisor",
                        workOrganization = "Field Partner Co."
                    ),
                    UserEntity(
                        email = "supervisor.academic@iau.edu.sa",
                        passwordHash = demoHash.hash,
                        passwordSalt = demoHash.salt,
                        fullName = "Academic Supervisor Demo",
                        role = "Academic Supervisor",
                        workOrganization = "IAU University"
                    )
                )

                for (user in demoUsers) {
                    if (userDao.getUserByEmail(user.email) == null) {
                        userDao.insertUser(user)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
