package com.example.fplyzer.ui.screens.league

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fplyzer.data.models.League
import com.example.fplyzer.data.models.StandingResult
import com.example.fplyzer.ui.theme.FplAccent
import com.example.fplyzer.ui.theme.FplBackground
import com.example.fplyzer.ui.theme.FplError
import com.example.fplyzer.ui.theme.FplGreen
import com.example.fplyzer.ui.theme.FplPrimary
import com.example.fplyzer.ui.theme.FplPrimaryDark
import com.example.fplyzer.ui.theme.FplRed
import com.example.fplyzer.ui.theme.FplTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueScreen(
    leagueId: Int,
    onNavigateBack: () -> Unit,
    viewModel: LeagueViewModel = viewModel()
) {
    val uiState by viewModel.uiState

    LaunchedEffect(leagueId) {
        viewModel.loadLeagueStandings(leagueId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.standings?.league?.name ?: "League",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FplPrimaryDark,
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
                        .padding(paddingValues)
                        .background(FplPrimaryDark),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = FplAccent)
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(FplPrimaryDark),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error loading league",
                            style = MaterialTheme.typography.titleLarge,
                            color = FplError
                        )
                        Text(
                            text = uiState.error!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = FplTextSecondary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Button(
                            onClick = { viewModel.loadLeagueStandings(leagueId) },
                            modifier = Modifier.padding(top = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FplAccent
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text("Retry", color = FplPrimaryDark)
                        }
                    }
                }
            }

            uiState.standings != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(FplPrimaryDark)
                ) {
                    item {
                        LeagueInfoHeader(uiState.standings!!.league)
                    }
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(FplBackground)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Pos",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(40.dp),
                                color = Color.White
                            )
                            Text(
                                text = "Team",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                color = Color.White
                            )
                            Text(
                                text = "GW",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(50.dp),
                                color = Color.White
                            )
                            Text(
                                text = "Total",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(60.dp),
                                color = Color.White
                            )
                        }
                    }
                    itemsIndexed(uiState.standings!!.standings.results) { index, standing ->
                        StandingRow(
                            standing = standing,
                            position = index + 1
                        )
                    }
                    if (uiState.standings!!.standings.hasNext) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.loadLeagueStandings(
                                            leagueId,
                                            uiState.currentPage + 1
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = FplAccent
                                    ),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Text("Load More", color = FplPrimaryDark)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeagueInfoHeader(league: League) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = FplPrimary
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = league.name,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                InfoChip(
                    icon = Icons.Default.Groups,
                    label = "Type",
                    value = league.leagueType
                )
                if (league.maxEntries != null) {
                    InfoChip(
                        icon = Icons.Default.People,
                        label = "Size",
                        value = league.maxEntries.toString()
                    )
                }
            }
        }
    }
}

@Composable
fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = FplAccent,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodySmall,
            color = FplAccent
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun StandingRow(
    standing: StandingResult,
    position: Int
) {
    val rankChange = standing.rankChange
    val rankColor = when {
        rankChange > 0 -> FplGreen
        rankChange < 0 -> FplRed
        else -> FplTextSecondary
    }
    val rankIcon = when {
        rankChange > 0 -> Icons.Default.ArrowUpward
        rankChange < 0 -> Icons.Default.ArrowDownward
        else -> Icons.Default.Remove
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FplPrimary)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.width(40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = position.toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            if (rankChange != 0) {
                Icon(
                    imageVector = rankIcon,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = rankColor
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = standing.entryName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White
            )
            Text(
                text = standing.playerName,
                style = MaterialTheme.typography.bodySmall,
                color = FplAccent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = standing.eventTotal.toString(),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.width(50.dp),
            color = FplTextSecondary
        )
        Text(
            text = standing.total.toString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(60.dp),
            color = Color.White
        )
    }
}