package com.example.fplyzer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fplyzer.ui.screens.home.HomeScreen
import com.example.fplyzer.ui.screens.league.LeagueScreen
import com.example.fplyzer.ui.screens.manager.ManagerScreen

@Composable
fun FplNavigation(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToManager = { managerId ->
                    navController.navigate("manager/$managerId")
                }
            )
        }

        composable(
            route = "manager/{managerId}",
            arguments = listOf(navArgument("managerId") { type = NavType.IntType })
        ) { backStackEntry ->
            val managerId = backStackEntry.arguments?.getInt("managerId") ?: 0
            ManagerScreen(
                managerId = managerId,
                onNavigateToLeague = { leagueId ->
                    navController.navigate("league/$leagueId")
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "league/{leagueId}",
            arguments = listOf( navArgument("leagueId") { type = NavType.IntType} )
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