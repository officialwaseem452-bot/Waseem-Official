package com.example.ui.screens

import android.os.SystemClock
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DrillType
import com.example.ui.DrillResultSummary
import com.example.ui.MainViewModel
import com.example.ui.theme.ZeroShotBackground
import com.example.ui.theme.ZeroShotError
import com.example.ui.theme.ZeroShotOutline
import com.example.ui.theme.ZeroShotPrimary
import com.example.ui.theme.ZeroShotSecondary
import com.example.ui.theme.ZeroShotSuccess
import com.example.ui.theme.ZeroShotSurface
import com.example.ui.theme.ZeroShotSurfaceVariant
import com.example.ui.theme.ZeroShotTertiary
import com.example.ui.theme.ZeroShotWarning
import kotlinx.coroutines.delay
import kotlin.math.hypot
import kotlin.random.Random

@Composable
fun AimTrainerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val drillTypes = DrillType.values().filter { it != DrillType.RECOIL }
    var selectedDrill by remember { mutableStateOf(viewModel.selectedDrillType.value) }

    var isDrillActive by remember { mutableStateOf(false) }
    var secondsLeft by remember { mutableIntStateOf(30) }
    var score by remember { mutableIntStateOf(0) }
    var hits by remember { mutableIntStateOf(0) }
    var misses by remember { mutableIntStateOf(0) }
    var headshots by remember { mutableIntStateOf(0) }
    var combo by remember { mutableIntStateOf(0) }
    var bestCombo by remember { mutableIntStateOf(0) }

    // Target positions & physics
    var targetX by remember { mutableFloatStateOf(0.5f) }
    var targetY by remember { mutableFloatStateOf(0.4f) }
    var targetRadius by remember { mutableFloatStateOf(44f) }
    var velX by remember { mutableFloatStateOf(0.008f) }
    var velY by remember { mutableFloatStateOf(0.006f) }

    // Reaction drill specific states
    var reactionState by remember { mutableStateOf("READY") } // "WAITING", "TRIGGERED", "FINISHED"
    var triggerStartTime by remember { mutableLongStateOf(0L) }
    var recordedReactionTime by remember { mutableLongStateOf(0L) }

    // Hit marker animation
    var lastHitX by remember { mutableFloatStateOf(-1f) }
    var lastHitY by remember { mutableFloatStateOf(-1f) }
    var lastHitIsHeadshot by remember { mutableStateOf(false) }
    var hitMarkerAlpha by remember { mutableFloatStateOf(0f) }

    // Tracking drill specific
    var isCurrentlyTracking by remember { mutableStateOf(false) }
    var trackingFramesOnTarget by remember { mutableIntStateOf(0) }
    var trackingTotalFrames by remember { mutableIntStateOf(0) }

    val soundEnabled = viewModel.soundEnabled.value
    val hapticEnabled = viewModel.vibrationEnabled.value

    fun spawnNewTarget() {
        targetX = Random.nextFloat().coerceIn(0.18f, 0.82f)
        targetY = Random.nextFloat().coerceIn(0.18f, 0.78f)
        velX = if (Random.nextBoolean()) 0.007f else -0.007f
        velY = if (Random.nextBoolean()) 0.005f else -0.005f
    }

    fun startDrill() {
        score = 0
        hits = 0
        misses = 0
        headshots = 0
        combo = 0
        bestCombo = 0
        secondsLeft = if (selectedDrill == DrillType.REACTION) 15 else 30
        trackingFramesOnTarget = 0
        trackingTotalFrames = 0
        recordedReactionTime = 0L
        spawnNewTarget()

        if (selectedDrill == DrillType.REACTION) {
            reactionState = "WAITING"
        }

        isDrillActive = true
        viewModel.audioHelper.playCountdown(soundEnabled)
    }

    fun endDrill() {
        isDrillActive = false
        reactionState = "READY"
        val reaction = if (selectedDrill == DrillType.REACTION) recordedReactionTime else 0L
        viewModel.onDrillFinished(
            type = selectedDrill,
            score = score,
            hits = hits,
            misses = misses,
            headshots = headshots,
            reactionTimeMs = reaction,
            durationSeconds = if (selectedDrill == DrillType.REACTION) 15 else 30
        )
    }

    // Timer Loop
    LaunchedEffect(isDrillActive) {
        if (isDrillActive) {
            while (secondsLeft > 0 && isDrillActive) {
                delay(1000)
                secondsLeft -= 1
            }
            if (isDrillActive) {
                endDrill()
            }
        }
    }

    // Game loop for moving targets / tracking / reaction delays
    LaunchedEffect(isDrillActive, selectedDrill, reactionState) {
        if (!isDrillActive) return@LaunchedEffect

        when (selectedDrill) {
            DrillType.MOVING, DrillType.TRACKING -> {
                while (isDrillActive) {
                    delay(16) // ~60fps
                    targetX += velX
                    targetY += velY
                    if (targetX <= 0.12f || targetX >= 0.88f) velX = -velX
                    if (targetY <= 0.15f || targetY >= 0.82f) velY = -velY

                    if (selectedDrill == DrillType.TRACKING) {
                        trackingTotalFrames++
                        if (isCurrentlyTracking) {
                            trackingFramesOnTarget++
                            score += 2
                            hits++
                        }
                    }
                }
            }
            DrillType.FLICK -> {
                while (isDrillActive) {
                    spawnNewTarget()
                    delay(700) // target expires after 700ms if not hit
                    misses++
                    combo = 0
                }
            }
            DrillType.REACTION -> {
                if (reactionState == "WAITING") {
                    val waitDelay = Random.nextLong(1500, 3200)
                    delay(waitDelay)
                    if (isDrillActive) {
                        reactionState = "TRIGGERED"
                        triggerStartTime = SystemClock.elapsedRealtime()
                        viewModel.audioHelper.playHit(soundEnabled)
                    }
                }
            }
            else -> {}
        }
    }

    // Fade hit marker
    LaunchedEffect(hitMarkerAlpha) {
        if (hitMarkerAlpha > 0f) {
            delay(40)
            hitMarkerAlpha = (hitMarkerAlpha - 0.15f).coerceAtLeast(0f)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ZeroShotBackground)
    ) {
        // Mode selector TabRow
        ScrollableTabRow(
            selectedTabIndex = drillTypes.indexOf(selectedDrill).coerceAtLeast(0),
            containerColor = ZeroShotSurface,
            contentColor = ZeroShotPrimary,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                val index = drillTypes.indexOf(selectedDrill).coerceAtLeast(0)
                if (index < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[index]),
                        color = ZeroShotPrimary,
                        height = 3.dp
                    )
                }
            }
        ) {
            drillTypes.forEach { drill ->
                val isSelected = drill == selectedDrill
                Tab(
                    selected = isSelected,
                    onClick = {
                        if (!isDrillActive) {
                            selectedDrill = drill
                            viewModel.selectDrillType(drill)
                        }
                    },
                    text = {
                        Text(
                            text = drill.displayName,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) ZeroShotPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // Live Heads-Up Display (Score, Time, Combo, Accuracy)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ZeroShotSurfaceVariant)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TIME",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${secondsLeft}s",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (secondsLeft <= 5) ZeroShotError else ZeroShotSecondary
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SCORE",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$score",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "HEADSHOTS",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$headshots",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZeroShotPrimary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "ACCURACY",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                val total = hits + misses
                val acc = if (total > 0) (hits.toFloat() / total * 100).toInt() else 100
                Text(
                    text = "$acc%",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (acc >= 75) ZeroShotSuccess else ZeroShotWarning
                )
            }
        }

        // Interactive Target Canvas
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF0C0E14))
                .border(1.dp, ZeroShotOutline)
        ) {
            val canvasWidth = constraints.maxWidth.toFloat()
            val canvasHeight = constraints.maxHeight.toFloat()

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(isDrillActive, selectedDrill, reactionState) {
                        if (!isDrillActive) return@pointerInput

                        if (selectedDrill == DrillType.TRACKING) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val px = targetX * canvasWidth
                                    val py = targetY * canvasHeight
                                    val dist = hypot(offset.x - px, offset.y - py)
                                    isCurrentlyTracking = dist <= 60f
                                },
                                onDrag = { change, _ ->
                                    val px = targetX * canvasWidth
                                    val py = targetY * canvasHeight
                                    val dist = hypot(change.position.x - px, change.position.y - py)
                                    isCurrentlyTracking = dist <= 60f
                                },
                                onDragEnd = { isCurrentlyTracking = false },
                                onDragCancel = { isCurrentlyTracking = false }
                            )
                        } else {
                            detectTapGestures { offset ->
                                if (selectedDrill == DrillType.REACTION) {
                                    if (reactionState == "TRIGGERED") {
                                        val reactionTime = SystemClock.elapsedRealtime() - triggerStartTime
                                        recordedReactionTime = reactionTime
                                        reactionState = "FINISHED"
                                        hits++
                                        score += (1000 - reactionTime.toInt()).coerceAtLeast(100)
                                        viewModel.audioHelper.playHeadshot(soundEnabled)
                                        viewModel.audioHelper.vibrateHit(hapticEnabled, true)
                                        // Auto end reaction test after hit
                                        endDrill()
                                    } else if (reactionState == "WAITING") {
                                        // False trigger / too early
                                        misses++
                                        score = (score - 50).coerceAtLeast(0)
                                        viewModel.audioHelper.playMiss(soundEnabled)
                                    }
                                    return@detectTapGestures
                                }

                                val px = targetX * canvasWidth
                                val py = targetY * canvasHeight

                                // In HEADSHOT drill: head is top circle, body is lower
                                val headRadius = 26f
                                val headCenterY = py - 20f
                                val distToHead = hypot(offset.x - px, offset.y - headCenterY)

                                val bodyRadius = 45f
                                val distToBody = hypot(offset.x - px, offset.y - (py + 15f))

                                if (distToHead <= headRadius) {
                                    // Critical Headshot!
                                    headshots++
                                    hits++
                                    combo++
                                    if (combo > bestCombo) bestCombo = combo
                                    score += 250 + (combo * 15)

                                    lastHitX = offset.x
                                    lastHitY = offset.y
                                    lastHitIsHeadshot = true
                                    hitMarkerAlpha = 1f

                                    viewModel.audioHelper.playHeadshot(soundEnabled)
                                    viewModel.audioHelper.vibrateHit(hapticEnabled, true)
                                    spawnNewTarget()
                                } else if (distToBody <= bodyRadius) {
                                    // Regular Body Hit
                                    hits++
                                    combo++
                                    if (combo > bestCombo) bestCombo = combo
                                    score += 100 + (combo * 5)

                                    lastHitX = offset.x
                                    lastHitY = offset.y
                                    lastHitIsHeadshot = false
                                    hitMarkerAlpha = 1f

                                    viewModel.audioHelper.playHit(soundEnabled)
                                    viewModel.audioHelper.vibrateHit(hapticEnabled, false)
                                    spawnNewTarget()
                                } else {
                                    // Miss
                                    misses++
                                    combo = 0
                                    viewModel.audioHelper.playMiss(soundEnabled)
                                }
                            }
                        }
                    }
            ) {
                // Background Tactical Grid Lines
                val gridSpacing = 60.dp.toPx()
                var gx = 0f
                while (gx < size.width) {
                    drawLine(
                        color = Color(0x15FFFFFF),
                        start = Offset(gx, 0f),
                        end = Offset(gx, size.height),
                        strokeWidth = 1f
                    )
                    gx += gridSpacing
                }
                var gy = 0f
                while (gy < size.height) {
                    drawLine(
                        color = Color(0x15FFFFFF),
                        start = Offset(0f, gy),
                        end = Offset(size.width, gy),
                        strokeWidth = 1f
                    )
                    gy += gridSpacing
                }

                if (isDrillActive) {
                    val px = targetX * size.width
                    val py = targetY * size.height

                    if (selectedDrill == DrillType.REACTION) {
                        // Big Reaction Reticle
                        val reticleColor = if (reactionState == "TRIGGERED") ZeroShotSuccess else ZeroShotError
                        drawCircle(
                            color = reticleColor.copy(alpha = 0.25f),
                            radius = 90.dp.toPx(),
                            center = Offset(size.width / 2f, size.height / 2f)
                        )
                        drawCircle(
                            color = reticleColor,
                            radius = 90.dp.toPx(),
                            center = Offset(size.width / 2f, size.height / 2f),
                            style = Stroke(width = 4.dp.toPx())
                        )
                        // Crosshair lines
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        drawLine(reticleColor, Offset(cx - 120.dp.toPx(), cy), Offset(cx + 120.dp.toPx(), cy), 3.dp.toPx())
                        drawLine(reticleColor, Offset(cx, cy - 120.dp.toPx()), Offset(cx, cy + 120.dp.toPx()), 3.dp.toPx())
                    } else if (selectedDrill == DrillType.TRACKING) {
                        // Cyber Drone Target
                        val droneColor = if (isCurrentlyTracking) ZeroShotSuccess else ZeroShotSecondary
                        drawCircle(
                            color = droneColor.copy(alpha = 0.3f),
                            radius = 50.dp.toPx(),
                            center = Offset(px, py)
                        )
                        drawCircle(
                            color = droneColor,
                            radius = 50.dp.toPx(),
                            center = Offset(px, py),
                            style = Stroke(width = 3.dp.toPx())
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 12.dp.toPx(),
                            center = Offset(px, py)
                        )
                    } else {
                        // Standard Target with distinct Headshot Zone & Body Hitbox
                        // Lower body
                        drawOval(
                            color = Color(0xFF1E2838),
                            topLeft = Offset(px - 32.dp.toPx(), py - 10.dp.toPx()),
                            size = Size(64.dp.toPx(), 70.dp.toPx())
                        )
                        drawOval(
                            color = ZeroShotSecondary,
                            topLeft = Offset(px - 32.dp.toPx(), py - 10.dp.toPx()),
                            size = Size(64.dp.toPx(), 70.dp.toPx()),
                            style = Stroke(width = 2.dp.toPx())
                        )

                        // Upper Headshot Circle (Critical zone)
                        val headY = py - 32.dp.toPx()
                        drawCircle(
                            color = ZeroShotPrimary.copy(alpha = 0.4f),
                            radius = 24.dp.toPx(),
                            center = Offset(px, headY)
                        )
                        drawCircle(
                            color = ZeroShotPrimary,
                            radius = 24.dp.toPx(),
                            center = Offset(px, headY),
                            style = Stroke(width = 3.dp.toPx())
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 6.dp.toPx(),
                            center = Offset(px, headY)
                        )
                    }

                    // Hit Marker display
                    if (hitMarkerAlpha > 0f && lastHitX > 0) {
                        val hitColor = if (lastHitIsHeadshot) ZeroShotPrimary else Color.White
                        val markerSize = if (lastHitIsHeadshot) 24.dp.toPx() else 14.dp.toPx()
                        drawLine(
                            color = hitColor.copy(alpha = hitMarkerAlpha),
                            start = Offset(lastHitX - markerSize, lastHitY - markerSize),
                            end = Offset(lastHitX + markerSize, lastHitY + markerSize),
                            strokeWidth = 3.dp.toPx()
                        )
                        drawLine(
                            color = hitColor.copy(alpha = hitMarkerAlpha),
                            start = Offset(lastHitX - markerSize, lastHitY + markerSize),
                            end = Offset(lastHitX + markerSize, lastHitY - markerSize),
                            strokeWidth = 3.dp.toPx()
                        )
                    }
                }
            }

            // Overlay instructions when idle
            if (!isDrillActive) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(16.dp))
                        .background(ZeroShotSurface.copy(alpha = 0.95f))
                        .border(1.dp, ZeroShotPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = selectedDrill.displayName.uppercase(),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = ZeroShotPrimary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when (selectedDrill) {
                            DrillType.HEADSHOT -> "Aim for the red head circle to trigger 250+ critical headshot points."
                            DrillType.MOVING -> "Targets traverse unpredictably. Lead your crosshair and strike."
                            DrillType.REACTION -> "Wait for reticle to flash NEON GREEN, then instant tap!"
                            DrillType.FLICK -> "Targets vanish rapidly (700ms). Snap your flick shots instantly."
                            DrillType.TRACKING -> "Press and hold your finger inside the gliding cyber drone."
                            else -> "Tap targets with maximum speed and precision."
                        },
                        fontSize = 13.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(260.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { startDrill() },
                        colors = ButtonDefaults.buttonColors(containerColor = ZeroShotPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("start_drill_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "START DRILL (30s)", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Reaction drill active banner
            if (isDrillActive && selectedDrill == DrillType.REACTION) {
                Text(
                    text = when (reactionState) {
                        "WAITING" -> "WAIT FOR GREEN FLASH..."
                        "TRIGGERED" -> "⚡ TAP NOW! ⚡"
                        else -> "RECORDING REACTION..."
                    },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (reactionState == "TRIGGERED") ZeroShotSuccess else ZeroShotError,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 24.dp)
                )
            }
        }

        // Bottom Controls Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ZeroShotSurface)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "COMBO: ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${combo}x",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (combo >= 5) ZeroShotTertiary else Color.White
                )
            }

            if (isDrillActive) {
                Button(
                    onClick = { endDrill() },
                    colors = ButtonDefaults.buttonColors(containerColor = ZeroShotError),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("stop_drill_button")
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("STOP DRILL")
                }
            } else {
                Button(
                    onClick = { startDrill() },
                    colors = ButtonDefaults.buttonColors(containerColor = ZeroShotPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("restart_drill_button")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("START DRILL")
                }
            }
        }
    }

    // Drill Results Dialog
    val summary = viewModel.drillSummaryDialog.value
    if (summary != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDrillSummary() },
            title = {
                Text(
                    text = "DRILL COMPLETED",
                    fontWeight = FontWeight.ExtraBold,
                    color = ZeroShotPrimary,
                    letterSpacing = 1.sp
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = summary.drillType.displayName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Score Card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ZeroShotSurfaceVariant)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("FINAL SCORE", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${summary.score}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = ZeroShotSecondary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("ACCURACY", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${summary.accuracy.toInt()}%", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = ZeroShotSuccess)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Detail Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Headshot Hits: ${summary.headshots}", fontSize = 13.sp, color = Color.White)
                        Text("Misses: ${summary.misses}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    if (summary.reactionTimeMs > 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Reaction Time: ${summary.reactionTimeMs}ms",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ZeroShotTertiary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Result saved to Player Stats record.",
                        fontSize = 11.sp,
                        color = ZeroShotSuccess
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissDrillSummary() },
                    colors = ButtonDefaults.buttonColors(containerColor = ZeroShotPrimary)
                ) {
                    Text("CONTINUE")
                }
            },
            containerColor = ZeroShotSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
