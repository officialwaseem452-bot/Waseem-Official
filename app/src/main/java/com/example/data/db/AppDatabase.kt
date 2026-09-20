package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.HudLayout
import com.example.data.model.SensitivityPreset
import com.example.data.model.TrainingRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TrainingRecord::class,
        SensitivityPreset::class,
        HudLayout::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trainingDao(): TrainingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "zero_shot_training.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed defaults in background
                        scope.launch(Dispatchers.IO) {
                            val dao = getDatabase(context, scope).trainingDao()
                            seedDefaultPresets(dao)
                            seedDefaultHudLayouts(dao)
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedDefaultPresets(dao: TrainingDao) {
            dao.insertPreset(
                SensitivityPreset(
                    name = "Esports One-Tap Pro",
                    generalSens = 98,
                    redDotSens = 95,
                    scope2xSens = 90,
                    scope4xSens = 88,
                    sniperSens = 58,
                    freeLookSens = 70,
                    deviceCategory = "Mobile (90/120Hz)",
                    dpi = 440,
                    isCustom = false,
                    notes = "Optimized for fast drag headshots and quick 360 camera control."
                )
            )
            dao.insertPreset(
                SensitivityPreset(
                    name = "Aggressive SMG Rush",
                    generalSens = 100,
                    redDotSens = 98,
                    scope2xSens = 94,
                    scope4xSens = 90,
                    sniperSens = 65,
                    freeLookSens = 80,
                    deviceCategory = "Mobile (High DPI)",
                    dpi = 500,
                    isCustom = false,
                    notes = "Maximum responsiveness for close-range MP40/UMP jump shots."
                )
            )
            dao.insertPreset(
                SensitivityPreset(
                    name = "Long-Range Sniper Anchor",
                    generalSens = 85,
                    redDotSens = 80,
                    scope2xSens = 78,
                    scope4xSens = 75,
                    sniperSens = 45,
                    freeLookSens = 65,
                    deviceCategory = "Universal Device",
                    dpi = 400,
                    isCustom = false,
                    notes = "Steady crosshair placement with minimal overshooting for AWM/M82B."
                )
            )
            dao.insertPreset(
                SensitivityPreset(
                    name = "PC / Emulator Precision",
                    generalSens = 48,
                    redDotSens = 45,
                    scope2xSens = 42,
                    scope4xSens = 38,
                    sniperSens = 26,
                    freeLookSens = 50,
                    deviceCategory = "Windows PC / Mouse",
                    dpi = 800,
                    isCustom = false,
                    notes = "Low DPI mouse settings calibrated for zero mouse-acceleration drift."
                )
            )
        }

        private suspend fun seedDefaultHudLayouts(dao: TrainingDao) {
            dao.insertHudLayout(
                HudLayout(
                    name = "2-Finger Balanced Starter",
                    fingerCount = 2,
                    fireButtonSize = 52,
                    fireButtonXPercent = 0.82f,
                    fireButtonYPercent = 0.70f,
                    leftFireButtonXPercent = 0.16f,
                    leftFireButtonYPercent = 0.32f,
                    scopeButtonXPercent = 0.86f,
                    scopeButtonYPercent = 0.38f,
                    jumpButtonXPercent = 0.90f,
                    jumpButtonYPercent = 0.52f,
                    crouchButtonXPercent = 0.76f,
                    crouchButtonYPercent = 0.82f,
                    glooWallXPercent = 0.18f,
                    glooWallYPercent = 0.65f,
                    isDefault = true,
                    playstyleTip = "Right thumb controls drag fire & camera; left thumb handles joystick & fast gloo wall."
                )
            )
            dao.insertHudLayout(
                HudLayout(
                    name = "3-Finger Claw Tournament",
                    fingerCount = 3,
                    fireButtonSize = 48,
                    fireButtonXPercent = 0.82f,
                    fireButtonYPercent = 0.68f,
                    leftFireButtonXPercent = 0.18f,
                    leftFireButtonYPercent = 0.16f,
                    scopeButtonXPercent = 0.86f,
                    scopeButtonYPercent = 0.35f,
                    jumpButtonXPercent = 0.90f,
                    jumpButtonYPercent = 0.50f,
                    crouchButtonXPercent = 0.76f,
                    crouchButtonYPercent = 0.82f,
                    glooWallXPercent = 0.15f,
                    glooWallYPercent = 0.60f,
                    isDefault = true,
                    playstyleTip = "Left index finger fires left button for stationary jump-shots; right thumb drags freely."
                )
            )
            dao.insertHudLayout(
                HudLayout(
                    name = "4-Finger Master Claw",
                    fingerCount = 4,
                    fireButtonSize = 44,
                    fireButtonXPercent = 0.82f,
                    fireButtonYPercent = 0.72f,
                    leftFireButtonXPercent = 0.16f,
                    leftFireButtonYPercent = 0.15f,
                    scopeButtonXPercent = 0.84f,
                    scopeButtonYPercent = 0.15f,
                    jumpButtonXPercent = 0.92f,
                    jumpButtonYPercent = 0.48f,
                    crouchButtonXPercent = 0.76f,
                    crouchButtonYPercent = 0.84f,
                    glooWallXPercent = 0.16f,
                    glooWallYPercent = 0.58f,
                    isDefault = true,
                    playstyleTip = "Both index fingers operate upper triggers (fire & scope) for instantaneous reaction times."
                )
            )
        }
    }
}
