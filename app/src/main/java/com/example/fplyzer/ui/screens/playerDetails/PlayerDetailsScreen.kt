package com.example.fplyzer.ui.screens.playerDetails

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fplyzer.data.models.Element
import com.example.fplyzer.data.models.PlayerFixture
import com.example.fplyzer.data.models.PlayerHistory
import com.example.fplyzer.data.models.PlayerPastHistory
import com.example.fplyzer.data.models.Team
import com.example.fplyzer.ui.theme.FplAccent
import com.example.fplyzer.ui.theme.FplBlue
import com.example.fplyzer.ui.theme.FplError
import com.example.fplyzer.ui.theme.FplGreen
import com.example.fplyzer.ui.theme.FplPrimary
import com.example.fplyzer.ui.theme.FplPrimaryDark
import com.example.fplyzer.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDetailsScreen(
    playerId: Int,
    onNavigateBack: () -> Unit,
    viewModel: PlayerDetailsViewModel = viewModel()
) {
    val uiState by viewModel.uiState

    LaunchedEffect(playerId) {
        viewModel.loadPlayerDetails(playerId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.player?.displayName ?: "Player Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FplPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = FplPrimary)
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = "Error",
                            modifier = Modifier.size(64.dp),
                            tint = FplError
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.error ?: "Unknown error",
                            color = FplError,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadPlayerDetails(playerId) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FplPrimary
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            uiState.player != null && uiState.playerDetails != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Player Info
                    item {
                        PlayerInfoCard(
                            player = uiState.player!!,
                            team = uiState.team?.name ?: "",
                            position = uiState.position?.singularName ?: ""
                        )
                    }

                    // Stats
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            StatCard(
                                modifier = Modifier.weight(1f),
                                title = "Total Points",
                                value = uiState.player!!.totalPoints.toString(),
                                icon = Icons.Default.Star,
                                containerColor = FplGreen
                            )
                            StatCard(
                                modifier = Modifier.weight(1f),
                                title = "Price",
                                value = uiState.player!!.displayPrice,
                                icon = Icons.Default.MonetizationOn,
                                containerColor = FplBlue
                            )
                        }
                    }

                    // Tabs
                    item {
                        TabRow(
                            selectedTabIndex = uiState.selectedTab,
                            containerColor = FplPrimary,
                            contentColor = Color.White
                        ) {
                            Tab(
                                selected = uiState.selectedTab == 0,
                                onClick = { viewModel.setSelectedTab(0) },
                                text = { Text("Fixtures") }
                            )
                            Tab(
                                selected = uiState.selectedTab == 1,
                                onClick = { viewModel.setSelectedTab(1) },
                                text = { Text("History") }
                            )
                            Tab(
                                selected = uiState.selectedTab == 2,
                                onClick = { viewModel.setSelectedTab(2) },
                                text = { Text("Past Seasons") }
                            )
                        }
                    }

                    // Tab Content
                    when (uiState.selectedTab) {
                        0 -> {
                            items(uiState.playerDetails!!.fixtures) { fixture ->
                                FixtureCard(fixture = fixture, teams = uiState.teams)
                            }
                        }
                        1 -> {
                            items(uiState.playerDetails!!.history.reversed()) { history ->
                                PlayerHistoryCard(history = history)
                            }
                        }
                        2 -> {
                            items(uiState.playerDetails!!.historyPast) { past ->
                                PastSeasonCard(past = past)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerInfoCard(
    player: Element,
    team: String,
    position: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = FplPrimary
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.radialGradient(
                        colors = listOf(FplPrimary, FplPrimaryDark)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = player.displayName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$position | $team",
                    style = MaterialTheme.typography.titleMedium,
                    color = FplAccent
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = player.selectedByPercent,
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Selected By",
                            style = MaterialTheme.typography.bodySmall,
                            color = FplAccent
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = player.form,
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Form",
                            style = MaterialTheme.typography.bodySmall,
                            color = FplAccent
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = containerColor.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = containerColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = containerColor
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = FplTextSecondary
            )
        }
    }
}

@Composable
fun FixtureCard(
    fixture: PlayerFixture,
    teams: List<Team>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "GW ${fixture.event}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${teams.find { it.id == (if (fixture.isHome) fixture.teamA else fixture.teamH) }?.name ?: "-"} ${if (fixture.isHome) "A" else "H"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FplTextSecondary
                )
                Text(
                    text = formatDate(fixture.kickoffTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = FplTextSecondary
                )
            }
            Text(
                text = "Difficulty: ${fixture.difficulty}",
                style = MaterialTheme.typography.bodyMedium,
                color = when (fixture.difficulty) {
                    in 1..2 -> FplGreen
                    3 -> FplYellow
                    else -> FplRed
                }
            )
        }
    }
}

@Composable
fun PlayerHistoryCard(
    history: PlayerHistory
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Gameweek ${history.round}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${history.totalPoints} pts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FplGreen
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Goals: ${history.goalsScored} | Assists: ${history.assists}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FplTextSecondary
                )
                Text(
                    text = "Minutes: ${history.minutes}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FplTextSecondary
                )
            }
        }
    }
}

@Composable
fun PastSeasonCard(
    past: PlayerPastHistory
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = past.seasonName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${past.totalPoints} pts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FplGreen
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Goals: ${past.goalsScored} | Assists: ${past.assists} | Minutes: ${past.minutes}",
                style = MaterialTheme.typography.bodyMedium,
                color = FplTextSecondary
            )
        }
    }
}

private fun formatDate(date: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US)
        outputFormat.timeZone = TimeZone.getDefault()
        val parsedDate = inputFormat.parse(date) ?: 0
        outputFormat.format(parsedDate)
    } catch (e: Exception) {
        date
    }
}