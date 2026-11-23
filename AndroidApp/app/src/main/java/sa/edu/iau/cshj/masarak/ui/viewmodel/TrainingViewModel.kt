package sa.edu.iau.cshj.masarak.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import sa.edu.iau.cshj.masarak.data.entity.TrainingRecordEntity
import sa.edu.iau.cshj.masarak.data.entity.TrainingWeekEntity
import sa.edu.iau.cshj.masarak.data.entity.UserEntity
import sa.edu.iau.cshj.masarak.data.entity.WeeklyEvaluationEntity
import sa.edu.iau.cshj.masarak.data.repository.TrainingRepository
import sa.edu.iau.cshj.masarak.data.dao.SupervisorPersonRow
import sa.edu.iau.cshj.masarak.notifications.NotificationEvent
import sa.edu.iau.cshj.masarak.notifications.NotificationEvents


class TrainingViewModel(
    private val trainingRepository: TrainingRepository,
    private val now: () -> Long = System::currentTimeMillis
) : ViewModel() {
    private val _people = MutableStateFlow<List<SupervisorPersonRow>>(emptyList())
    val people: StateFlow<List<SupervisorPersonRow>> = _people.asStateFlow()
    private val _trainingRecords = MutableStateFlow<List<TrainingRecordEntity>>(emptyList())
    val trainingRecords: StateFlow<List<TrainingRecordEntity>> = _trainingRecords.asStateFlow()

    private val _trainingWeeks = MutableStateFlow<List<TrainingWeekEntity>>(emptyList())
    val trainingWeeks: StateFlow<List<TrainingWeekEntity>> = _trainingWeeks.asStateFlow()

    fun loadTrainingRecords(studentId: Long) {
        viewModelScope.launch {
            trainingRepository.getTrainingRecordsForStudent(studentId).collect { records ->
                _trainingRecords.value = records
            }
        }
    }

    fun loadTrainingWeeks(recordId: Long) {
        viewModelScope.launch {
            trainingRepository.getTrainingWeeksForRecord(recordId).collect { weeks ->
                _trainingWeeks.value = weeks
            }
        }
    }

    fun loadPeopleForSupervisor(supervisorEmail: String) {
        viewModelScope.launch {
            trainingRepository.getPeopleForSupervisor(supervisorEmail).collect { result ->
                _people.value = result
            }
        }
    }

    suspend fun addTrainingRecord(record: TrainingRecordEntity): Long =
        trainingRepository.insertTrainingRecord(record)

    suspend fun addTrainingWeek(week: TrainingWeekEntity): Long =
        trainingRepository.insertTrainingWeek(week)

    suspend fun addWeeklyEvaluation(evaluation: WeeklyEvaluationEntity): Long =
        trainingRepository.insertWeeklyEvaluation(evaluation)

    // =====================================================================
    // My Training · Weekly Evaluation · Academic Weekly Evaluation
    // =====================================================================

    private val _myTraining = MutableStateFlow(MyTrainingUiState())
    val myTraining: StateFlow<MyTrainingUiState> = _myTraining.asStateFlow()

    private val _weeklyEvaluation = MutableStateFlow(WeeklyEvaluationUiState())
    val weeklyEvaluation: StateFlow<WeeklyEvaluationUiState> = _weeklyEvaluation.asStateFlow()

    private val _academicEvaluation = MutableStateFlow(AcademicEvaluationUiState())
    val academicEvaluation: StateFlow<AcademicEvaluationUiState> = _academicEvaluation.asStateFlow()

    // The latest data read from Room (used when saving)
    private var snapshot = TrainingSnapshot()
    private var loadJob: Job? = null

    /** Combines every table the screens need into one snapshot that updates by itself. */
    private fun observeRecord(recordId: Long, student: UserEntity?): Flow<TrainingSnapshot> = combine(
        trainingRepository.observeTrainingRecord(recordId),
        trainingRepository.getTrainingWeeksForRecord(recordId),
        trainingRepository.observeEvaluationsForRecord(recordId),
        trainingRepository.observeSubmissionsForRecord(recordId),
        trainingRepository.observeSupervisorsForRecord(recordId)
    ) { record, weeks, evaluations, submissions, supervisors ->
        TrainingSnapshot(student, record, weeks, evaluations, submissions, supervisors)
    }

    /** Student screens: find the student, pick the active training record, then watch it. */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadStudentTraining(studentEmail: String = DEMO_STUDENT_EMAIL) {
        loadJob?.cancel()
        _myTraining.value = MyTrainingUiState(isLoading = true)
        _weeklyEvaluation.value = WeeklyEvaluationUiState(isLoading = true)
        loadJob = viewModelScope.launch {
            try {
                val student = trainingRepository.getUserByEmail(studentEmail)
                if (student == null) {
                    val message = "Student account not found."
                    _myTraining.value = MyTrainingUiState(isLoading = false, error = message)
                    _weeklyEvaluation.value = WeeklyEvaluationUiState(isLoading = false, error = message)
                    return@launch
                }
                trainingRepository.getTrainingRecordsForStudent(student.userId)
                    .flatMapLatest { records ->
                        val record = TrainingLogic.pickRecord(records, now())
                        if (record == null) flowOf(TrainingSnapshot(student = student))
                        else observeRecord(record.recordId, student)
                    }
                    .collect { data ->
                        snapshot = data
                        _myTraining.value = TrainingLogic.myTraining(data, now())
                        _weeklyEvaluation.value = TrainingLogic.studentEvaluation(data, now())
                    }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (e: Exception) {
                val message = "Could not load your training. Please try again."
                _myTraining.value = MyTrainingUiState(isLoading = false, error = message)
                _weeklyEvaluation.value = WeeklyEvaluationUiState(isLoading = false, error = message)
            }
        }
    }

    /** Academic screen: watch one student's record (recordId comes from the person details screen). */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadAcademicEvaluation(recordId: Long) {
        loadJob?.cancel()
        _academicEvaluation.value = AcademicEvaluationUiState(isLoading = true)
        if (recordId <= 0L) {
            _academicEvaluation.value = AcademicEvaluationUiState(isLoading = false, error = "No student selected.")
            return
        }
        loadJob = viewModelScope.launch {
            try {
                trainingRepository.observeTrainingRecord(recordId)
                    .flatMapLatest { record ->
                        if (record == null) flowOf(TrainingSnapshot())
                        else trainingRepository.observeUser(record.studentId)
                            .flatMapLatest { student -> observeRecord(recordId, student) }
                    }
                    .collect { data ->
                        snapshot = data
                        _academicEvaluation.value = TrainingLogic.academicEvaluation(data, now())
                    }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (e: Exception) {
                _academicEvaluation.value = AcademicEvaluationUiState(
                    isLoading = false, error = "Could not load the evaluations. Please try again."
                )
            }
        }
    }

    /**
     * Student submits (or edits) the current week.
     * Returns null when saved, or an error message to show on the screen.
     */
    suspend fun submitCurrentWeek(
        daysText: String,
        hoursText: String,
        reportFileName: String?,
        reportFileRef: String?
    ): String? {
        val record = snapshot.record ?: return "No training record found."
        val state = _myTraining.value
        if (!state.canSubmitCurrentWeek) return "This week can't be submitted now."

        val input = TrainingLogic.validateWeek(daysText, hoursText)
        if (input.error != null) return input.error
        val reportName = reportFileName?.takeIf { it.isNotBlank() } ?: return "Attach your weekly report."
        val reportRef = reportFileRef?.takeIf { it.isNotBlank() } ?: return "Attach your weekly report."

        return try {
            val existing = snapshot.weeks
                .filter { it.weekNo == state.currentWeekNo }
                .maxByOrNull { it.submittedAt }
            if (existing?.confirmedAt != null) return "This week is already confirmed."

            val week = TrainingWeekEntity(
                weekId = existing?.weekId ?: 0L, // same id = update, 0 = new row
                recordId = record.recordId,
                weekNo = state.currentWeekNo,
                daysAttended = input.days,
                durationMinutes = input.minutes,
                reportFileName = reportName,
                reportFileRef = reportRef,
                submittedAt = now()
            )
            val insertedId = trainingRepository.insertTrainingWeek(week)
            val weekId = existing?.weekId ?: insertedId

            // Tell the field supervisor there is a week to confirm
            TrainingLogic.acceptedSupervisorId(snapshot.supervisors, "field")?.let { fieldId ->
                runCatching {
                    NotificationEvents().publish(
                        recipientId = fieldId,
                        event = NotificationEvent.ATTENDANCE_SUBMITTED,
                        body = "${snapshot.student?.fullName ?: "A student"} submitted Week ${state.currentWeekNo} attendance.",
                        weekId = weekId.takeIf { it > 0L }
                    )
                }
            }
            null
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (e: Exception) {
            "Could not save the week. Please try again."
        }
    }

    /**
     * Academic supervisor saves (or edits) the grade of one week.
     * Returns null when saved, or an error message to show on the screen.
     */
    suspend fun saveEvaluation(
        weekId: Long,
        gradeText: String,
        commentText: String,
        academicEmail: String = DEMO_ACADEMIC_EMAIL
    ): String? {
        val input = TrainingLogic.validateGrade(gradeText, commentText)
        if (input.error != null) return input.error

        val week = snapshot.weeks.firstOrNull { it.weekId == weekId } ?: return "This week was not submitted."

        return try {
            val academic = trainingRepository.getUserByEmail(academicEmail)
                ?: return "Academic supervisor account not found."

            // weekId is UNIQUE in weekly_evaluations, so reuse the old row when editing
            val existing = trainingRepository.getEvaluationForWeek(weekId)
            trainingRepository.insertWeeklyEvaluation(
                WeeklyEvaluationEntity(
                    evaluationId = existing?.evaluationId ?: 0L,
                    weekId = weekId,
                    academicId = academic.userId,
                    gradeOutOf10 = input.grade,
                    comment = input.comment,
                    gradedAt = now()
                )
            )

            // Tell the student the week was graded
            snapshot.record?.studentId?.let { studentId ->
                runCatching {
                    NotificationEvents().publish(
                        recipientId = studentId,
                        event = NotificationEvent.WEEKLY_EVALUATION_ADDED,
                        body = "Week ${week.weekNo} graded: ${TrainingLogic.gradeText(input.grade)} / 10",
                        weekId = weekId
                    )
                }
            }
            null
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (e: Exception) {
            "Could not save the evaluation. Please try again."
        }
    }
}