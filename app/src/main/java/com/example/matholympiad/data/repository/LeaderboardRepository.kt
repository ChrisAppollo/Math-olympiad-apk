package com.example.matholympiad.data.repository

import com.example.matholympiad.data.local.model.LeaderboardEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * 排行榜仓库
 */
@Singleton
class LeaderboardRepository @Inject constructor() {
    
    companion object {
        // 模拟数据
        private val mockLeaderboard = mutableListOf<LeaderboardEntry>().apply {
            repeat(100) { index ->
                add(LeaderboardEntry(
                    rank = index + 1,
                    userId = "user_$index",
                    userName = "学友${index + 1}",
                    avatarUrl = "",
                    score = (10000 - index * 80 + Random.nextInt(-20, 20)).coerceAtLeast(100),
                    streakDays = Random.nextInt(1, 30),
                    totalQuestions = Random.nextInt(50, 500),
                    accuracy = Random.nextFloat() * 0.4f + 0.5f
                ))
            }
        }
    }
    
    /**
     * 获取总积分榜
     */
    fun getTotalLeaderboard(): Flow<List<LeaderboardEntry>> = flow {
        emit(mockLeaderboard.sortedByDescending { it.score })
    }
    
    /**
     * 获取连胜榜
     */
    fun getStreakLeaderboard(): Flow<List<LeaderboardEntry>> = flow {
        emit(mockLeaderboard.sortedByDescending { it.streakDays })
    }
    
    /**
     * 获取进步榜
     */
    fun getProgressLeaderboard(): Flow<List<LeaderboardEntry>> = flow {
        emit(mockLeaderboard.sortedByDescending { it.accuracy })
    }
    
    /**
     * 获取用户排名
     */
    fun getUserRank(userId: String): Flow<LeaderboardEntry?> = flow {
        val entry = mockLeaderboard.find { it.userId == userId }
            ?: LeaderboardEntry(
                rank = 42,
                userId = userId,
                userName = "我",
                avatarUrl = "",
                score = 5200,
                streakDays = 7,
                totalQuestions = 150,
                accuracy = 0.75f
            )
        emit(entry)
    }
    
    /**
     * 获取指定范围排行榜
     */
    fun getLeaderboardRange(start: Int, count: Int): List<LeaderboardEntry> {
        return mockLeaderboard
            .sortedByDescending { it.score }
            .drop(start - 1)
            .take(count)
    }
}
