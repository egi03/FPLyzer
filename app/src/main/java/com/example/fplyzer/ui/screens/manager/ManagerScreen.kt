package com.example.fplyzer.ui.screens.manager

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fplyzer.data.models.ClassicLeague
import com.example.fplyzer.data.models.GameweekHistory
import com.example.fplyzer.data.models.Manager
import com.example.fplyzer.ui.theme.FplAccent
import com.example.fplyzer.ui.theme.FplBlue
import com.example.fplyzer.ui.theme.FplError
import com.example.fplyzer.ui.theme.FplGreen
import com.example.fplyzer.ui.theme.FplPrimary
import com.example.fplyzer.ui.theme.FplPrimaryDark
import java.text.NumberFormat
import java.util.Locale
import com.example.fplyzer.ui.theme.FplTextSecondary as FplTextSecondary1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerScreen(
    managerId: Int,
    onNavigateToLeague: (Int) -> Unit,
    onNavigateToTeamViewer: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ManagerViewModel = viewModel()
) {
    val uiState by viewModel.uiState

    LaunchedEffect(managerId) {
        viewModel.loadManagerData(managerId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manager Details", color = Color.White) },
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
                        Icon(
                            Icons.Default.Error,
                            contentDescription = "Error",
                            modifier = Modifier.size(64.dp),
                            tint = FplError
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.error!!,
                            color = FplError,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadManagerData(managerId) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FplAccent
                            )
                        ) {
                            Text("Retry", color = FplPrimaryDark)
                        }
                    }
                }
            }

            uiState.manager != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(FplPrimaryDark),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        ManagerInfoCard(
                            manager = uiState.manager!!,
                            onViewTeamClick = { onNavigateToTeamViewer(managerId) }
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            StatCard(
                                modifier = Modifier.weight(1f),
                                title = "GW Points",
                                value = uiState.manager!!.summaryEventPoints.toString(),
                                icon = Icons.Default.Star,
                                containerColor = FplGreen
                            )
                            StatCard(
                                modifier = Modifier.weight(1f),
                                title = "GW Rank",
                                value = formatNumber(uiState.manager!!.summaryEventRank),
                                icon = Icons.Default.TrendingUp,
                                containerColor = FplBlue
                            )
                        }
                    }

                    item {
                        TabRow(
                            selectedTabIndex = uiState.selectedTab,
                            containerColor = FplPrimaryDark,
                            contentColor = Color.White,
                            indicator = { tabPositions ->
                                TabRowDefaults.Indicator(
                                    Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
                                    color = FplAccent
                                )
                            }
                        ) {
                            Tab(
                                selected = uiState.selectedTab == 0,
                                onClick = { viewModel.selectTab(0) },
                                text = { Text("Leagues", color = Color.White) }
                            )
                            Tab(
                                selected = uiState.selectedTab == 1,
                                onClick = { viewModel.selectTab(1) },
                                text = { Text("History", color = Color.White) }
                            )
                        }
                    }

                    when (uiState.selectedTab) {
                        0 -> {
                            items(uiState.manager!!.leagues.classic) { league ->
                                LeagueCard(
                                    league = league,
                                    onClick = { onNavigateToLeague(league.id) }
                                )
                            }
                        }
                        1 -> {
                            uiState.history?.let { history ->
                                items(history.current.reversed()) { gameweek ->
                                    GameweekHistoryCard(gameweek)
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
fun ManagerInfoCard(
    manager: Manager,
    onViewTeamClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = FplPrimary
        ),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = MaterialTheme.shapes.medium
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
                    text = manager.fullName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = manager.name,
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
                            text = manager.summaryOverallPoints.toString(),
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Total Points",
                            style = MaterialTheme.typography.bodySmall,
                            color = FplAccent
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = formatNumber(manager.summaryOverallRank),
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Overall Rank",
                            style = MaterialTheme.typography.bodySmall,
                            color = FplAccent
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onViewTeamClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FplAccent,
                        contentColor = FplPrimaryDark
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "View Team",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
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
        ),
        shape = MaterialTheme.shapes.medium
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
                color = FplTextSecondary1
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueCard(
    league: ClassicLeague,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = FplPrimary
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = league.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Rank: ${league.rank ?: "-"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FplAccent
                    )
                    if (league.maxEntries != null) {
                        Text(
                            text = "Size: ${league.maxEntries}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FplAccent
                        )
                    }
                }
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "View League",
                tint = FplAccent
            )
        }
    }
}

@Composable
fun GameweekHistoryCard(gameweek: GameweekHistory) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = FplPrimary
        ),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = MaterialTheme.shapes.medium
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
                    text = "Gameweek ${gameweek.event}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Text(
                    text = "${gameweek.points} pts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FplGreen
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Overall Rank",
                            style = MaterialTheme.typography.bodySmall,
                            color = FplAccent
                        )
                        Text(
                            text = formatNumber(gameweek.overallRank),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Transfers",
                            style = MaterialTheme.typography.bodySmall,
                            color = FplAccent
                        )
                        Text(
                            text = "${gameweek.eventTransfers} (-${gameweek.eventTransfersCost})",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

fun formatNumber(number: Int): String {
    return NumberFormat.getNumberInstance(Locale.US).format(number)
}