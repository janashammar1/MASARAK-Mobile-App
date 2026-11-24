
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

    // Tasks
    private val _tasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val tasks: StateFlow<List<TaskEntity>> = _tasks.asStateFlow()

    // Submissions grouped by task ID
    private val _submissions =
        MutableStateFlow<Map<Long, List<TaskSubmissionEntity>>>(emptyMap())

    val submissions: StateFlow<Map<Long, List<TaskSubmissionEntity>>> =
        _submissions.asStateFlow()

    // Selected submission
    private val _selectedSubmission =
        MutableStateFlow<TaskSubmissionEntity?>(null)

    val selectedSubmission: StateFlow<TaskSubmissionEntity?> =
        _selectedSubmission.asStateFlow()

    // Reviews grouped by submission ID
    private val _reviews =
        MutableStateFlow<Map<Long, List<TaskReviewEntity>>>(emptyMap())

    val reviews: StateFlow<Map<Long, List<TaskReviewEntity>>> =
        _reviews.asStateFlow()

    // Attachments grouped by submission ID
    private val _attachments =
        MutableStateFlow<Map<Long, List<TaskAttachmentEntity>>>(emptyMap())

    val attachments: StateFlow<Map<Long, List<TaskAttachmentEntity>>> =
        _attachments.asStateFlow()

    // Jobs
    private var tasksJob: Job? = null
    private var selectedSubmissionJob: Job? = null

    private val submissionJobs = mutableMapOf<Long, Job>()
    private val reviewJobs = mutableMapOf<Long, Job>()
    private val attachmentJobs = mutableMapOf<Long, Job>()

    // Load tasks for a training record
    fun loadTasks(recordId: Long) {
        tasksJob?.cancel()

        tasksJob = viewModelScope.launch {
            taskRepository.getTasksForRecord(recordId).collect { taskList ->
                _tasks.value = taskList
            }
        }
    }

    // Load submissions for a specific task
    fun loadSubmissions(taskId: Long) {
        if (submissionJobs[taskId]?.isActive == true) return

        submissionJobs[taskId] = viewModelScope.launch {
            taskRepository.getSubmissionsForTask(taskId).collect { submissionList ->
                _submissions.value =
                    _submissions.value + (taskId to submissionList)
            }
        }
    }

    // Load one submission by its ID
    fun loadSubmission(submissionId: Long) {
        selectedSubmissionJob?.cancel()

        selectedSubmissionJob = viewModelScope.launch {
            _selectedSubmission.value = null

            _selectedSubmission.value =
                taskRepository.getSubmissionById(submissionId)
        }
    }

    // Load reviews for a submission
    fun loadReviews(submissionId: Long) {
        if (reviewJobs[submissionId]?.isActive == true) return

        reviewJobs[submissionId] = viewModelScope.launch {
            taskRepository.getReviewsForSubmission(submissionId).collect { reviewList ->
                _reviews.value =
                    _reviews.value + (submissionId to reviewList)
            }
        }
    }

    // Load attachments for a submission
    fun loadAttachments(submissionId: Long) {
        if (attachmentJobs[submissionId]?.isActive == true) return

        attachmentJobs[submissionId] = viewModelScope.launch {
            taskRepository.getAttachmentsForSubmission(submissionId).collect {
                    attachmentList ->
                _attachments.value =
                    _attachments.value + (submissionId to attachmentList)
            }
        }
    }

    // Insert a task
    suspend fun addTask(task: TaskEntity): Long {
        return taskRepository.insertTask(task)
    }

    // Insert a submission
    suspend fun addSubmission(submission: TaskSubmissionEntity): Long {
        return taskRepository.insertSubmission(submission)
    }

    // Insert an attachment
    suspend fun addAttachment(attachment: TaskAttachmentEntity): Long {
        return taskRepository.insertAttachment(attachment)
    }

    // Insert a review
    suspend fun addReview(review: TaskReviewEntity): Long {
        return taskRepository.insertReview(review)
    }
}
