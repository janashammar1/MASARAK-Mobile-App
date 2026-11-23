package sa.edu.iau.cshj.masarak.data.repository

import kotlinx.coroutines.flow.Flow
import sa.edu.iau.cshj.masarak.data.dao.TrainingDao
import sa.edu.iau.cshj.masarak.data.entity.SupervisorLinkEntity
import sa.edu.iau.cshj.masarak.data.entity.TrainingRecordEntity
import sa.edu.iau.cshj.masarak.data.entity.TrainingWeekEntity
import sa.edu.iau.cshj.masarak.data.entity.WeeklyEvaluationEntity
import sa.edu.iau.cshj.masarak.data.dao.SupervisorPersonRow
import sa.edu.iau.cshj.masarak.data.dao.RecordSupervisorRow
import sa.edu.iau.cshj.masarak.data.entity.TaskSubmissionEntity
import sa.edu.iau.cshj.masarak.data.entity.UserEntity

class TrainingRepository(private val trainingDao: TrainingDao) {
    fun getPeopleForSupervisor(supervisorEmail: String): Flow<List<SupervisorPersonRow>> =
        trainingDao.getPeopleForSupervisor(supervisorEmail)
    fun getTrainingRecordsForStudent(studentId: Long): Flow<List<TrainingRecordEntity>> =
        trainingDao.getTrainingRecordsForStudent(studentId)

    suspend fun insertTrainingRecord(record: TrainingRecordEntity): Long =
        trainingDao.insertTrainingRecord(record)

    fun getTrainingWeeksForRecord(recordId: Long): Flow<List<TrainingWeekEntity>> =
        trainingDao.getTrainingWeeksForRecord(recordId)

    suspend fun insertTrainingWeek(week: TrainingWeekEntity): Long =
        trainingDao.insertTrainingWeek(week)

    suspend fun getEvaluationForWeek(weekId: Long): WeeklyEvaluationEntity? =
        trainingDao.getEvaluationForWeek(weekId)

    suspend fun insertWeeklyEvaluation(evaluation: WeeklyEvaluationEntity): Long =
        trainingDao.insertWeeklyEvaluation(evaluation)

    fun getSupervisorLinksForRecord(recordId: Long): Flow<List<SupervisorLinkEntity>> =
        trainingDao.getSupervisorLinksForRecord(recordId)

    suspend fun insertSupervisorLink(link: SupervisorLinkEntity): Long =
        trainingDao.insertSupervisorLink(link)

    // ---------- My Training & Weekly Evaluation screens ----------

    suspend fun getUserByEmail(email: String): UserEntity? =
        trainingDao.getUserByEmail(email)

    fun observeUser(userId: Long): Flow<UserEntity?> =
        trainingDao.observeUser(userId)

    fun observeTrainingRecord(recordId: Long): Flow<TrainingRecordEntity?> =
        trainingDao.observeTrainingRecord(recordId)

    fun observeEvaluationsForRecord(recordId: Long): Flow<List<WeeklyEvaluationEntity>> =
        trainingDao.observeEvaluationsForRecord(recordId)

    fun observeSubmissionsForRecord(recordId: Long): Flow<List<TaskSubmissionEntity>> =
        trainingDao.observeSubmissionsForRecord(recordId)

    fun observeSupervisorsForRecord(recordId: Long): Flow<List<RecordSupervisorRow>> =
        trainingDao.observeSupervisorsForRecord(recordId)
}
