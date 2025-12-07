package com.example.snakegamestorage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.snakegamestorage.data.AppDatabase
import com.example.snakegamestorage.data.GameScoreRepository
import com.example.snakegamestorage.ui.GameViewModelFactory
import com.example.snakegamestorage.ui.SnakeGame

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val repository = GameScoreRepository(database.gameScoreDao())
        val factory = GameViewModelFactory(repository)

        setContent {
            SnakeGame(factory = factory)
        }
    }
}
