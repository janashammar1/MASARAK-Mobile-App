package sa.edu.iau.cshj.masarak.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "task_attachments",
    foreignKeys = [
        ForeignKey(
            entity = TaskSubmissionEntity::class,
            parentColumns = ["submissionId"],
            childColumns = ["submissionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("submissionId")]
)
data class TaskAttachmentEntity(
    @PrimaryKey(autoGenerate = true)
    val attachmentId: Long = 0L,
    val submissionId: Long,
    val fileName: String,
    val mimeType: String,
    val sizeBytes: Long,
    val fileRef: String
)
