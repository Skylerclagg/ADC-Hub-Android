@file:OptIn(ExperimentalMaterial3Api::class)

package com.skylerclagg.adc_hub

import LandingOption
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun PilotingSkillsCalculator() {
    // State variables for task counts
    var barrelRollCount by remember { mutableStateOf(0) }
    var highJumpCount by remember { mutableStateOf(0) }
    var obstacleCourseCount by remember { mutableStateOf(0) }
    var hoverCount by remember { mutableStateOf(0) }
    var landingOption by remember { mutableStateOf(LandingOption.NONE) }

    // Calculating the total score based on the selected inputs
    val totalScore = calculatePilotingScore(
            barrelRollCount, highJumpCount, obstacleCourseCount, hoverCount, landingOption
    )

    // Layout for the UI
    Column(
            modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar with title and clear button
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                    text = "Piloting Skills Calculator",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
            )
            Button(onClick = {
                    // Reset all inputs
                    barrelRollCount = 0
                    highJumpCount = 0
                    obstacleCourseCount = 0
                    hoverCount = 0
                    landingOption = LandingOption.NONE
            }) {
                Text("Clear Scores")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Total score display
        Text("Total Score: $totalScore", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // Task counters
        PilotStepperInput("Barrel Roll (Max 3)", barrelRollCount, maxValue = 3) { barrelRollCount = it }
        PilotStepperInput("High Jump (Max 2)", highJumpCount, maxValue = 2) { highJumpCount = it }
        PilotStepperInput("Obstacle Course (Max 1)", obstacleCourseCount, maxValue = 1) { obstacleCourseCount = it }
        PilotStepperInput("Hover for 10 Seconds (Max 4)", hoverCount, maxValue = 4) { hoverCount = it }

        Spacer(modifier = Modifier.height(16.dp))

        // Landing option selection
        Text("Landing Options", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        DropdownInput("Landing Option", LandingOption.values().toList(), landingOption) { landingOption = it }
    }
}

// Function to calculate total score based on the inputs
fun calculatePilotingScore(
        barrelRollCount: Int,
        highJumpCount: Int,
        obstacleCourseCount: Int,
        hoverCount: Int,
        landingOption: LandingOption
): Int {
    var score = 0
    score += barrelRollCount * 20
    score += highJumpCount * 25
    score += obstacleCourseCount * 50
    score += hoverCount * 10
    score += landingOption.points
    return score
}

// Stepper Input Component
@Composable
fun PilotStepperInput(label: String, value: Int, maxValue: Int = Int.MAX_VALUE, onValueChange: (Int) -> Unit) {
Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
Text(label, modifier = Modifier.align(Alignment.CenterVertically))
Button(onClick = { if (value > 0) onValueChange(value - 1) }, modifier = Modifier.padding(4.dp)) {
Text("-")
        }
Text("$value", modifier = Modifier.align(Alignment.CenterVertically).padding(4.dp))
Button(onClick = { if (value < maxValue) onValueChange(value + 1) }, modifier = Modifier.padding(4.dp)) {
Text("+")
        }
                }
                }

// Dropdown Input for Landing Option
@Composable
fun PilotDropdownInput(label: String, options: List<LandingOption>, selectedOption: LandingOption, onOptionChange: (LandingOption) -> Unit) {
var expanded by remember { mutableStateOf(false) }
ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
OutlinedTextField(
        value = selectedOption.name,
        onValueChange = {},
readOnly = true,
label = { Text(label) },
trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
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
                text = { Text(option.name) },
                onClick = {
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
fun PreviewPilotingSkillsCalculator() {
    PilotingSkillsCalculator()
}
