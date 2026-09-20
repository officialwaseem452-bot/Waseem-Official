package com.example.data.db

import androidx.room.TypeConverter
import com.example.data.model.DrillType

class Converters {
    @TypeConverter
    fun fromDrillType(value: DrillType?): String? = value?.name

    @TypeConverter
    fun toDrillType(value: String?): DrillType? = value?.let {
        try {
            DrillType.valueOf(it)
        } catch (_: Exception) {
            DrillType.HEADSHOT
        }
    }
}
