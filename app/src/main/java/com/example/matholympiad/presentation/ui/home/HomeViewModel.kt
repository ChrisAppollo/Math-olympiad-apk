package com.example.matholympiad.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matholympiad.data.repository.QuestionRepositoryInitializer
import com.example.matholympiad.data.repository.UserRepo
import com.example.matholympiad.domain.usecase.CheckBadges
import com.example.matholympiad.data.local.model.AppConstants
import com.example.matholympiad.domain.usecase.GetTodayQuestions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
 private val userRepo: UserRepo,
 private val getTodayQuestions: GetTodayQuestions,
 private val checkBadges: CheckBadges,
 private val questionInitializer: QuestionRepositoryInitializer
) : ViewModel() {

 private val _uiState = MutableStateFlow(HomeUiState(maxTodayQuestions = AppConstants.DAILY_QUESTION_COUNT))
 val uiState: StateFlow<HomeUiState> = _uiState

    init {
        initializeData()
    }

    private fun initializeData() {
        viewModelScope.launch {
            // 首先初始化题库
            questionInitializer.initializeIfNeeded()
            // 然后加载用户数据
            loadUserData()
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            try {
 val user = userRepo.getDefaultUser()
 val badges = user.getBadgesList()
 _uiState.value = _uiState.value.copy(
 totalScore = user.totalScore,
 todayCompleted = user.todayQuestionsCompleted,
 badgesCount = badges.size,
 unlockedBadges = badges,
 isQuizAvailable = user.todayQuestionsCompleted < AppConstants.DAILY_QUESTION_COUNT,
 loading = false
 )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(loading = false)
            }
        }
    }

    fun startQuiz() {
        viewModelScope.launch {
            try {
                val result = getTodayQuestions()
                if (result.isFailure) {
                    _uiState.value = _uiState.value.copy(
                        isQuizAvailable = false
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            loadUserData()
        }
    }
}
