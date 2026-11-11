package sa.edu.iau.cshj.masarak.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "supervisor_links",
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
            childColumns = ["supervisorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("recordId"), Index("supervisorId")]
)
data class SupervisorLinkEntity(
    @PrimaryKey(autoGenerate = true)
    val linkId: Long = 0L,
    val recordId: Long,
    val supervisorId: Long,
    val supervisorRole: String,
    val status: String,
    val requestedAt: Long,
    val respondedAt: Long? = null
)
