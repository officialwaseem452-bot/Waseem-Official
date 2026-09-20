package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.DrillType
import com.example.data.model.HudLayout
import com.example.data.model.SensitivityPreset
import com.example.data.model.TrainingRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {
    // Training Records
    @Query("SELECT * FROM training_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<TrainingRecord>>

    @Query("SELECT * FROM training_records WHERE drillType = :type ORDER BY timestamp DESC")
    fun getRecordsByType(type: DrillType): Flow<List<TrainingRecord>>

    @Query("SELECT MAX(score) FROM training_records WHERE drillType = :type")
    fun getBestScore(type: DrillType): Flow<Int?>

    @Query("SELECT MIN(reactionTimeMs) FROM training_records WHERE drillType = 'REACTION' AND reactionTimeMs > 0")
    fun getBestReactionTime(): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: TrainingRecord): Long

    @Query("DELETE FROM training_records")
    suspend fun clearAllRecords()

    // Sensitivity Presets
    @Query("SELECT * FROM sensitivity_presets ORDER BY id ASC")
    fun getAllPresets(): Flow<List<SensitivityPreset>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: SensitivityPreset): Long

    @Query("DELETE FROM sensitivity_presets WHERE id = :id")
    suspend fun deletePresetById(id: Long)

    // HUD Layouts
    @Query("SELECT * FROM hud_layouts ORDER BY fingerCount ASC")
    fun getAllHudLayouts(): Flow<List<HudLayout>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHudLayout(layout: HudLayout): Long

    @Query("DELETE FROM hud_layouts WHERE id = :id")
    suspend fun deleteHudLayoutById(id: Long)
}
