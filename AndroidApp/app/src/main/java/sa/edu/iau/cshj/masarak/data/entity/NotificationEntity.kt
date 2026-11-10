package sa.edu.iau.cshj.masarak.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notifications",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["recipientId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["taskId"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = TrainingWeekEntity::class,
            parentColumns = ["weekId"],
            childColumns = ["weekId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = SupervisorLinkEntity::class,
            parentColumns = ["linkId"],
            childColumns = ["linkId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["conversationId"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("recipientId"),
        Index("taskId"),
        Index("weekId"),
        Index("linkId"),
        Index("conversationId")
    ]
)
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val notificationId: Long = 0L,
    val recipientId: Long,
    val eventType: String,
    val body: String,
    val createdAt: Long,
    val readAt: Long? = null,
    val taskId: Long? = null,
    val weekId: Long? = null,
    val linkId: Long? = null,
    val conversationId: Long? = null
)
