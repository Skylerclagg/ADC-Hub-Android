@file:OptIn(ExperimentalMaterial3Api::class)

package com.skylerclagg.adc_hub

import android.content.Context
import android.os.Vibrator
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import kotlin.math.max

// Define custom colors
val CustomBlue = Color(0xFF0066C8)
val CustomGreen = Color(0xFF00D543)
val CustomRed = Color(0xFFD32020)

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun TeamworkScoreCalculator() {
    val isDarkTheme = isSystemInDarkTheme()

    // Obtain Vibrator service & user settings
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

    // (1) Retrieve the user's vibration setting
    val userSettings = UserSettings(LocalContext.current)
    val enableVibration = userSettings.getEnableVibration() // assumed existing method

    // State variables
    var dropZoneTopCleared by remember { mutableStateOf(0) }
    var greenBeanBags by remember { mutableStateOf(0) }
    var blueBeanBags by remember { mutableStateOf(0) }
    var neutralBalls by remember { mutableStateOf(0) }
    var greenBalls by remember { mutableStateOf(0) }
    var blueBalls by remember { mutableStateOf(0) }
    var redDroneSelection by remember { mutableStateOf("None") }
    var blueDroneSelection by remember { mutableStateOf("None") }

    val totalScore = calculateTotalScore(
        dropZoneTopCleared,
        greenBeanBags,
        blueBeanBags,
        neutralBalls,
        greenBalls,
        blueBalls,
        redDroneSelection,
        blueDroneSelection
    )

    val showTopsClearedWarning = (greenBeanBags + blueBeanBags) > dropZoneTopCleared
    val showDroneWarning = (redDroneSelection != "None" && redDroneSelection == blueDroneSelection)
    val showBothLandingPadWarning =
        (redDroneSelection == "Landing Pad" && blueDroneSelection == "Bullseye") ||
                (redDroneSelection == "Bullseye" && blueDroneSelection == "Landing Pad")

    val beanBagsUsed = greenBeanBags + blueBeanBags
    val remainingBeanBags = max(0, 7 - beanBagsUsed)

    val ballsUsed = neutralBalls + greenBalls + blueBalls
    val remainingBalls = max(0, 10 - ballsUsed)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Teamwork Score Calculator") },
                actions = {
                    IconButton(onClick = {
                        // (2) Only vibrate if enabled
                        if (enableVibration) triggerVibration(vibrator)

                        // Clear all inputs
                        dropZoneTopCleared = 0
                        greenBeanBags = 0
                        blueBeanBags = 0
                        neutralBalls = 0
                        greenBalls = 0
                        blueBalls = 0
                        redDroneSelection = "None"
                        blueDroneSelection = "None"
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear Scores",
                            tint = CustomRed
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // ScoreView at the top
            ScoreView(
                totalScore = totalScore,
                showWarning = showTopsClearedWarning,
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                val cardWidth = 200.dp

                // Bean Bag Card
                ElevatedCard(
                    modifier = Modifier
                        .width(cardWidth)
                        .padding(horizontal = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Bean Bags", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)

                        // Tops Cleared
                        CounterSection(
                            title = "Tops Cleared",
                            count = dropZoneTopCleared,
                            maxCount = 7,
                            onCountChange = { dropZoneTopCleared = it },
                            accentColor = Color(0xFFFFA500),
                            showWarning = showTopsClearedWarning,
                            titleFontSize = 14,
                            vibrator = vibrator,
                            enableVibration = enableVibration
                        )

                        // Green Drop Zone
                        CounterSection(
                            title = "Green Drop Zone",
                            count = greenBeanBags,
                            maxCount = greenBeanBags + remainingBeanBags,
                            onCountChange = { greenBeanBags = it },
                            accentColor = CustomGreen,
                            showWarning = showTopsClearedWarning,
                            titleFontSize = 14,
                            vibrator = vibrator,
                            enableVibration = enableVibration
                        )

                        // Blue Drop Zone
                        CounterSection(
                            title = "Blue Drop Zone",
                            count = blueBeanBags,
                            maxCount = blueBeanBags + remainingBeanBags,
                            onCountChange = { blueBeanBags = it },
                            accentColor = CustomBlue,
                            showWarning = showTopsClearedWarning,
                            titleFontSize = 14,
                            vibrator = vibrator,
                            enableVibration = enableVibration
                        )

                        if (showTopsClearedWarning) {
                            Text("Bean bags exceed tops cleared!", color = CustomRed, fontSize = 14.sp)
                        } else {
                            Text("Remaining Bean Bags: $remainingBeanBags", color = CustomRed, fontSize = 14.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Balls Card
                ElevatedCard(
                    modifier = Modifier
                        .width(cardWidth)
                        .padding(horizontal = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Balls", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)

                        // Green
                        CounterSection(
                            title = "Green Zone",
                            count = greenBalls,
                            maxCount = greenBalls + remainingBalls,
                            onCountChange = { greenBalls = it },
                            accentColor = CustomGreen,
                            titleFontSize = 14,
                            vibrator = vibrator,
                            enableVibration = enableVibration
                        )

                        // Neutral
                        CounterSection(
                            title = "Neutral Zone",
                            count = neutralBalls,
                            maxCount = neutralBalls + remainingBalls,
                            onCountChange = { neutralBalls = it },
                            accentColor = Color.Gray,
                            titleFontSize = 14,
                            vibrator = vibrator,
                            enableVibration = enableVibration
                        )

                        // Blue
                        CounterSection(
                            title = "Blue Zone",
                            count = blueBalls,
                            maxCount = blueBalls + remainingBalls,
                            onCountChange = { blueBalls = it },
                            accentColor = CustomBlue,
                            titleFontSize = 14,
                            vibrator = vibrator,
                            enableVibration = enableVibration
                        )

                        Text("Remaining Balls: $remainingBalls", color = CustomRed, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Drones
            Row(modifier = Modifier.fillMaxWidth()) {
                DroneBox(
                    droneColor = "Red",
                    selectedOption = redDroneSelection,
                    otherDroneSelection = blueDroneSelection,
                    onSelect = { redDroneSelection = it },
                    modifier = Modifier.fillMaxWidth(0.5f),
                    vibrator = vibrator,
                    enableVibration = enableVibration
                )

                DroneBox(
                    droneColor = "Blue",
                    selectedOption = blueDroneSelection,
                    otherDroneSelection = redDroneSelection,
                    onSelect = { blueDroneSelection = it },
                    modifier = Modifier.fillMaxWidth(),
                    vibrator = vibrator,
                    enableVibration = enableVibration
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Drone warnings
            if (showDroneWarning || showBothLandingPadWarning) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = "Warning", tint = CustomRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when {
                            showDroneWarning -> "Warning: Both drones cannot land on the same object."
                            showBothLandingPadWarning -> "Warning: Both Drones cannot be landed on the landing pad/bullseye combination."
                            else -> ""
                        },
                        color = CustomRed,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ScoreView(totalScore: Int, showWarning: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Blue.copy(alpha = 0.8f),
                        Color.Green.copy(alpha = 0.8f)
                    )
                )
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            if (showWarning) {
                Icon(Icons.Default.Warning, contentDescription = "Warning", tint = Color.Yellow, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Score: $totalScore", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
            if (showWarning) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Warning, contentDescription = "Warning", tint = Color.Yellow, modifier = Modifier.size(24.dp))
            }
        }
    }
}

/**
 * Updated to accept enableVibration, so we only trigger it if true.
 */
@Composable
fun CounterSection(
    title: String,
    count: Int,
    maxCount: Int,
    onCountChange: (Int) -> Unit,
    accentColor: Color,
    showWarning: Boolean = false,
    titleFontSize: Int = 12,
    vibrator: Vibrator,
    enableVibration: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            title,
            fontSize = titleFontSize.sp,
            fontWeight = FontWeight.Medium,
            color = accentColor,
            maxLines = 2
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            IconButton(onClick = {
                if (enableVibration) {
                    triggerVibration(vibrator)
                }
                if (count > 0) onCountChange(count - 1)
            }) {
                Icon(Icons.Default.RemoveCircle, contentDescription = "Decrease", tint = CustomRed)
            }

            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "$count",
                fontSize = 20.sp,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(16.dp))

            IconButton(onClick = {
                if (enableVibration) {
                    triggerVibration(vibrator)
                }
                if (count < maxCount) onCountChange(count + 1)
            }) {
                Icon(Icons.Default.AddCircle, contentDescription = "Increase", tint = CustomGreen)
            }
        }
        if (showWarning) {
            Icon(Icons.Default.Warning, contentDescription = "Warning", tint = CustomRed)
        }
    }
}

/**
 * DroneBox updated with enableVibration param
 */
@Composable
fun DroneBox(
    droneColor: String,
    selectedOption: String,
    otherDroneSelection: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    vibrator: Vibrator,
    enableVibration: Boolean
) {
    val droneUIColor = if (droneColor == "Red") CustomRed else CustomBlue
    val options = listOf("None", "Small Cube", "Large Cube", "Landing Pad", "Bullseye")

    ElevatedCard(
        modifier = modifier.heightIn(min = 200.dp, max = 220.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "$droneColor Drone",
                fontWeight = FontWeight.SemiBold,
                color = droneUIColor,
                fontSize = 14.sp
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(options) { option ->
                    val isSelected = selectedOption == option
                    val isDisabled = isOptionDisabled(option, selectedOption, otherDroneSelection)
                    DroneOptionButton(
                        label = option,
                        isSelected = isSelected,
                        isDisabled = isDisabled,
                        droneColor = droneUIColor,
                        onClick = {
                            if (enableVibration) {
                                triggerVibration(vibrator)
                            }
                            onSelect(option)
                        }
                    )
                }
            }
        }
    }
}

fun isOptionDisabled(option: String, selectedOption: String, otherDroneSelection: String): Boolean {
    // If the option is "None", it's never disabled
    if (option == "None") return false

    if (selectedOption == option) return false
    if (otherDroneSelection == option) return true

    val padOrBullseyeSelected = otherDroneSelection == "Landing Pad" || otherDroneSelection == "Bullseye"
    val currentIsPadOrBullseye = option == "Landing Pad" || option == "Bullseye"
    if (padOrBullseyeSelected && currentIsPadOrBullseye) return true

    return false
}

@Composable
fun DroneOptionButton(
    label: String,
    isSelected: Boolean,
    isDisabled: Boolean,
    droneColor: Color,
    onClick: () -> Unit
) {
    val colors = ButtonDefaults.outlinedButtonColors(
        containerColor = if (isSelected) droneColor.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surface,
        contentColor = if (isDisabled) Color.Gray else if (isSelected) Color.White else droneColor
    )

    OutlinedButton(
        onClick = onClick,
        enabled = !isDisabled,
        colors = colors,
        contentPadding = PaddingValues(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label, fontSize = 14.sp, maxLines = 1)
    }
}

fun calculateTotalScore(
    dropZoneTopCleared: Int,
    greenBeanBags: Int,
    blueBeanBags: Int,
    neutralBalls: Int,
    greenBalls: Int,
    blueBalls: Int,
    redDroneSelection: String,
    blueDroneSelection: String
): Int {
    val basicScore = dropZoneTopCleared + neutralBalls + greenBeanBags + blueBeanBags
    val greenBasePoints = greenBalls
    val blueBasePoints = blueBalls
    val greenColorMatch = if (greenBeanBags > 0) (greenBalls * greenBeanBags * 2) else 0
    val blueColorMatch = if (blueBeanBags > 0) (blueBalls * blueBeanBags * 2) else 0
    val redLandingScore = landingScore(redDroneSelection)
    val blueLandingScore = landingScore(blueDroneSelection)
    return basicScore + greenBasePoints + blueBasePoints + greenColorMatch + blueColorMatch + redLandingScore + blueLandingScore
}

fun landingScore(selection: String): Int {
    return when (selection) {
        "None" -> 0
        "Small Cube" -> 25
        "Large Cube" -> 15
        "Landing Pad" -> 15
        "Bullseye" -> 25
        else -> 0
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTeamworkScoreCalculator() {
    TeamworkScoreCalculator()
}
