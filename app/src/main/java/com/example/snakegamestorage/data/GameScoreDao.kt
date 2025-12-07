package com.example.snakegamestorage.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameScoreDao {
    @Insert
    suspend fun insert(score: GameScore)

    @Query("SELECT * FROM game_scores ORDER BY score DESC LIMIT 10")
    suspend fun getTopScores(): List<GameScore>
}
