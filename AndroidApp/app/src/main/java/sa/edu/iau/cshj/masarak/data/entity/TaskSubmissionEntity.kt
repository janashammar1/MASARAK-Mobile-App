package sa.edu.iau.cshj.masarak.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "task_submissions",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["taskId"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("taskId")]
)
data class TaskSubmissionEntity(
    @PrimaryKey(autoGenerate = true)
    val submissionId: Long = 0L,
    val taskId: Long,
    val attemptNo: Int,
    val title: String,
    val taskDate: Long,
    val durationMinutes: Int,
    val taskType: String,
    val description: String,
    val challenges: String? = null,
    val submittedAt: Long
)
