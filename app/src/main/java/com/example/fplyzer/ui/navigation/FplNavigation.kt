package com.example.fplyzer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fplyzer.ui.screens.dashboard.DashboardScreen
import com.example.fplyzer.ui.screens.home.HomeScreen
import com.example.fplyzer.ui.screens.league.LeagueScreen
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsScreen
import com.example.fplyzer.ui.screens.manager.ManagerScreen
import com.example.fplyzer.ui.screens.playerDetails.PlayerDetailsScreen
import com.example.fplyzer.ui.screens.players.PlayersScreen
import com.example.fplyzer.ui.screens.teamViewer.TeamViewerScreen

@Composable
fun FplNavigation(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            DashboardScreen(
                onNavigateToManager = { managerId ->
                    navController.navigate("manager/$managerId")
                },
                onNavigateToPlayers = {
                    navController.navigate("players")
                },
                onNavigateToPlayer = { playerId ->
                    navController.navigate("player/$playerId")
                },
                onNavigateToTeamViewer = { managerId ->
                    navController.navigate("team_viewer/$managerId")
                },
                onNavigateToLeagueStats = {
                    navController.navigate("league_stats")
                }
            )
        }

        composable("league_stats") {
            LeagueStatsScreen()
        }

        composable("players") {
            PlayersScreen(
                onNavigateToPlayer = { playerId ->
                    navController.navigate("player/$playerId")
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "player/{playerId}",
            arguments = listOf(
                navArgument("playerId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val playerId = backStackEntry.arguments?.getInt("playerId") ?: 0
            PlayerDetailsScreen(
                playerId = playerId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "team_viewer/{managerId}",
            arguments = listOf(
                navArgument("managerId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val managerId = backStackEntry.arguments?.getInt("managerId") ?: 0
            TeamViewerScreen(
                managerId = managerId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "manager/{managerId}",
            arguments = listOf(
                navArgument("managerId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val managerId = backStackEntry.arguments?.getInt("managerId") ?: 0
            ManagerScreen(
                managerId = managerId,
                onNavigateToLeague = { leagueId ->
                    navController.navigate("league/$leagueId")
                },
                onNavigateToTeamViewer = {
                    navController.navigate("team_viewer/$managerId")
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "league/{leagueId}",
            arguments = listOf(
                navArgument("leagueId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val leagueId = backStackEntry.arguments?.getInt("leagueId") ?: 0
            LeagueScreen(
                leagueId = leagueId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}