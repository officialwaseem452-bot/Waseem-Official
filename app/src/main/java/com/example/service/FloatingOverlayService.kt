package com.example.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.MainActivity
import com.example.R

class FloatingOverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as? WindowManager

        val channelId = "zero_shot_overlay_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Zero Shot Companion",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)

            val notification: Notification = Notification.Builder(this, channelId)
                .setContentTitle("ZERO SHOT TRAINING PANEL")
                .setContentText("Active Training Companion • Overlay Enabled")
                .setSmallIcon(R.drawable.ic_zero_shot_logo)
                .build()
            startForeground(1001, notification)
        }

        createOverlayView()
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun createOverlayView() {
        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 40
            y = 200
        }

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(24, 16, 24, 16)
            gravity = Gravity.CENTER_VERTICAL

            val backgroundDrawable = GradientDrawable().apply {
                setColor(Color.parseColor("#E60D0E12"))
                cornerRadius = 32f
                setStroke(3, Color.parseColor("#FF3D00"))
            }
            background = backgroundDrawable
        }

        // Logo / crosshair icon indicator
        val iconView = ImageView(this).apply {
            setImageResource(R.drawable.ic_zero_shot_logo)
            val size = (32 * resources.displayMetrics.density).toInt()
            layoutParams = LinearLayout.LayoutParams(size, size).apply {
                marginEnd = 16
            }
        }

        // Status Text container
        val textContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val titleView = TextView(this).apply {
            text = "ZERO SHOT"
            setTextColor(Color.WHITE)
            textSize = 12f
            paint.isFakeBoldText = true
        }

        val statusView = TextView(this).apply {
            text = "ACTIVE • READY"
            setTextColor(Color.parseColor("#00E5FF"))
            textSize = 10f
            paint.isFakeBoldText = true
        }

        textContainer.addView(titleView)
        textContainer.addView(statusView)

        // Close 'X' button
        val closeButton = TextView(this).apply {
            text = " ✕ "
            setTextColor(Color.parseColor("#888888"))
            textSize = 14f
            setPadding(16, 0, 0, 0)
            setOnClickListener {
                stopSelf()
            }
        }

        container.addView(iconView)
        container.addView(textContainer)
        container.addView(closeButton)

        // Dragging & Click to reopen
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var isClick = false

        container.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isClick = true
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - initialTouchX).toInt()
                    val dy = (event.rawY - initialTouchY).toInt()
                    if (Math.abs(dx) > 10 || Math.abs(dy) > 10) {
                        isClick = false
                    }
                    params.x = initialX + dx
                    params.y = initialY + dy
                    windowManager?.updateViewLayout(container, params)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (isClick) {
                        val launchIntent = Intent(this, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(launchIntent)
                    }
                    true
                }
                else -> false
            }
        }

        overlayView = container
        try {
            windowManager?.addView(container, params)
        } catch (_: Exception) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayView?.let {
            try {
                windowManager?.removeView(it)
            } catch (_: Exception) { }
        }
        overlayView = null
    }

    companion object {
        var isRunning: Boolean = false
    }
}
