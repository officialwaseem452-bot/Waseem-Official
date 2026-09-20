package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hud_layouts")
data class HudLayout(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val fingerCount: Int, // 2, 3, or 4
    val fireButtonSize: Int, // e.g. 52 (percentage)
    val fireButtonXPercent: Float = 0.82f,
    val fireButtonYPercent: Float = 0.68f,
    val leftFireButtonXPercent: Float = 0.15f,
    val leftFireButtonYPercent: Float = 0.25f,
    val scopeButtonXPercent: Float = 0.85f,
    val scopeButtonYPercent: Float = 0.35f,
    val jumpButtonXPercent: Float = 0.92f,
    val jumpButtonYPercent: Float = 0.50f,
    val crouchButtonXPercent: Float = 0.76f,
    val crouchButtonYPercent: Float = 0.82f,
    val glooWallXPercent: Float = 0.18f,
    val glooWallYPercent: Float = 0.62f,
    val isDefault: Boolean = false,
    val playstyleTip: String = ""
)
