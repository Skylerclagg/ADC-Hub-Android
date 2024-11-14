package com.skylerclagg.adc_hub.helperviews

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.navigate
import com.skylerclagg.adc_hub.ADCHubAPI
import com.skylerclagg.adc_hub.Event
import com.skylerclagg.adc_hub.Team

@Composable
fun EventRow(navController: NavController, event: Event, team: Team? = null, ) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(5.dp)
                .clickable {
                    navController.navigate(
                        if (team != null) com.skylerclagg.adc_hub.destinations.EventViewDestination(event, team)
                        else com.skylerclagg.adc_hub.destinations.EventViewDestination(event)
                    )
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1.0f)
                ) {
                    Text(
                        event.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row {
                        Text(
                            event.location.toString(),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.weight(1.0f))
                        Text(
                            ADCHubAPI.formatDate(event.startDate),
                            fontSize = 11.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForwardIos,
                    modifier = Modifier.size(15.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    contentDescription = "Show Events"
                )
            }
        }
        Spacer(modifier = Modifier.weight(1.0f))
        Icon(
            Icons.AutoMirrored.Filled.ArrowForwardIos,
            modifier = Modifier.size(15.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            contentDescription = "Show Event"
        )
    }
}