package com.example.fplyzer.ui.screens.leagueStats

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fplyzer.ui.components.*
import com.example.fplyzer.ui.screens.leagueStats.tabs.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LeagueStatsScreen(
    leagueId: Int,
    isDemo: Boolean = false,
    onNavigateBack: () -> Unit,
    viewModel: LeagueStatsViewModel = viewModel()
) {
    val uiState by viewModel.uiState

    LaunchedEffect(leagueId, isDemo) {
        if (isDemo) {
            viewModel.loadDemoData()
        } else {
            viewModel.loadLeagueStatistics(leagueId)
        }
    }

    val toggleFavourite: () -> Unit = if (!isDemo) {
        viewModel::toggleFavourite
    } else {
        { /* no-op for demo */ }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            EnhancedTopBar(
                leagueId = leagueId,
                leagueName = uiState.leagueStatistics?.leagueInfo?.name,
                isFavourite = uiState.isFavourite,
                isDemo = isDemo,
                onNavigateBack = onNavigateBack,
                onToggleFavourite = toggleFavourite
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                EnhancedLoadingScreen(paddingValues, isDemo)
            }

            uiState.error != null && !isDemo -> {
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
                    paddingValues = paddingValues,
                    isDemo = isDemo
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
    isFavourite: Boolean,
    isDemo: Boolean,
    onNavigateBack: () -> Unit,
    onToggleFavourite: () -> Unit
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
                        .clip(MaterialTheme.shapes.small)
                        .background(
                            brush = Brush.linearGradient(
                                colors = if (isDemo) {
                                    listOf(
                                        MaterialTheme.colorScheme.secondary,
                                        MaterialTheme.colorScheme.secondaryContainer
                                    )
                                } else {
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primaryContainer
                                    )
                                }
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isDemo) Icons.Default.Preview else Icons.Default.Analytics,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = leagueName ?: if (isDemo) "Demo League" else "League Analytics",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (isDemo) {
                            Surface(
                                shape = MaterialTheme.shapes.extraSmall,
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "DEMO",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = if (isDemo) "Sample Data" else "ID: $leagueId",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        },
        actions = {
            if (!isDemo) {
                IconButton(
                    onClick = onToggleFavourite,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (isFavourite) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                        )
                ) {
                    Icon(
                        if (isFavourite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = if (isFavourite) "Remove from favourites" else "Add to favourites",
                        tint = if (isFavourite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.shadow(
            elevation = 8.dp,
            spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EnhancedLeagueStatsContent(
    uiState: LeagueStatsUiState,
    viewModel: LeagueStatsViewModel,
    paddingValues: PaddingValues,
    isDemo: Boolean
) {
    val pagerState = rememberPagerState(
        initialPage = uiState.selectedTab,
        pageCount = { 7 } // Total number of tabs
    )

    // Only sync pager state with selected tab when tab is clicked
    LaunchedEffect(uiState.selectedTab) {
        if (pagerState.currentPage != uiState.selectedTab) {
            pagerState.animateScrollToPage(uiState.selectedTab)
        }
    }

    // Only sync selected tab with pager state when user swipes
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        // Only update if user is swiping and page actually changed
        if (!pagerState.isScrollInProgress && pagerState.currentPage != uiState.selectedTab) {
            viewModel.setSelectedTab(pagerState.currentPage)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Enhanced Tab Bar - now connected to pager
        EnhancedTabBar(
            selectedTab = uiState.selectedTab, // Use UI state, not pager state
            onTabSelected = viewModel::setSelectedTab,
            isDemo = isDemo
        )

        // Swipeable tab content using HorizontalPager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
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
    onTabSelected: (Int) -> Unit,
    isDemo: Boolean
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
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        edgePadding = 0.dp,
        indicator = { tabPositions ->
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedTab])
                    .height(3.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = if (isDemo) {
                                listOf(
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.colorScheme.secondaryContainer
                                )
                            } else {
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primaryContainer
                                )
                            }
                        ),
                        shape = MaterialTheme.shapes.extraSmall
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
                        tint = if (selectedTab == index) {
                            if (isDemo) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == index) {
                            if (isDemo) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EnhancedLoadingScreen(paddingValues: PaddingValues, isDemo: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.background),
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
                    color = if (isDemo) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                    strokeWidth = 8.dp
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = if (isDemo) {
                                    listOf(
                                        MaterialTheme.colorScheme.secondary,
                                        MaterialTheme.colorScheme.secondaryContainer
                                    )
                                } else {
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primaryContainer
                                    )
                                }
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isDemo) Icons.Default.Preview else Icons.Default.Analytics,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isDemo) "Loading Demo Data" else "Analyzing League Data",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (isDemo) "Preparing sample analytics..." else "This may take a moment...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        GradientCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            gradientColors = listOf(
                MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = "Error",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Unable to load league",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
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
                            contentColor = MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Text("Go Back")
                    }

                    Button(
                        onClick = onRetry,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
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