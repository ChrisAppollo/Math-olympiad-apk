package com.example.matholympiad.domain.usecase

import com.example.matholympiad.data.local.model.User
import com.example.matholympiad.data.repository.UserRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CheckBadges(private val userRepo: UserRepo) {
    
    suspend operator fun invoke(): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val user = userRepo.getDefaultUser()
            val newBadges = mutableListOf<String>()
            
            // 检查"新手入门"勋章
            if (user.todayQuestionsCompleted > 0 && !user.badges.contains("beginner")) {
                newBadges.add("beginner")
            }
            
            // 检查"坚持之星"勋章（连续打卡 7 天）
            if (user.streakCount >= 7 && !user.badges.contains("persistence_7days")) {
                newBadges.add("persistence_7days")
            }
            
            // 检查"数学小天才"勋章（总积分 100）
            if (user.totalScore >= 100 && !user.badges.contains("math_genius")) {
                newBadges.add("math_genius")
            }
            
            if (newBadges.isNotEmpty()) {
                userRepo.addBadges(newBadges)
            }
            
            Result.success(newBadges)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
