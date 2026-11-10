package sa.edu.iau.cshj.masarak.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sa.edu.iau.cshj.masarak.data.DatabaseProvider
import sa.edu.iau.cshj.masarak.data.repository.*

class MasarakViewModelFactory : ViewModelProvider.Factory {
    private val db = DatabaseProvider.db

    private val userRepository = UserRepository(db.userDao())
    private val trainingRepository = TrainingRepository(db.trainingDao())
    private val taskRepository = TaskRepository(db.taskDao())
    private val chatRepository = ChatRepository(db.chatDao())
    private val notificationRepository = NotificationRepository(db.notificationDao())

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(TrainingViewModel::class.java) -> {
                TrainingViewModel(trainingRepository) as T
            }
            modelClass.isAssignableFrom(TaskViewModel::class.java) -> {
                TaskViewModel(taskRepository) as T
            }
            modelClass.isAssignableFrom(ChatViewModel::class.java) -> {
                ChatViewModel(chatRepository) as T
            }
            modelClass.isAssignableFrom(NotificationViewModel::class.java) -> {
                NotificationViewModel(notificationRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
