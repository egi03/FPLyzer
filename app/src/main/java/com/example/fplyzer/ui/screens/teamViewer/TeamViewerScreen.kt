package com.example.fplyzer.ui.screens.teamViewer

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fplyzer.data.models.Element
import com.example.fplyzer.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamViewerScreen(
    managerId: Int,
    onNavigateBack: () -> Unit,
    viewModel: TeamViewerViewModel = viewModel()
) {
    val uiState by viewModel.uiState

    LaunchedEffect(managerId) {
        viewModel.loadTeamData(managerId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Team Selection") },
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
                            onClick = { viewModel.loadTeamData(managerId) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FplPrimary
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            uiState.managerTeam != null && uiState.managerInfo != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        TeamInfoCard(
                            managerName = uiState.managerInfo!!.name,
                            totalPoints = uiState.totalLivePoints,
                            captainPoints = uiState.captainPoints,
                            benchPoints = uiState.benchPoints
                        )
                    }

                    if (!uiState.isLiveDataAvailable) {
                        item {
                            Text(
                                text = "Live data is not available as the season has ended. Displaying team without live points.",
                                color = FplError,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }

                    item {
                        SectionHeader(title = "Starting XI")
                    }
                    val startingPicks = uiState.managerTeam!!.picks.filter { it.position <= 11 }
                    items(startingPicks) { pick ->
                        val player = uiState.players[pick.element]
                        player?.let {
                            PlayerPickCard(
                                player = it,
                                points = if (uiState.isLiveDataAvailable) (uiState.liveData[pick.element]?.totalPoints ?: 0) * pick.multiplier else 0,
                                isCaptain = pick.isCaptain,
                                isViceCaptain = pick.isViceCaptain
                            )
                        }
                    }

                    item {
                        SectionHeader(title = "Bench")
                    }
                    val benchPicks = uiState.managerTeam!!.picks.filter { it.position > 11 }
                    items(benchPicks) { pick ->
                        val player = uiState.players[pick.element]
                        player?.let {
                            PlayerPickCard(
                                player = it,
                                points = if (uiState.isLiveDataAvailable) uiState.liveData[pick.element]?.totalPoints ?: 0 else 0,
                                isCaptain = false,
                                isViceCaptain = false
                            )
                        }
                    }

                    uiState.managerTeam!!.activeChip?.let { chip ->
                        item {
                            ChipCard(chip = chip)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeamInfoCard(
    managerName: String,
    totalPoints: Int,
    captainPoints: Int,
    benchPoints: Int
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
                    Brush.verticalGradient(
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
                    text = managerName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
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
                            text = totalPoints.toString(),
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
                            text = captainPoints.toString(),
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Captain Points",
                            style = MaterialTheme.typography.bodySmall,
                            color = FplAccent
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = benchPoints.toString(),
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Bench Points",
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
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = FplPrimary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun PlayerPickCard(
    player: Element,
    points: Int,
    isCaptain: Boolean,
    isViceCaptain: Boolean
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = player.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = player.displayPrice,
                        style = MaterialTheme.typography.bodyMedium,
                        color = FplTextSecondary
                    )
                }
                if (isCaptain) {
                    Badge(
                        containerColor = FplAccent,
                        contentColor = FplPrimary
                    ) {
                        Text("C")
                    }
                } else if (isViceCaptain) {
                    Badge(
                        containerColor = FplSecondary,
                        contentColor = Color.White
                    ) {
                        Text("VC")
                    }
                }
            }
            Text(
                text = "$points pts",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = FplGreen
            )
        }
    }
}

@Composable
fun ChipCard(chip: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = FplSurface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Active Chip",
                tint = FplAccent,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Active Chip: $chip",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = FplPrimary
            )
        }
    }
}