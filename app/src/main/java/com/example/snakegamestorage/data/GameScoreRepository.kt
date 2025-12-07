package com.example.snakegamestorage.data

class GameScoreRepository(private val gameScoreDao: GameScoreDao) {

    suspend fun addScore(score: Int) {
        gameScoreDao.insert(GameScore(score = score))
    }

    suspend fun getTopScores(): List<GameScore> {
        return gameScoreDao.getTopScores()
    }
}
