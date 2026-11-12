
package sa.edu.iau.cshj.masarak.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sa.edu.iau.cshj.masarak.data.entity.ConversationEntity
import sa.edu.iau.cshj.masarak.data.entity.MessageEntity
import sa.edu.iau.cshj.masarak.data.repository.ChatRepository

class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _conversations =
        MutableStateFlow<List<ConversationEntity>>(emptyList())

    val conversations: StateFlow<List<ConversationEntity>> =
        _conversations.asStateFlow()

    private val _messages =
        MutableStateFlow<List<MessageEntity>>(emptyList())

    val messages: StateFlow<List<MessageEntity>> =
        _messages.asStateFlow()

    fun loadConversations(userId: Long) {
        viewModelScope.launch {
            chatRepository.getConversationsForUser(userId).collect { list ->
                _conversations.value = list
            }
        }
    }

    fun loadMessages(conversationId: Long) {
        viewModelScope.launch {
            chatRepository.getMessagesForConversation(conversationId).collect { msgList ->
                _messages.value = msgList
            }
        }
    }

    suspend fun startConversation(conversation: ConversationEntity): Long =
        chatRepository.insertConversation(conversation)

    suspend fun sendMessage(message: MessageEntity): Long =
        chatRepository.insertMessage(message)

    fun markConversationAsRead(conversationId: Long, userId: Long) {
        viewModelScope.launch {
            chatRepository.updateLastReadAt(
                conversationId = conversationId,
                userId = userId,
                readAt = System.currentTimeMillis()
            )
        }
    }
}
