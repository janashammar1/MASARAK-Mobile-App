package sa.edu.iau.cshj.masarak.data.repository

import kotlinx.coroutines.flow.Flow
import sa.edu.iau.cshj.masarak.data.dao.NotificationDao
import sa.edu.iau.cshj.masarak.data.entity.NotificationEntity

interface NotificationDataSource {
    fun getNotificationsForUser(recipientId: Long): Flow<List<NotificationEntity>>
    suspend fun insertNotification(notification: NotificationEntity): Long
    suspend fun markReadForUser(notificationId: Long, recipientId: Long, readAt: Long)
    suspend fun markAllReadForUser(recipientId: Long, readAt: Long)
    suspend fun getOwnedNotification(notificationId: Long, recipientId: Long): NotificationEntity?
}

class NotificationRepository(private val notificationDao: NotificationDao) : NotificationDataSource {
    override fun getNotificationsForUser(recipientId: Long) = notificationDao.getNotificationsForUser(recipientId)
    override suspend fun insertNotification(notification: NotificationEntity) = notificationDao.insertNotification(notification)
    override suspend fun markReadForUser(notificationId: Long, recipientId: Long, readAt: Long) =
        notificationDao.markReadForUser(notificationId, recipientId, readAt)
    override suspend fun markAllReadForUser(recipientId: Long, readAt: Long) = notificationDao.markAllReadForUser(recipientId, readAt)
    override suspend fun getOwnedNotification(notificationId: Long, recipientId: Long) =
        notificationDao.getOwnedNotification(notificationId, recipientId)

    // Keep the existing repository API for teammates; the feature uses the scoped methods above.
    suspend fun markAsRead(notificationId: Long, readAt: Long) = notificationDao.markAsRead(notificationId, readAt)
}
