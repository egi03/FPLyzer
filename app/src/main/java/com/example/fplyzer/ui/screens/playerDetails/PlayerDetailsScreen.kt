package com.example.fplyzer.ui.screens.playerDetails

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fplyzer.data.models.Element
import com.example.fplyzer.data.models.PlayerFixture
import com.example.fplyzer.data.models.PlayerHistory
import com.example.fplyzer.data.models.PlayerPastHistory
import com.example.fplyzer.data.models.Team
import com.example.fplyzer.ui.components.*
import com.example.fplyzer.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
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
        containerColor = FplBackground,
        topBar = {
            ModernPlayerDetailsTopBar(
                playerName = uiState.player?.displayName ?: "Player Details",
                onNavigateBack = onNavigateBack
            )
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
                    onRetry = { viewModel.loadPlayerDetails(playerId) }
                )
            }

            uiState.player != null && uiState.playerDetails != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Player profile card
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + scaleIn()
                        ) {
                            ModernPlayerProfileCard(
                                player = uiState.player!!,
                                team = uiState.team?.name ?: "",
                                position = uiState.position?.singularName ?: ""
                            )
                        }
                    }

                    // Quick stats
                    item {
                        PlayerQuickStats(
                            player = uiState.player!!,
                            history = uiState.playerDetails!!.history
                        )
                    }

                    // Tabs
                    item {
                        var selectedTab by remember { mutableStateOf(0) }
                        val tabs = listOf("Fixtures", "History", "Past Seasons", "Stats")

                        ScrollableTabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = Color.Transparent,
                            contentColor = FplTextPrimary,
                            edgePadding = 16.dp,
                            indicator = { tabPositions ->
                                TabRowDefaults.Indicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = FplAccent
                                )
                            }
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    text = {
                                        Text(
                                            title,
                                            fontWeight = if (selectedTab == index)
                                                FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        AnimatedContent(
                            targetState = selectedTab,
                            transitionSpec = {
                                fadeIn() + slideInHorizontally() with
                                        fadeOut() + slideOutHorizontally()
                            }
                        ) { tab ->
                            when (tab) {
                                0 -> FixturesContent(
                                    fixtures = uiState.playerDetails!!.fixtures,
                                    teams = uiState.teams
                                )
                                1 -> HistoryContent(
                                    history = uiState.playerDetails!!.history.reversed()
                                )
                                2 -> PastSeasonsContent(
                                    pastSeasons = uiState.playerDetails!!.historyPast
                                )
                                3 -> StatsContent(
                                    player = uiState.player!!,
                                    history = uiState.playerDetails!!.history
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
fun ModernPlayerDetailsTopBar(
    playerName: String,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "PLAYER PROFILE",
                    style = MaterialTheme.typography.labelMedium,
                    color = FplTextSecondary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = playerName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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

@Composable
fun ModernPlayerProfileCard(
    player: Element,
    team: String,
    position: String
) {
    GradientCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        gradientColors = listOf(
            when (player.elementType) {
                1 -> Color(0xFFFFD700) // GKP
                2 -> Color(0xFF00D685) // DEF
                3 -> Color(0xFF05F1FF) // MID
                4 -> Color(0xFFE90052) // FWD
                else -> FplPrimary
            },
            FplPrimaryDark
        ),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Box {
            // Background pattern
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            radius = 600f
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = player.displayName,
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            if (player.inDreamteam) {
                                Icon(
                                    Icons.Default.Stars,
                                    contentDescription = "In Dream Team",
                                    tint = FplAccent,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Text(
                            text = "$position • $team",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    // Player image placeholder
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = player.webName.take(2).uppercase(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    PlayerStatItem(
                        label = "Price",
                        value = player.displayPrice,
                        change = if (player.costChangeEvent != 0) player.priceChange else null
                    )
                    PlayerStatItem(
                        label = "Selected",
                        value = "${player.selectedByPercent}%",
                        icon = Icons.Default.People
                    )
                    PlayerStatItem(
                        label = "Form",
                        value = player.form,
                        icon = Icons.Default.TrendingUp
                    )
                    PlayerStatItem(
                        label = "Total",
                        value = "${player.totalPoints} pts",
                        icon = Icons.Default.Star
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerStatItem(
    label: String,
    value: String,
    change: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = FplAccent,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        if (change != null) {
            val changeColor = if (change.startsWith("+")) FplGreen else FplRed
            Text(
                text = change,
                style = MaterialTheme.typography.bodySmall,
                color = changeColor,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun PlayerQuickStats(
    player: Element,
    history: List<PlayerHistory>
) {
    val last5Games = history.take(5)
    val avgLast5 = if (last5Games.isNotEmpty())
        last5Games.map { it.totalPoints }.average() else 0.0

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            AnimatedStatCard(
                title = "Last 5 Avg",
                value = String.format("%.1f", avgLast5),
                icon = Icons.Default.Timeline,
                modifier = Modifier.width(140.dp),
                containerColor = FplAccent,
                delay = 0
            )
        }
        item {
            AnimatedStatCard(
                title = "Minutes",
                value = player.minutes.toString(),
                icon = Icons.Default.Timer,
                modifier = Modifier.width(140.dp),
                containerColor = FplBlue,
                delay = 100
            )
        }
        item {
            AnimatedStatCard(
                title = "Goals",
                value = player.goalsScored.toString(),
                icon = Icons.Default.SportsSoccer,
                modifier = Modifier.width(140.dp),
                containerColor = FplGreen,
                delay = 200
            )
        }
        item {
            AnimatedStatCard(
                title = "Assists",
                value = player.assists.toString(),
                icon = Icons.Default.Group,
                modifier = Modifier.width(140.dp),
                containerColor = FplOrange,
                delay = 300
            )
        }
    }
}

@Composable
fun FixturesContent(
    fixtures: List<PlayerFixture>,
    teams: List<Team>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        fixtures.forEach { fixture ->
            ModernFixtureCard(fixture, teams)
        }
    }
}

@Composable
fun ModernFixtureCard(
    fixture: PlayerFixture,
    teams: List<Team>
) {
    val difficultyColor = when (fixture.difficulty) {
        in 1..2 -> FplGreen
        3 -> FplYellow
        else -> FplRed
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = FplSurface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gameweek badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(FplPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "GW",
                        style = MaterialTheme.typography.labelSmall,
                        color = FplPrimary
                    )
                    Text(
                        text = fixture.event.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FplPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Match info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = teams.find {
                            it.id == (if (fixture.isHome) fixture.teamA else fixture.teamH)
                        }?.name ?: "-",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (fixture.isHome) "(A)" else "(H)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FplTextSecondary
                    )
                }
                Text(
                    text = formatDate(fixture.kickoffTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = FplTextSecondary
                )
            }

            // Difficulty
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(difficultyColor.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "FDR ${fixture.difficulty}",
                    style = MaterialTheme.typography.labelMedium,
                    color = difficultyColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun HistoryContent(
    history: List<PlayerHistory>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        history.forEach { gameweek ->
            ModernHistoryCard(gameweek)
        }
    }
}

@Composable
fun ModernHistoryCard(
    history: PlayerHistory
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = when {
                history.totalPoints >= 10 -> FplGreen.copy(alpha = 0.1f)
                history.totalPoints >= 6 -> FplSurface
                else -> FplRed.copy(alpha = 0.05f)
            }
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "GW${history.round}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (history.wasHome) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(FplPrimary.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "H",
                                style = MaterialTheme.typography.labelSmall,
                                color = FplPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${history.totalPoints} pts",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            history.totalPoints >= 10 -> FplGreen
                            history.totalPoints < 2 -> FplRed
                            else -> FplTextPrimary
                        }
                    )
                    Text(
                        text = "${history.minutes}'",
                        style = MaterialTheme.typography.bodySmall,
                        color = FplTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stats pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (history.goalsScored > 0) {
                    StatPill(
                        icon = Icons.Default.SportsSoccer,
                        value = history.goalsScored.toString(),
                        color = FplGreen
                    )
                }
                if (history.assists > 0) {
                    StatPill(
                        icon = Icons.Default.Group,
                        value = history.assists.toString(),
                        color = FplBlue
                    )
                }
                if (history.cleanSheets > 0) {
                    StatPill(
                        icon = Icons.Default.Shield,
                        value = "CS",
                        color = FplPrimary
                    )
                }
                if (history.bonus > 0) {
                    StatPill(
                        icon = Icons.Default.Star,
                        value = history.bonus.toString(),
                        color = FplYellow
                    )
                }
                if (history.yellowCards > 0 || history.redCards > 0) {
                    StatPill(
                        icon = Icons.Default.Square,
                        value = "${history.yellowCards + history.redCards}",
                        color = if (history.redCards > 0) FplRed else FplYellow
                    )
                }
            }
        }
    }
}

@Composable
private fun StatPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PastSeasonsContent(
    pastSeasons: List<PlayerPastHistory>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        pastSeasons.forEach { season ->
            ModernPastSeasonCard(season)
        }
    }
}

@Composable
fun ModernPastSeasonCard(
    season: PlayerPastHistory
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = FplSurface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = season.seasonName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${season.totalPoints} pts",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FplGreen
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SeasonStat("Goals", season.goalsScored.toString())
                SeasonStat("Assists", season.assists.toString())
                SeasonStat("Minutes", season.minutes.toString())
                SeasonStat("Bonus", season.bonus.toString())
            }
        }
    }
}

@Composable
private fun SeasonStat(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = FplTextPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = FplTextSecondary
        )
    }
}

@Composable
fun StatsContent(
    player: Element,
    history: List<PlayerHistory>
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Advanced stats card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = FplSurface),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Advanced Statistics",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FplTextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                val stats = listOf(
                    "xG" to player.expectedGoals,
                    "xA" to player.expectedAssists,
                    "xGI" to player.expectedGoalInvolvements,
                    "ICT Index" to player.ictIndex,
                    "Influence" to player.influence,
                    "Creativity" to player.creativity,
                    "Threat" to player.threat
                )

                stats.forEach { (label, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = FplTextSecondary
                        )
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = FplTextPrimary
                        )
                    }
                }
            }
        }

        // Per 90 stats
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = FplSurface),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Per 90 Minutes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FplTextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                val per90Stats = listOf(
                    "xG/90" to String.format("%.2f", player.expectedGoalsPer90),
                    "xA/90" to String.format("%.2f", player.expectedAssistsPer90),
                    "xGI/90" to String.format("%.2f", player.expectedGoalInvolvementsPer90)
                )

                per90Stats.forEach { (label, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = FplTextSecondary
                        )
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = FplTextPrimary
                        )
                    }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = FplAccent,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "Loading player data...",
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

private fun formatDate(date: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US)
        outputFormat.timeZone = TimeZone.getDefault()
        val parsedDate = inputFormat.parse(date) ?: Date()
        outputFormat.format(parsedDate)
    } catch (e: Exception) {
        date
    }
}