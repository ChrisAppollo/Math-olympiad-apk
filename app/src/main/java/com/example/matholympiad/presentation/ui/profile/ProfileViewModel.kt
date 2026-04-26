package com.example.matholympiad.presentation.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            try {
                val user = userRepo.getDefaultUser()
                _uiState.value = ProfileUiState(
                    totalScore = user.totalScore,
                    badgesCount = user.getBadgesList().size,
                    streakCount = user.streakCount
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
