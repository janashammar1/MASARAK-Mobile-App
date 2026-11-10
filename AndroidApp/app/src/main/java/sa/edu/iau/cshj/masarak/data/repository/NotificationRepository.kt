package sa.edu.iau.cshj.masarak.data.repository

import kotlinx.coroutines.flow.Flow
import sa.edu.iau.cshj.masarak.data.dao.NotificationDao
import sa.edu.iau.cshj.masarak.data.entity.NotificationEntity

class NotificationRepository(private val notificationDao: NotificationDao) {
    fun getNotificationsForUser(recipientId: Long): Flow<List<NotificationEntity>> =
        notificationDao.getNotificationsForUser(recipientId)

    suspend fun insertNotification(notification: NotificationEntity): Long =
        notificationDao.insertNotification(notification)

    suspend fun markAsRead(notificationId: Long, readAt: Long) =
        notificationDao.markAsRead(notificationId, readAt)
}
