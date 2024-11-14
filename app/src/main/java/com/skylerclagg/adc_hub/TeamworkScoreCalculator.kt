package com.skylerclagg.adc_hub

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination
fun TeamworkScoreCalculator() {
    // State variables for input fields
    var dropZoneTopCleared by remember { mutableStateOf(0) }
    var greenBeanBags by remember { mutableStateOf(0) }
    var blueBeanBags by remember { mutableStateOf(0) }
    var neutralBalls by remember { mutableStateOf(0) }
    var greenBalls by remember { mutableStateOf(0) }
    var blueBalls by remember { mutableStateOf(0) }
    var redDroneSelection by remember { mutableStateOf("None") }
    var blueDroneSelection by remember { mutableStateOf("None") }

    // List of possible landing options
    val landingOptions = listOf("None", "Small Cube", "Large Cube", "Landing Pad", "Bullseye")

    // Computed total score
    val totalScore = calculateTotalScore(
        dropZoneTopCleared, greenBeanBags, blueBeanBags, neutralBalls, greenBalls, blueBalls, redDroneSelection, blueDroneSelection
    )

    // Flags to show warnings if necessary
    val showTopsClearedWarning = (greenBeanBags + blueBeanBags) > dropZoneTopCleared
    val showDroneWarning = redDroneSelection != "None" && redDroneSelection == blueDroneSelection
    val showBothLandingPadWarning = (redDroneSelection == "Landing Pad" && blueDroneSelection == "Bullseye") ||
            (redDroneSelection == "Bullseye" && blueDroneSelection == "Landing Pad")

    // Layout for the UI
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Total score display with warning
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Total Score: $totalScore", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            if (showTopsClearedWarning) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_dialog_alert),
                    contentDescription = "Warning",
                    tint = Color.Red,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Drop Zone Top Cleared input with warning
        StepperInput(
            label = "Tops Cleared",
            value = dropZoneTopCleared,
            maxValue = 7,
            onValueChange = { dropZoneTopCleared = it },
            showWarning = showTopsClearedWarning
        )

        // Green Bean Bags input (combined limit of 7 between green and blue)
        StepperInput("Bean Bags on\nGreen Drop Zone", greenBeanBags, maxValue = 7 - blueBeanBags, onValueChange = { greenBeanBags = it })

        // Blue Bean Bags input (combined limit of 7 between green and blue)
        StepperInput("Bean Bags on\nBlue Drop Zone", blueBeanBags, maxValue = 7 - greenBeanBags, onValueChange = { blueBeanBags = it })

        // Remaining Bean Bags display
        Text(
            "Remaining Bean Bags: ${7 - greenBeanBags - blueBeanBags}",
            color = Color.Red,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Neutral Balls input (combined limit of 10 between all balls)
        StepperInput("Balls in Neutral Zone", neutralBalls, maxValue = 10 - greenBalls - blueBalls, onValueChange = { neutralBalls = it })

        // Green Balls input (combined limit of 10 between all balls)
        StepperInput("Balls in Green Zone", greenBalls, maxValue = 10 - neutralBalls - blueBalls, onValueChange = { greenBalls = it })

        // Blue Balls input (combined limit of 10 between all balls)
        StepperInput("Balls in Blue Zone", blueBalls, maxValue = 10 - neutralBalls - greenBalls, onValueChange = { blueBalls = it })

        // Remaining Balls display
        Text(
            "Remaining Balls: ${10 - neutralBalls - greenBalls - blueBalls}",
            color = Color.Red,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Red Drone Selection input with warning
        DropdownInput("Red Drone Selection", landingOptions, redDroneSelection) { redDroneSelection = it }

        // Blue Drone Selection input with warning
        DropdownInput("Blue Drone Selection", landingOptions, blueDroneSelection) { blueDroneSelection = it }

        // Warning for Drone Selection
        if (showDroneWarning || showBothLandingPadWarning) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_dialog_alert),
                    contentDescription = "Warning",
                    tint = Color.Red,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = when {
                        showDroneWarning -> "Warning: Both drones cannot be landed on the same object."
                        showBothLandingPadWarning -> "Warning: Both Drones cannot be landed on the landing pad."
                        else -> ""
                    },
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Clear button with updated color
        Button(
            onClick = {
                // Clear all inputs
                dropZoneTopCleared = 0
                greenBeanBags = 0
                blueBeanBags = 0
                neutralBalls = 0
                greenBalls = 0
                blueBalls = 0
                redDroneSelection = "None"
                blueDroneSelection = "None"
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            )
        ) {
            Text(text = "Clear Scores")
        }
    }
}

// Stepper Input Component with a max value constraint and updated button colors
@Composable
fun StepperInput(label: String, value: Int, maxValue: Int = Int.MAX_VALUE, onValueChange: (Int) -> Unit, showWarning: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(label, color = Color.Green, modifier = Modifier.align(Alignment.CenterVertically))
        if (showWarning) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_dialog_alert),
                contentDescription = "Warning",
                tint = Color.Red,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Button(
            onClick = { if (value > 0) onValueChange(value - 1) },
            modifier = Modifier.padding(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
                contentColor = Color.White
            )
        ) {
            Text("-")
        }
        Text("$value", modifier = Modifier.align(Alignment.CenterVertically).padding(4.dp))
        Button(
            onClick = { if (value < maxValue) onValueChange(value + 1) },
            modifier = Modifier.padding(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
                contentColor = Color.White
            )
        ) {
            Text("+")
        }
    }
}

// Dropdown Input for Drone Selection using ExposedDropdownMenuBox
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownInput(label: String, options: List<String>, selectedOption: String, onOptionChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
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
                    text = { Text(option) },
                    onClick = {
                        onOptionChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Function to calculate total score based on the inputs
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

// Function to calculate landing score based on drone selection
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
