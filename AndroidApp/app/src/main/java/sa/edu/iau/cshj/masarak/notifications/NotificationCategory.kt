package sa.edu.iau.cshj.masarak.notifications

import java.util.Locale

/** Stable channel IDs: changing them would discard the user's channel choices. */
enum class NotificationCategory(val channelId: String, val label: String, val description: String) {
    TASKS("masarak_tasks", "Tasks & reviews", "Task submissions, approvals and returned tasks"),
    ATTENDANCE("masarak_attendance", "Attendance", "Weekly attendance submissions and confirmations"),
    EVALUATIONS("masarak_evaluations", "Evaluations & alerts", "Weekly evaluations and academic follow-up alerts"),
    LINKS("masarak_links", "Linking requests", "Supervisor linking requests and responses"),
    MESSAGES("masarak_messages", "Messages", "New messages in your conversations"),
    GENERAL("masarak_general", "Other updates", "Other MASARAK updates")
}

enum class NotificationEvent(val category: NotificationCategory, val title: String) {
    TASK_SUBMITTED(NotificationCategory.TASKS, "Needs Review"),
    TASK_RESUBMITTED(NotificationCategory.TASKS, "Task resubmitted"),
    TASK_APPROVED(NotificationCategory.TASKS, "Task approved"),
    TASK_RETURNED(NotificationCategory.TASKS, "Returned for Updates"),
    TASK_REVIEWED(NotificationCategory.TASKS, "Task review updated"),
    TASK_RELEVANCE_REVIEWED(NotificationCategory.TASKS, "Task relevance reviewed"),
    ATTENDANCE_SUBMITTED(NotificationCategory.ATTENDANCE, "Attendance ready to confirm"),
    WEEK_CONFIRMED(NotificationCategory.ATTENDANCE, "Attendance confirmed"),
    WEEKLY_EVALUATION_ADDED(NotificationCategory.EVALUATIONS, "Weekly evaluation"),
    ACADEMIC_ALERT(NotificationCategory.EVALUATIONS, "Academic Alert"),
    LINK_REQUEST(NotificationCategory.LINKS, "Linking request"),
    LINK_ACCEPTED(NotificationCategory.LINKS, "Link accepted"),
    LINK_REJECTED(NotificationCategory.LINKS, "Link declined"),
    NEW_MESSAGE(NotificationCategory.MESSAGES, "New message"),
    GENERAL(NotificationCategory.GENERAL, "MASARAK update");

    companion object {
        fun from(raw: String): NotificationEvent {
            val key = raw.trim().uppercase(Locale.ROOT).replace(Regex("[ -]+"), "_")
            return when (key) {
                "RETURNED", "RETURNED_FOR_UPDATES", "TASK_RETURNED_FOR_UPDATES" -> TASK_RETURNED
                "NEEDS_REVIEW" -> TASK_SUBMITTED
                "WEEK_READY_TO_CONFIRM", "WEEK_SUBMITTED" -> ATTENDANCE_SUBMITTED
                "WEEKLY_EVALUATION", "EVALUATION_PUBLISHED" -> WEEKLY_EVALUATION_ADDED
                "RELEVANCE_ALERT" -> ACADEMIC_ALERT
                "LINKING_REQUEST" -> LINK_REQUEST
                "MESSAGE_RECEIVED" -> NEW_MESSAGE
                else -> entries.firstOrNull { it.name == key } ?: GENERAL
            }
        }
    }
}


