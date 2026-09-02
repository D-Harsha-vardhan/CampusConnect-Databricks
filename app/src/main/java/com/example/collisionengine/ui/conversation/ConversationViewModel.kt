package com.example.collisionengine.ui.conversation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.postgrest.from

class ConversationViewModel : ViewModel() {

    private val _suggestedMessage = MutableStateFlow("")
    val suggestedMessage: StateFlow<String> = _suggestedMessage.asStateFlow()

    fun generateMessage(name: String, reason: String) {
        val decodedReason = try {
            java.net.URLDecoder.decode(reason, "UTF-8")
        } catch (e: Exception) {
            reason
        }
        
        // Smart title handling
        val isFaculty = name.contains("Dr.", ignoreCase = true) || name.contains("Prof.", ignoreCase = true)
        val greetingName = if (isFaculty) {
            name.trim()
        } else {
            name.split(" ").firstOrNull() ?: name
        }
        
        // Topic extraction (take first skill/project if comma-separated)
        val topic = decodedReason.split(",").firstOrNull()?.trim() ?: "your research"
        
        val msg = "Hi $greetingName,\n\nI saw on Campus Connect that you have experience with $topic. I'm currently working on something very similar and struggling a bit. I'd love to connect and hear how you approached it!"
        _suggestedMessage.value = msg
    }

    fun updateMessage(newText: String) {
        _suggestedMessage.value = newText
    }

    fun sendMessageToSupabase() {
        val currentMessage = _suggestedMessage.value
        if (currentMessage.isBlank()) return
        
        viewModelScope.launch {
            try {
                val newMessage = com.example.collisionengine.data.models.ChatMessage(
                    senderId = "user_me",
                    content = currentMessage
                )
                com.example.collisionengine.data.network.SupabaseClient.client.from("messages").insert(newMessage)
            } catch (e: Exception) {
                android.util.Log.e("ConversationViewModel", "Error sending message", e)
            }
        }
    }
}
