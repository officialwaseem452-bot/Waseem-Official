package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.ControlCamera
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.components.ZeroShotCard
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

@Composable
fun HudGuideScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val selectedFingers by viewModel.selectedFingerMode.collectAsState()
    val fireButtonSize by viewModel.fireButtonSizePercent.collectAsState()
    val hudLayouts by viewModel.allHudLayouts.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Visualizer & Compare, 1: Fire Button Size Guide, 2: Drag Techniques

    val fingerTabs = listOf(2, 3, 4)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ZeroShotBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        SectionHeader(
            title = "HUD & CONTROLS GUIDE",
            subtitle = "Tournament claw setups & drag headshot ergonomics",
            badgeText = "${selectedFingers}-FINGER CLAW"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Navigation Tabs (Layouts vs Size Guide vs Drag Angle)
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = ZeroShotSurface,
            contentColor = ZeroShotPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = ZeroShotPrimary,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("HUD Layouts", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("Fire Button Guide", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = { Text("Drag Techniques", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (activeTab == 0) {
            // Finger Mode Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                fingerTabs.forEach { fingers ->
                    val isSelected = fingers == selectedFingers
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) ZeroShotPrimary else ZeroShotSurfaceVariant)
                            .clickable { viewModel.setFingerMode(fingers) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$fingers-FINGER",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive HUD Visualizer Canvas
            Text(
                text = "INTERACTIVE HUD SIMULATOR PREVIEW",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ZeroShotSecondary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF090B0E))
                    .border(BorderStroke(2.dp, ZeroShotOutline), RoundedCornerShape(12.dp))
            ) {
                val cw = constraints.maxWidth.toFloat()
                val ch = constraints.maxHeight.toFloat()

                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Safe zone border
                    drawRect(
                        color = Color(0x22FFFFFF),
                        size = Size(cw, ch),
                        style = Stroke(1.dp.toPx())
                    )

                    // Joystick base (Left)
                    drawCircle(
                        color = Color(0x3300E5FF),
                        radius = 36.dp.toPx(),
                        center = Offset(cw * 0.18f, ch * 0.72f)
                    )
                    drawCircle(
                        color = ZeroShotSecondary,
                        radius = 16.dp.toPx(),
                        center = Offset(cw * 0.18f, ch * 0.72f)
                    )

                    // Gloo Wall Button (Left)
                    drawCircle(
                        color = Color(0x44FFAB00),
                        radius = 24.dp.toPx(),
                        center = Offset(cw * 0.22f, ch * 0.42f)
                    )
                    drawCircle(
                        color = ZeroShotTertiary,
                        radius = 24.dp.toPx(),
                        center = Offset(cw * 0.22f, ch * 0.42f),
                        style = Stroke(2.dp.toPx())
                    )

                    // Left Fire Button (If 3 or 4 finger)
                    if (selectedFingers >= 3) {
                        drawCircle(
                            color = Color(0x44FF3D00),
                            radius = 26.dp.toPx(),
                            center = Offset(cw * 0.16f, ch * 0.18f)
                        )
                        drawCircle(
                            color = ZeroShotPrimary,
                            radius = 26.dp.toPx(),
                            center = Offset(cw * 0.16f, ch * 0.18f),
                            style = Stroke(2.dp.toPx())
                        )
                    }

                    // Scope Button (Right upper)
                    val scopeY = if (selectedFingers == 4) ch * 0.18f else ch * 0.35f
                    val scopeX = cw * 0.84f
                    drawCircle(
                        color = Color(0x4400E5FF),
                        radius = 24.dp.toPx(),
                        center = Offset(scopeX, scopeY)
                    )
                    drawCircle(
                        color = ZeroShotSecondary,
                        radius = 24.dp.toPx(),
                        center = Offset(scopeX, scopeY),
                        style = Stroke(2.dp.toPx())
                    )

                    // Jump Button
                    drawCircle(
                        color = Color(0x33FFFFFF),
                        radius = 22.dp.toPx(),
                        center = Offset(cw * 0.90f, ch * 0.50f)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 22.dp.toPx(),
                        center = Offset(cw * 0.90f, ch * 0.50f),
                        style = Stroke(2.dp.toPx())
                    )

                    // Crouch Button
                    drawCircle(
                        color = Color(0x33FFFFFF),
                        radius = 22.dp.toPx(),
                        center = Offset(cw * 0.76f, ch * 0.82f)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 22.dp.toPx(),
                        center = Offset(cw * 0.76f, ch * 0.82f),
                        style = Stroke(2.dp.toPx())
                    )

                    // Main Right Fire Button (Scaled according to fireButtonSize percentage)
                    val fbRadius = (fireButtonSize.toFloat() / 100f) * 48.dp.toPx()
                    val fbX = cw * 0.80f
                    val fbY = ch * 0.70f
                    drawCircle(
                        color = ZeroShotPrimary.copy(alpha = 0.3f),
                        radius = fbRadius,
                        center = Offset(fbX, fbY)
                    )
                    drawCircle(
                        color = ZeroShotPrimary,
                        radius = fbRadius,
                        center = Offset(fbX, fbY),
                        style = Stroke(3.dp.toPx())
                    )
                    // Inner drag arrow
                    drawLine(
                        color = Color.White,
                        start = Offset(fbX, fbY + 8.dp.toPx()),
                        end = Offset(fbX, fbY - 14.dp.toPx()),
                        strokeWidth = 3.dp.toPx()
                    )
                }

                // Overlay watermark / tags
                Text(
                    text = "RIGHT FIRE BUTTON (${fireButtonSize}%)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZeroShotPrimary,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Layout Breakdown & Finger Roles
            ZeroShotCard {
                Text(
                    text = "${selectedFingers}-FINGER PRO RECOMMENDATIONS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = ZeroShotPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                when (selectedFingers) {
                    2 -> {
                        Text("• Left Thumb: Movement Joystick + Gloo Wall placement", fontSize = 12.sp, color = Color.White)
                        Text("• Right Thumb: Camera Look + Drag Fire + Jump/Crouch", fontSize = 12.sp, color = Color.White)
                        Text("• Best For: Casual players, beginner drag headshots, easy grip.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("• Tip: Keep fire button slightly lower on screen to give thumb maximum upward dragging travel.", fontSize = 11.sp, color = ZeroShotSecondary)
                    }
                    3 -> {
                        Text("• Left Index: Upper Left Fire Button (Instant jump-shot trigger)", fontSize = 12.sp, color = Color.White)
                        Text("• Left Thumb: Joystick Movement + Weapon Switch", fontSize = 12.sp, color = Color.White)
                        Text("• Right Thumb: Aim Drag + Scope + Fast Gloo Wall", fontSize = 12.sp, color = Color.White)
                        Text("• Best For: Aggressive rushers, MP40/UMP spray with continuous movement.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("• Tip: Vastly increases drag headshot consistency because camera thumb isn't locked firing.", fontSize = 11.sp, color = ZeroShotSecondary)
                    }
                    4 -> {
                        Text("• Left Index: Top Fire Button", fontSize = 12.sp, color = Color.White)
                        Text("• Right Index: Top Scope Button + Fast Switch", fontSize = 12.sp, color = Color.White)
                        Text("• Left Thumb: Continuous Joystick Strafe", fontSize = 12.sp, color = Color.White)
                        Text("• Right Thumb: Precision Aim Flick & Jump-Crouch", fontSize = 12.sp, color = Color.White)
                        Text("• Best For: Esports tournament play, rapid 360 Gloo Wall, zero delay AWM switches.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else if (activeTab == 1) {
            // Fire Button Size Guide
            ZeroShotCard(borderColor = ZeroShotPrimary) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FIRE BUTTON SIZE CALIBRATOR",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        text = "$fireButtonSize%",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = ZeroShotPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = fireButtonSize.toFloat(),
                    onValueChange = { viewModel.setFireButtonSize(it.toInt()) },
                    valueRange = 35f..75f,
                    colors = SliderDefaults.colors(
                        thumbColor = ZeroShotPrimary,
                        activeTrackColor = ZeroShotPrimary,
                        inactiveTrackColor = ZeroShotSurfaceVariant
                    ),
                    modifier = Modifier.testTag("fire_button_size_slider")
                )

                // Sweet Spot Evaluation
                val (rating, color, desc) = when {
                    fireButtonSize in 45..54 -> Triple("OPTIMAL SWEET SPOT (45% - 54%)", ZeroShotSuccess, "Perfect balance! Provides comfortable thumb contact while preserving maximum vertical screen space for upward drag headshots.")
                    fireButtonSize < 45 -> Triple("SMALL / FAST (Under 45%)", ZeroShotSecondary, "Very fast drag travel, but higher chance of missing the button under panic rush pressure.")
                    else -> Triple("LARGE / STABLE (Over 55%)", ZeroShotWarning, "Easy to tap without looking, but limits vertical swipe distance, causing drags to stop at chest level.")
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.15f))
                        .border(BorderStroke(1.dp, color), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(text = rating, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = color)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = desc, fontSize = 11.sp, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            ZeroShotCard {
                Text(
                    text = "DPI vs BUTTON SIZE RELATION",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = ZeroShotSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("• Default DPI (360 - 411): Recommended Button Size 48% - 52%.", fontSize = 12.sp, color = Color.White)
                Text("• High DPI (440 - 550): Recommended Button Size 42% - 47%. High DPI accelerates swipe velocity, so a slightly smaller button gives wider drag margins.", fontSize = 12.sp, color = Color.White)
                Text("• Low Sensitivity Setting: Increase button position towards the bottom 25% of screen to gain 15% more vertical dragging room.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            // Drag Techniques (Straight Drag, J-Drag, Rotation Drag)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ZeroShotCard(borderColor = ZeroShotPrimary) {
                    Text("1. STRAIGHT DRAG (Medium & Long Range)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ZeroShotPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("When the enemy is stationary or moving directly towards/away from you:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("• Lock red dot on the chest.", fontSize = 12.sp, color = Color.White)
                    Text("• Pull fire button straight upward in a decisive, vertical stroke.", fontSize = 12.sp, color = Color.White)
                    Text("• Best with: AK47, SCAR, M4A1, Woodpecker.", fontSize = 11.sp, color = ZeroShotSecondary)
                }

                ZeroShotCard(borderColor = ZeroShotSecondary) {
                    Text("2. ROTATION DRAG (Strafing & Running Enemies)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ZeroShotSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("When the enemy is running horizontally left or right across your screen:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("• Drag the fire button curved in the exact angle of enemy travel.", fontSize = 12.sp, color = Color.White)
                    Text("• Example: Enemy running right -> flick fire button slightly right then upward in a smooth arc.", fontSize = 12.sp, color = Color.White)
                    Text("• Best with: MP40, Thompson, UMP.", fontSize = 11.sp, color = ZeroShotSecondary)
                }

                ZeroShotCard(borderColor = ZeroShotTertiary) {
                    Text("3. J-DRAG / DOWN-UP DRAG (Super Close Range)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ZeroShotTertiary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("When fighting inside 5 meters where auto-aim aggressively locks on enemy feet/stomach:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("• Briefly flick fire button down to break the chest auto-aim lock, then explosively swipe straight up to the head.", fontSize = 12.sp, color = Color.White)
                    Text("• Best with: M1887, M1014, Desert Eagle, UMP.", fontSize = 11.sp, color = ZeroShotSecondary)
                }
            }
        }
    }
}
