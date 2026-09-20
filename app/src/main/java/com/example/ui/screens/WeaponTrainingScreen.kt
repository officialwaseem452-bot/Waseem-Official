package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Weapon
import com.example.data.model.WeaponCategory
import com.example.data.model.WeaponDatabase
import com.example.ui.AppDestination
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
import kotlinx.coroutines.delay
import kotlin.math.hypot
import kotlin.random.Random

@Composable
fun WeaponTrainingScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedWeapon by viewModel.selectedWeapon.collectAsState()
    val categories = WeaponCategory.values()
    val weaponsInCategory = remember(selectedCategory) {
        WeaponDatabase.weapons.filter { it.category == selectedCategory }
    }

    // Recoil Simulator State
    var isFiringRecoilTest by remember { mutableStateOf(false) }
    var recoilBulletsFired by remember { mutableIntStateOf(0) }
    var currentMuzzleX by remember { mutableFloatStateOf(0.5f) }
    var currentMuzzleY by remember { mutableFloatStateOf(0.5f) }
    val bulletHoles = remember { mutableStateListOf<Offset>() }
    var recoilAccuracyScore by remember { mutableIntStateOf(100) }

    // Spray simulation loop when firing
    LaunchedEffect(isFiringRecoilTest) {
        if (isFiringRecoilTest) {
            bulletHoles.clear()
            recoilBulletsFired = 0
            currentMuzzleX = 0.5f
            currentMuzzleY = 0.5f

            while (isFiringRecoilTest && recoilBulletsFired < selectedWeapon.magazine) {
                delay(90) // fire rate delay
                recoilBulletsFired++

                // Weapon recoil kicks upward and slight horizontal shake
                val kickUp = 0.022f * selectedWeapon.recoilPullDownRate
                val jitterX = Random.nextFloat() * 0.018f - 0.009f
                currentMuzzleY -= kickUp
                currentMuzzleX += jitterX

                bulletHoles.add(Offset(currentMuzzleX, currentMuzzleY))
                viewModel.audioHelper.playHit(viewModel.soundEnabled.value)

                // Calculate deviation from center (0.5, 0.4 headshot sweetspot)
                val dist = hypot(currentMuzzleX - 0.5f, currentMuzzleY - 0.4f)
                val shotScore = (100 - (dist * 200)).toInt().coerceIn(0, 100)
                recoilAccuracyScore = shotScore
            }
            isFiringRecoilTest = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ZeroShotBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        SectionHeader(
            title = "WEAPON TRAINING",
            subtitle = "Ballistics, recoil compensation & drag guides",
            badgeText = selectedWeapon.name
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Tab Row
        ScrollableTabRow(
            selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
            containerColor = ZeroShotSurface,
            contentColor = ZeroShotPrimary,
            edgePadding = 8.dp,
            indicator = { tabPositions ->
                val idx = categories.indexOf(selectedCategory).coerceAtLeast(0)
                if (idx < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[idx]),
                        color = ZeroShotPrimary,
                        height = 3.dp
                    )
                }
            }
        ) {
            categories.forEach { cat ->
                val isSel = cat == selectedCategory
                Tab(
                    selected = isSel,
                    onClick = { viewModel.selectWeaponCategory(cat) },
                    text = {
                        Text(
                            text = cat.displayName,
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) ZeroShotPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Weapons Chips in Category
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            weaponsInCategory.forEach { weapon ->
                val isSelected = weapon.id == selectedWeapon.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) ZeroShotPrimary else ZeroShotSurfaceVariant)
                        .border(
                            BorderStroke(1.dp, if (isSelected) ZeroShotPrimary else ZeroShotOutline),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.selectWeapon(weapon) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = weapon.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Selected Weapon Details Card
        ZeroShotCard(borderColor = ZeroShotPrimary) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = selectedWeapon.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "${selectedWeapon.category.displayName} • Recoil: ${selectedWeapon.recoilLevel}",
                        fontSize = 12.sp,
                        color = ZeroShotSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ZeroShotPrimary.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "HEADSHOT: ${selectedWeapon.headDamage}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ZeroShotPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Weapon Stat Bars
            StatProgressBar(label = "Body Damage", value = selectedWeapon.bodyDamage, maxValue = 150, color = ZeroShotPrimary)
            StatProgressBar(label = "Fire Rate", value = selectedWeapon.fireRate, maxValue = 100, color = ZeroShotSecondary)
            StatProgressBar(label = "Effective Range", value = selectedWeapon.range, maxValue = 100, color = ZeroShotTertiary)
            StatProgressBar(label = "Magazine Capacity", value = selectedWeapon.magazine, maxValue = 50, color = Color.White)

            Spacer(modifier = Modifier.height(10.dp))

            // Pro Tip & Drag Technique
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ZeroShotSurfaceVariant)
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = "DRAG TECHNIQUE:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZeroShotSecondary
                    )
                    Text(text = selectedWeapon.dragTechnique, fontSize = 12.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "PRO TIP: ${selectedWeapon.proTip}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    viewModel.selectDrillType(selectedWeapon.recommendedDrill)
                    viewModel.navigateTo(AppDestination.AIM_TRAINER)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ZeroShotPrimary)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("PRACTICE ${selectedWeapon.recommendedDrill.displayName.uppercase()}", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive Recoil Control Exercise
        ZeroShotCard(borderColor = ZeroShotSecondary) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrackChanges, contentDescription = null, tint = ZeroShotSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RECOIL CONTROL SIMULATOR",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }

                Text(
                    text = "ACCURACY: $recoilAccuracyScore%",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = if (recoilAccuracyScore >= 70) ZeroShotSuccess else ZeroShotWarning
                )
            }

            Text(
                text = "Hold and pull DOWN on the canvas while firing to counteract ${selectedWeapon.name}'s vertical climb!",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Canvas for recoil spray & pull-down drag gesture
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0A0C11))
                    .border(BorderStroke(1.dp, ZeroShotOutline), RoundedCornerShape(10.dp))
            ) {
                val cw = constraints.maxWidth.toFloat()
                val ch = constraints.maxHeight.toFloat()

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(isFiringRecoilTest) {
                            detectDragGestures(
                                onDrag = { change, dragAmount ->
                                    if (isFiringRecoilTest) {
                                        // Counteract recoil by dragging down
                                        currentMuzzleY += (dragAmount.y / ch) * 0.9f
                                        currentMuzzleX += (dragAmount.x / cw) * 0.9f
                                        currentMuzzleY = currentMuzzleY.coerceIn(0.1f, 0.9f)
                                        currentMuzzleX = currentMuzzleX.coerceIn(0.1f, 0.9f)
                                    }
                                }
                            )
                        }
                ) {
                    // Bullseye Target (Headshot center)
                    val centerX = cw * 0.5f
                    val centerY = ch * 0.4f

                    // Target rings
                    drawCircle(color = Color(0x22FFFFFF), radius = 80.dp.toPx(), center = Offset(centerX, centerY))
                    drawCircle(color = Color(0x3300E5FF), radius = 50.dp.toPx(), center = Offset(centerX, centerY))
                    drawCircle(
                        color = ZeroShotSecondary,
                        radius = 50.dp.toPx(),
                        center = Offset(centerX, centerY),
                        style = Stroke(1.dp.toPx())
                    )
                    // Headshot bullseye
                    drawCircle(color = ZeroShotPrimary.copy(alpha = 0.3f), radius = 24.dp.toPx(), center = Offset(centerX, centerY))
                    drawCircle(
                        color = ZeroShotPrimary,
                        radius = 24.dp.toPx(),
                        center = Offset(centerX, centerY),
                        style = Stroke(2.dp.toPx())
                    )

                    // Draw all fired bullet holes
                    bulletHoles.forEach { pos ->
                        drawCircle(
                            color = ZeroShotTertiary,
                            radius = 4.dp.toPx(),
                            center = Offset(pos.x * cw, pos.y * ch)
                        )
                    }

                    // Current reticle position
                    val rx = currentMuzzleX * cw
                    val ry = currentMuzzleY * ch
                    drawCircle(color = Color.White, radius = 6.dp.toPx(), center = Offset(rx, ry))
                    drawLine(Color.White, Offset(rx - 15f, ry), Offset(rx + 15f, ry), 2f)
                    drawLine(Color.White, Offset(rx, ry - 15f), Offset(rx, ry + 15f), 2f)
                }

                // Fire Trigger Button Overlay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rounds: $recoilBulletsFired / ${selectedWeapon.magazine}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Button(
                        onClick = {
                            isFiringRecoilTest = !isFiringRecoilTest
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFiringRecoilTest) ZeroShotError else ZeroShotPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("fire_recoil_test_button")
                    ) {
                        Text(if (isFiringRecoilTest) "CEASE FIRE" else "HOLD & FIRE")
                    }
                }
            }
        }
    }
}

@Composable
fun StatProgressBar(
    label: String,
    value: Int,
    maxValue: Int,
    color: Color
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "$value", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { (value.toFloat() / maxValue.toFloat()).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = ZeroShotSurfaceVariant
        )
    }
}
