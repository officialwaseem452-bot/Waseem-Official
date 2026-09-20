package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.QueryBuilder
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DrillType
import com.example.data.model.TrainingRecord
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PlayerStatsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val records by viewModel.allRecords.collectAsState()
    var showClearConfirm by remember { mutableStateOf(false) }

    val totalDrills = records.size
    val totalPracticeSeconds = records.sumOf { it.durationSeconds }
    val avgAccuracy = if (records.isNotEmpty()) {
        records.map { it.accuracyPercentage }.average().toFloat()
    } else 0f

    val totalHits = records.sumOf { it.hits }
    val totalHeadshots = records.sumOf { it.headshots }
    val headshotPercentage = if (totalHits > 0) {
        (totalHeadshots.toFloat() / totalHits.toFloat()) * 100f
    } else 0f

    val bestScore = records.maxOfOrNull { it.score } ?: 0
    val bestReactionTime = records
        .filter { it.drillType == DrillType.REACTION && it.reactionTimeMs > 0 }
        .minOfOrNull { it.reactionTimeMs } ?: 0L

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ZeroShotBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PLAYER STAT TRACKER",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Live analytics across all Free Fire training sessions",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (records.isNotEmpty()) {
                IconButton(
                    onClick = { showClearConfirm = true },
                    modifier = Modifier.testTag("clear_stats_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear Stats",
                        tint = ZeroShotError
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Grid of 4 Core Metric Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricBadge(
                label = "TOTAL DRILLS",
                value = "$totalDrills",
                subText = "${totalPracticeSeconds / 60}m Practice",
                color = ZeroShotPrimary,
                icon = Icons.Default.QueryBuilder,
                modifier = Modifier.weight(1f)
            )
            MetricBadge(
                label = "AVG ACCURACY",
                value = "${avgAccuracy.toInt()}%",
                subText = "Target Precision",
                color = ZeroShotSuccess,
                icon = Icons.Default.TrendingUp,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricBadge(
                label = "HEADSHOT RATE",
                value = "${headshotPercentage.toInt()}%",
                subText = "$totalHeadshots Headshots",
                color = ZeroShotTertiary,
                icon = Icons.Default.FlashOn,
                modifier = Modifier.weight(1f)
            )
            MetricBadge(
                label = "BEST SCORE",
                value = "$bestScore",
                subText = if (bestReactionTime > 0) "${bestReactionTime}ms Best RT" else "High Score",
                color = ZeroShotSecondary,
                icon = Icons.Default.EmojiEvents,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Progress Chart (Bar Chart of Recent 10 Drills)
        ZeroShotCard(borderColor = ZeroShotSecondary) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timeline, contentDescription = null, tint = ZeroShotSecondary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PERFORMANCE PROGRESSION",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
                Text("Last 10 Drills", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(12.dp))

            val recentRecords = records.take(10).reversed()
            if (recentRecords.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No training data yet. Complete your first drill!",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    val barCount = recentRecords.size
                    val spacing = size.width / (barCount + 1)
                    val barWidth = (spacing * 0.65f).coerceAtMost(28.dp.toPx())

                    // Draw baseline
                    drawLine(
                        color = Color(0x33FFFFFF),
                        start = Offset(0f, size.height - 15f),
                        end = Offset(size.width, size.height - 15f),
                        strokeWidth = 1.dp.toPx()
                    )

                    recentRecords.forEachIndexed { i, r ->
                        val x = spacing * (i + 1)
                        val accuracyRatio = (r.accuracyPercentage / 100f).coerceIn(0.1f, 1f)
                        val barHeight = (size.height - 25f) * accuracyRatio

                        // Bar Gradient
                        val brush = Brush.verticalGradient(
                            colors = listOf(ZeroShotPrimary, ZeroShotSecondary)
                        )
                        drawRect(
                            brush = brush,
                            topLeft = Offset(x - (barWidth / 2f), size.height - 15f - barHeight),
                            size = Size(barWidth, barHeight)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // History Log List
        SectionHeader(
            title = "TRAINING LOG",
            subtitle = "Recent completed sessions",
            badgeText = "${records.size} Records"
        )

        if (records.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Launch Quick Training to record your stats.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                records.take(20).forEach { r ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(ZeroShotSurface)
                            .border(BorderStroke(1.dp, ZeroShotOutline), RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = r.drillType.displayName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Text(
                                text = "${dateFormat.format(Date(r.timestamp))} • ${r.durationSeconds}s",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${r.score} PTS",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = ZeroShotPrimary
                                )
                                Text(
                                    text = "${r.accuracyPercentage.toInt()}% Acc | ${r.headshots} HS",
                                    fontSize = 11.sp,
                                    color = ZeroShotSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Reset Training Data?", fontWeight = FontWeight.Bold, color = Color.White) },
            text = { Text("This will permanently delete all your training session records and personal bests.", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllStats()
                        showClearConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZeroShotError)
                ) {
                    Text("RESET DATA")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = ZeroShotSurface
        )
    }
}

@Composable
fun MetricBadge(
    label: String,
    value: String,
    subText: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(BorderStroke(1.dp, color.copy(alpha = 0.5f)), RoundedCornerShape(12.dp)),
        color = ZeroShotSurface
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    letterSpacing = 0.5.sp
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = subText,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
