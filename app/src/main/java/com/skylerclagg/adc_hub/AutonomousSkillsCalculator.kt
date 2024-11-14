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
fun AutonomousFlightSkillsCalculator() {
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
                text = "Autonomous Flight Calculator",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = {
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
                Text("Clear Scores")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Total score display
        Text("Total Score: $totalScore", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // Task counters
        AutoStepperInput("Take Off (Max 2)", takeOffCount, maxValue = 2) { takeOffCount = it }
        AutoStepperInput("Identify Color Count (Max 2)", identifyColorCount, maxValue = 2) { identifyColorCount = it }
        AutoStepperInput("Complete a Figure 8 (Max 2)", figure8Count, maxValue = 2) { figure8Count = it }
        AutoStepperInput("Fly Through Small Hole (Max 2)", smallHoleCount, maxValue = 2) { smallHoleCount = it }
        AutoStepperInput("Fly Through Large Hole (Max 2)", largeHoleCount, maxValue = 2) { largeHoleCount = it }
        AutoStepperInput("Fly Under Arch Gate (Max 4)", archGateCount, maxValue = 4) { archGateCount = it }
        AutoStepperInput("Fly Through Keyhole (Max 4)", keyholeCount, maxValue = 4) { keyholeCount = it }

        Spacer(modifier = Modifier.height(16.dp))

        // Landing option selection
        Text("Landing Options", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        DropdownInput(
            "Landing Option",
            LandingOption.values().toList(),
            selectedLandingOption
        ) { selectedLandingOption = it }
    }
}

// Function to calculate total score based on the inputs
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


// Stepper Input Component
@Composable
fun AutoStepperInput(label: String, value: Int, maxValue: Int = Int.MAX_VALUE, onValueChange: (Int) -> Unit) {
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

@Composable
fun DropdownInput(
    label: String,
    options: List<LandingOption>,
    selectedOption: LandingOption,
    onOptionChange: (LandingOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption.displayName,
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
                    text = { Text(option.displayName) },
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
fun PreviewAutonomousFlightSkillsCalculator() {
    AutonomousFlightSkillsCalculator()
}
