package com.example.matholympiad.presentation.ui.home

import com.example.matholympiad.presentation.ui.profile.Badges

/**
 * 首页 UI 状态
 */
data class HomeUiState(
 val totalScore: Int = 0,
 val todayCompleted: Int = 0,
 val maxTodayQuestions: Int = 3,
 val badgesCount: Int = 0,
 val unlockedBadges: List<String> = emptyList(), // 已解锁勋章ID列表
 val isQuizAvailable: Boolean = true,
 val loading: Boolean = false
) {
 /**
 * 获取已解锁勋章的完整信息
 */
 fun getUnlockedBadgeInfos(): List<com.example.matholympiad.presentation.ui.profile.Badge> {
 return Badges.getAllBadges().filter { it.id in unlockedBadges }
 }
}
