package sa.edu.iau.cshj.masarak.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weekly_evaluations",
    foreignKeys = [
        ForeignKey(
            entity = TrainingWeekEntity::class,
            parentColumns = ["weekId"],
            childColumns = ["weekId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["academicId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["weekId"], unique = true), Index("academicId")]
)
data class WeeklyEvaluationEntity(
    @PrimaryKey(autoGenerate = true)
    val evaluationId: Long = 0L,
    val weekId: Long,
    val academicId: Long,
    val gradeOutOf10: Float,
    val comment: String? = null,
    val gradedAt: Long
)
