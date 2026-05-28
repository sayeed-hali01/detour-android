package com.detour.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.detour.app.ui.detail.DetailScreen
import com.detour.app.ui.map.MapScreen
import com.detour.app.ui.setup.SetupScreen

@Composable
fun DetourNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "setup"
    ) {
        composable(route = "setup") {
            SetupScreen(navController = navController)
        }

        composable(
            route = "map/{response}",
            arguments = listOf(
                navArgument("response") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val responseJson = backStackEntry.arguments?.getString("response") ?: ""
            MapScreen(
                navController = navController,
                detourResponseJson = responseJson
            )
        }

        composable(
            route = "detail/{detour}",
            arguments = listOf(
                navArgument("detour") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val detourJson = backStackEntry.arguments?.getString("detour") ?: ""
            DetailScreen(
                navController = navController,
                detourJson = detourJson
            )
        }
    }
}
