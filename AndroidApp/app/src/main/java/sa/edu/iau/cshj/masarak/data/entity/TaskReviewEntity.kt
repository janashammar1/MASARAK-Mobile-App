package sa.edu.iau.cshj.masarak.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "task_reviews",
    foreignKeys = [
        ForeignKey(
            entity = TaskSubmissionEntity::class,
            parentColumns = ["submissionId"],
            childColumns = ["submissionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["reviewerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("submissionId"), Index("reviewerId")]
)
data class TaskReviewEntity(
    @PrimaryKey(autoGenerate = true)
    val reviewId: Long = 0L,
    val submissionId: Long,
    val reviewerId: Long,
    val reviewStage: String,
    val decision: String,
    val comment: String? = null,
    val reviewedAt: Long
)
