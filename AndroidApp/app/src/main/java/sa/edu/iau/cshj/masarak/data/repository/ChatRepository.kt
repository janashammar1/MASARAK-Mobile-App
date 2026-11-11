package sa.edu.iau.cshj.masarak.data.repository

import kotlinx.coroutines.flow.Flow
import sa.edu.iau.cshj.masarak.data.dao.ChatDao
import sa.edu.iau.cshj.masarak.data.entity.ConversationEntity
import sa.edu.iau.cshj.masarak.data.entity.ConversationMemberEntity
import sa.edu.iau.cshj.masarak.data.entity.MessageAttachmentEntity
import sa.edu.iau.cshj.masarak.data.entity.MessageEntity

class ChatRepository(private val chatDao: ChatDao) {
    fun getConversationsForUser(userId: Long): Flow<List<ConversationEntity>> =
        chatDao.getConversationsForUser(userId)

    suspend fun insertConversation(conversation: ConversationEntity): Long =
        chatDao.insertConversation(conversation)

    suspend fun insertConversationMember(member: ConversationMemberEntity) =
        chatDao.insertConversationMember(member)

    fun getMessagesForConversation(conversationId: Long): Flow<List<MessageEntity>> =
        chatDao.getMessagesForConversation(conversationId)

    suspend fun insertMessage(message: MessageEntity): Long =
        chatDao.insertMessage(message)

    fun getAttachmentsForMessage(messageId: Long): Flow<List<MessageAttachmentEntity>> =
        chatDao.getAttachmentsForMessage(messageId)

    suspend fun insertMessageAttachment(attachment: MessageAttachmentEntity): Long =
        chatDao.insertMessageAttachment(attachment)
}
