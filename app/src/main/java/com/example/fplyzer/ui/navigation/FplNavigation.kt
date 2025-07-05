package com.example.fplyzer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fplyzer.ui.screens.home.HomeScreen
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsScreen

@Composable
fun FplNavigation(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToLeagueStats = { leagueId ->
                    navController.navigate("league_stats/$leagueId")
                }
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
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}