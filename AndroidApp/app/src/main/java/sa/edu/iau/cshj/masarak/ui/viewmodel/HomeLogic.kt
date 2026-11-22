package sa.edu.iau.cshj.masarak.ui.viewmodel

import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Locale
import sa.edu.iau.cshj.masarak.data.entity.*
import sa.edu.iau.cshj.masarak.data.repository.HomeSnapshot

internal fun String.homeKey() = trim().lowercase(Locale.ROOT).replace(Regex("[ _-]+"), "_")
internal fun UserEntity.homeRole() = role.homeKey().removeSuffix("_supervisor")

internal object HomeLogic {
    private fun latestSubmissions(data: HomeSnapshot) = data.submissions.groupBy { it.taskId }.values
        .mapNotNull { it.maxWithOrNull(compareBy<TaskSubmissionEntity> { it.attemptNo }
            .thenBy { it.submittedAt }.thenBy { it.submissionId }) }

    private fun review(data: HomeSnapshot, submissionId: Long, stage: String) = data.reviews
        .filter { it.submissionId == submissionId && it.reviewStage.homeKey().removeSuffix("_supervisor") == stage }
        .maxWithOrNull(compareBy<TaskReviewEntity> { it.reviewedAt }.thenBy { it.reviewId })

    private fun date(at: Long, zone: ZoneId) = Instant.ofEpochMilli(at).atZone(zone).toLocalDate()

    fun student(data: HomeSnapshot, userId: Long, now: Long, zone: ZoneId = ZoneId.systemDefault()): StudentHomeViewModel.State {
        val today = date(now, zone)
        val owned = data.records.filter { it.studentId == userId }
        val order = compareBy<TrainingRecordEntity> { it.startDate }.thenBy { it.recordId }
        val record = owned.filter { date(it.startDate, zone) <= today && (it.endDate == null || date(it.endDate, zone) >= today) }
            .maxWithOrNull(order)
            ?: owned.filter { date(it.startDate, zone) > today }.minWithOrNull(order)
            ?: owned.maxWithOrNull(order)
        val base = StudentHomeViewModel.State(userId = userId, unreadNotifications = data.unreadNotifications, unreadChats = data.unreadChats)
        if (record == null) return base
        val weeks = data.weeks.filter { it.recordId == record.recordId }
        val start = date(record.startDate, zone)
        val notStarted = today < start
        val ended = record.endDate?.let { today > date(it, zone) } ?: false
        val effectiveDay = if (ended) date(record.endDate!!, zone) else today
        val week = if (notStarted) 0 else (ChronoUnit.DAYS.between(start, effectiveDay).coerceAtLeast(0L) / 7 + 1).toInt()
        val current = weeks.filter { it.weekNo == week }.maxWithOrNull(compareBy<TrainingWeekEntity> { it.submittedAt }.thenBy { it.weekId })
        val evaluation = data.evaluations.mapNotNull { e -> weeks.find { it.weekId == e.weekId }?.let { it to e } }
            .maxWithOrNull(compareBy<Pair<TrainingWeekEntity, WeeklyEvaluationEntity>> { it.first.weekNo }
                .thenBy { it.second.gradedAt }.thenBy { it.second.evaluationId })
        val taskIds = data.tasks.filter { it.recordId == record.recordId }.map { it.taskId }.toSet()
        return base.copy(
            recordId = record.recordId, targetMinutes = record.targetMinutes.toLong().coerceAtLeast(0),
            // Attendance is authoritative; task durations must not be added a second time.
            confirmedMinutes = weeks.filter { it.confirmedAt != null }.sumOf { it.durationMinutes.toLong().coerceAtLeast(0) },
            currentWeek = week, notStarted = notStarted, ended = ended,
            hasCurrentWeek = current != null, currentWeekConfirmed = current?.confirmedAt != null,
            returnedTasks = latestSubmissions(data).count { it.taskId in taskIds &&
                    review(data, it.submissionId, "field")?.decision?.homeKey() in setOf("returned", "returned_for_updates") },
            evaluationWeek = evaluation?.first?.weekNo, grade = evaluation?.second?.gradeOutOf10
        )
    }

    fun supervisor(data: HomeSnapshot, userId: Long, role: String): SupervisorHomeViewModel.State {
        val academic = role == "academic"
        val records = data.records.associateBy { it.recordId }
        val taskRecords = data.tasks.filter { it.recordId in records }.associate { it.taskId to it.recordId }
        val submissions = latestSubmissions(data).filter { it.taskId in taskRecords }
        val names = data.students.associate { it.userId to it.fullName }
        fun name(recordId: Long) = names[records[recordId]?.studentId].orEmpty().ifBlank { "Student" }
        val pending = submissions.count {
            val field = review(data, it.submissionId, "field")
            if (academic) field?.decision?.homeKey() == "approved" && review(data, it.submissionId, "academic") == null
            else field == null
        }
        val followUps = if (!academic) emptyList() else records.keys.mapNotNull { recordId ->
            val reviews = submissions.filter { taskRecords[it.taskId] == recordId }
                .mapNotNull { review(data, it.submissionId, "academic") }
                .sortedWith(compareByDescending<TaskReviewEntity> { it.reviewedAt }.thenByDescending { it.reviewId })
            val count = reviews.takeWhile { it.decision.homeKey() in setOf("not_relevant", "rejected") }.size
            if (count >= 2) SupervisorHomeViewModel.FollowUp(name(recordId), recordId, count) else null
        }.sortedWith(compareByDescending<SupervisorHomeViewModel.FollowUp> { it.count }.thenBy { it.recordId })
        val activities = mutableListOf<SupervisorHomeViewModel.Activity>()
        submissions.forEach { submission ->
            val recordId = taskRecords.getValue(submission.taskId)
            if (academic) {
                val field = review(data, submission.submissionId, "field")
                if (field?.decision?.homeKey() == "approved") activities += SupervisorHomeViewModel.Activity(
                    name(recordId), recordId, "Task approved by Field Supervisor", field.reviewedAt)
            } else activities += SupervisorHomeViewModel.Activity(name(recordId), recordId,
                if (submission.attemptNo > 1) "Resubmitted a task" else "Submitted a task", submission.submittedAt)
        }
        val weeks = data.weeks.filter { it.recordId in records }
        weeks.forEach {
            if (!academic || it.confirmedAt != null) activities += SupervisorHomeViewModel.Activity(name(it.recordId), it.recordId,
                if (academic) "Week ${it.weekNo} confirmed by Field Supervisor" else "Recorded Week ${it.weekNo} attendance",
                if (academic) it.confirmedAt!! else it.submittedAt)
        }
        return SupervisorHomeViewModel.State(userId = userId, role = role,
            peopleCount = data.records.map { it.studentId }.distinct().size, pendingTasks = pending,
            pendingWeeks = if (academic) 0 else weeks.count { it.confirmedAt == null }, followUps = followUps,
            activities = activities.sortedWith(compareByDescending<SupervisorHomeViewModel.Activity> { it.at }
                .thenBy { it.recordId }.thenBy { it.details }).take(5),
            unreadNotifications = data.unreadNotifications, unreadChats = data.unreadChats)
    }
}


