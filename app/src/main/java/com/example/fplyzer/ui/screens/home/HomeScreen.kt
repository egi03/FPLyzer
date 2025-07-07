package com.example.fplyzer.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fplyzer.data.models.FavouriteLeague
import com.example.fplyzer.ui.components.*
import com.example.fplyzer.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToLeagueStats: (Int) -> Unit,
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
            .imePadding()
    ) {
        AnimatedBackgroundShapes()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(800))
            ) {
                Icon(
                    Icons.Default.Analytics,
                    contentDescription = "FPL Analytics",
                    modifier = Modifier.size(80.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))


            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(1000)) +
                        slideInVertically(initialOffsetY = { -50 })
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
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

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(1200, delayMillis = 300)) +
                        slideInVertically(initialOffsetY = { 100 })
            ) {
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
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

                        Spacer(modifier = Modifier.height(24.dp))

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

                        Spacer(modifier = Modifier.height(24.dp))

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

            Spacer(modifier = Modifier.height(24.dp))

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

            AnimatedVisibility(
                visible = uiState.error != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
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

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

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