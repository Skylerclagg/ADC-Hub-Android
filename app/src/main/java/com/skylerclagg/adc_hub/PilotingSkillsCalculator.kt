@file:OptIn(ExperimentalMaterial3Api::class)

package com.skylerclagg.adc_hub

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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

enum class LandingOption(val displayName: String, val points: Int) {
    NONE("None", 0),
    LAND_ON_PAD("Landing Pad", 15),
    LANDING_CUBE_SMALL("Small Cube", 40),
    LANDING_CUBE_LARGE("Large Cube", 25)
}

@Destination
@Composable
fun PilotingSkillsCalculator() {
    val isDarkTheme = isSystemInDarkTheme()
    val primaryTextColor = if (isDarkTheme) Color.White else Color.Black

    val vibrator = LocalContext.current.let { remember { it.getVibrator() } }

    // State variables
    var didTakeOff by remember { mutableStateOf(false) }
    var figure8Count by remember { mutableStateOf(0) }
    var smallHoleCount by remember { mutableStateOf(0) }
    var largeHoleCount by remember { mutableStateOf(0) }
    var keyholeCount by remember { mutableStateOf(0) }
    var selectedLandingOption by remember { mutableStateOf(LandingOption.NONE) }

    // Calculate total score
    val totalScore = run {
        var score = 0
        if (didTakeOff) score += 10
        score += figure8Count * 40
        score += smallHoleCount * 40
        score += largeHoleCount * 20
        score += keyholeCount * 15
        score += selectedLandingOption.points
        score
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Piloting Skills Calculator", color = primaryTextColor, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        triggerVibration(vibrator)

                        // Reset all inputs
                        didTakeOff = false
                        figure8Count = 0
                        smallHoleCount = 0
                        largeHoleCount = 0
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

        CompositionLocalProvider(LocalContentColor provides primaryTextColor) {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Total Score Section
                item {
                    SectionHeader("Total Score", primaryTextColor)
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
                    SectionHeader("Tasks:", primaryTextColor)
                }

                // Take Off Toggle
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Take Off:", color = primaryTextColor, fontWeight = FontWeight.Bold)
                        Switch(
                            checked = didTakeOff,
                            onCheckedChange = {
                                triggerVibration(vibrator)
                                didTakeOff = it
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Green)
                        )
                    }
                }

                // Figure 8 Stepper
                item {
                    PilotStepperInput(
                        label = "Complete a Figure 8:",
                        value = figure8Count,
                        onValueChange = { figure8Count = it },
                        labelColor = primaryTextColor,
                        valueColor = primaryTextColor
                    )
                }

                // Small Hole Stepper
                item {
                    PilotStepperInput(
                        label = "Fly Through Small Hole:",
                        value = smallHoleCount,
                        onValueChange = { smallHoleCount = it },
                        labelColor = primaryTextColor,
                        valueColor = primaryTextColor
                    )
                }

                // Large Hole Stepper
                item {
                    PilotStepperInput(
                        label = "Fly Through Large Hole:",
                        value = largeHoleCount,
                        onValueChange = { largeHoleCount = it },
                        labelColor = primaryTextColor,
                        valueColor = primaryTextColor
                    )
                }

                // Keyhole Stepper
                item {
                    PilotStepperInput(
                        label = "Fly Through Keyhole:",
                        value = keyholeCount,
                        onValueChange = { keyholeCount = it },
                        labelColor = primaryTextColor,
                        valueColor = primaryTextColor
                    )
                }

                // Landing Options Section

                item {
                    PilotDropdownInput(
                        label = "Select Landing Option",
                        options = LandingOption.values().toList(),
                        selectedOption = selectedLandingOption,
                        onOptionChange = { selectedLandingOption = it },
                        labelColor = primaryTextColor
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(text: String, textColor: Color) {
    Column {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp), color = textColor.copy(alpha = 0.5f))
    }
}

@Composable
fun PilotStepperInput(
    label: String,
    value: Int,
    maxValue: Int = Int.MAX_VALUE,
    onValueChange: (Int) -> Unit,
    labelColor: Color,
    valueColor: Color
) {
    val vibrator = LocalContext.current.let { remember { it.getVibrator() } }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Make the label bold
        Text(label, color = labelColor, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Change fontSize as needed for + and -.
            // For example, to make the text larger, use fontSize = 20.sp
            Button(
                onClick = {
                    triggerVibration(vibrator)
                    if (value > 0) onValueChange(value - 1)
                },
                modifier = Modifier.padding(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("-", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 25.sp)
            }
            Text(
                "$value",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 8.dp),
                color = valueColor,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = {
                    triggerVibration(vibrator)
                    if (value < maxValue) onValueChange(value + 1)
                },
                modifier = Modifier.padding(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("+", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 25.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PilotDropdownInput(
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

@Composable
fun primaryTextColor(): Color {
    return if (isSystemInDarkTheme()) Color.White else Color.Black
}

@Preview(showBackground = true)
@Composable
fun PreviewPilotingSkillsCalculator() {
    PilotingSkillsCalculator()
}
