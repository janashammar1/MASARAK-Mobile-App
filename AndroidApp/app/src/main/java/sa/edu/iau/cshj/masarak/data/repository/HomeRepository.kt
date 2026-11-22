package sa.edu.iau.cshj.masarak.data.repository

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sa.edu.iau.cshj.masarak.data.MasarakDatabase
import sa.edu.iau.cshj.masarak.data.entity.*

data class HomeSnapshot(
    val records: List<TrainingRecordEntity> = emptyList(),
    val students: List<UserEntity> = emptyList(),
    val weeks: List<TrainingWeekEntity> = emptyList(),
    val evaluations: List<WeeklyEvaluationEntity> = emptyList(),
    val tasks: List<TaskEntity> = emptyList(),
    val submissions: List<TaskSubmissionEntity> = emptyList(),
    val reviews: List<TaskReviewEntity> = emptyList(),
    val unreadNotifications: Int = 0,
    val unreadChats: Int = 0
)

interface HomeDataSource {
    fun observe(userId: Long, role: String): Flow<HomeSnapshot>
}

class HomeRepository(private val db: MasarakDatabase) : HomeDataSource {
    override fun observe(userId: Long, role: String): Flow<HomeSnapshot> {
        val dao = db.homeDao()
        return dao.changes().map {
            // A single consistent snapshot prevents mixing a new attempt with an older review.
            db.withTransaction {
                val records = dao.records(userId, role)
                val ids = records.map { it.recordId }
                HomeSnapshot(
                    records = records,
                    students = if (ids.isEmpty()) emptyList() else dao.students(records.map { it.studentId }.distinct()),
                    weeks = if (ids.isEmpty()) emptyList() else dao.weeks(ids),
                    evaluations = if (ids.isEmpty()) emptyList() else dao.evaluations(ids),
                    tasks = if (ids.isEmpty()) emptyList() else dao.tasks(ids),
                    submissions = if (ids.isEmpty()) emptyList() else dao.submissions(ids),
                    reviews = if (ids.isEmpty()) emptyList() else dao.reviews(ids),
                    unreadNotifications = dao.unreadNotifications(userId),
                    unreadChats = dao.unreadChats(userId)
                )
            }
        }
    }
}


