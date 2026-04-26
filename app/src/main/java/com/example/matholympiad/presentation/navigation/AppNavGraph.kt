package com.example.matholympiad.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.matholympiad.presentation.ui.home.HomeScreen
import com.example.matholympiad.presentation.ui.home.HomeViewModel
import com.example.matholympiad.presentation.ui.profile.ProfileScreen
import com.example.matholympiad.presentation.ui.profile.ProfileViewModel
import com.example.matholympiad.presentation.ui.quiz.QuizScreen
import com.example.matholympiad.presentation.ui.quiz.QuizViewModel

open class NavGraphRoute(val route: String)

object Home : NavGraphRoute("home")
object Quiz : NavGraphRoute("quiz/{questionIndex}") {
    const val QUESTION_INDEX = "questionIndex"
}
object Profile : NavGraphRoute("profile")

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                uiState = viewModel.uiState.value,
                onQuizClick = { navController.navigate(Quiz.route.replace("{questionIndex}", "0")) },
                onProfileClick = { navController.navigate(Profile.route) },
                onLeaderboardClick = { /* TODO */ }
            )
        }

        composable(
            Quiz.route,
            arguments = listOf(navArgument(Quiz.QUESTION_INDEX) {
                type = NavType.IntType
                defaultValue = 0
            })
        ) { backStackEntry ->
            val questionIndex = backStackEntry.arguments?.getInt(Quiz.QUESTION_INDEX) ?: 0
            val viewModel: QuizViewModel = hiltViewModel()
            QuizScreen(
                uiState = viewModel.uiState.value,
                onBackClick = { navController.popBackStack() },
                onNextClick = {
                    if (questionIndex < 2) {
                        navController.navigate("quiz/${questionIndex + 1}")
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(Profile.route) {
            val viewModel: ProfileViewModel = hiltViewModel()
            ProfileScreen(uiState = viewModel.uiState.value)
        }
    }
}
