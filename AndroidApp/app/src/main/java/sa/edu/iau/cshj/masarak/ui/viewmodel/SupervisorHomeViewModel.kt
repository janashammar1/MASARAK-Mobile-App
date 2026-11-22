package sa.edu.iau.cshj.masarak.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import sa.edu.iau.cshj.masarak.data.DatabaseProvider
import sa.edu.iau.cshj.masarak.data.entity.UserEntity
import sa.edu.iau.cshj.masarak.data.repository.HomeDataSource
import sa.edu.iau.cshj.masarak.data.repository.HomeRepository

class SupervisorHomeViewModel(private val source: HomeDataSource) : ViewModel() {
    data class FollowUp(val name: String, val recordId: Long, val count: Int)
    data class Activity(val name: String, val recordId: Long, val details: String, val at: Long)
    data class State(
        val userId: Long? = null, val role: String? = null,
        val isLoading: Boolean = false, val error: String? = null,
        val peopleCount: Int = 0, val pendingTasks: Int = 0, val pendingWeeks: Int = 0,
        val followUps: List<FollowUp> = emptyList(), val activities: List<Activity> = emptyList(),
        val unreadNotifications: Int = 0, val unreadChats: Int = 0
    )
    private val mutableState = MutableStateFlow(State())
    val state = mutableState.asStateFlow()
    private var job: Job? = null

    fun load(user: UserEntity?, isAcademic: Boolean) {
        job?.cancel()
        val role = if (isAcademic) "academic" else "field"
        mutableState.value = State(userId = user?.userId, role = role, isLoading = user != null)
        if (user == null) return
        if (user.homeRole() != role) {
            mutableState.value = State(userId = user.userId, role = role, error = "This account does not match the supervisor role.")
            return
        }
        job = viewModelScope.launch {
            try {
                source.observe(user.userId, role).collect {
                    mutableState.value = HomeLogic.supervisor(it, user.userId, role)
                }
            } catch (cancelled: CancellationException) { throw cancelled
            } catch (_: Exception) {
                mutableState.value = State(userId = user.userId, role = role, error = "Could not load your supervisor home. Please retry.")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass == SupervisorHomeViewModel::class.java)
                @Suppress("UNCHECKED_CAST")
                return SupervisorHomeViewModel(HomeRepository(DatabaseProvider.db)) as T
            }
        }
    }
}
