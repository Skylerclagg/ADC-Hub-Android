package com.skylerclagg.adc_hub

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.skylerclagg.adc_hub.destinations.TeamEventsViewDestination
import com.skylerclagg.adc_hub.helperviews.EventRow
import com.skylerclagg.adc_hub.helperviews.SegmentText
import com.skylerclagg.adc_hub.helperviews.SegmentedControl
import com.skylerclagg.adc_hub.ui.theme.button
import com.skylerclagg.adc_hub.ui.theme.onTopContainer
import com.skylerclagg.adc_hub.ui.theme.topContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.decodeFromJsonElement
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LookupViewModel : ViewModel() {
    var lookupType = mutableStateOf("Teams")
    var applicationContext: Context? = null

    // TeamLookup
    var teamTextColor = mutableStateOf(Color.Gray)
    var number = mutableStateOf("12345a\u200B")
    var team = mutableStateOf(Team())
    var wsEntry = mutableStateOf<Pair<WSEntry, Int>?>(null)
    var avgRanking = mutableDoubleStateOf(0.0)
    var awardCounts = LinkedHashMap<String, Int>()
    var fetchedTeams = mutableStateOf(false)
    var loadingTeams = mutableStateOf(false)

    // EventLookup
    var eventTextColor = mutableStateOf(Color.Gray)
    var eventName = mutableStateOf("Event Name\u200B")
    var events = mutableStateOf(listOf<Event>())
    var page = mutableIntStateOf(1)
    var fetchedEvents = mutableStateOf(false)
    var loadingEvents = mutableStateOf(false)

    /**
     * Mirroring the iOS approach: if true, we apply a date filter
     * (two weeks ago) when the user hasn’t entered a name.
     * If false, we fetch all events from 01-Jan-1970.
     */
    var isDateFilterActive = mutableStateOf(true)

    fun fetchTeam() {
        loadingTeams.value = true
        teamTextColor.value = Color.Unspecified
        CoroutineScope(Dispatchers.Default).launch {
            val fetchedTeam = Team(number.value)
            fetchedTeam.fetchAwards()
            val fetchedAwardCounts = LinkedHashMap<String, Int>()
            for (award in fetchedTeam.awards) {
                fetchedAwardCounts[award.title] = (fetchedAwardCounts[award.title] ?: 0) + 1
            }
            withContext(Dispatchers.Main) {
                team.value = fetchedTeam
                wsEntry.value = API.worldSkillsFor(fetchedTeam)
                avgRanking.doubleValue = fetchedTeam.averageQualifiersRanking()
                awardCounts = fetchedAwardCounts
                fetchedTeams.value = fetchedTeam.id != 0
                loadingTeams.value = false
            }
        }
    }

    fun fetchEvents(
        name: String? = null,
        season: Int? = null,
        level: Int? = null,
        region: Int? = null,
        noLeagues: Boolean = false,
        page: Int = 1
    ) {
        loadingEvents.value = true
        eventTextColor.value = Color.Unspecified

        val scraperParams = mutableMapOf<String, Any>()

        // If user typed a name, we apply the "name" filter
        if (!name.isNullOrEmpty()) {
            scraperParams["name"] = name
        }
        if (season != null) {
            scraperParams["seasonId"] = season
        }
        // If 'noLeagues' is true or user typed no name, we set eventType = 1
        if (noLeagues || name.isNullOrEmpty()) {
            scraperParams["eventType"] = 1
        }
        if (level != null) {
            scraperParams["level_class_id"] = level
        }
        if (region != null) {
            scraperParams["event_region"] = region
        }

        // Page number
        scraperParams["page"] = page

        // Always override "seasonId" if none was passed
        scraperParams["seasonId"] = season ?: UserSettings(applicationContext!!).getSelectedSeasonId()

        // If the date filter is active, show events from two weeks ago; else show everything
        val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
        if (isDateFilterActive.value) {
            // Calculate 2 weeks ago
            val twoWeeksAgoMillis = System.currentTimeMillis() - (14L * 24L * 60L * 60L * 1000L)
            val twoWeeksAgo = Date(twoWeeksAgoMillis)
            scraperParams["from_date"] = formatter.format(twoWeeksAgo)
        } else {
            // If date filter is disabled, fetch everything
            scraperParams["from_date"] = "01-Jan-1970"
        }

        CoroutineScope(Dispatchers.Default).launch {
            val skuArray = ADCHubAPI.roboteventsCompetitionScraper(params = scraperParams)
            if (skuArray.isEmpty()) {
                withContext(Dispatchers.Main) {
                    events.value = listOf()
                    fetchedEvents.value = false
                    loadingEvents.value = false
                }
                return@launch
            }
            val data = ADCHubAPI.roboteventsRequest(
                requestUrl = "/seasons/${scraperParams["seasonId"]}/events",
                params = mapOf("sku" to skuArray)
            )
            withContext(Dispatchers.Main) {
                val fetchedEventsList = data.map { jsonWorker.decodeFromJsonElement<Event>(it) }
                events.value = fetchedEventsList
                fetchedEvents.value = true
                loadingEvents.value = false
            }
        }
    }
}

/**
 * A simple row composable with a label on the left and
 * a value on the right, wrapped in multiple lines if needed.
 */
@Composable
fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    labelWeight: Float = 1f,
    valueWeight: Float = 2f,
    textAlign: TextAlign = TextAlign.Start
) {
    Row(
        modifier = modifier
            // Slightly more space on the left
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(labelWeight),
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            modifier = Modifier.weight(valueWeight),
            textAlign = textAlign
            // No maxLines here, so it will wrap automatically.
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun LookupView(
    lookupViewModel: LookupViewModel = viewModels["lookup_view"] as LookupViewModel,
    navController: NavController
) {
    val userSettings = UserSettings(LocalContext.current)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        lookupViewModel.applicationContext = LocalContext.current.applicationContext
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.topContainer,
                        titleContentColor = MaterialTheme.colorScheme.onTopContainer,
                    ),
                    title = {
                        Text("Lookup", fontWeight = FontWeight.Bold)
                    }
                )
            },
            bottomBar = {
                // Only show bottom bar if user is on "Events" tab
                if (lookupViewModel.lookupType.value != "Teams") {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth()
                    ) {
                        IconButton(
                            enabled = lookupViewModel.page.intValue != 1,
                            onClick = {
                                // Re-assert date filter based on current search text
                                lookupViewModel.isDateFilterActive.value =
                                    lookupViewModel.eventName.value.isEmpty()

                                lookupViewModel.page.intValue -= 1
                                lookupViewModel.fetchEvents(
                                    name = lookupViewModel.eventName.value,
                                    page = lookupViewModel.page.intValue
                                )
                            }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBackIos,
                                contentDescription = "Previous Page",
                                modifier = Modifier.width(30.dp),
                                tint = if (lookupViewModel.page.intValue != 1)
                                    MaterialTheme.colorScheme.button
                                else
                                    Color.Gray
                            )
                        }
                        Text(
                            "${lookupViewModel.page.intValue}",
                            modifier = Modifier.padding(horizontal = 20.dp),
                            fontSize = 25.sp,
                            textAlign = TextAlign.Center
                        )
                        IconButton(
                            enabled = lookupViewModel.events.value.size == 20,
                            onClick = {
                                // Re-assert date filter based on current search text
                                lookupViewModel.isDateFilterActive.value =
                                    lookupViewModel.eventName.value.isEmpty()

                                lookupViewModel.page.intValue += 1
                                lookupViewModel.fetchEvents(
                                    name = lookupViewModel.eventName.value,
                                    page = lookupViewModel.page.intValue
                                )
                            }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = "Next Page",
                                modifier = Modifier.width(30.dp),
                                tint = if (lookupViewModel.events.value.size == 20)
                                    MaterialTheme.colorScheme.button
                                else
                                    Color.Gray
                            )
                        }
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier.padding(padding)
            ) {
                SegmentedControl(
                    listOf("Teams", "Events"),
                    lookupViewModel.lookupType.value,
                    onSegmentSelected = { lookupViewModel.lookupType.value = it },
                    modifier = Modifier.padding(10.dp)
                ) {
                    SegmentText(text = it)
                }
                if (lookupViewModel.lookupType.value == "Teams") {
                    TeamLookup(
                        lookupViewModel = lookupViewModel,
                        navController = navController
                    )
                } else {
                    EventLookup(
                        lookupViewModel = lookupViewModel,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun TeamLookup(lookupViewModel: LookupViewModel, navController: NavController) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val localContext = LocalContext.current
    val userSettings = remember { UserSettings(localContext) }
    val isFocused = remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        var favoriteTeams by remember {
            mutableStateOf(
                userSettings.getData("favoriteTeams", "").replace("[", "").replace("]", "")
                    .split(", ")
            )
        }
        Spacer(Modifier.height(5.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
        ) {
            Spacer(Modifier.width(14.dp))
            Icon(
                Icons.Filled.Star,
                modifier = Modifier
                    .size(30.dp)
                    .alpha(0F),
                contentDescription = "Spacer",
            )
            Spacer(modifier = Modifier.weight(1.0f))
            TextField(
                modifier = Modifier.sizeIn(maxWidth = 200.dp),
                value = lookupViewModel.number.value,
                onValueChange = { lookupViewModel.number.value = it.trim() },
                singleLine = true,
                interactionSource = remember { MutableInteractionSource() }
                    .also { interactionSource ->
                        LaunchedEffect(interactionSource) {
                            interactionSource.interactions.collect {
                                when (it) {
                                    is FocusInteraction.Focus -> isFocused.value = true
                                    is FocusInteraction.Unfocus -> isFocused.value = false
                                    is PressInteraction.Release -> {
                                        lookupViewModel.number.value = ""
                                        lookupViewModel.fetchedTeams.value = false
                                    }
                                }
                            }
                        }
                    },
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 34.sp,
                    color = if (
                        lookupViewModel.number.value.isEmpty() ||
                        lookupViewModel.number.value == "12345a\u200B"
                    ) Color.Gray else MaterialTheme.colorScheme.onSurface
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedTextColor = lookupViewModel.teamTextColor.value,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        lookupViewModel.fetchTeam()
                    }
                ),
                placeholder = {
                    if (!isFocused.value && lookupViewModel.number.value.isEmpty()) {
                        Text(
                            "12345a\u200B",
                            modifier = Modifier.fillMaxWidth(),
                            style = LocalTextStyle.current.copy(
                                color = Color.Gray,
                                fontSize = 34.sp,
                                textAlign = TextAlign.Center,
                            )
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.weight(1.0f))
            Box {
                IconButton(
                    enabled = lookupViewModel.number.value != "12345a\u200B" &&
                            lookupViewModel.number.value.isNotBlank(),
                    modifier = Modifier.alpha(
                        if (
                            lookupViewModel.number.value != "12345a\u200B" &&
                            lookupViewModel.number.value.isNotBlank()
                        ) 1F else 0F
                    ),
                    onClick = {
                        favoriteTeams =
                            if (
                                lookupViewModel.number.value.isEmpty() ||
                                lookupViewModel.number.value == "12345a\u200B"
                            ) {
                                return@IconButton
                            } else if (
                                favoriteTeams.contains(lookupViewModel.number.value.uppercase()) &&
                                !lookupViewModel.loadingTeams.value
                            ) {
                                userSettings.removeFavoriteTeam(
                                    lookupViewModel.number.value.uppercase()
                                )
                                userSettings.getData("favoriteTeams", "").replace("[", "")
                                    .replace("]", "")
                                    .split(", ")
                            } else {
                                // allow adding to favorites only after fetching team data
                                if (!lookupViewModel.fetchedTeams.value) {
                                    keyboardController?.hide()
                                    lookupViewModel.fetchTeam()
                                    return@IconButton
                                } else {
                                    userSettings.addFavoriteTeam(
                                        lookupViewModel.number.value.uppercase()
                                    )
                                    userSettings.getData("favoriteTeams", "").replace("[", "")
                                        .replace("]", "")
                                        .split(", ")
                                }
                            }
                    }
                ) {
                    if (
                        favoriteTeams.contains(lookupViewModel.number.value.uppercase()) &&
                        lookupViewModel.number.value.isNotBlank()
                    ) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = "Unfavorite",
                            modifier = Modifier.size(30.dp),
                            tint = MaterialTheme.colorScheme.button
                        )
                    } else {
                        Icon(
                            Icons.Outlined.StarOutline,
                            contentDescription = "Favorite",
                            modifier = Modifier.size(30.dp),
                            tint = MaterialTheme.colorScheme.button
                        )
                    }
                }
            }
        }
        if (lookupViewModel.loadingTeams.value) {
            Column(
                modifier = Modifier.height(20.dp),
            ) {
                LoadingView()
            }
        } else {
            Spacer(Modifier.height(20.dp))
        }
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.padding(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(10.dp)
                ) {

                    // Name
                    InfoRow(
                        label = "Name",
                        value = if (lookupViewModel.fetchedTeams.value)
                            lookupViewModel.team.value.name
                        else ""
                    )
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                    )

                    // Robot
                    InfoRow(
                        label = "Robot",
                        value = if (lookupViewModel.fetchedTeams.value)
                            (lookupViewModel.team.value.robotName ?: "")
                        else ""
                    )
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                    )

                    // Organization (wraps if it's too long)
                    InfoRow(
                        label = "Organization",
                        value = if (lookupViewModel.fetchedTeams.value)
                            lookupViewModel.team.value.organization
                        else ""
                    )
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                    )

                    // Location
                    InfoRow(
                        label = "Location",
                        value =
                        if (
                            lookupViewModel.fetchedTeams.value &&
                            (lookupViewModel.team.value.location.country ?: "").isNotEmpty()
                        ) {
                            lookupViewModel.team.value.location.toString()
                        } else ""
                    )
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                    )

                    // World Skills Ranking
                    InfoRow(
                        label = "World Skills Ranking",
                        value =
                        if (lookupViewModel.fetchedTeams.value) {
                            if (lookupViewModel.wsEntry.value != null) {
                                val (wsEntry, cacheSize) = lookupViewModel.wsEntry.value!!
                                "# ${wsEntry.rank} of $cacheSize"
                            } else {
                                "No Data Available"
                            }
                        } else ""
                    )
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                    )

                    // World Skills Score (clickable with dropdown)
                    var wsExpanded by remember { mutableStateOf(false) }

                    Row {
                        Text(
                            "World Skills Score",
                            modifier =
                            if (lookupViewModel.fetchedTeams.value &&
                                lookupViewModel.wsEntry.value != null
                            ) {
                                Modifier.clickable { wsExpanded = !wsExpanded }
                            } else {
                                Modifier
                            },
                            color = MaterialTheme.colorScheme.button
                        )
                        DropdownMenu(
                            expanded = wsExpanded,
                            onDismissRequest = { wsExpanded = false }
                        ) {
                            if (lookupViewModel.wsEntry.value != null) {
                                val (wsEntry, _) = lookupViewModel.wsEntry.value!!
                                DropdownMenuItem(
                                    text = { Text("${wsEntry.scores.programming} autonomous") },
                                    onClick = { },
                                    enabled = false
                                )
                                DropdownMenuItem(
                                    text = { Text("${wsEntry.scores.driver} Piloting") },
                                    onClick = { },
                                    enabled = false
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { Text("No Data Available") },
                                    onClick = { },
                                    enabled = false
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(1.0f))
                        Text(
                            if (lookupViewModel.fetchedTeams.value) {
                                if (lookupViewModel.wsEntry.value != null) {
                                    lookupViewModel.wsEntry.value!!.first.scores.score.toString()
                                } else {
                                    "No Data"
                                }
                            } else ""
                        )
                    }
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                    )

                    // Awards (clickable with dropdown)
                    var awardsExpanded by remember { mutableStateOf(false) }

                    Row {
                        Text(
                            "Awards",
                            modifier =
                            if (lookupViewModel.fetchedTeams.value) {
                                Modifier.clickable { awardsExpanded = !awardsExpanded }
                            } else {
                                Modifier
                            },
                            color = MaterialTheme.colorScheme.button
                        )
                        DropdownMenu(
                            expanded = awardsExpanded,
                            onDismissRequest = { awardsExpanded = false }
                        ) {
                            lookupViewModel.awardCounts.forEach { award ->
                                DropdownMenuItem(
                                    text = { Text("${award.value}x ${award.key}") },
                                    onClick = { },
                                    enabled = false
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(1.0f))
                        Text(
                            if (lookupViewModel.fetchedTeams.value)
                                "${lookupViewModel.team.value.awards.size}"
                            else ""
                        )
                    }
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                    )

                    // Events row (clickable)
                    Column(
                        modifier =
                        if (lookupViewModel.fetchedTeams.value) {
                            Modifier.clickable {
                                navController.navigate(
                                    TeamEventsViewDestination(lookupViewModel.team.value)
                                )
                            }
                        } else {
                            Modifier
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Events",
                                color = MaterialTheme.colorScheme.button
                            )
                            Spacer(modifier = Modifier.weight(1.0f))
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForwardIos,
                                modifier = Modifier
                                    .size(15.dp)
                                    .alpha(if (lookupViewModel.fetchedTeams.value) 1F else 0F),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                contentDescription = "Show Events"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventLookup(lookupViewModel: LookupViewModel, navController: NavController) {
    val userSettings = UserSettings(LocalContext.current)
    val keyboardController = LocalSoftwareKeyboardController.current
    val isFocused = remember { mutableStateOf(false) }

    // If events list is empty, fetch on first load
    LaunchedEffect(Unit) {
        if (lookupViewModel.events.value.isEmpty()) {
            lookupViewModel.fetchEvents()
            lookupViewModel.page.intValue = 1
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(Modifier.height(5.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
        ) {
            // Search text field
            TextField(
                value = lookupViewModel.eventName.value,
                onValueChange = {
                    lookupViewModel.eventName.value = it

                    // If user typed something, disable the date filter (like iOS)
                    // If user cleared the name, enable the date filter
                    lookupViewModel.isDateFilterActive.value = it.isEmpty()
                },
                singleLine = true,
                interactionSource = remember { MutableInteractionSource() }
                    .also { interactionSource ->
                        LaunchedEffect(interactionSource) {
                            interactionSource.interactions.collect {
                                when (it) {
                                    is FocusInteraction.Focus -> isFocused.value = true
                                    is FocusInteraction.Unfocus -> isFocused.value = false
                                    is PressInteraction.Release -> {
                                        lookupViewModel.eventName.value = ""
                                        lookupViewModel.fetchedEvents.value = false
                                        // Re-enable date filter if needed
                                        lookupViewModel.isDateFilterActive.value = true
                                    }
                                }
                            }
                        }
                    },
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 34.sp,
                    color = if (
                        lookupViewModel.eventName.value.isEmpty() ||
                        lookupViewModel.eventName.value == "Event Name\u200B"
                    ) Color.Gray else MaterialTheme.colorScheme.onSurface
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedTextColor = lookupViewModel.eventTextColor.value
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        // Now fetch events:
                        lookupViewModel.page.intValue = 1
                        lookupViewModel.fetchEvents(
                            name = lookupViewModel.eventName.value,
                            page = lookupViewModel.page.intValue
                        )
                    }
                ),
                placeholder = {
                    if (!isFocused.value && lookupViewModel.eventName.value.isEmpty()) {
                        Text(
                            "Event Name\u200B",
                            modifier = Modifier.fillMaxWidth(),
                            style = LocalTextStyle.current.copy(
                                color = Color.Gray,
                                fontSize = 34.sp,
                                textAlign = TextAlign.Center,
                            )
                        )
                    }
                }
            )
        }
        if (lookupViewModel.loadingEvents.value) {
            Column(
                modifier = Modifier.height(60.dp),
            ) {
                LoadingView()
            }
        } else {
            Spacer(Modifier.height(60.dp))
        }
        if (lookupViewModel.events.value.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(1f, false)
            ) {
                Card(
                    modifier = Modifier.padding(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp)
                    ) {
                        lookupViewModel.events.value.forEach { event ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                EventRow(navController, event)
                            }
                            if (lookupViewModel.events.value.indexOf(event) !=
                                lookupViewModel.events.value.size - 1
                            ) {
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
    }
}
