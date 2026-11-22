package sa.edu.iau.cshj.masarak.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import sa.edu.iau.cshj.masarak.data.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE recipientId = :recipientId ORDER BY createdAt DESC")
    fun getNotificationsForUser(recipientId: Long): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE notificationId = :notificationId AND recipientId = :recipientId LIMIT 1")
    suspend fun getOwnedNotification(notificationId: Long, recipientId: Long): NotificationEntity?

    @Query("UPDATE notifications SET readAt = :readAt WHERE notificationId = :notificationId AND recipientId = :recipientId AND readAt IS NULL")
    suspend fun markReadForUser(notificationId: Long, recipientId: Long, readAt: Long)

    @Query("UPDATE notifications SET readAt = :readAt WHERE recipientId = :recipientId AND readAt IS NULL")
    suspend fun markAllReadForUser(recipientId: Long, readAt: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET readAt = :readAt WHERE notificationId = :notificationId")
    suspend fun markAsRead(notificationId: Long, readAt: Long)
}
