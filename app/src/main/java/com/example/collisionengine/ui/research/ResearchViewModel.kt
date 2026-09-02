package com.example.collisionengine.ui.research

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collisionengine.data.network.DatabricksClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.collisionengine.data.model.ChatMessage
import kotlinx.coroutines.launch

class ResearchViewModel : ViewModel() {
    companion object {
        private val sessionMessages = mutableListOf<ChatMessage>()
        private var sessionQuery = ""

        fun clearSession() {
            sessionMessages.clear()
            sessionQuery = ""
        }
    }

    private val _queryText = MutableStateFlow(sessionQuery)
    val queryText: StateFlow<String> = _queryText.asStateFlow()
    
<<<<<<< HEAD
=======
    private val _messages = MutableStateFlow<List<ChatMessage>>(sessionMessages.toList())
>>>>>>> databricks/adithya
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    companion object {
        // In-memory chat session persistence
        private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
        
        fun clearSession() {
            _messages.value = emptyList()
        }
    }

    fun onQueryChanged(newText: String) {
        _queryText.value = newText
        sessionQuery = newText
    }

    fun clearChat() {
        _messages.value = emptyList()
        clearSession()
    }

    fun askDatabricks() {
        val query = _queryText.value
        if (query.isBlank()) return
        
        val userMsg = ChatMessage(text = query, isUser = true)
        val updatedList = _messages.value + userMsg
        _messages.value = updatedList
        sessionMessages.clear()
        sessionMessages.addAll(updatedList)
        
        _isLoading.value = true
        _queryText.value = "" // clear input
        sessionQuery = ""
        
        viewModelScope.launch {
            val result = DatabricksClient.askGenie(query)
            
<<<<<<< HEAD
            // Add AI response, making TopMatch dynamic based on extracted names and keywords
            val extractedNames = com.example.collisionengine.data.network.NvidiaClient.extractNames(result)
            val nameMatchedProfiles = com.example.collisionengine.data.network.LocalDatasetClient.searchProfilesByNames(extractedNames)
            val keywordMatchedProfiles = com.example.collisionengine.data.network.LocalDatasetClient.searchProfilesByKeywords(query)
            
            val allMatchedProfiles = (nameMatchedProfiles + keywordMatchedProfiles).distinctBy { it.name }.take(5)
=======
            // Add AI response, making TopMatch dynamic based on extracted names and semantic dataset matching
            val extractedNames = com.example.collisionengine.data.network.NvidiaClient.extractNames(result)
            val matchedProfiles = com.example.collisionengine.data.network.LocalDatasetClient.findMatches(query, result, extractedNames)
>>>>>>> databricks/adithya
            
            val aiMsg = ChatMessage(
                text = result, 
                isUser = false, 
                isTopMatch = allMatchedProfiles.isNotEmpty(),
                topMatches = allMatchedProfiles
            )
            val finalMessages = _messages.value + aiMsg
            _messages.value = finalMessages
            sessionMessages.clear()
            sessionMessages.addAll(finalMessages)
            _isLoading.value = false
        }
    }
}
