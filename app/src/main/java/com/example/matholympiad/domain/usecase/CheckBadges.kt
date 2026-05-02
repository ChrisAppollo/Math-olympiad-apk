package com.example.matholympiad.domain.usecase

import com.example.matholympiad.data.local.model.Badge
import com.example.matholympiad.data.local.model.BadgeType
import com.example.matholympiad.data.local.model.User
import com.example.matholympiad.data.repository.UserRepo
import javax.inject.Inject

/**
 * 检查并解锁勋章
 * 在每次答题完成后调用
 */
class CheckBadges @Inject constructor(
 private val userRepo: UserRepo
) {
 /**
  * 检查并解锁新勋章
  * @return 返回本次新解锁的勋章列表
  */
 suspend operator fun invoke(user: User? = null): List<Badge> {
 val currentUser = user ?: userRepo.getDefaultUser()
 val newlyUnlocked = mutableListOf<Badge>()

 // 获取用户当前已解锁的勋章
 val currentBadges = currentUser.getBadgesList().toSet()

 // 检查各勋章解锁条件
 val allBadgeChecks = listOf(
 checkFirstQuiz(currentUser),
 checkPerfectScore(currentUser),
 checkStreakBadges(currentUser),
 checkMasteryBadges(currentUser),
 checkMaster(currentUser)
 )

 // 收集新解锁的勋章
 allBadgeChecks.forEach { badge ->
 if (badge != null && badge.id !in currentBadges) {
 newlyUnlocked.add(badge)
 // 更新用户勋章列表
 userRepo.addBadges(listOf(badge.id))
 }
 }

 return newlyUnlocked
 }

 /**
  * 首次答题勋章
  */
 private fun checkFirstQuiz(user: User): Badge? {
 return if (user.totalAnswered >= 1) {
 Badge(
 id = "first_quiz",
 name = "初次尝试",
 emoji = "🎯",
 description = "完成第一次答题",
 requiredPoints = 0,
 type = BadgeType.BEGINNER,
 isUnlocked = true
 )
 } else null
 }

 /**
  * 满分勋章 - 100% 正确率
  */
 private fun checkPerfectScore(user: User): Badge? {
 val accuracy = if (user.totalAnswered > 0) {
 user.totalCorrect.toFloat() / user.totalAnswered
 } else 0f
 
 return if (accuracy == 1.0f && user.totalAnswered >= 1) {
 Badge(
 id = "perfect_score",
 name = "满分高手",
 emoji = "💯",
 description = "单日满分完成所有题目",
 requiredPoints = 0,
 requiredAccuracy = 1.0f,
 type = BadgeType.ACHIEVEMENT,
 isUnlocked = true
 )
 } else null
 }

 /**
  * 连续答题勋章
  */
 private fun checkStreakBadges(user: User): Badge? {
 return when {
 user.streakCount >= 30 -> Badge(
 id = "streak_30",
 name = "月度之星",
 emoji = "⭐",
 description = "连续 30 天完成每日答题",
 requiredPoints = 0,
 requiredStreak = 30,
 type = BadgeType.STREAK,
 isUnlocked = true
 )
 user.streakCount >= 7 -> Badge(
 id = "streak_7",
 name = "一周坚持",
 emoji = "🔥",
 description = "连续 7 天完成每日答题",
 requiredPoints = 0,
 requiredStreak = 7,
 type = BadgeType.STREAK,
 isUnlocked = true
 )
 user.streakCount >= 3 -> Badge(
 id = "streak_3",
 name = "三日坚持",
 emoji = "🔥",
 description = "连续 3 天完成每日答题",
 requiredPoints = 0,
 requiredStreak = 3,
 type = BadgeType.STREAK,
 isUnlocked = true
 )
 else -> null
 }
 }

 /**
  * 精通勋章 - 累计答对 50 题
  */
 private fun checkMasteryBadges(user: User): Badge? {
 return if (user.totalCorrect >= 50) {
 Badge(
 id = "math_wizard",
 name = "数学奇才",
 emoji = "🔢",
 description = "累计答对 50 道题目",
 requiredPoints = 500,
 type = BadgeType.MASTERY,
 isUnlocked = true
 )
 } else null
 }

 /**
  * 大师勋章 - 1000 积分
  */
 private fun checkMaster(user: User): Badge? {
 return if (user.totalScore >= 1000) {
 Badge(
 id = "master",
 name = "数学大师",
 emoji = "🎓",
 description = "累计获得 1000 积分",
 requiredPoints = 1000,
 type = BadgeType.SPECIAL,
 isUnlocked = true
 )
 } else null
 }
}
