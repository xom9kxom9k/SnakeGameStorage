package com.example.snakegamestorage.data

data class GameScore(
    var id: String = "",
    var score: Int = 0,
    var timestamp: Long = System.currentTimeMillis()
)
