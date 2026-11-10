package sa.edu.iau.cshj.masarak.data.dao

import androidx.room.Dao
import androidx.room.Upsert
import androidx.room.Query
import sa.edu.iau.cshj.masarak.data.entity.ConversationEntity
import sa.edu.iau.cshj.masarak.data.entity.ConversationMemberEntity
import sa.edu.iau.cshj.masarak.data.entity.MessageEntity
import sa.edu.iau.cshj.masarak.data.entity.MessageAttachmentEntity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("""
        SELECT c.* FROM conversations c
        INNER JOIN conversation_members cm ON c.conversationId = cm.conversationId
        WHERE cm.userId = :userId
    """)
    fun getConversationsForUser(userId: Long): Flow<List<ConversationEntity>>

    @Upsert
    suspend fun insertConversation(conversation: ConversationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversationMember(member: ConversationMemberEntity)

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY sentAt ASC")
    fun getMessagesForConversation(conversationId: Long): Flow<List<MessageEntity>>

    @Upsert
    suspend fun insertMessage(message: MessageEntity): Long

    @Query("SELECT * FROM message_attachments WHERE messageId = :messageId")
    fun getAttachmentsForMessage(messageId: Long): Flow<List<MessageAttachmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessageAttachment(attachment: MessageAttachmentEntity): Long
}
