
package sa.edu.iau.cshj.masarak.ui.screens.chat

import android.content.Intent
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import sa.edu.iau.cshj.masarak.data.DatabaseProvider
import sa.edu.iau.cshj.masarak.data.dao.ChatInboxRow
import sa.edu.iau.cshj.masarak.data.entity.ConversationEntity
import sa.edu.iau.cshj.masarak.data.entity.ConversationMemberEntity
import sa.edu.iau.cshj.masarak.data.entity.MessageAttachmentEntity
import sa.edu.iau.cshj.masarak.data.entity.MessageEntity
import sa.edu.iau.cshj.masarak.ui.components.BottomBarItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RoomChatViewModel : ViewModel() {

    private val db
        get() = DatabaseProvider.db

    private val dao
        get() = db.chatDao()

    val inbox = MutableStateFlow<List<ChatInboxRow>>(emptyList())
    val error = MutableStateFlow<String?>(null)
    val thread = MutableStateFlow<List<ConversationMessage>>(emptyList())

    private var inboxJob: Job? = null
    private var contactsJob: Job? = null
    private var threadJob: Job? = null
    private var account: Long? = null

    fun bind(userId: Long?) {
        if (account == userId) return

        inboxJob?.cancel()
        contactsJob?.cancel()
        threadJob?.cancel()

        account = userId
        inbox.value = emptyList()
        thread.value = emptyList()
        error.value = null

        if (userId == null) return

        inboxJob = viewModelScope.launch {
            try {
                combine(
                    dao.observeInbox(userId),
                    dao.observeLinkedContacts(userId)
                ) { rows, contacts ->
                    rows.filter { row ->
                        contacts.any { it.userId == row.peerId }
                    }
                }.collect {
                    inbox.value = it
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                error.value =
                    "Could not load conversations. Please reopen Chat."
            }
        }

        contactsJob = viewModelScope.launch {
            try {
                dao.observeLinkedContacts(userId).collect { contacts ->
                    contacts.forEach { peer ->
                        db.withTransaction {
                            val existing = dao.findDirectConversation(
                                userId,
                                peer.userId
                            )

                            if (existing == null) {
                                val id = dao.insertConversation(
                                    ConversationEntity(
                                        createdAt = System.currentTimeMillis()
                                    )
                                )

                                dao.insertConversationMember(
                                    ConversationMemberEntity(
                                        conversationId = id,
                                        userId = userId
                                    )
                                )

                                dao.insertConversationMember(
                                    ConversationMemberEntity(
                                        conversationId = id,
                                        userId = peer.userId
                                    )
                                )
                            }
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                error.value =
                    "Could not prepare conversations. Please reopen Chat."
            }
        }
    }

    fun open(
        conversationId: Long,
        userId: Long
    ) {
        threadJob?.cancel()
        thread.value = emptyList()

        threadJob = viewModelScope.launch {
            try {
                check(
                    account == userId &&
                            dao.isMember(conversationId, userId)
                )

                combine(
                    dao.getMessagesForConversation(conversationId),
                    dao.observeConversationAttachments(conversationId)
                ) { messages, attachments ->
                    messages.map { message ->
                        val files = attachments.filter {
                            it.messageId == message.messageId
                        }

                        val attachment = files.firstOrNull()

                        ConversationMessage(
                            text = message.body.orEmpty(),
                            time = chatTime(message.sentAt),
                            isSent = message.senderUserId == userId,
                            attachmentType = if (attachment != null) {
                                "file"
                            } else {
                                null
                            },
                            attachmentName = attachment?.fileName,
                            attachmentSize = attachment?.let {
                                "${(it.sizeBytes + 1023) / 1024} KB"
                            },
                            attachmentRef = attachment?.fileRef,
                            attachmentMime = attachment?.mimeType
                        )
                    }
                }.collect {
                    thread.value = it
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                error.value = "Could not load this conversation."
            }
        }
    }

    fun closeThread() {
        threadJob?.cancel()
        thread.value = emptyList()
    }

    suspend fun markVisibleRead(
        conversationId: Long,
        userId: Long
    ) {
        dao.getMessagesForConversation(conversationId)
            .collect { messages ->
                val lastIncoming = messages
                    .filter { it.senderUserId != userId }
                    .maxOfOrNull { it.sentAt }

                if (
                    lastIncoming != null &&
                    account == userId &&
                    dao.isMember(conversationId, userId)
                ) {
                    dao.markReadThrough(
                        conversationId = conversationId,
                        userId = userId,
                        readAt = lastIncoming
                    )
                }
            }
    }

    fun send(
        conversationId: Long,
        userId: Long,
        body: String,
        attachment: MessageAttachmentEntity? = null,
        done: (Boolean) -> Unit = {}
    ) {
        if (body.isBlank() && attachment == null) return

        viewModelScope.launch {
            try {
                db.withTransaction {
                    check(
                        account == userId &&
                                dao.isMember(conversationId, userId)
                    ) {
                        "Please sign in again."
                    }

                    val row = dao.observeInbox(userId)
                        .first()
                        .firstOrNull {
                            it.conversationId == conversationId
                        }

                    check(
                        row != null &&
                                dao.observeLinkedContacts(userId)
                                    .first()
                                    .any { it.userId == row.peerId }
                    ) {
                        "This contact is no longer linked to your training."
                    }

                    val previousTime =
                        dao.getMessagesForConversation(conversationId)
                            .first()
                            .maxOfOrNull { it.sentAt } ?: 0L

                    val messageId = dao.insertMessage(
                        MessageEntity(
                            conversationId = conversationId,
                            senderUserId = userId,
                            body = body.trim().takeIf {
                                it.isNotEmpty()
                            },
                            sentAt = maxOf(
                                System.currentTimeMillis(),
                                previousTime + 1
                            )
                        )
                    )

                    if (attachment != null) {
                        dao.insertMessageAttachment(
                            attachment.copy(
                                messageId = messageId
                            )
                        )
                    }
                }

                done(true)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                error.value =
                    e.message ?: "Message was not saved. Try again."

                done(false)
            }
        }
    }

    companion object {
        val Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T {
                return RoomChatViewModel() as T
            }
        }
    }
}

private fun chatTime(time: Long): String {
    return SimpleDateFormat(
        "dd MMM HH:mm",
        Locale.ENGLISH
    ).format(Date(time))
}

private fun ChatInboxRow.toUi(): ChatConversation {
    return ChatConversation(
        conversationId = conversationId.toString(),
        initials = fullName.trim()
            .split(Regex("\\s+"))
            .take(2)
            .mapNotNull { it.firstOrNull() }
            .joinToString("")
            .uppercase(),
        name = fullName,
        role = when (role.trim().lowercase()) {
            "field", "field supervisor" -> "Field Supervisor"
            "academic", "academic supervisor" -> "Academic Supervisor"
            else -> "Student"
        },
        lastMessage = lastMessage ?: if (lastSentAt != null) {
            "Attachment"
        } else {
            "Start a conversation"
        },
        time = lastSentAt?.let(::chatTime).orEmpty(),
        unreadCount = unreadCount,
        isAcademic = role.trim()
            .lowercase()
            .startsWith("academic"),
        isFieldSupervisor = role.trim()
            .lowercase()
            .startsWith("field")
    )
}

@Composable
fun RoomChatRoute(
    vm: RoomChatViewModel,
    thirdTabLabel: String,
    academic: Boolean,
    onConversation: (String) -> Unit,
    onTab: (BottomBarItem) -> Unit
) {
    val inbox by vm.inbox.collectAsState()

    ChatScreen(
        conversations = inbox.map { it.toUi() },
        thirdTabLabel = thirdTabLabel,
        usePlainBackground = academic,
        onConversationClick = onConversation,
        onBottomBarItemSelected = onTab
    )

    ChatError(vm)
}

@Composable
private fun ChatError(
    vm: RoomChatViewModel
) {
    val error by vm.error.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(
                context,
                it,
                Toast.LENGTH_LONG
            ).show()

            vm.error.value = null
        }
    }
}

@Composable
fun RoomConversationRoute(
    vm: RoomChatViewModel,
    userId: Long,
    conversationId: Long,
    thirdTabLabel: String,
    onBack: () -> Unit,
    onTab: (BottomBarItem) -> Unit
) {
    val inbox by vm.inbox.collectAsState()
    val messages by vm.thread.collectAsState()

    val contact = inbox.firstOrNull {
        it.conversationId == conversationId
    }

    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(conversationId, userId) {
        vm.open(conversationId, userId)
    }

    DisposableEffect(conversationId, userId) {
        onDispose {
            vm.closeThread()
        }
    }

    LaunchedEffect(conversationId, userId, lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            vm.markVisibleRead(conversationId, userId)
        }
    }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver
                    .takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )

                var name = "Attachment"
                var size = 0L

                context.contentResolver.query(
                    uri,
                    arrayOf(
                        OpenableColumns.DISPLAY_NAME,
                        OpenableColumns.SIZE
                    ),
                    null,
                    null,
                    null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        name = cursor.getString(0) ?: name

                        if (!cursor.isNull(1)) {
                            size = cursor.getLong(1)
                        }
                    }
                }

                vm.send(
                    conversationId = conversationId,
                    userId = userId,
                    body = "",
                    attachment = MessageAttachmentEntity(
                        messageId = 0L,
                        fileName = name,
                        mimeType = context.contentResolver
                            .getType(uri)
                            ?: "application/octet-stream",
                        sizeBytes = size,
                        fileRef = uri.toString()
                    )
                )
            } catch (e: Exception) {
                vm.error.value =
                    "Could not attach this file. Please choose it again."
            }
        }
    }

    ConversationScreen(
        contactName = contact?.fullName ?: "Conversation",
        role = when {
            contact?.role?.lowercase()
                ?.startsWith("field") == true -> "Field"

            contact?.role?.lowercase()
                ?.startsWith("academic") == true -> "Academic"

            else -> "Student"
        },
        conversationInfo = null,
        messages = messages,
        thirdTabLabel = thirdTabLabel,
        onBack = onBack,
        onBottomBarItemSelected = onTab,
        onSendPersisted = { text, done ->
            vm.send(
                conversationId = conversationId,
                userId = userId,
                body = text,
                done = done
            )
        },
        onAttachmentClick = {
            picker.launch(arrayOf("*/*"))
        }
    )

    ChatError(vm)
}