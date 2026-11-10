package sa.edu.iau.cshj.masarak.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sa.edu.iau.cshj.masarak.data.entity.TrainingRecordEntity
import sa.edu.iau.cshj.masarak.data.entity.TrainingWeekEntity
import sa.edu.iau.cshj.masarak.data.entity.WeeklyEvaluationEntity
import sa.edu.iau.cshj.masarak.data.repository.TrainingRepository

class TrainingViewModel(private val trainingRepository: TrainingRepository) : ViewModel() {
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

    suspend fun addTrainingRecord(record: TrainingRecordEntity): Long =
        trainingRepository.insertTrainingRecord(record)

    suspend fun addTrainingWeek(week: TrainingWeekEntity): Long =
        trainingRepository.insertTrainingWeek(week)

    suspend fun addWeeklyEvaluation(evaluation: WeeklyEvaluationEntity): Long =
        trainingRepository.insertWeeklyEvaluation(evaluation)
}
