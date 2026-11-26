package sa.edu.iau.cshj.masarak.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import sa.edu.iau.cshj.masarak.data.entity.ConversationEntity
import sa.edu.iau.cshj.masarak.data.entity.ConversationMemberEntity
import sa.edu.iau.cshj.masarak.data.entity.MessageAttachmentEntity
import sa.edu.iau.cshj.masarak.data.entity.MessageEntity
import sa.edu.iau.cshj.masarak.data.entity.UserEntity

@Dao
interface ChatDao {

    @Query("""
        SELECT c.* FROM conversations c
        INNER JOIN conversation_members cm
            ON c.conversationId = cm.conversationId
        WHERE cm.userId = :userId
    """)
    fun getConversationsForUser(
        userId: Long
    ): Flow<List<ConversationEntity>>

    @Upsert
    suspend fun insertConversation(
        conversation: ConversationEntity
    ): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversationMember(
        member: ConversationMemberEntity
    )

    @Query("""
        SELECT * FROM messages
        WHERE conversationId = :conversationId
        ORDER BY sentAt ASC
    """)
    fun getMessagesForConversation(
        conversationId: Long
    ): Flow<List<MessageEntity>>

    @Upsert
    suspend fun insertMessage(
        message: MessageEntity
    ): Long

    @Query("""
        SELECT * FROM message_attachments
        WHERE messageId = :messageId
    """)
    fun getAttachmentsForMessage(
        messageId: Long
    ): Flow<List<MessageAttachmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessageAttachment(
        attachment: MessageAttachmentEntity
    ): Long

    @Query("""
        UPDATE conversation_members
        SET lastReadAt = :readAt
        WHERE conversationId = :conversationId
            AND userId = :userId
    """)
    suspend fun updateLastReadAt(
        conversationId: Long,
        userId: Long,
        readAt: Long
    )

    @Query("""
        SELECT a.*
        FROM message_attachments a
        JOIN messages m ON m.messageId = a.messageId
        WHERE m.conversationId = :conversationId
        ORDER BY a.attachmentId
    """)
    fun observeConversationAttachments(
        conversationId: Long
    ): Flow<List<MessageAttachmentEntity>>

    @Query("""
        SELECT u.*
        FROM users u
        WHERE u.userId != :userId
            AND (
                EXISTS (
                    SELECT 1
                    FROM training_records r
                    JOIN supervisor_links l
                        ON l.recordId = r.recordId
                    WHERE LOWER(TRIM(l.status)) = 'accepted'
                        AND (
                            (
                                r.studentId = :userId
                                AND l.supervisorId = u.userId
                            )
                            OR (
                                l.supervisorId = :userId
                                AND r.studentId = u.userId
                            )
                        )
                )
                OR EXISTS (
                    SELECT 1
                    FROM supervisor_links a
                    JOIN supervisor_links b
                        ON a.recordId = b.recordId
                    WHERE a.supervisorId = :userId
                        AND b.supervisorId = u.userId
                        AND LOWER(TRIM(a.status)) = 'accepted'
                        AND LOWER(TRIM(b.status)) = 'accepted'
                )
            )
        ORDER BY u.fullName
    """)
    fun observeLinkedContacts(
        userId: Long
    ): Flow<List<UserEntity>>

    @Query("""
        SELECT c.conversationId
        FROM conversations c
        JOIN conversation_members a
            ON a.conversationId = c.conversationId
            AND a.userId = :userId
        JOIN conversation_members b
            ON b.conversationId = c.conversationId
            AND b.userId = :peerId
        WHERE (
            SELECT COUNT(*)
            FROM conversation_members x
            WHERE x.conversationId = c.conversationId
        ) = 2
        ORDER BY c.conversationId
        LIMIT 1
    """)
    suspend fun findDirectConversation(
        userId: Long,
        peerId: Long
    ): Long?

    @Query("""
        SELECT
            c.conversationId,
            u.userId AS peerId,
            u.fullName,
            u.role,
            (
                SELECT m.body
                FROM messages m
                WHERE m.conversationId = c.conversationId
                ORDER BY m.sentAt DESC, m.messageId DESC
                LIMIT 1
            ) AS lastMessage,
            (
                SELECT MAX(m.sentAt)
                FROM messages m
                WHERE m.conversationId = c.conversationId
            ) AS lastSentAt,
            (
                SELECT COUNT(*)
                FROM messages m
                WHERE m.conversationId = c.conversationId
                    AND m.senderUserId != :userId
                    AND (
                        a.lastReadAt IS NULL
                        OR m.sentAt > a.lastReadAt
                    )
            ) AS unreadCount
        FROM conversations c
        JOIN conversation_members a
            ON a.conversationId = c.conversationId
            AND a.userId = :userId
        JOIN conversation_members b
            ON b.conversationId = c.conversationId
            AND b.userId != :userId
        JOIN users u ON u.userId = b.userId
        WHERE (
            SELECT COUNT(*)
            FROM conversation_members x
            WHERE x.conversationId = c.conversationId
        ) = 2
        ORDER BY COALESCE(lastSentAt, 0) DESC, u.fullName
    """)
    fun observeInbox(
        userId: Long
    ): Flow<List<ChatInboxRow>>

    @Query("""
        SELECT EXISTS(
            SELECT 1
            FROM conversation_members
            WHERE conversationId = :conversationId
                AND userId = :userId
        )
    """)
    suspend fun isMember(
        conversationId: Long,
        userId: Long
    ): Boolean

    @Query("""
        UPDATE conversation_members
        SET lastReadAt = :readAt
        WHERE conversationId = :conversationId
            AND userId = :userId
            AND (
                lastReadAt IS NULL
                OR lastReadAt < :readAt
            )
    """)
    suspend fun markReadThrough(
        conversationId: Long,
        userId: Long,
        readAt: Long
    )
}

data class ChatInboxRow(
    val conversationId: Long,
    val peerId: Long,
    val fullName: String,
    val role: String,
    val lastMessage: String?,
    val lastSentAt: Long?,
    val unreadCount: Int
)