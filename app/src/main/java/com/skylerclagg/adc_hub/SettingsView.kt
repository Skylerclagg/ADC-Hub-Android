@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)

package com.skylerclagg.adc_hub

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import com.skylerclagg.adc_hub.ui.theme.button
import com.skylerclagg.adc_hub.ui.theme.onTopContainer
import com.skylerclagg.adc_hub.ui.theme.topContainer
import io.mhssn.colorpicker.ColorPickerDialog
import io.mhssn.colorpicker.ColorPickerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Destination
@Composable
fun SettingsView(navController: NavController) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.topContainer,
                    titleContentColor = MaterialTheme.colorScheme.onTopContainer,
                ),
                title = {
                    Text("Settings", fontWeight = FontWeight.Bold)
                }
            )
        }
    ) { padding ->

        var update by remember { mutableStateOf(false) }
        val userSettings = UserSettings(LocalContext.current)
        val uriHandler = LocalUriHandler.current

        Column(
            modifier = Modifier.padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(0.95f)
            ) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    // (Optional) placeholder card or remove if empty
                    Card(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                        colors = CardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            disabledContainerColor = Color.Unspecified.copy(alpha = 0.3f),
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContentColor = Color.Unspecified
                        ),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column {
                            // ...
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "COMPETITION",
                        modifier = Modifier.padding(horizontal = 20.dp),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    // ----- Season Selector (Re-implemented) -----
                    Card(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                        colors = CardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f),
                            disabledContainerColor = Color.Unspecified.copy(alpha = 0.5f),
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContentColor = Color.Unspecified
                        )
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(0.dp),
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            // Check if there's data in your seasons cache
                            if (API.seasonsCache[0].isNotEmpty()) {
                                // For spacing
                                HorizontalDivider(
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                                )
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp)
                                ) {
                                    Box {
                                        var expanded by remember { mutableStateOf(false) }

                                        DropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false }
                                        ) {
                                            // Populate from seasonsCache[0]
                                            API.seasonsCache[0].forEach { entry ->
                                                DropdownMenuItem(
                                                    text = { Text(entry.shortName) },
                                                    onClick = {
                                                        expanded = false
                                                        userSettings.setSelectedSeasonId(entry.id)
                                                        API.importedWS = false
                                                        // Possibly update your WS Cache
                                                        CoroutineScope(Dispatchers.Default).launch {
                                                            API.updateWorldSkillsCache()
                                                        }
                                                    }
                                                )
                                            }
                                        }

                                        // Show the currently selected season
                                        val currentSeasonId = userSettings.getSelectedSeasonId()
                                        val currentSeason = (API.seasonsCache[0])
                                            .firstOrNull { it.id == currentSeasonId }

                                        Text(
                                            text = currentSeason?.shortName ?: "Unknown Season",
                                            color = MaterialTheme.colorScheme.button,
                                            modifier = Modifier.clickable {
                                                expanded = !expanded
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "APPEARANCE",
                        modifier = Modifier.padding(horizontal = 20.dp),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Card(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                        colors = CardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f),
                            disabledContainerColor = Color.Unspecified.copy(alpha = 0.5f),
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContentColor = Color.Unspecified
                        )
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(0.dp),
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            // ----------- Top Bar Color -----------
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                var showDialog by remember { mutableStateOf(false) }
                                var topContainerColor by remember { mutableStateOf(userSettings.getTopContainerColor()) }
                                var updateColor by remember { mutableStateOf(false) }

                                ColorPickerDialog(
                                    show = showDialog,
                                    type = ColorPickerType.Circle(
                                        showBrightnessBar = true,
                                        showAlphaBar = false,
                                        lightCenter = true
                                    ),
                                    onDismissRequest = {
                                        showDialog = false
                                    },
                                    onPickedColor = {
                                        topContainerColor = it
                                        userSettings.setTopContainerColor(it)
                                        updateColor = true
                                        showDialog = false
                                    }
                                )

                                if (updateColor) {
                                    MaterialTheme.colorScheme.topContainer = topContainerColor
                                    val view = LocalView.current
                                    if (!view.isInEditMode && !userSettings.getMinimalisticMode()) {
                                        SideEffect {
                                            val window = (view.context as Activity).window
                                            window.statusBarColor = topContainerColor.toArgb()
                                            window.navigationBarColor = topContainerColor.toArgb()
                                        }
                                    }
                                    updateColor = false
                                }

                                Text("Top Bar Color", modifier = Modifier.weight(1f))
                                Text(
                                    "Change",
                                    color = if (userSettings.getTopContainerColor().isSpecified) {
                                        userSettings.getTopContainerColor()
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    },
                                    modifier = Modifier.clickable {
                                        showDialog = true
                                    }
                                )
                            }
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                            )

                            // ----------- Top Bar Content Color -----------
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                var showDialog by remember { mutableStateOf(false) }
                                var onTopContainerColor by remember {
                                    mutableStateOf(userSettings.getOnTopContainerColor())
                                }
                                var updateColor by remember { mutableStateOf(false) }

                                ColorPickerDialog(
                                    show = showDialog,
                                    type = ColorPickerType.Circle(
                                        showBrightnessBar = true,
                                        showAlphaBar = false,
                                        lightCenter = true
                                    ),
                                    onDismissRequest = {
                                        showDialog = false
                                    },
                                    onPickedColor = {
                                        onTopContainerColor = it
                                        userSettings.setOnTopContainerColor(it)
                                        updateColor = true
                                        showDialog = false
                                    }
                                )

                                if (updateColor) {
                                    MaterialTheme.colorScheme.onTopContainer = onTopContainerColor
                                    updateColor = false
                                }

                                Text("Top Bar Content Color", modifier = Modifier.weight(1f))
                                Text(
                                    "Change",
                                    color = if (userSettings.getOnTopContainerColor().isSpecified) {
                                        userSettings.getOnTopContainerColor()
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    },
                                    modifier = Modifier.clickable {
                                        showDialog = true
                                    }
                                )
                            }
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                            )

                            // ----------- Button & Tab Color -----------
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                var showDialog by remember { mutableStateOf(false) }
                                var buttonColor by remember { mutableStateOf(userSettings.getButtonColor()) }
                                var updateColor by remember { mutableStateOf(false) }

                                ColorPickerDialog(
                                    show = showDialog,
                                    type = ColorPickerType.Circle(
                                        showBrightnessBar = true,
                                        showAlphaBar = false,
                                        lightCenter = true
                                    ),
                                    onDismissRequest = {
                                        showDialog = false
                                    },
                                    onPickedColor = {
                                        buttonColor = it
                                        userSettings.setButtonColor(it)
                                        updateColor = true
                                        showDialog = false
                                    }
                                )

                                if (updateColor) {
                                    MaterialTheme.colorScheme.button = buttonColor
                                    updateColor = false
                                }

                                Text("Button and Tab Color", modifier = Modifier.weight(1f))
                                Text(
                                    "Change",
                                    color = MaterialTheme.colorScheme.button,
                                    modifier = Modifier.clickable {
                                        showDialog = true
                                    }
                                )
                            }
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                            )

                            // ----------- Minimalistic Mode -----------
                            var minimalistic by remember { mutableStateOf(userSettings.getMinimalisticMode()) }
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                if (minimalistic && update) {
                                    val background = MaterialTheme.colorScheme.background
                                    MaterialTheme.colorScheme.topContainer = background
                                    val view = LocalView.current
                                    if (!view.isInEditMode) {
                                        SideEffect {
                                            val window = (view.context as Activity).window
                                            window.statusBarColor = background.toArgb()
                                            window.navigationBarColor = background.toArgb()
                                        }
                                    }
                                    update = false
                                } else if (!minimalistic && update) {
                                    MaterialTheme.colorScheme.topContainer =
                                        if (userSettings.getTopContainerColor().isSpecified) {
                                            userSettings.getTopContainerColor()
                                        } else {
                                            MaterialTheme.colorScheme.primaryContainer
                                        }
                                    val view = LocalView.current
                                    if (!view.isInEditMode) {
                                        SideEffect {
                                            val window = (view.context as Activity).window
                                            window.statusBarColor =
                                                userSettings.getTopContainerColor().toArgb()
                                            window.navigationBarColor =
                                                userSettings.getTopContainerColor().toArgb()
                                        }
                                    }
                                    update = false
                                }

                                Text("Minimalistic", modifier = Modifier.weight(1f))
                                Switch(
                                    checked = minimalistic,
                                    modifier = Modifier
                                        .size(44.dp, 24.dp)
                                        .padding(end = 10.dp),
                                    onCheckedChange = {
                                        minimalistic = it
                                        userSettings.setMinimalisticMode(it)
                                        update = true
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.button,
                                        checkedTrackColor = MaterialTheme.colorScheme.button.copy(alpha = 0.5f),
                                        uncheckedThumbColor = MaterialTheme.colorScheme.button,
                                        uncheckedTrackColor = MaterialTheme.colorScheme.button.copy(alpha = 0f)
                                    )
                                )
                            }
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                            )

                            // ----------- Enable Vibration (default: off) -----------
                            var enableVibration by remember { mutableStateOf(userSettings.getEnableVibration()) }
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                Text("Enable Vibration", modifier = Modifier.weight(1f))
                                Switch(
                                    checked = enableVibration,
                                    modifier = Modifier
                                        .size(44.dp, 24.dp)
                                        .padding(end = 10.dp),
                                    onCheckedChange = { isChecked ->
                                        enableVibration = isChecked
                                        userSettings.setEnableVibration(isChecked)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.button,
                                        checkedTrackColor = MaterialTheme.colorScheme.button.copy(alpha = 0.5f),
                                        uncheckedThumbColor = MaterialTheme.colorScheme.button,
                                        uncheckedTrackColor = MaterialTheme.colorScheme.button.copy(alpha = 0f)
                                    )
                                )
                            }
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                            )

                            // ----------- Reset to Default -----------
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                var reset by remember { mutableStateOf(false) }

                                if (reset) {
                                    MaterialTheme.colorScheme.topContainer =
                                        MaterialTheme.colorScheme.primaryContainer
                                    MaterialTheme.colorScheme.onTopContainer =
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    MaterialTheme.colorScheme.button =
                                        MaterialTheme.colorScheme.primary

                                    val view = LocalView.current
                                    val colorScheme = MaterialTheme.colorScheme
                                    if (!view.isInEditMode) {
                                        SideEffect {
                                            val window = (view.context as Activity).window
                                            window.statusBarColor =
                                                colorScheme.primaryContainer.toArgb()
                                            window.navigationBarColor =
                                                colorScheme.primaryContainer.toArgb()
                                        }
                                    }
                                    reset = false
                                }

                                Text("Reset to Default", modifier = Modifier.weight(1f))
                                Text(
                                    "Reset",
                                    color = MaterialTheme.colorScheme.button,
                                    modifier = Modifier.clickable {
                                        userSettings.resetColors()
                                        // also set minimalistic mode to default if desired
                                        userSettings.setMinimalisticMode(true)
                                        minimalistic = true
                                        reset = true
                                        update = true
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "DEVELOPER",
                        modifier = Modifier.padding(horizontal = 20.dp),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Card(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                        colors = CardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f),
                            disabledContainerColor = Color.Unspecified.copy(alpha = 0.5f),
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContentColor = Color.Unspecified
                        )
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(0.dp),
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                Text("Version", modifier = Modifier.weight(1f))
                                Text(
                                    "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})" +
                                            if (BuildConfig.DEBUG) " (DEBUG)" else ""
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "DEVELOPED BY Skyler Clagg, Based on TEAMS ACE 229V AND JELLY 2733J VRC RoboScout",
                        modifier = Modifier.padding(horizontal = 20.dp),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp)
                    .clickable {
                        uriHandler.openUri("https://discord.gg/KczJZUfs5f")
                    },
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Join the Discord Server",
                    color = MaterialTheme.colorScheme.button
                )
            }
        }
    }
}
