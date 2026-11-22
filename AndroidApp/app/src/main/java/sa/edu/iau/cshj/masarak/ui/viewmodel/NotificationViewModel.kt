package sa.edu.iau.cshj.masarak.ui.viewmodel

import java.util.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import sa.edu.iau.cshj.masarak.data.DatabaseProvider
import sa.edu.iau.cshj.masarak.data.entity.NotificationEntity
import sa.edu.iau.cshj.masarak.data.entity.UserEntity
import sa.edu.iau.cshj.masarak.data.repository.NotificationDataSource
import sa.edu.iau.cshj.masarak.data.repository.NotificationRepository
import sa.edu.iau.cshj.masarak.notifications.NotificationCategory
import sa.edu.iau.cshj.masarak.notifications.NotificationEvent

class NotificationViewModel(private val notificationRepository: NotificationDataSource) : ViewModel() {
    data class State(
        val recipientId: Long? = null, val role: String? = null,
        val isLoading: Boolean = false, val error: String? = null, val actionError: String? = null,
        val notifications: List<NotificationEntity> = emptyList(),
        val category: NotificationCategory? = null, val unreadOnly: Boolean = false,
        val isSaving: Boolean = false
    ) {
        val unreadCount get() = notifications.count { it.readAt == null }
        val visible get() = notifications.filter {
            (!unreadOnly || it.readAt == null) && (category == null || NotificationEvent.from(it.eventType).category == category)
        }
    }
    private val mutableState = MutableStateFlow(State())
    val state = mutableState.asStateFlow()
    private val mutableNotifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val notifications = mutableNotifications.asStateFlow()
    private var loadJob: Job? = null
    private var actionJob: Job? = null

    fun load(user: UserEntity?, role: String) {
        reset()
        if (user == null) return
        val expected = role.trim().lowercase(Locale.ROOT)
        mutableState.value = State(recipientId = user.userId, role = expected, isLoading = true)
        val actual = user.role.trim().lowercase(Locale.ROOT).replace(' ', '_').removeSuffix("_supervisor")
        if (expected !in setOf("student", "academic", "field") || expected != actual) {
            mutableState.value = State(recipientId = user.userId, role = expected, error = "This account does not match the selected role.")
            return
        }
        observe(user.userId, expected)
    }

    // Compatibility with the existing ViewModel API.
    fun loadNotifications(recipientId: Long) {
        reset()
        mutableState.value = State(recipientId = recipientId, isLoading = true)
        observe(recipientId, null)
    }
    private fun reset() {
        loadJob?.cancel(); actionJob?.cancel()
        mutableNotifications.value = emptyList()
        mutableState.value = State()
    }
    private fun observe(id: Long, role: String?) {
        loadJob = viewModelScope.launch {
            try {
                notificationRepository.getNotificationsForUser(id).collect { values ->
                    val owned = values.filter { it.recipientId == id }.sortedWith(
                        compareByDescending<NotificationEntity> { it.createdAt }.thenByDescending { it.notificationId })
                    mutableNotifications.value = owned
                    mutableState.value = mutableState.value.copy(recipientId = id, role = role, notifications = owned, isLoading = false, error = null)
                }
            } catch (cancelled: CancellationException) { throw cancelled
            } catch (_: Exception) {
                mutableNotifications.value = emptyList()
                mutableState.value = State(recipientId = id, role = role, error = "Could not load notifications. Please retry.")
            }
        }
    }
    fun selectCategory(category: NotificationCategory?) { mutableState.value = mutableState.value.copy(category = category) }
    fun showUnread(only: Boolean) { mutableState.value = mutableState.value.copy(unreadOnly = only) }
    suspend fun sendNotification(notification: NotificationEntity): Long = notificationRepository.insertNotification(notification)

    fun markAsRead(notificationId: Long) {
        val current = mutableState.value
        val id = current.recipientId ?: return
        if (current.notifications.none { it.notificationId == notificationId && it.readAt == null }) return
        save(id) { notificationRepository.markReadForUser(notificationId, id, System.currentTimeMillis()) }
    }
    fun markAllAsRead() {
        val current = mutableState.value
        val id = current.recipientId ?: return
        if (current.unreadCount == 0) return
        save(id) { notificationRepository.markAllReadForUser(id, System.currentTimeMillis()) }
    }
    private fun save(id: Long, block: suspend () -> Unit) {
        if (mutableState.value.isSaving || mutableState.value.error != null) return
        mutableState.value = mutableState.value.copy(isSaving = true, actionError = null)
        actionJob = viewModelScope.launch {
            try { block() }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) {
                if (mutableState.value.recipientId == id) mutableState.value = mutableState.value.copy(actionError = "Could not update notifications. Please try again.")
            } finally {
                if (mutableState.value.recipientId == id) mutableState.value = mutableState.value.copy(isSaving = false)
            }
        }
    }
    companion object {
        val Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass == NotificationViewModel::class.java)
                @Suppress("UNCHECKED_CAST")
                return NotificationViewModel(NotificationRepository(DatabaseProvider.db.notificationDao())) as T
            }
        }
    }
}
