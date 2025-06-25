package com.example.fplyzer.ui.screens.league

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.fplyzer.data.models.League
import com.example.fplyzer.data.models.StandingResult
import com.example.fplyzer.ui.components.*
import com.example.fplyzer.ui.theme.*

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
        containerColor = FplBackground,
        topBar = {
            ModernLeagueTopBar(
                leagueName = uiState.standings?.league?.name ?: "League",
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
                    onRetry = { viewModel.loadLeagueStandings(leagueId) }
                )
            }

            uiState.standings != null -> {
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
                            ModernLeagueInfoCard(uiState.standings!!.league)
                        }
                    }

                    item {
                        StandingsHeader()
                    }

                    itemsIndexed(
                        items = uiState.standings!!.standings.results,
                        key = { _, standing -> standing.id }
                    ) { index, standing ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(
                                animationSpec = tween(300, index * 30)
                            ) + slideInHorizontally(
                                animationSpec = tween(300, index * 30)
                            )
                        ) {
                            ModernStandingRow(
                                standing = standing,
                                position = index + 1
                            )
                        }
                    }

                    if (uiState.standings!!.standings.hasNext) {
                        item {
                            LoadMoreButton(
                                onClick = {
                                    viewModel.loadLeagueStandings(
                                        leagueId,
                                        uiState.currentPage + 1
                                    )
                                }
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
fun ModernLeagueTopBar(
    leagueName: String,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "LEAGUE STANDINGS",
                    style = MaterialTheme.typography.labelMedium,
                    color = FplTextSecondary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = leagueName,
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

@Composable
fun ModernLeagueInfoCard(league: League) {
    GradientCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        gradientColors = listOf(FplPrimary, FplPrimaryDark),
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = league.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        InfoChip(
                            icon = Icons.Default.Category,
                            label = league.leagueType.uppercase(),
                            color = FplAccent
                        )
                        league.maxEntries?.let {
                            InfoChip(
                                icon = Icons.Default.Groups,
                                label = "$it players",
                                color = FplAccentLight
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(FplAccent, FplAccentDark)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = FplPrimaryDark,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            if (league.adminEntry != null || league.startEvent > 1) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (league.startEvent > 1) {
                        Text(
                            text = "Started GW${league.startEvent}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FplAccentLight
                        )
                    }
                    if (league.hasCup) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = FplAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Cup Active",
                                style = MaterialTheme.typography.bodyMedium,
                                color = FplAccentLight
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun StandingsHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = FplPrimary.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pos",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(50.dp),
                color = FplPrimary
            )
            Text(
                text = "Team",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                color = FplPrimary
            )
            Text(
                text = "GW",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.Center,
                color = FplPrimary
            )
            Text(
                text = "Total",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(60.dp),
                textAlign = TextAlign.End,
                color = FplPrimary
            )
        }
    }
}

@Composable
fun ModernStandingRow(
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

    var isExpanded by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isExpanded) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        onClick = { isExpanded = !isExpanded },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = when (position) {
                1 -> FplYellow.copy(alpha = 0.1f)
                2 -> Color(0xFFC0C0C0).copy(alpha = 0.1f)
                3 -> Color(0xFFCD7F32).copy(alpha = 0.1f)
                else -> FplSurface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (position <= 3) 8.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Position with medal for top 3
            Box(
                modifier = Modifier.width(50.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (position <= 3) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = when (position) {
                                        1 -> listOf(FplYellow, FplOrange)
                                        2 -> listOf(Color(0xFFC0C0C0), Color(0xFF9E9E9E))
                                        3 -> listOf(Color(0xFFCD7F32), Color(0xFFA0522D))
                                        else -> listOf(FplPrimary, FplPrimary)
                                    }
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = position.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                } else {
                    Text(
                        text = position.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FplTextPrimary
                    )
                }

                if (rankChange != 0) {
                    Icon(
                        imageVector = rankIcon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(12.dp)
                            .align(Alignment.CenterEnd),
                        tint = rankColor
                    )
                }
            }

            // Team info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = standing.entryName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = FplTextPrimary
                )
                Text(
                    text = standing.playerName,
                    style = MaterialTheme.typography.bodySmall,
                    color = FplTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // GW Points
            Text(
                text = standing.eventTotal.toString(),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.Center,
                color = FplTextSecondary
            )

            // Total Points
            Text(
                text = standing.total.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(60.dp),
                textAlign = TextAlign.End,
                color = FplGreen
            )
        }
    }
}

@Composable
fun LoadMoreButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        ModernButton(
            onClick = onClick,
            text = "Load More",
            icon = Icons.Default.ExpandMore,
            gradient = listOf(FplPrimary, FplPrimaryDark)
        )
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
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .graphicsLayer { rotationZ = rotation }
                    .clip(CircleShape)
                    .background(
                        brush = Brush.sweepGradient(
                            colors = listOf(FplPrimary, FplSecondary, FplPrimary)
                        )
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(FplBackground)
                )
            }
            Text(
                text = "Loading standings...",
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
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(FplError.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = "Error",
                        modifier = Modifier.size(40.dp),
                        tint = FplError
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Unable to load league",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FplTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = FplError,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                ModernButton(
                    onClick = onRetry,
                    text = "Try Again",
                    icon = Icons.Default.Refresh,
                    gradient = listOf(FplError, FplPink)
                )
            }
        }
    }
}