package sa.edu.iau.cshj.masarak.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sa.edu.iau.cshj.masarak.data.entity.NotificationEntity
import sa.edu.iau.cshj.masarak.data.repository.NotificationRepository

class NotificationViewModel(private val notificationRepository: NotificationRepository) : ViewModel() {
    private val _notifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val notifications: StateFlow<List<NotificationEntity>> = _notifications.asStateFlow()

    fun loadNotifications(recipientId: Long) {
        viewModelScope.launch {
            notificationRepository.getNotificationsForUser(recipientId).collect { list ->
                _notifications.value = list
            }
        }
    }

    suspend fun sendNotification(notification: NotificationEntity): Long =
        notificationRepository.insertNotification(notification)

    fun markAsRead(notificationId: Long) {
        viewModelScope.launch {
            notificationRepository.markAsRead(notificationId, System.currentTimeMillis())
        }
    }
}
