package com.example.fplyzer.ui.screens.players

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fplyzer.data.models.Element
import com.example.fplyzer.data.models.ElementType
import com.example.fplyzer.data.models.Team
import com.example.fplyzer.ui.components.*
import com.example.fplyzer.ui.theme.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayersScreen(
    onNavigateToPlayer: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: PlayersViewModel = viewModel()
) {
    val uiState by viewModel.uiState

    Scaffold(
        containerColor = FplBackground,
        topBar = {
            ModernPlayersTopBar(onNavigateBack)
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                ModernLoadingState(paddingValues)
            }

            uiState.error != null -> {
                ModernErrorState(
                    error = uiState.error!!,
                    paddingValues = paddingValues,
                    onRetry = { viewModel.loadPlayers() }
                )
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Search and filters
                    ModernFilterSection(
                        searchQuery = uiState.searchQuery,
                        positions = uiState.positions,
                        selectedPosition = uiState.selectedPosition,
                        teams = uiState.teams,
                        selectedTeam = uiState.selectedTeam,
                        sortBy = uiState.sortBy,
                        onSearchQueryChange = viewModel::updateSearchQuery,
                        onPositionChange = viewModel::selectPosition,
                        onTeamChange = viewModel::selectTeam,
                        onSortByChange = viewModel::updateSortBy
                    )

                    // Players list with stats header
                    if (uiState.filteredPlayers.isNotEmpty()) {
                        PlayersStatsHeader(uiState.filteredPlayers)
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.filteredPlayers,
                            key = { it.id }
                        ) { player ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + slideInHorizontally()
                            ) {
                                ModernPlayerCard(
                                    player = player,
                                    onClick = { onNavigateToPlayer(player.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernPlayersTopBar(
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(FplAccent, FplAccentDark)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.People,
                        contentDescription = null,
                        tint = FplPrimaryDark,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "All Players",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clip(CircleShape)
                    .background(FplAccent.copy(alpha = 0.1f))
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = FplAccent
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = FplSurface,
            titleContentColor = FplTextPrimary
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernFilterSection(
    searchQuery: String,
    positions: List<ElementType>,
    selectedPosition: Int?,
    teams: List<Team>,
    selectedTeam: Int?,
    sortBy: SortBy,
    onSearchQueryChange: (String) -> Unit,
    onPositionChange: (Int?) -> Unit,
    onTeamChange: (Int?) -> Unit,
    onSortByChange: (SortBy) -> Unit
) {
    var showPositionMenu by remember { mutableStateOf(false) }
    var showTeamMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Search field with modern styling
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Search players...",
                        color = FplTextSecondary
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = FplAccent
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FplAccent,
                    unfocusedBorderColor = FplDivider,
                    cursorColor = FplAccent,
                    focusedTextColor = FplTextPrimary,
                    unfocusedTextColor = FplTextPrimary
                ),
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filter chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Position filter
                item {
                    FilterChip(
                        selected = selectedPosition != null,
                        onClick = { showPositionMenu = true },
                        label = {
                            Text(
                                positions.find { it.id == selectedPosition }?.singularNameShort ?: "Position"
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.SportsSoccer,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FplAccent,
                            selectedLabelColor = FplPrimaryDark,
                            selectedLeadingIconColor = FplPrimaryDark
                        )
                    )

                    DropdownMenu(
                        expanded = showPositionMenu,
                        onDismissRequest = { showPositionMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Positions") },
                            onClick = {
                                onPositionChange(null)
                                showPositionMenu = false
                            }
                        )
                        positions.forEach { position ->
                            DropdownMenuItem(
                                text = { Text(position.singularName) },
                                onClick = {
                                    onPositionChange(position.id)
                                    showPositionMenu = false
                                }
                            )
                        }
                    }
                }

                // Team filter
                item {
                    FilterChip(
                        selected = selectedTeam != null,
                        onClick = { showTeamMenu = true },
                        label = {
                            Text(
                                teams.find { it.id == selectedTeam }?.shortName ?: "Team"
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Shield,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FplSecondary,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )

                    DropdownMenu(
                        expanded = showTeamMenu,
                        onDismissRequest = { showTeamMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Teams") },
                            onClick = {
                                onTeamChange(null)
                                showTeamMenu = false
                            }
                        )
                        teams.forEach { team ->
                            DropdownMenuItem(
                                text = { Text(team.name) },
                                onClick = {
                                    onTeamChange(team.id)
                                    showTeamMenu = false
                                }
                            )
                        }
                    }
                }

                // Sort filter
                item {
                    FilterChip(
                        selected = true,
                        onClick = { showSortMenu = true },
                        label = {
                            Text(
                                sortBy.name.replace("_", " ").lowercase()
                                    .replaceFirstChar { it.uppercase() }
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Sort,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FplPrimary,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        SortBy.values().forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        option.name.replace("_", " ").lowercase()
                                            .replaceFirstChar { it.uppercase() }
                                    )
                                },
                                onClick = {
                                    onSortByChange(option)
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayersStatsHeader(players: List<Element>) {
    val avgPrice = players.map { it.nowCost / 10.0 }.average()
    val avgPoints = players.map { it.totalPoints }.average()
    val totalPlayers = players.size

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Players",
            value = totalPlayers.toString(),
            icon = Icons.Default.People,
            color = FplSecondary
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Avg Price",
            value = "£${String.format("%.1f", avgPrice)}m",
            icon = Icons.Default.AttachMoney,
            color = FplGreen
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Avg Points",
            value = String.format("%.1f", avgPoints),
            icon = Icons.Default.Star,
            color = FplAccent
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = FplTextSecondary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernPlayerCard(
    player: Element,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = FplSurface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Player photo placeholder with position color
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        when (player.elementType) {
                            1 -> Color(0xFFFFD700) // GKP
                            2 -> Color(0xFF00D685) // DEF
                            3 -> Color(0xFF05F1FF) // MID
                            4 -> Color(0xFFE90052) // FWD
                            else -> FplPrimary
                        }.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.webName.take(2).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when (player.elementType) {
                        1 -> Color(0xFFFFD700)
                        2 -> Color(0xFF00D685)
                        3 -> Color(0xFF05F1FF)
                        4 -> Color(0xFFE90052)
                        else -> FplPrimary
                    }
                )
            }

            // Player info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = player.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = FplTextPrimary
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = player.displayPrice,
                        style = MaterialTheme.typography.bodyMedium,
                        color = FplTextSecondary
                    )
                    if (player.costChangeEvent != 0) {
                        val changeColor = if (player.costChangeEvent > 0) FplGreen else FplRed
                        val changeIcon = if (player.costChangeEvent > 0)
                            Icons.Default.TrendingUp else Icons.Default.TrendingDown

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = changeIcon,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = changeColor
                            )
                            Text(
                                text = player.priceChange,
                                style = MaterialTheme.typography.bodySmall,
                                color = changeColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                Text(
                    text = "${player.selectedByPercent}% • ${player.pointsPerGame} pts/game",
                    style = MaterialTheme.typography.bodySmall,
                    color = FplTextSecondary
                )
            }

            // Stats column
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${player.totalPoints}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = FplGreen
                )
                Text(
                    text = "Total pts",
                    style = MaterialTheme.typography.labelSmall,
                    color = FplTextSecondary
                )
                if (player.news.isNotEmpty()) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Has news",
                        modifier = Modifier.size(16.dp),
                        tint = if (player.chanceOfPlayingNextRound ?: 100 < 75) FplRed else FplYellow
                    )
                }
            }
        }
    }
}

@Composable
fun ModernLoadingState(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(FplBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            val infiniteTransition = rememberInfiniteTransition()
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.8f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(FplAccent, FplAccentDark)
                        )
                    )
            ) {
                Icon(
                    Icons.Default.People,
                    contentDescription = null,
                    tint = FplPrimaryDark,
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.Center)
                )
            }
            Text(
                text = "Loading players...",
                style = MaterialTheme.typography.titleMedium,
                color = FplTextSecondary
            )
        }
    }
}

@Composable
fun ModernErrorState(
    error: String,
    paddingValues: PaddingValues,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(FplBackground),
        contentAlignment = Alignment.Center
    ) {
        GlassmorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
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
                    text = error,
                    style = MaterialTheme.typography.bodyLarge,
                    color = FplError,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                ModernButton(
                    onClick = onRetry,
                    text = "Try Again",
                    icon = Icons.Default.Refresh
                )
            }
        }
    }
}