package com.example.ui.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class AudioFeedbackHelper(private val context: Context) {
    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 70)
        } catch (_: Exception) { }
    }

    fun playHit(soundEnabled: Boolean) {
        if (!soundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 40)
        } catch (_: Exception) { }
    }

    fun playHeadshot(soundEnabled: Boolean) {
        if (!soundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_DTMF_D, 90)
        } catch (_: Exception) { }
    }

    fun playMiss(soundEnabled: Boolean) {
        if (!soundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 50)
        } catch (_: Exception) { }
    }

    fun playCountdown(soundEnabled: Boolean) {
        if (!soundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 60)
        } catch (_: Exception) { }
    }

    fun playComplete(soundEnabled: Boolean) {
        if (!soundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 180)
        } catch (_: Exception) { }
    }

    fun vibrateHit(hapticsEnabled: Boolean, isHeadshot: Boolean) {
        if (!hapticsEnabled) return
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val duration = if (isHeadshot) 60L else 30L
                val amplitude = if (isHeadshot) VibrationEffect.DEFAULT_AMPLITUDE else 120
                vibrator?.vibrate(VibrationEffect.createOneShot(duration, amplitude))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(if (isHeadshot) 50L else 25L)
            }
        } catch (_: Exception) { }
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}
