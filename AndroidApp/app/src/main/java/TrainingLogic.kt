package sa.edu.iau.cshj.masarak.ui.viewmodel

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import sa.edu.iau.cshj.masarak.data.dao.RecordSupervisorRow
import sa.edu.iau.cshj.masarak.data.entity.TaskSubmissionEntity
import sa.edu.iau.cshj.masarak.data.entity.TrainingRecordEntity
import sa.edu.iau.cshj.masarak.data.entity.TrainingWeekEntity
import sa.edu.iau.cshj.masarak.data.entity.UserEntity
import sa.edu.iau.cshj.masarak.data.entity.WeeklyEvaluationEntity

// Demo accounts created in MasarakApplication (used until login passes the real user)
const val DEMO_STUDENT_EMAIL = "student@iau.edu.sa"
const val DEMO_ACADEMIC_EMAIL = "supervisor.academic@iau.edu.sa"

/** Everything the three training screens need, read from Room in one place. */
data class TrainingSnapshot(
    val student: UserEntity? = null,
    val record: TrainingRecordEntity? = null,
    val weeks: List<TrainingWeekEntity> = emptyList(),
    val evaluations: List<WeeklyEvaluationEntity> = emptyList(),
    val submissions: List<TaskSubmissionEntity> = emptyList(),
    val supervisors: List<RecordSupervisorRow> = emptyList()
)

// ---------------- UI states ----------------

data class SupervisorUi(val name: String? = null, val status: String = "none") // accepted / pending / none

data class TrainingWeekUi(
    val weekId: Long,
    val weekNo: Int,
    val dateRange: String,
    val daysAttended: Int,
    val hours: String,
    val reportFileName: String,
    val reportFileRef: String,
    val confirmed: Boolean
)

data class MyTrainingUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val hasRecord: Boolean = false,
    val organization: String = "",
    val period: String = "",
    val confirmedHours: String = "0",
    val targetHours: String = "0",
    val fieldSupervisor: SupervisorUi = SupervisorUi(),
    val academicSupervisor: SupervisorUi = SupervisorUi(),
    val currentWeekNo: Int = 0,
    val currentWeekRange: String = "",
    val notStarted: Boolean = false,
    val ended: Boolean = false,
    val canSubmitCurrentWeek: Boolean = false,
    val currentWeekPending: TrainingWeekUi? = null, // submitted but not confirmed yet (still editable)
    val pastWeeks: List<TrainingWeekUi> = emptyList()
)

data class EvaluationWeekUi(
    val weekId: Long?,
    val weekNo: Int,
    val dateRange: String,
    val tasks: List<String>,
    val grade: Float?,
    val gradeText: String?,
    val comment: String?,
    val status: String?,
    val confirmed: Boolean
)

data class WeeklyEvaluationUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val academicName: String? = null,
    val gradedCount: Int = 0,
    val totalWeeks: Int = 0,
    val bars: List<Pair<String, Float>> = emptyList(), // "W1" to grade
    val weeks: List<EvaluationWeekUi> = emptyList()
)

data class AcademicEvaluationUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val studentName: String = "",
    val subtitle: String = "",
    val studentId: Long = 0L,
    val weeks: List<EvaluationWeekUi> = emptyList()
)

/** Validated values ready to be saved, or an error message for the user. */
data class WeekInput(val days: Int = 0, val minutes: Int = 0, val error: String? = null)
data class GradeInput(val grade: Float = 0f, val comment: String? = null, val error: String? = null)

// ---------------- Pure logic (no database, no Compose) ----------------

internal object TrainingLogic {

    private val dayMonth = DateTimeFormatter.ofPattern("d MMMM", Locale.ENGLISH)
    private val day = DateTimeFormatter.ofPattern("d", Locale.ENGLISH)

    private fun date(at: Long, zone: ZoneId): LocalDate = Instant.ofEpochMilli(at).atZone(zone).toLocalDate()

    /** Same rule as HomeLogic: the record running today, otherwise the next one, otherwise the latest. */
    fun pickRecord(records: List<TrainingRecordEntity>, now: Long, zone: ZoneId = ZoneId.systemDefault()): TrainingRecordEntity? {
        val today = date(now, zone)
        val order = compareBy<TrainingRecordEntity> { it.startDate }.thenBy { it.recordId }
        return records.filter { date(it.startDate, zone) <= today && (it.endDate == null || date(it.endDate, zone) >= today) }
            .maxWithOrNull(order)
            ?: records.filter { date(it.startDate, zone) > today }.minWithOrNull(order)
            ?: records.maxWithOrNull(order)
    }

    /** Week 1 starts on the record start date; every 7 days is a new week. */
    fun weekOf(record: TrainingRecordEntity, at: Long, zone: ZoneId = ZoneId.systemDefault()): Int {
        val start = date(record.startDate, zone)
        val days = ChronoUnit.DAYS.between(start, date(at, zone))
        return if (days < 0) 0 else (days / 7 + 1).toInt()
    }

    fun currentWeek(record: TrainingRecordEntity, now: Long, zone: ZoneId = ZoneId.systemDefault()): Int {
        val ended = record.endDate != null && date(now, zone) > date(record.endDate, zone)
        return weekOf(record, if (ended) record.endDate!! else now, zone)
    }

    /** "14–18 September" or "31 August–4 September" (5 working days). */
    fun weekRange(record: TrainingRecordEntity, weekNo: Int, zone: ZoneId = ZoneId.systemDefault()): String {
        if (weekNo < 1) return ""
        val from = date(record.startDate, zone).plusWeeks((weekNo - 1).toLong())
        val to = from.plusDays(4)
        return if (from.month == to.month) "${from.format(day)}–${to.format(dayMonth)}"
        else "${from.format(dayMonth)}–${to.format(dayMonth)}"
    }

    fun hoursText(minutes: Long): String {
        val hours = minutes / 60.0
        return if (hours % 1.0 == 0.0) hours.toLong().toString() else String.format(Locale.ENGLISH, "%.1f", hours)
    }

    fun gradeText(grade: Float): String =
        if (grade % 1f == 0f) grade.toInt().toString() else String.format(Locale.ENGLISH, "%.1f", grade)

    private fun supervisor(rows: List<RecordSupervisorRow>, role: String): SupervisorUi {
        val mine = rows.filter { it.supervisorRole.homeKey().removeSuffix("_supervisor") == role }
        val accepted = mine.firstOrNull { it.status.homeKey() == "accepted" }
        if (accepted != null) return SupervisorUi(accepted.fullName, "accepted")
        val pending = mine.firstOrNull { it.status.homeKey() == "pending" }
        return if (pending != null) SupervisorUi(pending.fullName, "pending") else SupervisorUi()
    }

    fun acceptedSupervisorId(rows: List<RecordSupervisorRow>, role: String): Long? = rows.firstOrNull {
        it.supervisorRole.homeKey().removeSuffix("_supervisor") == role && it.status.homeKey() == "accepted"
    }?.supervisorId

    /** If a week was saved twice, the latest one wins. */
    private fun latestWeeks(weeks: List<TrainingWeekEntity>): Map<Int, TrainingWeekEntity> = weeks.groupBy { it.weekNo }
        .mapValues { (_, list) -> list.maxWith(compareBy<TrainingWeekEntity> { it.submittedAt }.thenBy { it.weekId }) }

    private fun weekUi(record: TrainingRecordEntity, week: TrainingWeekEntity) = TrainingWeekUi(
        weekId = week.weekId, weekNo = week.weekNo, dateRange = weekRange(record, week.weekNo),
        daysAttended = week.daysAttended, hours = hoursText(week.durationMinutes.toLong()),
        reportFileName = week.reportFileName, reportFileRef = week.reportFileRef,
        confirmed = week.confirmedAt != null
    )

    /** Latest attempt of each task, grouped by the training week of its task date. */
    private fun tasksByWeek(record: TrainingRecordEntity, submissions: List<TaskSubmissionEntity>): Map<Int, List<String>> =
        submissions.groupBy { it.taskId }.values
            .map { list -> list.maxWith(compareBy<TaskSubmissionEntity> { it.attemptNo }.thenBy { it.submittedAt }) }
            .sortedBy { it.taskDate }
            .groupBy({ weekOf(record, it.taskDate) }, { it.title })

    // ---------- My Training ----------

    fun myTraining(data: TrainingSnapshot, now: Long, zone: ZoneId = ZoneId.systemDefault()): MyTrainingUiState {
        val record = data.record ?: return MyTrainingUiState(isLoading = false, hasRecord = false)
        val today = date(now, zone)
        val notStarted = today < date(record.startDate, zone)
        val ended = record.endDate != null && today > date(record.endDate, zone)
        val current = currentWeek(record, now, zone)
        val weeks = latestWeeks(data.weeks)
        val currentEntity = weeks[current]
        val pendingCurrent = currentEntity?.takeIf { it.confirmedAt == null }
        val confirmedMinutes = weeks.values.filter { it.confirmedAt != null }.sumOf { it.durationMinutes.toLong() }
        val end = record.endDate?.let { " – " + date(it, zone).format(dayMonth) } ?: ""

        return MyTrainingUiState(
            isLoading = false,
            hasRecord = true,
            organization = record.trainingOrganization,
            period = date(record.startDate, zone).format(dayMonth) + end,
            confirmedHours = hoursText(confirmedMinutes),
            targetHours = hoursText(record.targetMinutes.toLong()),
            fieldSupervisor = supervisor(data.supervisors, "field"),
            academicSupervisor = supervisor(data.supervisors, "academic"),
            currentWeekNo = current,
            currentWeekRange = weekRange(record, current, zone),
            notStarted = notStarted,
            ended = ended,
            // Can submit while the training is running and the week is not confirmed yet
            canSubmitCurrentWeek = !notStarted && !ended && current >= 1 && currentEntity?.confirmedAt == null,
            currentWeekPending = pendingCurrent?.let { weekUi(record, it) },
            pastWeeks = weeks.values
                .filter { !(it.weekNo == current && it.confirmedAt == null && !ended) }
                .sortedByDescending { it.weekNo }
                .map { weekUi(record, it) }
        )
    }

    fun validateWeek(daysText: String, hoursText: String): WeekInput {
        val days = daysText.trim().toIntOrNull()
        val hours = hoursText.trim().replace(',', '.').toDoubleOrNull()
        return when {
            days == null || days !in 1..7 -> WeekInput(error = "Days attended must be a whole number from 1 to 7.")
            hours == null || hours <= 0.0 -> WeekInput(error = "Training hours must be more than 0.")
            hours > days * 12 -> WeekInput(error = "Hours can't be more than 12 per attended day.")
            else -> WeekInput(days = days, minutes = Math.round(hours * 60).toInt())
        }
    }

    // ---------- Weekly Evaluation (student) ----------

    fun studentEvaluation(data: TrainingSnapshot, now: Long, zone: ZoneId = ZoneId.systemDefault()): WeeklyEvaluationUiState {
        val record = data.record ?: return WeeklyEvaluationUiState(isLoading = false)
        val ended = record.endDate != null && date(now, zone) > date(record.endDate, zone)
        val current = currentWeek(record, now, zone)
        val weeks = latestWeeks(data.weeks)
        val evaluations = data.evaluations.associateBy { it.weekId }
        val tasks = tasksByWeek(record, data.submissions)
        val total = maxOf(record.endDate?.let { weekOf(record, it, zone) } ?: 0, current, weeks.keys.maxOrNull() ?: 0)
        val lastWeek = maxOf(current, weeks.keys.maxOrNull() ?: 0)

        val cards = (lastWeek downTo 1).map { no ->
            val week = weeks[no]
            val evaluation = week?.let { evaluations[it.weekId] }
            EvaluationWeekUi(
                weekId = week?.weekId, weekNo = no, dateRange = weekRange(record, no, zone),
                tasks = tasks[no].orEmpty(),
                grade = evaluation?.gradeOutOf10, gradeText = evaluation?.gradeOutOf10?.let(::gradeText),
                comment = evaluation?.comment,
                status = when {
                    evaluation != null -> null
                    no == current && !ended -> "Week in progress"
                    week == null -> "Week not submitted"
                    else -> "Not graded yet"
                },
                confirmed = week?.confirmedAt != null
            )
        }
        val graded = cards.filter { it.grade != null }.sortedBy { it.weekNo }
        return WeeklyEvaluationUiState(
            isLoading = false,
            academicName = supervisor(data.supervisors, "academic").takeIf { it.status == "accepted" }?.name,
            gradedCount = graded.size,
            totalWeeks = total,
            bars = graded.map { "W${it.weekNo}" to it.grade!! },
            weeks = cards
        )
    }

    // ---------- Weekly Evaluation (academic supervisor) ----------

    fun academicEvaluation(data: TrainingSnapshot, now: Long, zone: ZoneId = ZoneId.systemDefault()): AcademicEvaluationUiState {
        val record = data.record ?: return AcademicEvaluationUiState(isLoading = false, error = "Training record not found.")
        val weeks = latestWeeks(data.weeks)
        val evaluations = data.evaluations.associateBy { it.weekId }
        val tasks = tasksByWeek(record, data.submissions)
        val confirmedMinutes = weeks.values.filter { it.confirmedAt != null }.sumOf { it.durationMinutes.toLong() }
        val current = currentWeek(record, now, zone)

        return AcademicEvaluationUiState(
            isLoading = false,
            studentName = data.student?.fullName.orEmpty().ifBlank { "Student" },
            studentId = record.studentId,
            subtitle = "${record.trainingOrganization} · Week ${current.coerceAtLeast(1)} · " +
                    "${hoursText(confirmedMinutes)} / ${hoursText(record.targetMinutes.toLong())} h",
            // Only weeks the student submitted can be graded
            weeks = weeks.values.sortedByDescending { it.weekNo }.map { week ->
                val evaluation = evaluations[week.weekId]
                EvaluationWeekUi(
                    weekId = week.weekId, weekNo = week.weekNo, dateRange = weekRange(record, week.weekNo, zone),
                    tasks = tasks[week.weekNo].orEmpty(),
                    grade = evaluation?.gradeOutOf10, gradeText = evaluation?.gradeOutOf10?.let(::gradeText),
                    comment = evaluation?.comment,
                    status = if (evaluation == null) "Not evaluated yet" else null,
                    confirmed = week.confirmedAt != null
                )
            }
        )
    }

    fun validateGrade(gradeText: String, commentText: String): GradeInput {
        val grade = gradeText.trim().replace(',', '.').toFloatOrNull()
        val comment = commentText.trim()
        return when {
            grade == null -> GradeInput(error = "Enter a grade from 0 to 10.")
            grade < 0f || grade > 10f -> GradeInput(error = "The grade must be between 0 and 10.")
            Math.round(grade * 2) / 2f != grade -> GradeInput(error = "Use whole or half marks, like 8 or 8.5.")
            comment.length > 500 -> GradeInput(error = "Keep the comment under 500 characters.")
            else -> GradeInput(grade = grade, comment = comment.ifBlank { null })
        }
    }
}