package com.example.matholympiad.data.repository

import com.example.matholympiad.data.local.dao.UserDao
import com.example.matholympiad.data.local.model.AnswerRecord
import com.example.matholympiad.data.local.model.Badge
import com.example.matholympiad.data.local.model.TodayQuestion
import com.example.matholympiad.data.local.model.User
import kotlinx.coroutines.flow.Flow

/**
 * 用户数据仓库
 */
class UserRepo(private val userDao: UserDao) {
    
    companion object {
        const val DEFAULT_USER_ID = "student_001"
    }
    
    /**
     * 获取或创建默认用户
     */
    suspend fun getDefaultUser(): User {
        var user = userDao.getUser(DEFAULT_USER_ID)
        if (user == null) {
            val todayDate = getCurrentDate()
            user = User(
                userId = DEFAULT_USER_ID,
                totalScore = 0,
                badges = "[]",
                streakCount = 0,
                lastLoginDate = todayDate,
                todayQuestionsCompleted = 0
            )
            userDao.insertUser(user)
        }
        return user
    }
    
    /**
     * 获取用户数据流（用于实时 UI 更新）
     */
    fun getUserFlow(): Flow<User?> {
        return userDao.getUserFlow(DEFAULT_USER_ID)
    }
    
    /**
     * 增加积分
     */
    suspend fun addPoints(points: Int) {
        userDao.addPoints(DEFAULT_USER_ID, points)
    }
    
    /**
     * 更新今日完成题数
     */
    suspend fun updateTodayCompletedCount(count: Int) {
        userDao.updateTodayCompletedCount(DEFAULT_USER_ID, count)
    }
    
    /**
     * 更新连续打卡天数和最后登录日期
     */
    suspend fun updateStreak(streakCount: Int, lastLoginDate: String) {
        userDao.updateStreak(DEFAULT_USER_ID, streakCount, lastLoginDate)
    }
    
    /**
     * 添加新解锁的勋章
     */
    suspend fun addBadges(newBadgeIds: List<String>) {
        userDao.updateUserBadges(DEFAULT_USER_ID, newBadgeIds)
    }
    
    /**
     * 插入答题记录
     */
    suspend fun insertAnswerRecord(answer: AnswerRecord) {
        // TODO: 需要 AnswerHistoryDao，后续实现
    }
}

/**
 * 获取当前日期字符串（格式：yyyy-MM-dd）
 */
private fun getCurrentDate(): String {
    val calendar = java.util.Calendar.getInstance()
    return String.format("%04d-%02d-%02d",
        calendar.get(java.util.Calendar.YEAR),
        calendar.get(java.util.Calendar.MONTH) + 1,
        calendar.get(java.util.Calendar.DAY_OF_MONTH)
    )
}