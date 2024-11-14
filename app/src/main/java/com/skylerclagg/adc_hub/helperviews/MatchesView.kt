package com.skylerclagg.adc_hub.helperviews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skylerclagg.adc_hub.AllianceColor
import com.skylerclagg.adc_hub.Match
import com.skylerclagg.adc_hub.Team
import com.skylerclagg.adc_hub.ui.theme.allianceBlue
import com.skylerclagg.adc_hub.ui.theme.allianceRed

@Composable
fun MatchesView(matchList: List<Match>, team: Team? = null) {

    val timeFormat = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())

    fun determineColor(match: Match, team: Team?, defaultColor: Color): Color {
        if (match.completed()) {
            return if (team != null) {
                if (match.winningAlliance() == null) {
                    Color.Yellow
                } else if (match.winningAlliance() == AllianceColor.RED && match.redAlliance.members.find { member -> member.team.id == team.id } != null) {
                    Color.Green
                } else if (match.winningAlliance() == AllianceColor.BLUE && match.blueAlliance.members.find { member -> member.team.id == team.id } != null) {
                    Color.Green
                } else {
                    Color.Red
                }
            } else {
                defaultColor
            }
        }
        else {
            return defaultColor
        }
    }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.padding(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.onSurface,
            )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 0.dp)
            ) {
                matchList.forEach { match ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 5.dp)
                            .fillMaxWidth()
                    ) {
                        // Match Info Column
                        Column(
                            modifier = Modifier.width(60.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = match.shortName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = determineColor(match, team, MaterialTheme.colorScheme.onSurface)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = match.startedDate?.let { timeFormat.format(it) }
                                    ?: match.scheduledDate?.let { timeFormat.format(it) }
                                    ?: "",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Red Alliance Teams
                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier.width(70.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            match.redAlliance.members.forEach { member ->
                                Text(
                                    text = member.team.name,
                                    fontSize = 15.sp,
                                    color = allianceRed,
                                    textDecoration = if (team != null && member.team.id == team.id) TextDecoration.Underline else TextDecoration.None
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Center Display (Scores or Field)
                        if (match.completed()) {
                            if (match.redScore == match.blueScore) {
                                Text(
                                    text = match.redScore.toString(),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Green,
                                    textAlign = TextAlign.Center,
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.width(80.dp)
                                ) {
                                    Text(
                                        text = match.redScore.toString(),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = allianceRed,
                                        textDecoration = if (team != null && match.redAlliance.members.any { it.team.id == team.id }) TextDecoration.Underline else TextDecoration.None
                                    )
                                    Spacer(modifier = Modifier.width(40.dp))
                                    Text(
                                        text = match.blueScore.toString(),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = allianceBlue,
                                        textDecoration = if (team != null && match.blueAlliance.members.any { it.team.id == team.id }) TextDecoration.Underline else TextDecoration.None
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = match.field ?: "",
                                fontSize = 14.sp,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(80.dp)
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Blue Alliance Teams
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.width(70.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            match.blueAlliance.members.forEach { member ->
                                Text(
                                    text = member.team.name,
                                    fontSize = 15.sp,
                                    color = allianceBlue,
                                    textDecoration = if (team != null && member.team.id == team.id) TextDecoration.Underline else TextDecoration.None
                                )
                            }
                        }
                    }
                    if (match != matchList.last()) {
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                        )
                    }
                }
            }
        }
    }
}
