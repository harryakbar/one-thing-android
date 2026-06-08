package dev.harryakbar.onething

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import dev.harryakbar.onething.ui.complete.CompleteScreen
import dev.harryakbar.onething.ui.streak.StreakScreen
import dev.harryakbar.onething.ui.theme.OneThingTheme
import dev.harryakbar.onething.ui.today.TodayScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OneThingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = dev.harryakbar.onething.ui.theme.Background
                ) {
                    OneThingNavHost()
                }
            }
        }
    }
}

@Composable
fun OneThingNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "today"
    ) {
        composable("today") {
            TodayScreen(
                onTaskCompleted = { streak ->
                    navController.navigate("complete/$streak") {
                        popUpTo("today") { inclusive = false }
                    }
                }
            )
        }
        composable(
            route = "complete/{streak}",
            arguments = listOf(navArgument("streak") { type = NavType.IntType })
        ) { backStackEntry ->
            val streak = backStackEntry.arguments?.getInt("streak") ?: 1
            CompleteScreen(
                streak = streak,
                onSeeHistory = {
                    navController.navigate("streak")
                },
                onBackToToday = {
                    navController.navigate("today") {
                        popUpTo("today") { inclusive = true }
                    }
                }
            )
        }
        composable("streak") {
            StreakScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
