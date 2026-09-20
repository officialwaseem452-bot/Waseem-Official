package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DrillType
import com.example.ui.AppDestination
import com.example.ui.MainViewModel
import com.example.ui.components.GameStatusCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.ZeroShotCard
import com.example.ui.theme.ZeroShotBackground
import com.example.ui.theme.ZeroShotOutline
import com.example.ui.theme.ZeroShotPrimary
import com.example.ui.theme.ZeroShotSecondary
import com.example.ui.theme.ZeroShotSuccess
import com.example.ui.theme.ZeroShotSurface
import com.example.ui.theme.ZeroShotSurfaceVariant
import com.example.ui.theme.ZeroShotTertiary

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val gameStatus by viewModel.gameStatus.collectAsState()
    val overlayEnabled by viewModel.overlayEnabled.collectAsState()
    val activePresetName by viewModel.activePresetName.collectAsState()
    val records by viewModel.allRecords.collectAsState()

    val totalDrills = records.size
    val bestScore = records.maxOfOrNull { it.score } ?: 0
    val avgAccuracy = if (records.isNotEmpty()) records.map { it.accuracyPercentage }.average().toInt() else 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ZeroShotBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Hero Branding Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(BorderStroke(1.dp, ZeroShotPrimary.copy(alpha = 0.5f)), RoundedCornerShape(16.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.hero_training_banner),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xE6090A0D),
                                Color(0x99090A0D),
                                Color(0x33090A0D)
                            )
                        )
                    )
            )

            // Brand Text & Tagline
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_zero_shot_logo),
                    contentDescription = "ZERO SHOT Logo",
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .border(1.dp, ZeroShotPrimary, CircleShape)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "ZERO SHOT",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "TRAIN • IMPROVE • DOMINATE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZeroShotPrimary,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Esports Training Panel & Sensitivity Suite",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Game Status & Movable Overlay Module
        GameStatusCard(
            status = gameStatus,
            overlayEnabled = overlayEnabled,
            onToggleOverlay = { viewModel.toggleOverlay(context, it) },
            onRefresh = { viewModel.refreshGameStatus() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // QUICK TRAINING Section (1-Tap Launchers)
        SectionHeader(
            title = "QUICK TRAINING",
            subtitle = "Instant 1-tap access to tactical drills",
            badgeText = "5 Drills Ready"
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickDrillButton(
                title = "Headshot Drill",
                subtitle = "Target precision & drag flick headshots",
                tag = "ONE-TAP",
                tagColor = ZeroShotPrimary,
                icon = Icons.Default.GpsFixed,
                onClick = {
                    viewModel.selectDrillType(DrillType.HEADSHOT)
                    viewModel.navigateTo(AppDestination.AIM_TRAINER)
                }
            )

            QuickDrillButton(
                title = "Reaction Drill",
                subtitle = "Sub-200ms trigger response training",
                tag = "REFLEX",
                tagColor = ZeroShotTertiary,
                icon = Icons.Default.Bolt,
                onClick = {
                    viewModel.selectDrillType(DrillType.REACTION)
                    viewModel.navigateTo(AppDestination.AIM_TRAINER)
                }
            )

            QuickDrillButton(
                title = "Tracking Drill",
                subtitle = "Follow moving targets with crosshair lock",
                tag = "SMG SPRAY",
                tagColor = ZeroShotSecondary,
                icon = Icons.Default.CenterFocusStrong,
                onClick = {
                    viewModel.selectDrillType(DrillType.TRACKING)
                    viewModel.navigateTo(AppDestination.AIM_TRAINER)
                }
            )

            QuickDrillButton(
                title = "Flick Drill",
                subtitle = "Fast muscle-memory snap acquisition",
                tag = "FLICK SHOT",
                tagColor = ZeroShotPrimary,
                icon = Icons.Default.AdsClick,
                onClick = {
                    viewModel.selectDrillType(DrillType.FLICK)
                    viewModel.navigateTo(AppDestination.AIM_TRAINER)
                }
            )

            QuickDrillButton(
                title = "Accuracy Test",
                subtitle = "Benchmark your overall hit percentage",
                tag = "EVALUATION",
                tagColor = ZeroShotSuccess,
                icon = Icons.Default.FitScreen,
                onClick = {
                    viewModel.selectDrillType(DrillType.ACCURACY)
                    viewModel.navigateTo(AppDestination.AIM_TRAINER)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sensitivity & Stats Snapshot Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ZeroShotCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.navigateTo(AppDestination.SENSITIVITY) },
                borderColor = ZeroShotPrimary
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("SENSITIVITY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ZeroShotPrimary)
                    Icon(Icons.Default.Tune, contentDescription = null, tint = ZeroShotPrimary, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = activePresetName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
                Text(
                    text = "Tap to tune sliders",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            ZeroShotCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.navigateTo(AppDestination.PLAYER_STATS) },
                borderColor = ZeroShotSecondary
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("TRAINING STATS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ZeroShotSecondary)
                    Icon(Icons.Default.Speed, contentDescription = null, tint = ZeroShotSecondary, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "$totalDrills Completed",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "$avgAccuracy% Avg Accuracy",
                    fontSize = 10.sp,
                    color = ZeroShotSuccess
                )
            }
        }
    }
}

@Composable
fun QuickDrillButton(
    title: String,
    subtitle: String,
    tag: String,
    tagColor: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ZeroShotSurface)
            .border(BorderStroke(1.dp, ZeroShotOutline), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tagColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tagColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(tagColor.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tag,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = tagColor
                        )
                    }
                }
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}
