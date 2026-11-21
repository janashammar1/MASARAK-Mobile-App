
package sa.edu.iau.cshj.masarak

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import sa.edu.iau.cshj.masarak.data.DatabaseProvider
import sa.edu.iau.cshj.masarak.data.entity.UserEntity
import sa.edu.iau.cshj.masarak.data.util.PasswordHasher
import sa.edu.iau.cshj.masarak.data.entity.TrainingRecordEntity
import sa.edu.iau.cshj.masarak.data.entity.SupervisorLinkEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskSubmissionEntity

class MasarakApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DatabaseProvider.initialize(this)

        // Create demo accounts before login queries
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

                // Create a demo training record if it does not exist
                val student = userDao.getUserByEmail("student@iau.edu.sa")
                val academicSupervisor = userDao.getUserByEmail(
                    "supervisor.academic@iau.edu.sa"
                )

                if (student != null) {
                    val trainingDao = DatabaseProvider.db.trainingDao()
                    val taskDao = DatabaseProvider.db.taskDao()

                    var records = trainingDao
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

                        records = trainingDao
                            .getTrainingRecordsForStudent(student.userId)
                            .first()
                    }

                    val trainingRecord = records.firstOrNull()

                    if (trainingRecord != null) {

                        // Link the student to the academic supervisor
                        if (academicSupervisor != null) {
                            val existingLink = trainingDao.getLinkRequest(
                                recordId = trainingRecord.recordId,
                                supervisorId = academicSupervisor.userId
                            )

                            if (existingLink == null) {
                                trainingDao.insertSupervisorLink(
                                    SupervisorLinkEntity(
                                        recordId = trainingRecord.recordId,
                                        supervisorId = academicSupervisor.userId,
                                        supervisorRole = "Academic",
                                        status = "accepted",
                                        requestedAt = System.currentTimeMillis()
                                    )
                                )
                            } else if (existingLink.status != "accepted") {
                                trainingDao.insertSupervisorLink(
                                    existingLink.copy(status = "accepted")
                                )
                            }
                        }

                        // Create the demo task submission if it does not exist
                        val tasks = taskDao
                            .getTasksForRecord(trainingRecord.recordId)
                            .first()

                        var submissionExists = false

                        for (task in tasks) {
                            val submissions = taskDao
                                .getSubmissionsForTask(task.taskId)
                                .first()

                            if (submissions.any {
                                    it.title == "Build the login API"
                                }) {
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

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
