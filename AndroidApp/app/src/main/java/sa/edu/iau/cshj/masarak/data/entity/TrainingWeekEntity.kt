package sa.edu.iau.cshj.masarak.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "training_weeks",
    foreignKeys = [
        ForeignKey(
            entity = TrainingRecordEntity::class,
            parentColumns = ["recordId"],
            childColumns = ["recordId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["confirmedBy"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("recordId"), Index("confirmedBy")]
)
data class TrainingWeekEntity(
    @PrimaryKey(autoGenerate = true)
    val weekId: Long = 0L,
    val recordId: Long,
    val weekNo: Int,
    val daysAttended: Int,
    val durationMinutes: Int,
    val reportFileName: String,
    val reportFileRef: String,
    val submittedAt: Long,
    val confirmedBy: Long? = null,
    val confirmedAt: Long? = null
)
