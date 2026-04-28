package com.example.matholympiad.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.matholympiad.presentation.ui.home.HomeScreen
import com.example.matholympiad.presentation.ui.home.HomeViewModel
import com.example.matholympiad.presentation.ui.profile.ProfileScreen
import com.example.matholympiad.presentation.ui.profile.ProfileViewModel
import com.example.matholympiad.presentation.ui.quiz.QuizScreen
import com.example.matholympiad.presentation.viewmodel.QuizViewModel
import com.example.matholympiad.presentation.ui.wronganswers.WrongAnswersScreen
import com.example.matholympiad.presentation.ui.wronganswers.WrongAnswersViewModel

open class NavGraphRoute(val route: String)

object Home : NavGraphRoute("home")
object Quiz : NavGraphRoute("quiz")
object Profile : NavGraphRoute("profile")
object WrongAnswers : NavGraphRoute("wrong_answers")

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
            val uiState by viewModel.uiState.collectAsState()
            HomeScreen(
                uiState = uiState,
                onQuizClick = { navController.navigate(Quiz.route) },
                onProfileClick = { navController.navigate(Profile.route) },
                onLeaderboardClick = { /* TODO: 实现排行榜 */ }
            )
        }

composable(Quiz.route) {
            val viewModel: QuizViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()
            
            QuizScreen(
                uiState = uiState,
                userAnswer = viewModel.userAnswer,
                onAnswerChanged = { viewModel.onAnswerChanged(it) },
                onSubmitClick = { viewModel.onSubmitClick() },
                onNextClick = { 
                    if (uiState.quizCompleted) {
                        viewModel.resetQuiz()
                        navController.popBackStack()
                    } else {
                        viewModel.onNextClick()
                    }
                },
                onHintClick = { viewModel.onHintClick() },
                onBackClick = { 
                    viewModel.resetQuiz()
                    navController.popBackStack() 
                }
            )
        }

        composable(Profile.route) {
            val viewModel: ProfileViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()
            ProfileScreen(
                uiState = uiState,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(WrongAnswers.route) {
            val viewModel: WrongAnswersViewModel = hiltViewModel()
            WrongAnswersScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
