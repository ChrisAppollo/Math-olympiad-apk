package com.example.matholympiad.presentation.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matholympiad.data.local.model.User
import com.example.matholympiad.data.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepo: UserRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(loading = true)
                
                val userData = userRepo.getCurrentUserId()
                val score = userRepo.getUserScore(userData)
                
                // 计算勋章状态
                val allBadges = Badges.getAllBadges()
                val unlockedBadges = calculateUnlockedBadges(score, allBadges)
                
                // 计算正确率
                val answeredCount = userRepo.getUserAnswerQuestionNums(userData)
                val correctCount = userRepo.getUserCorrectAnswerNums(userData)
             val correctRate = if (answeredCount > 0) correctCount.toFloat() / answeredCount else 0f
                
                _uiState.value = ProfileUiState(
                    loading = false,
                    user = null, // 简化处理
                    totalScore = score,
                    badgesCount = unlockedBadges.count { it.value },
                    streakCount = userRepo.getUserStreak(userData),
                    badges = allBadges.map { it.copy(isUnlocked = it.id in unlockedBadges.filterValues { v -> v }.keys) },
                    answeredQuestionsCount = answeredCount,
                    correctRate = correctRate
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(loading = false)
            }
        }
    }
    
    private fun calculateUnlockedBadges(score: Int, badges: List<Badge>): Map<String, Boolean> {
        return badges.associate { badge ->
            val unlocked = when (badge.id) {
                "first_quiz" -> true // 首次答题
                "perfect_score", "math_wizard", "streak_3", "streak_7", "collector", "quick_solver" -> 
                    score >= badge.requiredPoints
                "master" -> score >= 1000
                else -> false
            }
            badge.id to unlocked
        }
    }
}
