package com.example.snakegamestorage.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.snakegamestorage.data.FirebaseRepository
import com.example.snakegamestorage.data.GameScore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel(private val repository: FirebaseRepository) : ViewModel() {

    private val _topScores = MutableStateFlow<List<GameScore>>(emptyList())
    val topScores: StateFlow<List<GameScore>> = _topScores

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun addScore(score: Int) {
        viewModelScope.launch {
            try {
                repository.addScore(score)
                loadTopScores()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to save score: ${e.message}"
            }
        }
    }

    fun loadTopScores() {
        viewModelScope.launch {
            try {
                val scores = repository.getTopScores()
                _topScores.value = scores
                if (scores.isEmpty()) {
                    _errorMessage.value = "No scores yet"
                } else {
                    _errorMessage.value = null
                }
            } catch (e: Exception) {
                _topScores.value = emptyList()
                _errorMessage.value = "Error loading scores: ${e.message}"
            }
        }
    }
}

class GameViewModelFactory(private val repository: FirebaseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
