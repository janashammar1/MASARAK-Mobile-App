
package sa.edu.iau.cshj.masarak.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sa.edu.iau.cshj.masarak.data.entity.TaskAttachmentEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskReviewEntity
import sa.edu.iau.cshj.masarak.data.entity.TaskSubmissionEntity
import sa.edu.iau.cshj.masarak.data.repository.TaskRepository

class TaskViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val tasks: StateFlow<List<TaskEntity>> = _tasks.asStateFlow()

    private val _submissions =
        MutableStateFlow<Map<Long, List<TaskSubmissionEntity>>>(emptyMap())
    val submissions: StateFlow<Map<Long, List<TaskSubmissionEntity>>> =
        _submissions.asStateFlow()

    private val _reviews =
        MutableStateFlow<Map<Long, List<TaskReviewEntity>>>(emptyMap())
    val reviews: StateFlow<Map<Long, List<TaskReviewEntity>>> =
        _reviews.asStateFlow()

    private val _attachments =
        MutableStateFlow<Map<Long, List<TaskAttachmentEntity>>>(emptyMap())
    val attachments: StateFlow<Map<Long, List<TaskAttachmentEntity>>> =
        _attachments.asStateFlow()

    private val submissionJobs = mutableMapOf<Long, Job>()
    private val reviewJobs = mutableMapOf<Long, Job>()
    private val attachmentJobs = mutableMapOf<Long, Job>()

    fun loadTasks(recordId: Long) {
        viewModelScope.launch {
            taskRepository.getTasksForRecord(recordId).collect {
                _tasks.value = it
            }
        }
    }

    fun loadSubmissions(taskId: Long) {
        if (submissionJobs[taskId]?.isActive == true) return

        submissionJobs[taskId] = viewModelScope.launch {
            taskRepository.getSubmissionsForTask(taskId).collect {
                _submissions.value = _submissions.value + (taskId to it)
            }
        }
    }

    fun loadReviews(submissionId: Long) {
        if (reviewJobs[submissionId]?.isActive == true) return

        reviewJobs[submissionId] = viewModelScope.launch {
            taskRepository.getReviewsForSubmission(submissionId).collect {
                _reviews.value = _reviews.value + (submissionId to it)
            }
        }
    }

    fun loadAttachments(submissionId: Long) {
        if (attachmentJobs[submissionId]?.isActive == true) return

        attachmentJobs[submissionId] = viewModelScope.launch {
            taskRepository.getAttachmentsForSubmission(submissionId).collect {
                _attachments.value = _attachments.value + (submissionId to it)
            }
        }
    }

    suspend fun addTask(task: TaskEntity): Long =
        taskRepository.insertTask(task)

    suspend fun addSubmission(submission: TaskSubmissionEntity): Long =
        taskRepository.insertSubmission(submission)

    suspend fun addAttachment(attachment: TaskAttachmentEntity): Long =
        taskRepository.insertAttachment(attachment)

    suspend fun addReview(review: TaskReviewEntity): Long =
        taskRepository.insertReview(review)
}
