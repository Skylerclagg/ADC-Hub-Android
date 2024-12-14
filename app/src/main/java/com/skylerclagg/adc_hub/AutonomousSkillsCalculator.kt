@file:OptIn(ExperimentalMaterial3Api::class)

package com.skylerclagg.adc_hub

import LandingOption
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun AutonomousFlightSkillsCalculator() {
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = primaryTextColor()

    // State variables for task counts
    var takeOffCount by remember { mutableStateOf(0) }
    var identifyColorCount by remember { mutableStateOf(0) }
    var figure8Count by remember { mutableStateOf(0) }
    var smallHoleCount by remember { mutableStateOf(0) }
    var largeHoleCount by remember { mutableStateOf(0) }
    var archGateCount by remember { mutableStateOf(0) }
    var keyholeCount by remember { mutableStateOf(0) }
    var selectedLandingOption by remember { mutableStateOf(LandingOption.NONE) }

    // Calculating the total score based on the selected inputs
    val totalScore = calculateTotalScore(
        takeOffCount, identifyColorCount, figure8Count,
        smallHoleCount, largeHoleCount, archGateCount, keyholeCount,
        selectedLandingOption
    )

    val vibrator = LocalContext.current.let { remember { it.getVibrator() } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Autonomous Flight Calculator", color = textColor, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        triggerVibration(vibrator)

                        // Reset all inputs
                        takeOffCount = 0
                        identifyColorCount = 0
                        figure8Count = 0
                        smallHoleCount = 0
                        largeHoleCount = 0
                        archGateCount = 0
                        keyholeCount = 0
                        selectedLandingOption = LandingOption.NONE
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear Scores",
                            tint = Color.Red
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = if (isDarkTheme) Color.Black else MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->

        CompositionLocalProvider(LocalContentColor provides textColor) {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Reduced spacing
            ) {
                // Total Score Section
                item {
                    SectionHeader("Total Score", textColor)
                    Text(
                        text = "$totalScore",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        style = LocalTextStyle.current.copy(
                            brush = Brush.linearGradient(
                                colors = listOf(Color.Blue, Color.Green),
                                start = Offset(0f, 0f),
                                end = Offset(100f, 100f)
                            )
                        )
                    )
                }

                // Tasks Section
                item {
                    SectionHeader("Tasks:", textColor)
                }

                // Tasks card with black dividers and less padding
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDarkTheme) Color.DarkGray else Color.LightGray
                        )
                    ) {
                        // No extra padding around the Column to reduce whitespace
                        Column {
                            AutoStyledStepper(
                                "Take Off:",
                                takeOffCount,
                                maxValue = 2,
                                labelColor = textColor
                            ) { takeOffCount = it }

                            Divider(color = Color.Black, thickness = 1.dp)

                            AutoStyledStepper(
                                "Identify Color Count:",
                                identifyColorCount,
                                maxValue = 2,
                                labelColor = textColor
                            ) { identifyColorCount = it }

                            Divider(color = Color.Black, thickness = 1.dp)

                            AutoStyledStepper(
                                "Complete a Figure 8:",
                                figure8Count,
                                maxValue = 2,
                                labelColor = textColor
                            ) { figure8Count = it }

                            Divider(color = Color.Black, thickness = 1.dp)

                            AutoStyledStepper(
                                "Fly Through Small Hole:",
                                smallHoleCount,
                                maxValue = 2,
                                labelColor = textColor
                            ) { smallHoleCount = it }

                            Divider(color = Color.Black, thickness = 1.dp)

                            AutoStyledStepper(
                                "Fly Through Large Hole:",
                                largeHoleCount,
                                maxValue = 2,
                                labelColor = textColor
                            ) { largeHoleCount = it }

                            Divider(color = Color.Black, thickness = 1.dp)

                            AutoStyledStepper(
                                "Fly Under Arch Gate:",
                                archGateCount,
                                maxValue = 4,
                                labelColor = textColor
                            ) { archGateCount = it }

                            Divider(color = Color.Black, thickness = 1.dp)

                            AutoStyledStepper(
                                "Fly Through Keyhole:",
                                keyholeCount,
                                maxValue = 4,
                                labelColor = textColor
                            ) { keyholeCount = it }
                        }
                    }
                }

                item {
                    StyledDropdownInput(
                        label = "Landing Option",
                        options = LandingOption.values().toList(),
                        selectedOption = selectedLandingOption,
                        onOptionChange = { selectedLandingOption = it },
                        labelColor = textColor
                    )
                }
            }
        }
    }
}

// Calculation logic unchanged
fun calculateTotalScore(
    takeOffCount: Int,
    identifyColorCount: Int,
    figure8Count: Int,
    smallHoleCount: Int,
    largeHoleCount: Int,
    archGateCount: Int,
    keyholeCount: Int,
    selectedLandingOption: LandingOption
): Int {
    var score = 0
    score += takeOffCount * 10
    score += identifyColorCount * 15
    score += figure8Count * 40
    score += smallHoleCount * 40
    score += largeHoleCount * 20
    score += archGateCount * 5
    score += keyholeCount * 15
    score += selectedLandingOption.points
    return score
}

// Reduced vertical padding from 12.dp to 8.dp inside stepper rows
@Composable
fun AutoStyledStepper(
    label: String,
    value: Int,
    maxValue: Int,
    labelColor: Color,
    onValueChange: (Int) -> Unit
) {
    val vibrator = LocalContext.current.let { remember { it.getVibrator() } }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = labelColor, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = {
                    triggerVibration(vibrator)
                    if (value > 0) onValueChange(value - 1)
                },
                modifier = Modifier.padding(2.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("-", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Text(
                "$value",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 4.dp),
                color = labelColor,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = {
                    triggerVibration(vibrator)
                    if (value < maxValue) onValueChange(value + 1)
                },
                modifier = Modifier.padding(2.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("+", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyledDropdownInput(
    label: String,
    options: List<LandingOption>,
    selectedOption: LandingOption,
    onOptionChange: (LandingOption) -> Unit,
    labelColor: Color
) {
    var expanded by remember { mutableStateOf(false) }
    val vibrator = LocalContext.current.let { remember { it.getVibrator() } }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption.displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, color = labelColor, fontWeight = FontWeight.Bold) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedLabelColor = labelColor,
                unfocusedLabelColor = labelColor
            ),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.displayName, color = primaryTextColor(), fontWeight = FontWeight.Bold) },
                    onClick = {
                        triggerVibration(vibrator)
                        onOptionChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAutonomousFlightSkillsCalculator() {
    AutonomousFlightSkillsCalculator()
}
