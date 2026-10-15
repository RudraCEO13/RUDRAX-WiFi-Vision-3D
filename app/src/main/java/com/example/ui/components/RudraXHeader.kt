package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBorderMuted
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppScreen

@Composable
fun RudraXHeader(
    title: String = "RUDRAX WiFi Vision 3D",
    subtitle: String = "Live Wi-Fi Intelligence",
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onMenuSelect: (AppScreen) -> Unit = {}
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (showBackButton) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("header_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate Back",
                            tint = CyanNeon
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                } else {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "RudraX WiFi Vision 3D",
                        tint = CyanNeon,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        ),
                        color = TextPrimary
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.8.sp
                        ),
                        color = CyanBright
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onAboutClick,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("header_about_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "About and Help",
                        tint = ElectricBlue
                    )
                }

                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("header_overflow_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Advanced Network Tools",
                            tint = TextSecondary
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.testTag("three_dot_menu")
                    ) {
                        DropdownMenuItem(
                            text = { Text("1. Wi-Fi Analyzer", color = TextPrimary) },
                            onClick = {
                                menuExpanded = false
                                onMenuSelect(AppScreen.DIAGNOSTICS)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("2. Nearby Wi-Fi Networks", color = TextPrimary) },
                            onClick = {
                                menuExpanded = false
                                onMenuSelect(AppScreen.NEARBY_NETWORKS)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("3. My Local Network", color = TextPrimary) },
                            onClick = {
                                menuExpanded = false
                                onMenuSelect(AppScreen.LOCAL_NETWORK)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("4. Connected Device Discovery", color = TextPrimary) },
                            onClick = {
                                menuExpanded = false
                                onMenuSelect(AppScreen.LOCAL_NETWORK)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("5. Router Information", color = TextPrimary) },
                            onClick = {
                                menuExpanded = false
                                onMenuSelect(AppScreen.LOCAL_NETWORK)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("6. Internet Connectivity Test", color = TextPrimary) },
                            onClick = {
                                menuExpanded = false
                                onMenuSelect(AppScreen.CONNECTIVITY_TEST)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("7. Signal History", color = TextPrimary) },
                            onClick = {
                                menuExpanded = false
                                onMenuSelect(AppScreen.SAVED_ROOMS)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("8. Saved Room Scans", color = TextPrimary) },
                            onClick = {
                                menuExpanded = false
                                onMenuSelect(AppScreen.SAVED_ROOMS)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("9. Export Reports", color = TextPrimary) },
                            onClick = {
                                menuExpanded = false
                                onMenuSelect(AppScreen.SAVED_ROOMS)
                            }
                        )
                        HorizontalDivider(color = GlassBorderMuted)
                        DropdownMenuItem(
                            text = { Text("10. Permissions & Privacy", color = CyanBright) },
                            onClick = {
                                menuExpanded = false
                                onMenuSelect(AppScreen.ABOUT)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("11. Settings", color = TextPrimary) },
                            onClick = {
                                menuExpanded = false
                                onMenuSelect(AppScreen.SETTINGS)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("12. About RudraX WiFi Vision 3D", color = CyanNeon, fontWeight = FontWeight.Bold) },
                            onClick = {
                                menuExpanded = false
                                onMenuSelect(AppScreen.ABOUT)
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        HorizontalDivider(color = GlassBorderMuted, thickness = 1.dp)
    }
}
