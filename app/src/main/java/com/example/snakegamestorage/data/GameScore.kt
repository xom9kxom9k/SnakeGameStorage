package com.example.snakegamestorage.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_scores")
data class GameScore(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val score: Int,
    val timestamp: Long = System.currentTimeMillis()
)
