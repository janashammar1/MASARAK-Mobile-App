package sa.edu.iau.cshj.masarak.notifications

import sa.edu.iau.cshj.masarak.data.DatabaseProvider
import sa.edu.iau.cshj.masarak.data.entity.NotificationEntity
import sa.edu.iau.cshj.masarak.data.repository.NotificationDataSource
import sa.edu.iau.cshj.masarak.data.repository.NotificationRepository

/** Call once after a successful business operation, never from a composable body. */
class NotificationEvents(
    private val source: NotificationDataSource = NotificationRepository(DatabaseProvider.db.notificationDao())
) {
    suspend fun publish(
        recipientId: Long, event: NotificationEvent, body: String,
        taskId: Long? = null, weekId: Long? = null,
        linkId: Long? = null, conversationId: Long? = null
    ): Long {
        require(recipientId > 0L && body.isNotBlank())
        require(listOfNotNull(taskId, weekId, linkId, conversationId).all { it > 0L })
        // Persist regardless of permission. The active account's delivery session handles OS alerts.
        return source.insertNotification(NotificationEntity(
            recipientId = recipientId, eventType = event.name, body = body.trim(),
            createdAt = System.currentTimeMillis(), taskId = taskId, weekId = weekId,
            linkId = linkId, conversationId = conversationId
        ))
    }
}
