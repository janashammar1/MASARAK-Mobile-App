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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET readAt = :readAt WHERE notificationId = :notificationId")
    suspend fun markAsRead(notificationId: Long, readAt: Long)
}
