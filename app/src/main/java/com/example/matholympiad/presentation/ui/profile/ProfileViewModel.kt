package com.example.matholympiad.presentation.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matholympiad.data.local.model.User
import com.example.matholympiad.data.repository.UserRepo
import com.example.matholympiad.domain.usecase.CheckBadges
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val checkBadges: CheckBadges
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
                val user = userRepo.getDefaultUser()
                
                // 使用 CheckBadges UseCase 获取已解锁勋章 ID
                val unlockedBadgeIds = user.getBadgesList().toSet()
                
                // 计算勋章状态 — 基于 CheckBadges 的实际解锁条件
                val allBadges = Badges.getAllBadges()
                val badgesWithStatus = allBadges.map { badge ->
                    badge.copy(isUnlocked = badge.id in unlockedBadgeIds)
                }
                
                // 计算正确率
                val answeredCount = userRepo.getUserAnswerQuestionNums(userData)
                val correctCount = userRepo.getUserCorrectAnswerNums(userData)
                val correctRate = if (answeredCount > 0) correctCount.toFloat() / answeredCount else 0f
                
                _uiState.value = ProfileUiState(
                    loading = false,
                    user = user,
                    totalScore = score,
                    badgesCount = badgesWithStatus.count { it.isUnlocked },
                    streakCount = userRepo.getUserStreak(userData),
                    badges = badgesWithStatus,
                    answeredQuestionsCount = answeredCount,
                    correctRate = correctRate
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(loading = false)
            }
        }
    }
}
