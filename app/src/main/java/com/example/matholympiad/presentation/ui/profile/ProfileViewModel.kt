package com.example.matholympiad.presentation.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matholympiad.data.repository.UserRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val totalScore: Int = 0,
    val badgesCount: Int = 0,
    val streakCount: Int = 0,
    val recentHistory: List<AnswerRecord> = emptyList()
)

class ProfileViewModel(private val userRepo: UserRepo) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState
    
    init {
        loadUserData()
    }
    
    fun loadData() {
        loadUserData()
    }
    
    private fun loadUserData() {
        viewModelScope.launch {
            try {
                val user = userRepo.getDefaultUser()
                _uiState.value = ProfileUiState(
                    totalScore = user.totalScore,
                    badgesCount = user.badges.size,
                    streakCount = user.streakCount
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
