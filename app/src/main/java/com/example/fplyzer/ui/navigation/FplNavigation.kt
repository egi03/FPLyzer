package com.example.fplyzer.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fplyzer.ui.screens.home.HomeScreen
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsScreen
import com.example.fplyzer.ui.screens.home.DeveloperInfoScreen
import com.example.fplyzer.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FplNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val viewModelFactory = ViewModelFactory(context.applicationContext as android.app.Application)

    NavHost(
        navController = navController,
        startDestination = "home",
        enterTransition = {
            fadeIn(animationSpec = tween(300)) +
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(400)
                    )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) +
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(400)
                    )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) +
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(400)
                    )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) +
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(400)
                    )
        }
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToLeagueStats = { leagueId ->
                    navController.navigate("league_stats/$leagueId")
                },
                onNavigateToDemo = {
                    navController.navigate("demo_league")
                },
                onNavigateToDeveloperInfo = {
                    navController.navigate("developer_info")
                },
                viewModel = viewModel(factory = viewModelFactory)
            )
        }

        composable(
            route = "league_stats/{leagueId}",
            arguments = listOf(
                navArgument("leagueId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val leagueId = backStackEntry.arguments?.getInt("leagueId") ?: 0
            LeagueStatsScreen(
                leagueId = leagueId,
                isDemo = false,
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = viewModel(factory = viewModelFactory)
            )
        }

        composable("demo_league") {
            LeagueStatsScreen(
                leagueId = 999999,
                isDemo = true,
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = viewModel(factory = viewModelFactory)
            )
        }

        composable("developer_info") {
            DeveloperInfoScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}