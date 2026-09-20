package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.DetectedGameType
import com.example.ui.AppDestination
import com.example.ui.MainViewModel
import com.example.ui.screens.AimTrainerScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HudGuideScreen
import com.example.ui.screens.PlayerStatsScreen
import com.example.ui.screens.SensitivityScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WeaponTrainingScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ZeroShotBackground
import com.example.ui.theme.ZeroShotOutline
import com.example.ui.theme.ZeroShotPrimary
import com.example.ui.theme.ZeroShotSecondary
import com.example.ui.theme.ZeroShotSuccess
import com.example.ui.theme.ZeroShotSurface
import com.example.ui.theme.ZeroShotSurfaceVariant

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ZeroShotMainApp(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshGameStatus()
    }
}

data class NavItem(
    val destination: AppDestination,
    val icon: ImageVector,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZeroShotMainApp(viewModel: MainViewModel) {
    val currentDestination by viewModel.currentDestination.collectAsState()
    val gameStatus by viewModel.gameStatus.collectAsState()

    val navItems = listOf(
        NavItem(AppDestination.DASHBOARD, Icons.Default.Home, "Dashboard"),
        NavItem(AppDestination.AIM_TRAINER, Icons.Default.CenterFocusStrong, "Trainer"),
        NavItem(AppDestination.SENSITIVITY, Icons.Default.Tune, "Sensitivity"),
        NavItem(AppDestination.HUD_GUIDE, Icons.Default.Gamepad, "HUD"),
        NavItem(AppDestination.WEAPON_TRAINING, Icons.Default.Security, "Weapons"),
        NavItem(AppDestination.PLAYER_STATS, Icons.Default.BarChart, "Stats")
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(ZeroShotBackground)) {
        val isWideScreen = maxWidth >= 600.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = ZeroShotBackground,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_zero_shot_logo),
                                contentDescription = "ZERO SHOT Logo",
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ZERO SHOT",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (gameStatus.isGameRunning) ZeroShotSuccess.copy(alpha = 0.2f)
                                        else ZeroShotPrimary.copy(alpha = 0.2f)
                                    )
                                    .border(
                                        1.dp,
                                        if (gameStatus.isGameRunning) ZeroShotSuccess else ZeroShotPrimary,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (gameStatus.isGameRunning) "FF ACTIVE" else "READY",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (gameStatus.isGameRunning) ZeroShotSuccess else ZeroShotPrimary
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.navigateTo(AppDestination.SETTINGS) },
                            modifier = Modifier.testTag("settings_top_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = if (currentDestination == AppDestination.SETTINGS) ZeroShotPrimary else Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = ZeroShotSurface
                    )
                )
            },
            bottomBar = {
                if (!isWideScreen) {
                    NavigationBar(
                        containerColor = ZeroShotSurface,
                        tonalElevation = 6.dp,
                        modifier = Modifier.testTag("bottom_nav_bar")
                    ) {
                        navItems.forEach { item ->
                            val isSelected = currentDestination == item.destination
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.navigateTo(item.destination) },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.label,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = ZeroShotPrimary,
                                    indicatorColor = ZeroShotPrimary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.testTag("nav_${item.destination.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Wide-screen Navigation Rail for PC / Tablets / Landscape
                if (isWideScreen) {
                    NavigationRail(
                        containerColor = ZeroShotSurface,
                        modifier = Modifier
                            .fillMaxHeight()
                            .border(1.dp, ZeroShotOutline)
                            .testTag("wide_nav_rail")
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        navItems.forEach { item ->
                            val isSelected = currentDestination == item.destination
                            NavigationRailItem(
                                selected = isSelected,
                                onClick = { viewModel.navigateTo(item.destination) },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = ZeroShotPrimary,
                                    indicatorColor = ZeroShotPrimary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }

                // Main Content Screen
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ZeroShotBackground)
                ) {
                    when (currentDestination) {
                        AppDestination.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                        AppDestination.AIM_TRAINER -> AimTrainerScreen(viewModel = viewModel)
                        AppDestination.SENSITIVITY -> SensitivityScreen(viewModel = viewModel)
                        AppDestination.HUD_GUIDE -> HudGuideScreen(viewModel = viewModel)
                        AppDestination.WEAPON_TRAINING -> WeaponTrainingScreen(viewModel = viewModel)
                        AppDestination.PLAYER_STATS -> PlayerStatsScreen(viewModel = viewModel)
                        AppDestination.SETTINGS -> SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "ZERO SHOT $name!", modifier = modifier, color = Color.White)
}
