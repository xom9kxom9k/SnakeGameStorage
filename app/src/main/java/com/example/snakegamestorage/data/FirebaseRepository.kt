package com.example.snakegamestorage.data

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.example.snakegamestorage.data.GameScore
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class FirebaseRepository(context: Context) {

    private val deviceId: String = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    ) ?: "default_device_id"

    private val database = FirebaseDatabase.getInstance()
    private val scoresRef = database.getReference("scores").child(deviceId)

    suspend fun addScore(score: Int) {
        try {
            val newScoreRef = scoresRef.push()
            val gameScore = GameScore(
                id = newScoreRef.key ?: "",
                score = score,
                timestamp = System.currentTimeMillis()
            )
            newScoreRef.setValue(gameScore).await()
            Log.d("FirebaseRepository", "Score added successfully: $score")
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Failed to add score", e)
            throw e
        }
    }

    suspend fun getTopScores(): List<GameScore> {
        return try {
            Log.d("FirebaseRepository", "Fetching top scores for $deviceId")
            val snapshot = scoresRef
                .get()
                .await()
            
            val scores = mutableListOf<GameScore>()
            if (snapshot.exists()) {
                snapshot.children.forEach { child ->
                    try {
                        child.getValue(GameScore::class.java)?.let { score ->
                            scores.add(score)
                        }
                    } catch (e: Exception) {
                        Log.e("FirebaseRepository", "Error parsing score: ${child.key}", e)
                    }
                }
            } else {
                Log.d("FirebaseRepository", "No scores found for this device")
            }
            
            // Sort descending and take top 10
            val sorted = scores.sortedByDescending { it.score }.take(10)
            Log.d("FirebaseRepository", "Loaded ${sorted.size} scores")
            sorted
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error fetching top scores", e)
            emptyList()
        }
    }
    fun getDatabaseUrl(): String {
        return try {
            database.reference.toString()
        } catch (e: Exception) {
            "Error getting URL"
        }
    }
}
