package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SensitivityPreset
import com.example.ui.MainViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.components.ZeroShotCard
import com.example.ui.theme.ZeroShotBackground
import com.example.ui.theme.ZeroShotError
import com.example.ui.theme.ZeroShotOutline
import com.example.ui.theme.ZeroShotPrimary
import com.example.ui.theme.ZeroShotSecondary
import com.example.ui.theme.ZeroShotSecondaryContainer
import com.example.ui.theme.ZeroShotSuccess
import com.example.ui.theme.ZeroShotSurface
import com.example.ui.theme.ZeroShotSurfaceVariant
import com.example.ui.theme.ZeroShotTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensitivityScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val generalSens by viewModel.generalSens.collectAsState()
    val redDotSens by viewModel.redDotSens.collectAsState()
    val scope2xSens by viewModel.scope2xSens.collectAsState()
    val scope4xSens by viewModel.scope4xSens.collectAsState()
    val sniperSens by viewModel.sniperSens.collectAsState()
    val freeLookSens by viewModel.freeLookSens.collectAsState()
    val activePresetName by viewModel.activePresetName.collectAsState()
    val presets by viewModel.allPresets.collectAsState()

    var showSaveDialog by remember { mutableStateOf(false) }
    var savePresetName by remember { mutableStateOf("") }
    var savePresetNotes by remember { mutableStateOf("") }

    // Recommendation state
    var showRecommendationSheet by remember { mutableStateOf(false) }
    var selectedDeviceType by remember { mutableStateOf("Compact Phone") }
    var selectedRefreshRate by remember { mutableIntStateOf(90) }
    var selectedDpi by remember { mutableIntStateOf(400) }
    var selectedPlaystyle by remember { mutableStateOf("One-Tap Headshot") }

    val deviceOptions = listOf("Compact Phone", "Large Flagship", "Tablet", "PC Emulator")
    val refreshRateOptions = listOf(60, 90, 120, 144)
    val playstyleOptions = listOf("One-Tap Headshot", "Aggressive Close SMG", "Sniper Anchor", "Balanced")

    fun copyToClipboard() {
        val text = """
            === ZERO SHOT SENSITIVITY CONFIG ===
            General: $generalSens
            Red Dot: $redDotSens
            2x Scope: $scope2xSens
            4x Scope: $scope4xSens
            Sniper Scope: $sniperSens
            Free Look: $freeLookSens
            Preset: $activePresetName
            Apply these values manually in Free Fire > Settings > Sensitivity
        """.trimIndent()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Zero Shot Sensitivity", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Sensitivity copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ZeroShotBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SENSITIVITY ASSISTANT",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Calibrated for precision drag headshots",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { copyToClipboard() },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(ZeroShotSurfaceVariant)
                    .testTag("copy_sensitivity_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Values",
                    tint = ZeroShotSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Active Preset Card
        ZeroShotCard(borderColor = ZeroShotPrimary) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ACTIVE PRESET",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZeroShotPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = activePresetName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row {
                    Button(
                        onClick = { showRecommendationSheet = !showRecommendationSheet },
                        colors = ButtonDefaults.buttonColors(containerColor = ZeroShotSecondaryContainer),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("ai_recommend_button")
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = ZeroShotSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Recommend", color = ZeroShotSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Button(
                        onClick = { showSaveDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = ZeroShotPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("save_preset_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Recommendation Calculator Card (Expandable)
        AnimatedVisibility(visible = showRecommendationSheet) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ZeroShotSurfaceVariant)
                    .border(1.dp, ZeroShotSecondary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ZeroShotSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CALCULATE FOR YOUR HARDWARE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = ZeroShotSecondary
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                Text("1. Device Category", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    deviceOptions.forEach { opt ->
                        val sel = opt == selectedDeviceType
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (sel) ZeroShotPrimary else ZeroShotSurface)
                                .clickable { selectedDeviceType = opt }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = opt.replace(" ", "\n"),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (sel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("2. Screen Refresh Rate", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    refreshRateOptions.forEach { hz ->
                        val sel = hz == selectedRefreshRate
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (sel) ZeroShotSecondary else ZeroShotSurface)
                                .clickable { selectedRefreshRate = hz }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${hz}Hz",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (sel) Color.Black else Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("3. Target Playstyle", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    playstyleOptions.forEach { ps ->
                        val sel = ps == selectedPlaystyle
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (sel) ZeroShotTertiary else ZeroShotSurface)
                                .clickable { selectedPlaystyle = ps }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ps.replace(" ", "\n"),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (sel) Color.Black else Color.White,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        viewModel.calculateRecommendedSensitivity(
                            selectedDeviceType,
                            selectedRefreshRate,
                            selectedDpi,
                            selectedPlaystyle
                        )
                        showRecommendationSheet = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ZeroShotSecondary)
                ) {
                    Text("APPLY RECOMMENDED SENSITIVITY", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sensitivity Sliders
        SensitivitySliderItem(
            name = "General Sensitivity",
            description = "Controls 360 camera speed and drag headshot speed",
            value = generalSens,
            onValueChange = { viewModel.updateSensitivity(general = it) }
        )

        SensitivitySliderItem(
            name = "Red Dot",
            description = "Iron-sight and red dot drag response",
            value = redDotSens,
            onValueChange = { viewModel.updateSensitivity(redDot = it) }
        )

        SensitivitySliderItem(
            name = "2x Scope",
            description = "Medium-distance drag tracking",
            value = scope2xSens,
            onValueChange = { viewModel.updateSensitivity(scope2x = it) }
        )

        SensitivitySliderItem(
            name = "4x Scope",
            description = "Long-range precision tracking",
            value = scope4xSens,
            onValueChange = { viewModel.updateSensitivity(scope4x = it) }
        )

        SensitivitySliderItem(
            name = "Sniper Scope",
            description = "AWM & Kar98k quick-scope alignment speed",
            value = sniperSens,
            onValueChange = { viewModel.updateSensitivity(sniper = it) }
        )

        SensitivitySliderItem(
            name = "Free Look",
            description = "Eye button 360 surrounding scan speed",
            value = freeLookSens,
            onValueChange = { viewModel.updateSensitivity(freeLook = it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Saved Presets Section
        SectionHeader(
            title = "SAVED PRESETS",
            subtitle = "Quick-swap between tournament & rush loadouts",
            badgeText = "${presets.size} Available"
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            presets.forEach { preset ->
                val isActive = preset.name == activePresetName
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isActive) ZeroShotSurfaceVariant else ZeroShotSurface)
                        .border(
                            BorderStroke(1.dp, if (isActive) ZeroShotPrimary else ZeroShotOutline),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { viewModel.applyPreset(preset) }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = preset.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isActive) ZeroShotPrimary else Color.White
                            )
                            if (isActive) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ZeroShotPrimary)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                        Text(
                            text = "Gen: ${preset.generalSens} | Red: ${preset.redDotSens} | 2x: ${preset.scope2xSens} | 4x: ${preset.scope4xSens}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (preset.notes.isNotBlank()) {
                            Text(
                                text = preset.notes,
                                fontSize = 10.sp,
                                color = ZeroShotSecondary
                            )
                        }
                    }

                    Row {
                        if (preset.isCustom) {
                            IconButton(onClick = { viewModel.deletePreset(preset.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ZeroShotError)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Important Fair-Play & Manual Application Notice
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF141924))
                .border(BorderStroke(1.dp, ZeroShotOutline), RoundedCornerShape(8.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = ZeroShotSecondary)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Notice: ZERO SHOT never modifies Free Fire game files or memory automatically. Tap the copy button above and paste/input these sensitivity values inside Free Fire Settings > Sensitivity.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    // Save Preset Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Sensitivity Preset", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column {
                    OutlinedTextField(
                        value = savePresetName,
                        onValueChange = { savePresetName = it },
                        label = { Text("Preset Name") },
                        placeholder = { Text("e.g. My Tournament DPI 440") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ZeroShotPrimary,
                            unfocusedBorderColor = ZeroShotOutline
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = savePresetNotes,
                        onValueChange = { savePresetNotes = it },
                        label = { Text("Notes / Playstyle") },
                        placeholder = { Text("e.g. For MP40 jump-shots") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ZeroShotPrimary,
                            unfocusedBorderColor = ZeroShotOutline
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveCurrentAsPreset(
                            name = savePresetName,
                            device = selectedDeviceType,
                            dpi = selectedDpi,
                            notes = savePresetNotes
                        )
                        showSaveDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZeroShotPrimary)
                ) {
                    Text("SAVE")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = ZeroShotSurface
        )
    }
}

@Composable
fun SensitivitySliderItem(
    name: String,
    description: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(BorderStroke(1.dp, ZeroShotOutline), RoundedCornerShape(12.dp)),
        color = ZeroShotSurface
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        text = description,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ZeroShotPrimary.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$value",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ZeroShotPrimary
                    )
                }
            }

            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toInt()) },
                valueRange = 0f..100f,
                steps = 99,
                colors = SliderDefaults.colors(
                    thumbColor = ZeroShotPrimary,
                    activeTrackColor = ZeroShotPrimary,
                    inactiveTrackColor = ZeroShotSurfaceVariant
                ),
                modifier = Modifier.testTag("slider_${name.lowercase().replace(" ", "_")}")
            )
        }
    }
}
