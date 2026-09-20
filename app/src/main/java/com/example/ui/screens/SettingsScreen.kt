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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val overlayEnabled by viewModel.overlayEnabled.collectAsState()

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    val languages = listOf("English", "Español", "Português", "Bahasa Indonesia", "हिन्दी")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ZeroShotBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        SectionHeader(
            title = "SETTINGS & PREFERENCES",
            subtitle = "Audio, haptics, overlay and anti-cheat policies"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Audio & Haptics Section
        ZeroShotCard {
            Text(
                text = "FEEDBACK & CONTROLS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ZeroShotPrimary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            SettingSwitchRow(
                title = "Audio Sound Effects",
                subtitle = "Headshot pings, hit markers and countdown beeps",
                icon = Icons.Default.VolumeUp,
                checked = soundEnabled,
                onCheckedChange = { viewModel.toggleSound(it) },
                testTag = "sound_switch"
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingSwitchRow(
                title = "Vibration & Haptics",
                subtitle = "Tactile impulse on hit and critical headshot",
                icon = Icons.Default.Vibration,
                checked = vibrationEnabled,
                onCheckedChange = { viewModel.toggleVibration(it) },
                testTag = "vibration_switch"
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingSwitchRow(
                title = "Movable Overlay Status",
                subtitle = "Optional floating pill over external apps",
                icon = Icons.Default.Layers,
                checked = overlayEnabled,
                onCheckedChange = { viewModel.toggleOverlay(context, it) },
                testTag = "overlay_settings_switch"
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Language & Localization
        ZeroShotCard {
            Text(
                text = "LOCALIZATION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ZeroShotSecondary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ZeroShotSurfaceVariant)
                    .clickable { showLanguageDialog = true }
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = ZeroShotSecondary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("App Language", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(selectedLanguage, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ZeroShotSecondary.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("CHANGE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ZeroShotSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Anti-Cheat & Fair Play Compliance Card
        ZeroShotCard(borderColor = ZeroShotSuccess) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Security, contentDescription = null, tint = ZeroShotSuccess)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "FAIR PLAY & ANTI-CHEAT COMPLIANCE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = ZeroShotSuccess,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ZERO SHOT is strictly a training companion, sensitivity calculator, and layout guide. It does NOT modify Free Fire game files, system memory, network traffic, APK binaries, or anti-cheat engines.",
                fontSize = 12.sp,
                color = Color.White,
                lineHeight = 17.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "• Zero aimbot, teleport, wallhack, or auto-headshot cheats\n• Zero memory hooks or packet injection\n• 100% safe for your game account and compliant with developer guidelines",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Reset Data
        ZeroShotCard(borderColor = ZeroShotError) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Clear All Training Data", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    Text("Reset records, matches and accuracy stats", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Button(
                    onClick = { showResetDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ZeroShotError),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("RESET", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // About ZERO SHOT
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_zero_shot_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "ZERO SHOT TRAINING PANEL v2.4.0",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Esports Edition • Cross-Platform Companion",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Free Fire is a registered trademark of Garena. This app is an independent utility.",
                fontSize = 9.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }

    // Language Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select Language", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column {
                    languages.forEach { lang ->
                        val isSelected = lang == selectedLanguage
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ZeroShotSurfaceVariant else ZeroShotSurface)
                                .clickable {
                                    viewModel.setLanguage(lang)
                                    showLanguageDialog = false
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lang,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) ZeroShotPrimary else Color.White
                            )
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = ZeroShotPrimary)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close", color = Color.Gray)
                }
            },
            containerColor = ZeroShotSurface
        )
    }

    // Reset Confirm Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset All Training Data?", fontWeight = FontWeight.Bold, color = Color.White) },
            text = { Text("Are you sure you want to clear all practice records? This cannot be undone.", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllStats()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZeroShotError)
                ) {
                    Text("CONFIRM RESET")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = ZeroShotSurface
        )
    }
}

@Composable
fun SettingSwitchRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = ZeroShotSecondary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
                Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ZeroShotPrimary,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = ZeroShotSurfaceVariant
            )
        )
    }
}
