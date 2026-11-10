package sa.edu.iau.cshj.masarak.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "training_records",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("studentId")]
)
data class TrainingRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val recordId: Long = 0L,
    val studentId: Long,
    val trainingOrganization: String,
    val startDate: Long,
    val endDate: Long? = null,
    val targetMinutes: Int
)
