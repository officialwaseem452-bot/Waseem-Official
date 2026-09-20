package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DrillType(val displayName: String) {
    HEADSHOT("Headshot Drill"),
    MOVING("Moving Target Drill"),
    REACTION("Reaction Drill"),
    TRACKING("Tracking Drill"),
    FLICK("Flick Shot Drill"),
    ACCURACY("Accuracy Test"),
    RECOIL("Recoil Control")
}

@Entity(tableName = "training_records")
data class TrainingRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val drillType: DrillType,
    val score: Int,
    val hits: Int,
    val misses: Int,
    val headshots: Int,
    val accuracyPercentage: Float,
    val reactionTimeMs: Long = 0,
    val durationSeconds: Int = 30,
    val timestamp: Long = System.currentTimeMillis()
)
