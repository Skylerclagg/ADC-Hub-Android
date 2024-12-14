@file:OptIn(ExperimentalMaterial3Api::class)

package com.skylerclagg.adc_hub

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.annotation.Destination
import com.skylerclagg.adc_hub.destinations.AutonomousFlightSkillsCalculatorDestination
import com.skylerclagg.adc_hub.destinations.PilotingSkillsCalculatorDestination
import com.skylerclagg.adc_hub.destinations.TeamworkScoreCalculatorDestination




@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination
fun ScoreCalculatorsHome(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Score Calculators Home") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Score Calculators",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Button for Teamwork Score Calculator
            Button(
                onClick = {
                    navController.navigate(TeamworkScoreCalculatorDestination.route)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomGreen,   // Changed to custom green
                    contentColor = Color.White      // Keep white text
                )
            ) {
                Text("Teamwork Score Calculator")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button for Autonomous Flight Skills Calculator
            Button(
                onClick = {
                    navController.navigate(AutonomousFlightSkillsCalculatorDestination.route)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomGreen, // Changed to custom green
                    contentColor = Color.White
                )
            ) {
                Text("Autonomous Flight Skills Calculator")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button for Piloting Skills Calculator
            Button(
                onClick = {
                    navController.navigate(PilotingSkillsCalculatorDestination.route)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomGreen, // Changed to custom green
                    contentColor = Color.White
                )
            ) {
                Text("Piloting Skills Calculator")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewScoreCalculatorsHome() {
    ScoreCalculatorsHome(navController = rememberNavController())
}
