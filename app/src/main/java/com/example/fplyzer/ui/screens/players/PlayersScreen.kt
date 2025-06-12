package com.example.fplyzer.ui.screens.players

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fplyzer.data.models.Element
import com.example.fplyzer.data.models.ElementType
import com.example.fplyzer.data.models.Team
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
        topBar = {
            TopAppBar(
                title = { Text("Players") },
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
                            onClick = { viewModel.loadPlayers() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FplPrimary
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Filter Section
                    FilterSection(
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

                    // Players List
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.filteredPlayers) { player ->
                            PlayerCard(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSection(
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(FplBackground)
            .padding(16.dp)
    ) {
        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search Players") },
            placeholder = { Text("Enter player name") },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = FplAccent)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FplAccent,
                unfocusedBorderColor = FplDivider,
                focusedLabelColor = FplPrimary,
                unfocusedLabelColor = FplTextSecondary,
                cursorColor = FplAccent,
                focusedTextColor = FplTextPrimary,
                unfocusedTextColor = FplTextPrimary
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Position Filter
        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = { /* Controlled by click */ }
        ) {
            OutlinedTextField(
                value = positions.find { it.id == selectedPosition }?.singularName ?: "All Positions",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = { Text("Position") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FplAccent,
                    unfocusedBorderColor = FplDivider,
                    focusedLabelColor = FplPrimary,
                    unfocusedLabelColor = FplTextSecondary
                )
            )
            ExposedDropdownMenu(
                expanded = false,
                onDismissRequest = {}
            ) {
                DropdownMenuItem(
                    text = { Text("All Positions") },
                    onClick = { onPositionChange(null) }
                )
                positions.forEach { position ->
                    DropdownMenuItem(
                        text = { Text(position.singularName) },
                        onClick = { onPositionChange(position.id) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Team Filter
        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = { /* Controlled by click */ }
        ) {
            OutlinedTextField(
                value = teams.find { it.id == selectedTeam }?.name ?: "All Teams",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = { Text("Team") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FplAccent,
                    unfocusedBorderColor = FplDivider,
                    focusedLabelColor = FplPrimary,
                    unfocusedLabelColor = FplTextSecondary
                )
            )
            ExposedDropdownMenu(
                expanded = false,
                onDismissRequest = {}
            ) {
                DropdownMenuItem(
                    text = { Text("All Teams") },
                    onClick = { onTeamChange(null) }
                )
                teams.forEach { team ->
                    DropdownMenuItem(
                        text = { Text(team.name) },
                        onClick = { onTeamChange(team.id) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Sort By
        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = { /* Controlled by click */ }
        ) {
            OutlinedTextField(
                value = sortBy.name.replace("_", " ").lowercase().capitalize(Locale.US),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = { Text("Sort By") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FplAccent,
                    unfocusedBorderColor = FplDivider,
                    focusedLabelColor = FplPrimary,
                    unfocusedLabelColor = FplTextSecondary
                )
            )
            ExposedDropdownMenu(
                expanded = false,
                onDismissRequest = {}
            ) {
                SortBy.values().forEach { sortOption ->
                    DropdownMenuItem(
                        text = { Text(sortOption.name.replace("_", " ").lowercase().capitalize(Locale.US)) },
                        onClick = { onSortByChange(sortOption) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerCard(
    player: Element,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = player.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${player.pointsPerGame} pts/game | ${player.displayPrice}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FplTextSecondary
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${player.totalPoints} pts",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = FplGreen
                )
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "View Player",
                    tint = FplTextSecondary
                )
            }
        }
    }
}