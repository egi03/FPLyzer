package com.example.fplyzer.ui.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fplyzer.data.models.Element
import com.example.fplyzer.data.models.Event
import com.example.fplyzer.ui.theme.FplAccent
import com.example.fplyzer.ui.theme.FplDivider
import com.example.fplyzer.ui.theme.FplError
import com.example.fplyzer.ui.theme.FplGreen
import com.example.fplyzer.ui.theme.FplPrimary
import com.example.fplyzer.ui.theme.FplPrimaryDark
import com.example.fplyzer.ui.theme.FplSurface
import com.example.fplyzer.ui.theme.FplTextPrimary
import com.example.fplyzer.ui.theme.FplTextSecondary
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToManager: (Int) -> Unit,
    onNavigateToPlayers: () -> Unit,
    onNavigateToPlayer: (Int) -> Unit,
    onNavigateToTeamViewer: (Int) -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FPL Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FplPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onNavigateToPlayers) {
                        Icon(Icons.Default.People, "Players", tint = Color.White)
                    }
                    IconButton(onClick = { /* TODO: Implement search */ }) {
                        Icon(Icons.Default.Search, "Search", tint = Color.White)
                    }
                }
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
                            onClick = { viewModel.loadDashboardData() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FplPrimary
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            uiState.bootstrapData != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Gameweek Info
                    uiState.currentEvent?.let { event ->
                        item {
                            GameweekInfoCard(event)
                        }
                    }

                    // Manager ID Input
                    item {
                        ManagerIdInputCard(onNavigateToManager = onNavigateToManager)
                    }

                    // Top Players
                    if (uiState.topPlayers.isNotEmpty()) {
                        item {
                            SectionHeader(title = "Top Players")
                        }
                        items(uiState.topPlayers) { player ->
                            PlayerCard(
                                player = player,
                                onClick = { onNavigateToPlayer(player.id) }
                            )
                        }
                    }

                    // Most Transferred In
                    if (uiState.mostTransferredIn.isNotEmpty()) {
                        item {
                            SectionHeader(title = "Most Transferred In")
                        }
                        items(uiState.mostTransferredIn) { player ->
                            PlayerCard(
                                player = player,
                                onClick = { onNavigateToPlayer(player.id) }
                            )
                        }
                    }

                    // Dream Team
                    if (uiState.dreamTeam.isNotEmpty()) {
                        item {
                            SectionHeader(title = "Dream Team")
                        }
                        items(uiState.dreamTeam) { player ->
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

@Composable
fun GameweekInfoCard(event: Event) {
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
                    text = "Gameweek ${event.id}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Deadline: ${formatDeadline(event.deadlineTime)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = FplAccent
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Average Score",
                            style = MaterialTheme.typography.bodySmall,
                            color = FplTextSecondary
                        )
                        Text(
                            text = "${event.averageEntryScore} pts",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Highest Score",
                            style = MaterialTheme.typography.bodySmall,
                            color = FplTextSecondary
                        )
                        Text(
                            text = "${event.highestScore ?: "-"} pts",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ManagerIdInputCard(onNavigateToManager: (Int) -> Unit) {
    var managerIdInput by remember { mutableStateOf("") }
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = FplSurface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = managerIdInput,
                onValueChange = { managerIdInput = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Enter FPL Manager ID") },
                placeholder = { Text("e.g., 123456") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (managerIdInput.isNotEmpty()) {
                            keyboardController?.hide()
                            onNavigateToManager(managerIdInput.toInt())
                        }
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FplAccent,
                    unfocusedBorderColor = FplDivider,
                    focusedLabelColor = FplPrimary,
                    unfocusedLabelColor = FplTextSecondary,
                    cursorColor = FplAccent,
                    focusedTextColor = FplTextPrimary,
                    unfocusedTextColor = FplTextPrimary
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = FplAccent
                    )
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    if (managerIdInput.isNotEmpty()) {
                        keyboardController?.hide()
                        onNavigateToManager(managerIdInput.toInt())
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = managerIdInput.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FplAccent,
                    contentColor = FplPrimary
                )
            ) {
                Text(
                    text = "View Manager",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
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
                    text = "${player.eventPoints} pts",
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

private fun formatDeadline(deadline: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US)
        outputFormat.timeZone = TimeZone.getDefault()
        val date = inputFormat.parse(deadline) ?: 0
        outputFormat.format(date)
    } catch (e: Exception) {
        deadline
    }
}