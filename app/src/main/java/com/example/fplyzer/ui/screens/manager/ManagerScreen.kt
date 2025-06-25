package com.example.fplyzer.ui.screens.manager

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fplyzer.data.models.ClassicLeague
import com.example.fplyzer.data.models.GameweekHistory
import com.example.fplyzer.data.models.Manager
import com.example.fplyzer.ui.components.*
import com.example.fplyzer.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

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
        containerColor = FplBackground,
        topBar = {
            ModernManagerTopBar(
                managerName = uiState.manager?.fullName ?: "Manager",
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
                    onRetry = { viewModel.loadManagerData(managerId) }
                )
            }

            uiState.manager != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + scaleIn()
                        ) {
                            ModernManagerProfileCard(
                                manager = uiState.manager!!,
                                onViewTeamClick = { onNavigateToTeamViewer(managerId) }
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AnimatedStatCard(
                                modifier = Modifier.weight(1f),
                                title = "GW Points",
                                value = uiState.manager!!.summaryEventPoints.toString(),
                                icon = Icons.Default.Star,
                                containerColor = FplGreen,
                                delay = 200
                            )
                            AnimatedStatCard(
                                modifier = Modifier.weight(1f),
                                title = "GW Rank",
                                value = formatNumber(uiState.manager!!.summaryEventRank),
                                icon = Icons.Default.TrendingUp,
                                containerColor = FplBlue,
                                delay = 300
                            )
                        }
                    }

                    item {
                        ModernTabSection(
                            selectedTab = uiState.selectedTab,
                            onTabSelected = viewModel::selectTab
                        )
                    }

                    when (uiState.selectedTab) {
                        0 -> {
                            items(
                                items = uiState.manager!!.leagues.classic,
                                key = { it.id }
                            ) { league ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn() + slideInHorizontally()
                                ) {
                                    ModernLeagueCard(
                                        league = league,
                                        onClick = { onNavigateToLeague(league.id) }
                                    )
                                }
                            }
                        }
                        1 -> {
                            uiState.history?.let { history ->
                                item {
                                    ModernSeasonStats(history)
                                }
                                items(
                                    items = history.current.reversed(),
                                    key = { it.event }
                                ) { gameweek ->
                                    AnimatedVisibility(
                                        visible = true,
                                        enter = fadeIn() + slideInVertically()
                                    ) {
                                        ModernGameweekHistoryCard(gameweek)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernManagerTopBar(
    managerName: String,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "MANAGER PROFILE",
                    style = MaterialTheme.typography.labelMedium,
                    color = FplTextSecondary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = managerName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clip(CircleShape)
                    .background(FplPrimary.copy(alpha = 0.1f))
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = FplPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = FplSurface,
            titleContentColor = FplTextPrimary,
            navigationIconContentColor = FplPrimary
        )
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ModernManagerProfileCard(
    manager: Manager,
    onViewTeamClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isExpanded) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    GradientCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .scale(scale),
        gradientColors = listOf(
            FplGradientStart,
            FplGradientMiddle,
            FplGradientEnd
        ),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = manager.fullName,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = manager.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = FplAccentLight
                    )
                }
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(FplGlass.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = manager.playerFirstName.first().toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = FplAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedContent(
                        targetState = manager.summaryOverallPoints,
                        transitionSpec = {
                            slideInVertically { -it } + fadeIn() with
                                    slideOutVertically { it } + fadeOut()
                        }
                    ) { points ->
                        Text(
                            text = points.toString(),
                            style = MaterialTheme.typography.displaySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Text(
                        text = "TOTAL POINTS",
                        style = MaterialTheme.typography.labelLarge,
                        color = FplAccentLight,
                        letterSpacing = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(60.dp)
                        .background(FplGlass.copy(alpha = 0.3f))
                )

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedContent(
                        targetState = formatNumber(manager.summaryOverallRank),
                        transitionSpec = {
                            slideInVertically { -it } + fadeIn() with
                                    slideOutVertically { it } + fadeOut()
                        }
                    ) { rank ->
                        Text(
                            text = rank,
                            style = MaterialTheme.typography.displaySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Text(
                        text = "OVERALL RANK",
                        style = MaterialTheme.typography.labelLarge,
                        color = FplAccentLight,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ModernButton(
                onClick = {
                    isExpanded = !isExpanded
                    onViewTeamClick()
                },
                modifier = Modifier.fillMaxWidth(),
                text = "View Current Team",
                icon = Icons.Default.Groups,
                gradient = listOf(FplAccent, FplAccentDark)
            )
        }
    }
}

@Composable
fun ModernTabSection(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf(
        "Leagues" to Icons.Default.EmojiEvents,
        "History" to Icons.Default.Timeline
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEachIndexed { index, (title, icon) ->
            val isSelected = selectedTab == index
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) FplPrimary else FplSurface,
                animationSpec = tween(300)
            )
            val contentColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else FplTextSecondary,
                animationSpec = tween(300)
            )

            Surface(
                onClick = { onTabSelected(index) },
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.medium,
                color = backgroundColor
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = contentColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernLeagueCard(
    league: ClassicLeague,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = FplSurface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(FplSecondaryLight, FplSecondary)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = league.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FplTextPrimary
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    league.rank?.let { rank ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.TrendingUp,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = FplAccent
                            )
                            Text(
                                text = "Rank $rank",
                                style = MaterialTheme.typography.bodyMedium,
                                color = FplTextSecondary
                            )
                        }
                    }
                    league.maxEntries?.let { size ->
                        Text(
                            text = "• $size players",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FplTextSecondary
                        )
                    }
                }
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "View League",
                tint = FplPrimary
            )
        }
    }
}

@Composable
fun ModernSeasonStats(history: com.example.fplyzer.data.models.ManagerHistory) {
    val totalTransfers = history.current.sumOf { it.eventTransfers }
    val totalHits = history.current.sumOf { it.eventTransfersCost }
    val avgPoints = history.current.map { it.points }.average()

    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Season Statistics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FplTextPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%.1f", avgPoints),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = FplGreen
                    )
                    Text(
                        text = "Avg Points",
                        style = MaterialTheme.typography.bodySmall,
                        color = FplTextSecondary
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = totalTransfers.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = FplBlue
                    )
                    Text(
                        text = "Transfers",
                        style = MaterialTheme.typography.bodySmall,
                        color = FplTextSecondary
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "-$totalHits",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = FplRed
                    )
                    Text(
                        text = "Hits Taken",
                        style = MaterialTheme.typography.bodySmall,
                        color = FplTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun ModernGameweekHistoryCard(gameweek: GameweekHistory) {
    val rankChange = when {
        gameweek.event == 1 -> 0
        else -> 0 // Would need previous GW data
    }

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
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(FplPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "GW",
                            style = MaterialTheme.typography.labelLarge,
                            color = FplPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = gameweek.event.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${gameweek.points} pts",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = FplGreen
                    )
                    if (gameweek.eventTransfersCost > 0) {
                        Text(
                            text = "-${gameweek.eventTransfersCost} hit",
                            style = MaterialTheme.typography.bodySmall,
                            color = FplRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Overall Rank",
                        style = MaterialTheme.typography.bodySmall,
                        color = FplTextSecondary
                    )
                    Text(
                        text = formatNumber(gameweek.overallRank),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Transfers",
                        style = MaterialTheme.typography.bodySmall,
                        color = FplTextSecondary
                    )
                    Text(
                        text = gameweek.eventTransfers.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    .size(64.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(FplAccent, FplSecondary)
                        )
                    )
            )
            Text(
                text = "Loading Manager Data...",
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

fun formatNumber(number: Int): String {
    return NumberFormat.getNumberInstance(Locale.US).format(number)
}