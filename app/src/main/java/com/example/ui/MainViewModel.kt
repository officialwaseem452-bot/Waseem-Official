package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.DrillType
import com.example.data.model.HudLayout
import com.example.data.model.SensitivityPreset
import com.example.data.model.TrainingRecord
import com.example.data.model.Weapon
import com.example.data.model.WeaponCategory
import com.example.data.model.WeaponDatabase
import com.example.data.repository.TrainingRepository
import com.example.service.FloatingOverlayService
import com.example.service.GameStatusManager
import com.example.service.GameStatusState
import com.example.ui.util.AudioFeedbackHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppDestination(val label: String) {
    DASHBOARD("Dashboard"),
    AIM_TRAINER("Aim Trainer"),
    SENSITIVITY("Sensitivity"),
    HUD_GUIDE("HUD Guide"),
    WEAPON_TRAINING("Weapons"),
    PLAYER_STATS("Stats"),
    SETTINGS("Settings")
}

data class DrillResultSummary(
    val drillType: DrillType,
    val score: Int,
    val hits: Int,
    val misses: Int,
    val headshots: Int,
    val accuracy: Float,
    val reactionTimeMs: Long,
    val isNewPersonalBest: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TrainingRepository
    val gameStatusManager: GameStatusManager = GameStatusManager(application)
    val audioHelper: AudioFeedbackHelper = AudioFeedbackHelper(application)

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = TrainingRepository(db.trainingDao())
        gameStatusManager.checkStatus()
    }

    // Navigation
    private val _currentDestination = MutableStateFlow(AppDestination.DASHBOARD)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    fun navigateTo(destination: AppDestination) {
        _currentDestination.value = destination
    }

    // Game Status
    val gameStatus: StateFlow<GameStatusState> = gameStatusManager.statusState

    fun refreshGameStatus() {
        gameStatusManager.checkStatus()
    }

    // Database records
    val allRecords: StateFlow<List<TrainingRecord>> = repository.allRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPresets: StateFlow<List<SensitivityPreset>> = repository.allPresets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allHudLayouts: StateFlow<List<HudLayout>> = repository.allHudLayouts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Settings
    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _vibrationEnabled = MutableStateFlow(true)
    val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("English")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _overlayEnabled = MutableStateFlow(false)
    val overlayEnabled: StateFlow<Boolean> = _overlayEnabled.asStateFlow()

    fun toggleSound(enabled: Boolean) { _soundEnabled.value = enabled }
    fun toggleVibration(enabled: Boolean) { _vibrationEnabled.value = enabled }
    fun setLanguage(lang: String) { _selectedLanguage.value = lang }

    fun toggleOverlay(context: Context, enabled: Boolean) {
        _overlayEnabled.value = enabled
        if (enabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}")
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                _overlayEnabled.value = false
            } else {
                try {
                    val intent = Intent(context, FloatingOverlayService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent)
                    } else {
                        context.startService(intent)
                    }
                } catch (_: Exception) {
                    _overlayEnabled.value = false
                }
            }
        } else {
            try {
                val intent = Intent(context, FloatingOverlayService::class.java)
                context.stopService(intent)
            } catch (_: Exception) { }
        }
    }

    // Sensitivity State
    private val _generalSens = MutableStateFlow(98)
    val generalSens: StateFlow<Int> = _generalSens.asStateFlow()

    private val _redDotSens = MutableStateFlow(95)
    val redDotSens: StateFlow<Int> = _redDotSens.asStateFlow()

    private val _scope2xSens = MutableStateFlow(90)
    val scope2xSens: StateFlow<Int> = _scope2xSens.asStateFlow()

    private val _scope4xSens = MutableStateFlow(88)
    val scope4xSens: StateFlow<Int> = _scope4xSens.asStateFlow()

    private val _sniperSens = MutableStateFlow(58)
    val sniperSens: StateFlow<Int> = _sniperSens.asStateFlow()

    private val _freeLookSens = MutableStateFlow(72)
    val freeLookSens: StateFlow<Int> = _freeLookSens.asStateFlow()

    private val _activePresetName = MutableStateFlow("Esports One-Tap Pro")
    val activePresetName: StateFlow<String> = _activePresetName.asStateFlow()

    fun updateSensitivity(
        general: Int? = null,
        redDot: Int? = null,
        scope2x: Int? = null,
        scope4x: Int? = null,
        sniper: Int? = null,
        freeLook: Int? = null
    ) {
        general?.let { _generalSens.value = it.coerceIn(0, 100) }
        redDot?.let { _redDotSens.value = it.coerceIn(0, 100) }
        scope2x?.let { _scope2xSens.value = it.coerceIn(0, 100) }
        scope4x?.let { _scope4xSens.value = it.coerceIn(0, 100) }
        sniper?.let { _sniperSens.value = it.coerceIn(0, 100) }
        freeLook?.let { _freeLookSens.value = it.coerceIn(0, 100) }
    }

    fun applyPreset(preset: SensitivityPreset) {
        _activePresetName.value = preset.name
        _generalSens.value = preset.generalSens
        _redDotSens.value = preset.redDotSens
        _scope2xSens.value = preset.scope2xSens
        _scope4xSens.value = preset.scope4xSens
        _sniperSens.value = preset.sniperSens
        _freeLookSens.value = preset.freeLookSens
    }

    fun saveCurrentAsPreset(name: String, device: String, dpi: Int, notes: String) {
        viewModelScope.launch {
            val preset = SensitivityPreset(
                name = name.ifBlank { "Custom Preset #${System.currentTimeMillis() % 1000}" },
                generalSens = _generalSens.value,
                redDotSens = _redDotSens.value,
                scope2xSens = _scope2xSens.value,
                scope4xSens = _scope4xSens.value,
                sniperSens = _sniperSens.value,
                freeLookSens = _freeLookSens.value,
                deviceCategory = device,
                dpi = dpi,
                isCustom = true,
                notes = notes
            )
            repository.savePreset(preset)
            _activePresetName.value = preset.name
        }
    }

    fun deletePreset(id: Long) {
        viewModelScope.launch {
            repository.deletePreset(id)
        }
    }

    // Recommendation Generator based on manually entered specs
    fun calculateRecommendedSensitivity(
        deviceType: String, // "Compact Phone", "Large Flagship", "Tablet", "PC Emulator"
        refreshRateHz: Int, // 60, 90, 120, 144
        screenDpi: Int,     // 320 .. 800
        playstyle: String   // "One-Tap Headshot", "Aggressive Close SMG", "Sniper Anchor", "Balanced"
    ) {
        val baseMultiplier = when (refreshRateHz) {
            144 -> 0.88f
            120 -> 0.92f
            90 -> 0.96f
            else -> 1.0f
        }

        val dpiScale = when {
            screenDpi >= 600 -> 0.85f
            screenDpi >= 440 -> 0.95f
            screenDpi <= 360 -> 1.10f
            else -> 1.0f
        }

        val baseValues = when (playstyle) {
            "One-Tap Headshot" -> listOf(99, 96, 92, 90, 60, 75)
            "Aggressive Close SMG" -> listOf(100, 98, 95, 92, 65, 85)
            "Sniper Anchor" -> listOf(85, 82, 78, 72, 42, 60)
            else -> listOf(95, 90, 85, 80, 52, 70) // Balanced
        }
        val genBase = baseValues[0]
        val redBase = baseValues[1]
        val s2Base = baseValues[2]
        val s4Base = baseValues[3]
        val snipBase = baseValues[4]
        val flBase = baseValues[5]

        val isPc = deviceType.contains("PC", ignoreCase = true)
        val finalScale = if (isPc) 0.50f else (baseMultiplier * dpiScale)

        val calcGen = (genBase * finalScale).toInt().coerceIn(20, 100)
        val calcRed = (redBase * finalScale).toInt().coerceIn(20, 100)
        val calcS2 = (s2Base * finalScale).toInt().coerceIn(15, 100)
        val calcS4 = (s4Base * finalScale).toInt().coerceIn(15, 100)
        val calcSnip = (snipBase * finalScale).toInt().coerceIn(10, 100)
        val calcFl = (flBase * finalScale).toInt().coerceIn(10, 100)

        updateSensitivity(calcGen, calcRed, calcS2, calcS4, calcSnip, calcFl)
        _activePresetName.value = "Recommended: $deviceType (${refreshRateHz}Hz)"
    }

    // Aim Trainer State
    private val _selectedDrillType = MutableStateFlow(DrillType.HEADSHOT)
    val selectedDrillType: StateFlow<DrillType> = _selectedDrillType.asStateFlow()

    private val _drillSummaryDialog = MutableStateFlow<DrillResultSummary?>(null)
    val drillSummaryDialog: StateFlow<DrillResultSummary?> = _drillSummaryDialog.asStateFlow()

    fun selectDrillType(type: DrillType) {
        _selectedDrillType.value = type
    }

    fun dismissDrillSummary() {
        _drillSummaryDialog.value = null
    }

    fun onDrillFinished(
        type: DrillType,
        score: Int,
        hits: Int,
        misses: Int,
        headshots: Int,
        reactionTimeMs: Long,
        durationSeconds: Int
    ) {
        val totalShots = hits + misses
        val accuracy = if (totalShots > 0) (hits.toFloat() / totalShots) * 100f else 0f

        viewModelScope.launch {
            val record = TrainingRecord(
                drillType = type,
                score = score,
                hits = hits,
                misses = misses,
                headshots = headshots,
                accuracyPercentage = accuracy,
                reactionTimeMs = reactionTimeMs,
                durationSeconds = durationSeconds
            )
            repository.saveRecord(record)

            _drillSummaryDialog.value = DrillResultSummary(
                drillType = type,
                score = score,
                hits = hits,
                misses = misses,
                headshots = headshots,
                accuracy = accuracy,
                reactionTimeMs = reactionTimeMs
            )
            audioHelper.playComplete(_soundEnabled.value)
        }
    }

    fun clearAllStats() {
        viewModelScope.launch {
            repository.clearAllRecords()
        }
    }

    // Weapon Training
    private val _selectedCategory = MutableStateFlow(WeaponCategory.AR)
    val selectedCategory: StateFlow<WeaponCategory> = _selectedCategory.asStateFlow()

    private val _selectedWeapon = MutableStateFlow(WeaponDatabase.weapons.first())
    val selectedWeapon: StateFlow<Weapon> = _selectedWeapon.asStateFlow()

    fun selectWeaponCategory(category: WeaponCategory) {
        _selectedCategory.value = category
        val firstInCat = WeaponDatabase.weapons.firstOrNull { it.category == category }
        firstInCat?.let { _selectedWeapon.value = it }
    }

    fun selectWeapon(weapon: Weapon) {
        _selectedWeapon.value = weapon
    }

    // HUD Guide State
    private val _selectedFingerMode = MutableStateFlow(2)
    val selectedFingerMode: StateFlow<Int> = _selectedFingerMode.asStateFlow()

    private val _fireButtonSizePercent = MutableStateFlow(52)
    val fireButtonSizePercent: StateFlow<Int> = _fireButtonSizePercent.asStateFlow()

    fun setFingerMode(fingers: Int) {
        _selectedFingerMode.value = fingers
    }

    fun setFireButtonSize(size: Int) {
        _fireButtonSizePercent.value = size.coerceIn(30, 80)
    }

    override fun onCleared() {
        super.onCleared()
        audioHelper.release()
    }
}
