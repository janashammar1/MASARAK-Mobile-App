package sa.edu.iau.cshj.masarak

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import sa.edu.iau.cshj.masarak.data.DatabaseProvider
import sa.edu.iau.cshj.masarak.data.entity.TaskEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskSubmissionEntity
import sa.edu.iau.cshj.masarak.data.entity.TrainingRecordEntity
import sa.edu.iau.cshj.masarak.data.entity.UserEntity
import sa.edu.iau.cshj.masarak.data.util.PasswordHasher

class MasarakApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DatabaseProvider.initialize(this)

        runBlocking(Dispatchers.IO) {
            try {
                val db = DatabaseProvider.db
                val userDao = db.userDao()
                val trainingDao = db.trainingDao()
                val taskDao = db.taskDao()

                val demoHash =
                    PasswordHasher.hashPassword("password123")

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
                        email = "student2@iau.edu.sa",
                        passwordHash = demoHash.hash,
                        passwordSalt = demoHash.salt,
                        fullName = "Second Student Demo",
                        role = "Student",
                        workOrganization = "IAU University"
                    ),
                    UserEntity(
                        email = "student3@iau.edu.sa",
                        passwordHash = demoHash.hash,
                        passwordSalt = demoHash.salt,
                        fullName = "Student Without Training",
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
                        email = "supervisor.field2@iau.edu.sa",
                        passwordHash = demoHash.hash,
                        passwordSalt = demoHash.salt,
                        fullName = "Second Field Supervisor Demo",
                        role = "Field Supervisor",
                        workOrganization = "Second Field Partner Co."
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

                // Create missing accounts without overwriting existing ones.
                for (user in demoUsers) {
                    if (userDao.getUserByEmail(user.email) == null) {
                        userDao.insertUser(user)
                    }
                }

                // Only the first two students receive training records.
                // student3 intentionally has no training record.
                val studentsWithTraining = listOf(
                    "student@iau.edu.sa",
                    "student2@iau.edu.sa"
                )

                for (email in studentsWithTraining) {
                    val student =
                        userDao.getUserByEmail(email) ?: continue

                    val records = trainingDao
                        .getTrainingRecordsForStudent(student.userId)
                        .first()

                    if (records.isEmpty()) {
                        trainingDao.insertTrainingRecord(
                            TrainingRecordEntity(
                                studentId = student.userId,
                                trainingOrganization = "IAU University",
                                startDate = System.currentTimeMillis(),
                                targetMinutes = 240
                            )
                        )
                    }
                }

                // Keep the existing demo task for the first student.
                val student = userDao.getUserByEmail(
                    "student@iau.edu.sa"
                )

                if (student != null) {
                    val records = trainingDao
                        .getTrainingRecordsForStudent(student.userId)
                        .first()

                    val trainingRecord = records.firstOrNull()

                    if (trainingRecord != null) {
                        val tasks = taskDao
                            .getTasksForRecord(trainingRecord.recordId)
                            .first()

                        var submissionExists = false

                        for (task in tasks) {
                            val submissions = taskDao
                                .getSubmissionsForTask(task.taskId)
                                .first()

                            if (
                                submissions.any {
                                    it.title == "Build the login API"
                                }
                            ) {
                                submissionExists = true
                                break
                            }
                        }

                        if (!submissionExists) {
                            val taskId = taskDao.insertTask(
                                TaskEntity(
                                    recordId = trainingRecord.recordId
                                )
                            )

                            taskDao.insertSubmission(
                                TaskSubmissionEntity(
                                    taskId = taskId,
                                    attemptNo = 1,
                                    title = "Build the login API",
                                    taskDate = System.currentTimeMillis(),
                                    durationMinutes = 360,
                                    taskType = "Individual",
                                    description = "Built the login endpoint with token sign-in, then tested it with the mobile app.",
                                    challenges = "Handling expired tokens.",
                                    submittedAt = System.currentTimeMillis()
                                )
                            )
                        }
                    }
                }

                // Supervisor links are never accepted automatically.

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}