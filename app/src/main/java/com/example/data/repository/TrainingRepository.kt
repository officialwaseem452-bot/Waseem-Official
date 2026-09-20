package com.example.data.repository

import com.example.data.db.TrainingDao
import com.example.data.model.DrillType
import com.example.data.model.HudLayout
import com.example.data.model.SensitivityPreset
import com.example.data.model.TrainingRecord
import kotlinx.coroutines.flow.Flow

class TrainingRepository(private val trainingDao: TrainingDao) {
    val allRecords: Flow<List<TrainingRecord>> = trainingDao.getAllRecords()
    val allPresets: Flow<List<SensitivityPreset>> = trainingDao.getAllPresets()
    val allHudLayouts: Flow<List<HudLayout>> = trainingDao.getAllHudLayouts()

    fun getRecordsByType(type: DrillType): Flow<List<TrainingRecord>> =
        trainingDao.getRecordsByType(type)

    fun getBestScore(type: DrillType): Flow<Int?> =
        trainingDao.getBestScore(type)

    fun getBestReactionTime(): Flow<Long?> =
        trainingDao.getBestReactionTime()

    suspend fun saveRecord(record: TrainingRecord): Long =
        trainingDao.insertRecord(record)

    suspend fun clearAllRecords() =
        trainingDao.clearAllRecords()

    suspend fun savePreset(preset: SensitivityPreset): Long =
        trainingDao.insertPreset(preset)

    suspend fun deletePreset(id: Long) =
        trainingDao.deletePresetById(id)

    suspend fun saveHudLayout(layout: HudLayout): Long =
        trainingDao.insertHudLayout(layout)

    suspend fun deleteHudLayout(id: Long) =
        trainingDao.deleteHudLayoutById(id)
}
