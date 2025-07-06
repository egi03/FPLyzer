package com.example.fplyzer.ui.screens.leagueStats

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fplyzer.ui.components.*
import com.example.fplyzer.ui.screens.leagueStats.tabs.*
import com.example.fplyzer.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueStatsScreen(
    leagueId: Int,
    onNavigateBack: () -> Unit,
    viewModel: LeagueStatsViewModel = viewModel()
) {
    val uiState by viewModel.uiState

    LaunchedEffect(leagueId) {
        viewModel.loadLeagueStatistics(leagueId)
    }

    Scaffold(
        containerColor = FplBackground,
        topBar = {
            EnhancedTopBar(
                leagueId = leagueId,
                leagueName = uiState.leagueStatistics?.leagueInfo?.name,
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                EnhancedLoadingScreen(paddingValues)
            }

            uiState.error != null -> {
                EnhancedErrorScreen(
                    error = uiState.error!!,
                    paddingValues = paddingValues,
                    onRetry = { viewModel.loadLeagueStatistics(leagueId) },
                    onNavigateBack = onNavigateBack
                )
            }

            uiState.leagueStatistics != null -> {
                EnhancedLeagueStatsContent(
                    uiState = uiState,
                    viewModel = viewModel,
                    paddingValues = paddingValues
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnhancedTopBar(
    leagueId: Int,
    leagueName: String?,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(FplSecondary, FplSecondaryDark)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Analytics,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = leagueName ?: "League Analytics",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = FplTextPrimary
                    )
                    Text(
                        text = "ID: $leagueId",
                        style = MaterialTheme.typography.bodySmall,
                        color = FplTextSecondary
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(FplSecondary.copy(alpha = 0.1f))
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = FplSecondary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = FplSurface
        ),
        modifier = Modifier.shadow(
            elevation = 8.dp,
            spotColor = Color.Black.copy(alpha = 0.1f)
        )
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun EnhancedLeagueStatsContent(
    uiState: LeagueStatsUiState,
    viewModel: LeagueStatsViewModel,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Enhanced Tab Bar matching iOS
        EnhancedTabBar(
            selectedTab = uiState.selectedTab,
            onTabSelected = viewModel::setSelectedTab
        )

        // Tab content with animations
        AnimatedContent(
            targetState = uiState.selectedTab,
            transitionSpec = {
                (fadeIn(animationSpec = tween(300)) +
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )) with
                        (fadeOut(animationSpec = tween(200)) +
                                slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> -fullWidth },
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioNoBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                ))
            }
        ) { tab ->
            when (tab) {
                0 -> RankingsTab(uiState, viewModel)
                1 -> HeadToHeadTab(uiState, viewModel)
                2 -> ChipsTab(uiState, viewModel)
                3 -> TrendsTab(uiState, viewModel)
                4 -> PlayersTab(uiState, viewModel)
                5 -> com.example.fplyzer.ui.screens.leagueStats.tabs.DifferentialsTab(uiState, viewModel)
                6 -> com.example.fplyzer.ui.screens.leagueStats.tabs.WhatIfTab(uiState, viewModel)
            }
        }
    }
}

@Composable
private fun EnhancedTabBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf(
        TabItem("Rankings", Icons.Default.Leaderboard),
        TabItem("H2H", Icons.Default.CompareArrows),
        TabItem("Chips", Icons.Default.Stars),
        TabItem("Trends", Icons.Default.TrendingUp),
        TabItem("Players", Icons.Default.Group),
        TabItem("Differentials", Icons.Default.Psychology),
        TabItem("What If", Icons.Default.QuestionMark)
    )

    ScrollableTabRow(
        selectedTabIndex = selectedTab,
        containerColor = FplSurface,
        contentColor = FplTextPrimary,
        edgePadding = 0.dp,
        indicator = { tabPositions ->
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedTab])
                    .height(3.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(FplSecondary, FplSecondaryDark)
                        ),
                        shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                    )
            )
        }
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                modifier = Modifier.height(56.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = null,
                        tint = if (selectedTab == index) FplSecondary else FplTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == index) FplSecondary else FplTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun EnhancedLoadingScreen(paddingValues: PaddingValues) {
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

            // Pulsing animation
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.9f,
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize(),
                    color = FplSecondary,
                    strokeWidth = 8.dp
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(FplSecondary, FplSecondaryDark)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Analytics,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Analyzing League Data",
                    style = MaterialTheme.typography.titleLarge,
                    color = FplTextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "This may take a moment...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FplTextSecondary
                )
            }
        }
    }
}

@Composable
private fun EnhancedErrorScreen(
    error: String,
    paddingValues: PaddingValues,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(FplBackground),
        contentAlignment = Alignment.Center
    ) {
        GradientCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            gradientColors = listOf(FplError.copy(alpha = 0.1f), Color.White)
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
                        modifier = Modifier.size(48.dp),
                        tint = FplError
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Unable to load league",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = FplTextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = FplError,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = FplTextPrimary
                        )
                    ) {
                        Text("Go Back")
                    }

                    Button(
                        onClick = onRetry,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FplError
                        )
                    ) {
                        Text("Try Again")
                    }
                }
            }
        }
    }
}

// Helper data class
private data class TabItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)