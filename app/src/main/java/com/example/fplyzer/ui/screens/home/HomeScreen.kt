package com.example.fplyzer.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fplyzer.data.models.FavouriteLeague
import com.example.fplyzer.ui.components.GlassmorphicCard
import com.example.fplyzer.ui.components.ModernButton
import com.example.fplyzer.ui.theme.FplAccent
import com.example.fplyzer.ui.theme.FplAccentDark
import com.example.fplyzer.ui.theme.FplDivider
import com.example.fplyzer.ui.theme.FplError
import com.example.fplyzer.ui.theme.FplGradientEnd
import com.example.fplyzer.ui.theme.FplGradientMiddle
import com.example.fplyzer.ui.theme.FplGradientStart
import com.example.fplyzer.ui.theme.FplPrimary
import com.example.fplyzer.ui.theme.FplRed
import com.example.fplyzer.ui.theme.FplSecondary
import com.example.fplyzer.ui.theme.FplSecondaryDark
import com.example.fplyzer.ui.theme.FplTextPrimary
import com.example.fplyzer.ui.theme.FplTextSecondary
import com.example.fplyzer.ui.theme.FplYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToLeagueStats: (Int) -> Unit,
    onNavigateToDemo: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        viewModel.loadFavouriteLeagues()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        FplGradientStart,
                        FplGradientMiddle,
                        FplGradientEnd
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
            .systemBarsPadding()
            .imePadding()
    ) {
        AnimatedBackgroundShapes()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Logo Section
            item {
                Spacer(modifier = Modifier.height(16.dp)) // Extra padding to avoid notch
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(800))
                ) {
                    Icon(
                        Icons.Default.Analytics,
                        contentDescription = "FPL Analytics",
                        modifier = Modifier
                            .size(80.dp)
                            .padding(top = 16.dp), // Additional top padding
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(1000)) +
                            slideInVertically(initialOffsetY = { -50 })
                ) {
                    Text(
                        text = "FPL.stats",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Demo Button Section
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(1100, delayMillis = 200)) +
                            slideInVertically(initialOffsetY = { 100 })
                ) {
                    GlassmorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    Icons.Default.Preview,
                                    contentDescription = null,
                                    tint = FplSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Try Demo",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = FplSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Experience all features with sample data",
                                style = MaterialTheme.typography.bodyMedium,
                                color = FplTextSecondary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            ModernButton(
                                onClick = onNavigateToDemo,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                text = "View Demo",
                                icon = Icons.Default.PlayArrow,
                                gradient = listOf(FplSecondary, FplSecondaryDark)
                            )
                        }
                    }
                }
            }

            // League ID Input Section
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(1200, delayMillis = 300)) +
                            slideInVertically(initialOffsetY = { 100 })
                ) {
                    GlassmorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    Icons.Default.Analytics,
                                    contentDescription = null,
                                    tint = FplPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Enter League ID",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = FplPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Find your league ID in the FPL app or website",
                                style = MaterialTheme.typography.bodyMedium,
                                color = FplTextSecondary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            TextField(
                                value = uiState.leagueIdInput,
                                onValueChange = viewModel::updateLeagueId,
                                placeholder = "e.g. 314159",
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Search
                                ),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        if (uiState.leagueIdInput.isNotEmpty()) {
                                            keyboardController?.hide()
                                            onNavigateToLeagueStats(uiState.leagueIdInput.toInt())
                                        }
                                    }
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            ModernButton(
                                onClick = {
                                    if (uiState.leagueIdInput.isNotEmpty()) {
                                        keyboardController?.hide()
                                        onNavigateToLeagueStats(uiState.leagueIdInput.toInt())
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                enabled = uiState.leagueIdInput.isNotEmpty() && !uiState.isLoading,
                                isLoading = uiState.isLoading,
                                text = "Analyze League",
                                icon = Icons.Default.Analytics,
                                gradient = listOf(FplAccent, FplAccentDark)
                            )
                        }
                    }
                }
            }

            // Favourite Leagues Section
            item {
                AnimatedVisibility(
                    visible = uiState.favouriteLeagues.isNotEmpty(),
                    enter = fadeIn(animationSpec = tween(800, delayMillis = 200)) +
                            slideInVertically(initialOffsetY = { 50 })
                ) {
                    FavouriteLeaguesSection(
                        favouriteLeagues = uiState.favouriteLeagues,
                        onLeagueClick = onNavigateToLeagueStats,
                        onRemoveFavourite = viewModel::removeFavouriteLeague
                    )
                }
            }

            // Error Section
            item {
                AnimatedVisibility(
                    visible = uiState.error != null,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = FplError.copy(alpha = 0.1f)
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = FplError,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = uiState.error ?: "",
                                color = FplError,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp)) // Bottom padding for scroll
            }
        }
    }
}

// Rest of the composables (FavouriteLeaguesSection, FavouriteLeagueCard, AnimatedBackgroundShapes, TextField) remain unchanged
@Composable
private fun FavouriteLeaguesSection(
    favouriteLeagues: List<FavouriteLeague>,
    onLeagueClick: (Int) -> Unit,
    onRemoveFavourite: (Int) -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = FplYellow,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Favourite Leagues",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FplPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favouriteLeagues) { league ->
                    FavouriteLeagueCard(
                        league = league,
                        onClick = { onLeagueClick(league.id) },
                        onRemove = { onRemoveFavourite(league.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavouriteLeagueCard(
    league: FavouriteLeague,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = league.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FplTextPrimary
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Group,
                            contentDescription = null,
                            tint = FplTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${league.totalManagers} managers",
                            style = MaterialTheme.typography.bodySmall,
                            color = FplTextSecondary
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.BarChart,
                            contentDescription = null,
                            tint = FplTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${String.format("%.1f", league.averagePoints)} avg",
                            style = MaterialTheme.typography.bodySmall,
                            color = FplTextSecondary
                        )
                    }
                }
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(FplRed.copy(alpha = 0.1f))
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove favourite",
                    tint = FplRed,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun AnimatedBackgroundShapes() {
    val infiniteTransition = rememberInfiniteTransition()

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-50).dp, y = 100.dp)
                .rotate(rotation)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            FplAccent.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = 200.dp, y = 500.dp)
                .rotate(-rotation)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            FplSecondary.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("League ID") },
        placeholder = { Text(placeholder) },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FplAccent,
            unfocusedBorderColor = FplDivider,
            focusedLabelColor = FplAccent,
            cursorColor = FplAccent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = FplAccent
            )
        }
    )
}