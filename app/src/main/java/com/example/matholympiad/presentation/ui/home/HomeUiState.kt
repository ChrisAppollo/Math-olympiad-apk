package com.example.matholympiad.presentation.ui.home

/**
 * 首页 UI 状态
 */
data class HomeUiState(
    val totalScore: Int = 0,
    val todayCompleted: Int = 0,
    val maxTodayQuestions: Int = 3,
    val badgesCount: Int = 0,
    val isQuizAvailable: Boolean = true,
    val loading: Boolean = false
)
