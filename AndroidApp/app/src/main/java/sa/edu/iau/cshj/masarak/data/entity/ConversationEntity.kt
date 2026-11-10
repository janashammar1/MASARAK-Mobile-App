package sa.edu.iau.cshj.masarak.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true)
    val conversationId: Long = 0L,
    val createdAt: Long
)
