package sa.edu.iau.cshj.masarak.data.dao

import androidx.room.Dao
import androidx.room.Upsert
import androidx.room.Query
import sa.edu.iau.cshj.masarak.data.entity.TrainingRecordEntity
import sa.edu.iau.cshj.masarak.data.entity.TrainingWeekEntity
import sa.edu.iau.cshj.masarak.data.entity.WeeklyEvaluationEntity
import sa.edu.iau.cshj.masarak.data.entity.SupervisorLinkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {
    @Query("SELECT * FROM training_records WHERE studentId = :studentId")
    fun getTrainingRecordsForStudent(studentId: Long): Flow<List<TrainingRecordEntity>>

    @Upsert
    suspend fun insertTrainingRecord(record: TrainingRecordEntity): Long

    @Query("SELECT * FROM training_weeks WHERE recordId = :recordId")
    fun getTrainingWeeksForRecord(recordId: Long): Flow<List<TrainingWeekEntity>>

    @Upsert
    suspend fun insertTrainingWeek(week: TrainingWeekEntity): Long

    @Query("SELECT * FROM weekly_evaluations WHERE weekId = :weekId LIMIT 1")
    suspend fun getEvaluationForWeek(weekId: Long): WeeklyEvaluationEntity?

    @Upsert
    suspend fun insertWeeklyEvaluation(evaluation: WeeklyEvaluationEntity): Long

    @Query("SELECT * FROM supervisor_links WHERE recordId = :recordId")
    fun getSupervisorLinksForRecord(recordId: Long): Flow<List<SupervisorLinkEntity>>

    @Upsert
    suspend fun insertSupervisorLink(link: SupervisorLinkEntity): Long
}
