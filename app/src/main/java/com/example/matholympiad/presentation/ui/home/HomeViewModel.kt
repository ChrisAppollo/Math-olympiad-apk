package com.example.matholympiad.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matholympiad.data.repository.UserRepo
import com.example.matholympiad.domain.usecase.CheckBadges
import com.example.matholympiad.domain.usecase.GetTodayQuestions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val totalScore: Int = 0,
    val todayCompleted: Int = 0,
    val maxTodayQuestions: Int = 3,
    val badgesCount: Int = 0,
    val isQuizAvailable: Boolean = true,
    val loading: Boolean = false
)

class HomeViewModel(
    private val userRepo: UserRepo,
    private val getTodayQuestions: GetTodayQuestions,
    private val checkBadges: CheckBadges
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState
    
    init {
        loadUserData()
    }
    
    fun loadData() {
        loadUserData()
    }
    
    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            try {
                val user = userRepo.getDefaultUser()
                checkBadges()
                _uiState.value = _uiState.value.copy(
                    totalScore = user.totalScore,
                    todayCompleted = user.todayQuestionsCompleted,
                    badgesCount = user.badges.size,
                    isQuizAvailable = user.todayQuestionsCompleted < 3,
                    loading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(loading = false)
            }
        }
    }
    
    fun startQuiz() {
        viewModelScope.launch {
            userRepo.updateTodayCompletedCount(0)
        }
    }
}
