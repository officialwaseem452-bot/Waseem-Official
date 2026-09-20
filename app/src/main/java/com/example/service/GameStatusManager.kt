package com.example.service

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class DetectedGameType(val displayName: String, val packageName: String) {
    NONE("No supported game detected", ""),
    FREE_FIRE("Free Fire", "com.dts.freefireth"),
    FREE_FIRE_MAX("Free Fire MAX", "com.dts.freefiremax")
}

data class GameStatusState(
    val detectedGame: DetectedGameType = DetectedGameType.NONE,
    val isGameRunning: Boolean = false,
    val isFreeFireInstalled: Boolean = false,
    val isFreeFireMaxInstalled: Boolean = false,
    val hasUsageStatsPermission: Boolean = false,
    val companionStatus: String = "ZERO SHOT: ACTIVE",
    val trainingStatus: String = "TRAINING MODE: READY"
)

class GameStatusManager(private val context: Context) {

    private val _statusState = MutableStateFlow(GameStatusState())
    val statusState: StateFlow<GameStatusState> = _statusState.asStateFlow()

    fun checkStatus() {
        val pm = context.packageManager
        val isFfInstalled = isPackageInstalled("com.dts.freefireth", pm)
        val isFfMaxInstalled = isPackageInstalled("com.dts.freefiremax", pm)
        val hasUsagePermission = hasUsageStatsPermission()

        var detected = DetectedGameType.NONE
        var isRunning = false

        if (hasUsagePermission) {
            val foregroundPackage = getForegroundPackageName()
            when (foregroundPackage) {
                "com.dts.freefiremax" -> {
                    detected = DetectedGameType.FREE_FIRE_MAX
                    isRunning = true
                }
                "com.dts.freefireth" -> {
                    detected = DetectedGameType.FREE_FIRE
                    isRunning = true
                }
                else -> {
                    if (isFfMaxInstalled) {
                        detected = DetectedGameType.FREE_FIRE_MAX
                        isRunning = false
                    } else if (isFfInstalled) {
                        detected = DetectedGameType.FREE_FIRE
                        isRunning = false
                    }
                }
            }
        } else {
            // Check installed packages
            if (isFfMaxInstalled) {
                detected = DetectedGameType.FREE_FIRE_MAX
            } else if (isFfInstalled) {
                detected = DetectedGameType.FREE_FIRE
            }
        }

        _statusState.value = GameStatusState(
            detectedGame = detected,
            isGameRunning = isRunning,
            isFreeFireInstalled = isFfInstalled,
            isFreeFireMaxInstalled = isFfMaxInstalled,
            hasUsageStatsPermission = hasUsagePermission,
            companionStatus = "ZERO SHOT: ACTIVE",
            trainingStatus = "TRAINING MODE: READY"
        )
    }

    private fun isPackageInstalled(packageName: String, pm: PackageManager): Boolean {
        return try {
            pm.getPackageInfo(packageName, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager ?: return false
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun getForegroundPackageName(): String? {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager ?: return null
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 1000 * 60 * 2 // 2 minutes ago
        val usageEvents = usageStatsManager.queryEvents(startTime, endTime) ?: return null

        val event = UsageEvents.Event()
        var lastForegroundPackage: String? = null

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                lastForegroundPackage = event.packageName
            }
        }
        return lastForegroundPackage
    }

    fun requestUsageStatsPermissionIntent(): Intent {
        return Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
    }
}
