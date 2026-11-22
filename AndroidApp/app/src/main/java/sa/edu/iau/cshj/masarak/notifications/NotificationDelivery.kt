package sa.edu.iau.cshj.masarak.notifications

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import sa.edu.iau.cshj.masarak.data.entity.NotificationEntity

/** Receives only the signed-in account's stream. Android permission never gates the inbox. */
class NotificationDelivery {
    suspend fun collect(
        recipientId: Long,
        stream: Flow<List<NotificationEntity>>,
        synchronize: (Set<Long>) -> Unit,
        post: (NotificationEntity) -> Unit
    ) {
        var highWater: Long? = null
        stream.collect { values ->
            val owned = values.filter { it.recipientId == recipientId }
            synchronize(owned.filter { it.readAt == null }.map { it.notificationId }.toSet())
            val previous = highWater
            // Existing history is not replayed at sign-in, rotation, or after permission changes.
            highWater = maxOf(previous ?: 0L, owned.maxOfOrNull { it.notificationId } ?: 0L)
            if (previous != null) owned.filter { it.notificationId > previous && it.readAt == null }
                .sortedBy { it.notificationId }.forEach(post)
        }
    }
}
