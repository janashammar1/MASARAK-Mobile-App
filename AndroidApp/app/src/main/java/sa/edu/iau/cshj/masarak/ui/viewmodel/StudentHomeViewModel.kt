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

class StudentHomeViewModel(
    private val source: HomeDataSource,
    private val now: () -> Long = System::currentTimeMillis
) : ViewModel() {
    data class State(
        val userId: Long? = null, val isLoading: Boolean = false, val error: String? = null,
        val recordId: Long? = null, val confirmedMinutes: Long = 0, val targetMinutes: Long = 0,
        val currentWeek: Int = 0, val notStarted: Boolean = false, val ended: Boolean = false,
        val hasCurrentWeek: Boolean = false, val currentWeekConfirmed: Boolean = false,
        val returnedTasks: Int = 0, val evaluationWeek: Int? = null, val grade: Float? = null,
        val unreadNotifications: Int = 0, val unreadChats: Int = 0
    )
    private val mutableState = MutableStateFlow(State())
    val state = mutableState.asStateFlow()
    private var job: Job? = null

    fun load(user: UserEntity?) {
        job?.cancel()
        mutableState.value = State(userId = user?.userId, isLoading = user != null)
        if (user == null) return
        if (user.homeRole() != "student") {
            mutableState.value = State(userId = user.userId, error = "A student account is required.")
            return
        }
        job = viewModelScope.launch {
            try {
                // Recompute the week at date changes even when the database is idle.
                val clock = flow { while (true) { emit(now()); delay(60_000) } }
                combine(source.observe(user.userId, "student"), clock) { data, time ->
                    HomeLogic.student(data, user.userId, time)
                }.collect { mutableState.value = it }
            } catch (cancelled: CancellationException) { throw cancelled
            } catch (_: Exception) {
                mutableState.value = State(userId = user.userId, error = "Could not load your training. Please retry.")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass == StudentHomeViewModel::class.java)
                @Suppress("UNCHECKED_CAST")
                return StudentHomeViewModel(HomeRepository(DatabaseProvider.db)) as T
            }
        }
    }
}


