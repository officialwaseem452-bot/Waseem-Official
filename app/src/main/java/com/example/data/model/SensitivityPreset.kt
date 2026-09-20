package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sensitivity_presets")
data class SensitivityPreset(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val generalSens: Int,
    val redDotSens: Int,
    val scope2xSens: Int,
    val scope4xSens: Int,
    val sniperSens: Int,
    val freeLookSens: Int,
    val deviceCategory: String = "Mobile Phone",
    val dpi: Int = 400,
    val isCustom: Boolean = true,
    val notes: String = ""
)
