package com.skylerclagg.adc_hub

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import com.skylerclagg.adc_hub.ui.theme.button
import com.skylerclagg.adc_hub.ui.theme.onTopContainer
import com.skylerclagg.adc_hub.ui.theme.topContainer
import kotlinx.coroutines.launch
import me.saket.cascade.CascadeDropdownMenu

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun WorldSkillsView(navController: NavController) {

    // State to track selected grade level
    var selectedGradeLevel by remember { mutableStateOf("High School") }

    var viewTitle by rememberSaveable { mutableStateOf("World Skills") }
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    // filters
    var isFilteredByFavorites by remember { mutableStateOf(false) }
    var filteredLetter by remember { mutableStateOf(' ') }
    var filteredRegion by remember { mutableStateOf(0) }

    // get favorite teams
    val favoriteTeams = remember {
        UserSettings(context).getData("favoriteTeams", "").replace("[", "").replace("]", "")
            .split(", ")
    }

    // clear filters
    fun clearFilters() {
        isFilteredByFavorites = false
        filteredLetter = ' '
        filteredRegion = 0
        viewTitle = "World Skills"
    }

    // filter by favorites
    fun filterByFavorites() {
        isFilteredByFavorites = true
        filteredRegion = 0
        filteredLetter = ' '
        viewTitle = "Favorites Skills"
    }

    // filter by letter
    fun filterByLetter(letter: Char) {
        filteredLetter = letter
        filteredRegion = 0
        isFilteredByFavorites = false
        viewTitle = "$letter Skills"
    }

    // filter by region
    fun filterByRegion(region: Int, regionName: String) {
        filteredRegion = region
        isFilteredByFavorites = false
        filteredLetter = ' '
        viewTitle = "$regionName Skills"
    }

    var filterDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.topContainer,
                    titleContentColor = MaterialTheme.colorScheme.onTopContainer,
                ),
                title = {
                    Text(text = viewTitle, fontWeight = FontWeight.Bold)
                },
                actions = {
                    // link to world skills
                    IconButton(
                        onClick = {
                            uriHandler.openUri("https://www.robotevents.com/robot-competitions/vex-robotics-competition/standings/skills")
                        },
                        modifier = Modifier.padding(horizontal = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "Open World Skills in Browser",
                            tint = MaterialTheme.colorScheme.onTopContainer,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            filterDropdownExpanded = true
                        },
                        modifier = Modifier.padding(horizontal = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = MaterialTheme.colorScheme.onTopContainer,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            )
        }
    ) { padding ->

        var importing by rememberSaveable { mutableStateOf(true) }
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            coroutineScope.launch {
                API.updateWorldSkillsCache()
                importing = false
            }
        }

        // For debugging
        println("Importing: $importing")
        println("Middle School Cache Size: ${API.middleSchoolWorldSkillsCache.size}")
        println("High School Cache Size: ${API.highSchoolWorldSkillsCache.size}")

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth(),
        ) {
            CascadeDropdownMenu(
                expanded = filterDropdownExpanded,
                onDismissRequest = { filterDropdownExpanded = false },
                modifier = Modifier
                    .heightIn(max = 265.dp),
                offset = DpOffset(10.dp, (-10).dp),
            ) {
                DropdownMenuItem(
                    text = { Text("Favorites") },
                    onClick = {
                        filterDropdownExpanded = false
                        filterByFavorites()
                    }
                )

                HorizontalDivider(
                    color = Color.Gray.copy(alpha = 0.1f),
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 5.dp)
                )

                DropdownMenuItem(
                    text = { Text("Region") },
                    children = {
                        API.regionsMap.toSortedMap().forEach { (name, id) ->
                            HorizontalDivider(
                                color = Color.Gray.copy(alpha = 0.1f),
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(horizontal = 5.dp)
                            )

                            DropdownMenuItem(
                                text = { Text(name) },
                                onClick = {
                                    filterByRegion(id, name)
                                    filterDropdownExpanded = false
                                }
                            )
                        }
                    }
                )

                HorizontalDivider(
                    color = Color.Gray.copy(alpha = 0.1f),
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 5.dp)
                )

                DropdownMenuItem(
                    text = { Text("Letter") },
                    children = {
                        // make a list of all the letters
                        for (letter in 'A'..'Z') {
                            HorizontalDivider(
                                color = Color.Gray.copy(alpha = 0.1f),
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(horizontal = 5.dp)
                            )

                            DropdownMenuItem(
                                text = { Text(letter.toString()) },
                                onClick = {
                                    filterByLetter(letter)
                                    filterDropdownExpanded = false
                                }
                            )
                        }
                    }
                )

                HorizontalDivider(
                    color = Color.Gray.copy(alpha = 0.1f),
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 5.dp)
                )

                DropdownMenuItem(
                    text = { Text("Clear Filters") },
                    onClick = {
                        clearFilters()
                        filterDropdownExpanded = false
                    }
                )
            }
        }


        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Selector for Middle School or High School
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Middle School",
                    modifier = Modifier
                        .clickable { selectedGradeLevel = "Middle School" }
                        .padding(horizontal = 8.dp),
                    color = if (selectedGradeLevel == "Middle School") Color.Blue else Color.Gray
                )

                Text(
                    "High School",
                    modifier = Modifier
                        .clickable { selectedGradeLevel = "High School" }
                        .padding(horizontal = 8.dp),
                    color = if (selectedGradeLevel == "High School") Color.Blue else Color.Gray
                )
            }

            if (importing) {
                ImportingDataView()
            } else {
                val displayedCache = if (selectedGradeLevel == "Middle School") {
                    API.middleSchoolWorldSkillsCache
                } else {
                    API.highSchoolWorldSkillsCache
                }

                // For debugging
                println("Displayed Cache Size ($selectedGradeLevel): ${displayedCache.size}")

                if (displayedCache.isEmpty()) {
                    NoDataView()
                } else {
                    Card(
                        modifier = Modifier.padding(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(0.dp),
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            val filteredList = when {
                                isFilteredByFavorites -> displayedCache.filter { it.team.number in favoriteTeams }
                                filteredLetter != ' ' -> displayedCache.filter { it.team.number.last() == filteredLetter }
                                filteredRegion != 0 -> displayedCache.filter { it.team.eventRegionId == filteredRegion }
                                else -> displayedCache
                            }

                            // For debugging
                            println("Filtered List Size: ${filteredList.size}")

                            if (filteredList.isEmpty()) {
                                item {
                                    Text(
                                        "No teams found!",
                                        fontSize = 18.sp,
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .fillMaxSize()
                                            .wrapContentSize(align = Alignment.Center)
                                    )
                                }
                            } else {
                                itemsIndexed(filteredList) { index, wsEntry ->

                                    var expanded by remember { mutableStateOf(false) }

                                    // Determine if a filter is applied
                                    val filterApplied = isFilteredByFavorites || filteredLetter != ' ' || filteredRegion != 0

                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier
                                            .padding(horizontal = 0.dp)
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 7.dp)
                                        ) {
                                            Text(
                                                "#" + (index + 1),
                                                fontSize = 18.sp,
                                                modifier = Modifier.width(130.dp)
                                            )
                                            if (filterApplied) {
                                                Text(
                                                    "(#" + wsEntry.rank.toString() + ")",
                                                    fontSize = 18.sp,
                                                    modifier = Modifier.width(130.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.weight(1.0f))
                                        Text(wsEntry.team.number, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.weight(1.0f))
                                        Row(
                                            modifier = Modifier.width(130.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Spacer(modifier = Modifier.weight(1.0f))
                                            Text(
                                                wsEntry.scores.score.toString(),
                                                fontSize = 18.sp,
                                                color = MaterialTheme.colorScheme.button,
                                                modifier = Modifier.clickable {
                                                    expanded = !expanded
                                                })
                                            DropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false }
                                            ) {
                                                val DisabledAlpha = 0.38f
                                                val disabledItemColors = MenuDefaults.itemColors(
                                                    textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = DisabledAlpha),
                                                    leadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = DisabledAlpha),
                                                    trailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = DisabledAlpha)
                                                )
                                                DropdownMenuItem(
                                                    text = { Text("${wsEntry.scores.score} Combined") },
                                                    onClick = { },
                                                    enabled = false,
                                                    colors = disabledItemColors
                                                )
                                                DropdownMenuItem(
                                                    text = { Text("${wsEntry.scores.programming} Autonomous Flight") },
                                                    onClick = { },
                                                    enabled = false,
                                                    colors = disabledItemColors
                                                )
                                                DropdownMenuItem(
                                                    text = { Text("${wsEntry.scores.driver} Piloting") },
                                                    onClick = { },
                                                    enabled = false,
                                                    colors = disabledItemColors
                                                )

                                            }
                                            Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                                            Column {
                                                Text(
                                                    wsEntry.scores.programming.toString(),
                                                    fontSize = 12.sp
                                                )
                                                Text(wsEntry.scores.driver.toString(), fontSize = 12.sp)
                                            }
                                        }
                                    }

                                    if (index != filteredList.size - 1) {
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
    }
}