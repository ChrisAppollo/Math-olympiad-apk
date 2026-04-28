package com.example.matholympiad.data.local.model

/**
 * 排行榜条目数据模型
 * Used by LeaderboardRepository
 */
data class LeaderboardEntry(
    val rank: Int,
    val userId: String,
    val userName: String,
    val avatarUrl: String = "",
    val score: Int = 0,
    val streakDays: Int = 0,
    val totalQuestions: Int = 0,
    val accuracy: Float = 0f
)
