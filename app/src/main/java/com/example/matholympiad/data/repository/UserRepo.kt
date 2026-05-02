package com.example.matholympiad.data.repository

import com.example.matholympiad.data.local.dao.UserDao
import com.example.matholympiad.data.local.model.AnswerRecord
import com.example.matholympiad.data.local.model.AppConstants
import com.example.matholympiad.data.local.model.Badge
import com.example.matholympiad.data.local.model.TodayQuestion
import com.example.matholympiad.data.local.model.User
import kotlinx.coroutines.flow.Flow

/**
 * 用户数据仓库
 */
class UserRepo(private val userDao: UserDao) {
 
 companion object {
 const val DEFAULT_USER_ID = AppConstants.DEFAULT_USER_ID
 }
 
 /**
 * 获取或创建默认用户
 * 自动检查日期，如果是新的一天则重置今日答题状态
 */
 suspend fun getDefaultUser(): User {
 var user = userDao.getUser(DEFAULT_USER_ID)
 val todayDate = getCurrentDate()
 
 if (user == null) {
 user = User(
 userId = DEFAULT_USER_ID,
 totalScore = 0,
 badges = "[]",
 streakCount = 0,
 lastLoginDate = todayDate,
 todayQuestionsCompleted = 0,
 totalAnswered = 0,
 totalCorrect = 0
 )
 userDao.insertUser(user)
 } else {
 // 检查是否是新的一天，如果是则重置今日答题状态
 if (user.lastLoginDate != todayDate) {
 user = user.copy(
 lastLoginDate = todayDate,
 todayQuestionsCompleted = 0
 )
 userDao.updateUser(user)
 }
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
 * 获取当前用户ID
 */
 fun getCurrentUserId(): String = DEFAULT_USER_ID
 
 /**
 * 获取用户积分
 */
 suspend fun getUserScore(userId: String = DEFAULT_USER_ID): Int {
 return userDao.getUserScore(userId)
 }
 
 /**
 * 获取用户连续打卡天数
 */
 suspend fun getUserStreak(userId: String = DEFAULT_USER_ID): Int {
 return userDao.getUserStreak(userId)
 }
 
 /**
 * 获取用户答题数量
 */
 suspend fun getUserAnswerQuestionNums(userId: String = DEFAULT_USER_ID): Int {
 return userDao.getUserAnswerQuestionNums(userId)
 }
 
 /**
 * 获取用户正确答题数量
 */
 suspend fun getUserCorrectAnswerNums(userId: String = DEFAULT_USER_ID): Int {
 return userDao.getUserCorrectAnswerNums(userId)
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
 
 /**
 * 更新用户累计答题统计
 */
 suspend fun updateUserStats(userId: String, correct: Boolean) {
 val user = userDao.getUser(userId) ?: return
 val newAnswered = user.totalAnswered + 1
 val newCorrect = if (correct) user.totalCorrect + 1 else user.totalCorrect
 userDao.updateUserStats(userId, newAnswered, newCorrect)
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
