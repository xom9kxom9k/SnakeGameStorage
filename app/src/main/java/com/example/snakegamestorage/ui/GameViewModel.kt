package com.example.snakegamestorage.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.snakegamestorage.data.GameScore
import com.example.snakegamestorage.data.GameScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel(private val repository: GameScoreRepository) : ViewModel() {

    private val _topScores = MutableStateFlow<List<GameScore>>(emptyList())
    val topScores: StateFlow<List<GameScore>> = _topScores

    fun addScore(score: Int) {
        viewModelScope.launch {
            repository.addScore(score)
            loadTopScores()
        }
    }

    fun loadTopScores() {
        viewModelScope.launch {
            _topScores.value = repository.getTopScores()
        }
    }
}

class GameViewModelFactory(private val repository: GameScoreRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
