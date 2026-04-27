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
            checkFirstSteps(currentUser),
            checkQuickLearner(currentUser),
            checkPerfectScore(currentUser),
            checkStreakBadges(currentUser),
            checkMasteryBadges(currentUser),
            checkChallengeBadges(currentUser),
            checkGrandMaster(currentUser)
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
    private fun checkFirstSteps(user: User): Badge? {
        return if (user.totalAnswered >= 1) {
            Badge(
                id = "first_steps",
                name = "初次尝试",
                emoji = "🌱",
                description = "完成第一次答题",
                requiredPoints = 0,
                type = BadgeType.BEGINNER
            )
        } else null
    }

    /**
     * 小学者勋章 - 累计100积分
     */
    private fun checkQuickLearner(user: User): Badge? {
        return if (user.totalScore >= 100) {
            Badge(
                id = "quick_learner",
                name = "小学者",
                emoji = "📚",
                description = "累计获得100积分",
                requiredPoints = 100,
                type = BadgeType.ACHIEVEMENT
            )
        } else null
    }

    /**
     * 满分勋章 - 单日正确率100%且完成3题
     */
    private fun checkPerfectScore(user: User): Badge? {
        // 检查今日答题情况
        val hasTodayPerfect = user.todayQuestionsCompleted >= 3 &&
            user.totalAnswered > 0 &&
            (user.totalCorrect.toFloat() / user.totalAnswered >= 0.99f)

        // 简化逻辑：只要今日完成3题且正确率100%
        return if (hasTodayPerfect) {
            Badge(
                id = "perfect_score",
                name = "满分高手",
                emoji = "💯",
                description = "单日满分完成所有题目",
                requiredPoints = 0,
                requiredAccuracy = 1.0f,
                type = BadgeType.ACHIEVEMENT
            )
        } else null
    }

    /**
     * 连续答题勋章
     */
    private fun checkStreakBadges(user: User): Badge? {
        val badgeId = when {
            user.streakCount >= 30 -> "daily_streak_30"
            user.streakCount >= 7 -> "daily_streak_7"
            user.streakCount >= 3 -> "daily_streak_3"
            else -> return null
        }

        return when (badgeId) {
            "daily_streak_30" -> Badge(
                id = badgeId,
                name = "月度之星",
                emoji = "⭐",
                description = "连续30天完成每日答题",
                requiredPoints = 0,
                requiredStreak = 30,
                type = BadgeType.STREAK
            )
            "daily_streak_7" -> Badge(
                id = badgeId,
                name = "坚持一周",
                emoji = "🔥",
                description = "连续7天完成每日答题",
                requiredPoints = 0,
                requiredStreak = 7,
                type = BadgeType.STREAK
            )
            "daily_streak_3" -> Badge(
                id = badgeId,
                name = "三日连击",
                emoji = "🔥",
                description = "连续3天完成每日答题",
                requiredPoints = 0,
                requiredStreak = 3,
                type = BadgeType.STREAK
            )
            else -> null
        }
    }

    /**
     * 精通勋章 - 基于总答题数
     */
    private fun checkMasteryBadges(user: User): Badge? {
        return when {
            user.totalAnswered >= 500 -> Badge(
                id = "master_advanced",
                name = "奥数大师",
                emoji = "🏆",
                description = "累计挑战500道题目",
                requiredPoints = 5000,
                type = BadgeType.MASTERY
            )
            user.totalAnswered >= 100 -> Badge(
                id = "master_intermediate",
                name = "进阶学者",
                emoji = "📈",
                description = "累计挑战100道题目",
                requiredPoints = 1000,
                type = BadgeType.MASTERY
            )
            user.totalAnswered >= 30 -> Badge(
                id = "master_beginner",
                name = "入门学者",
                emoji = "🎯",
                description = "累计挑战30道题目",
                requiredPoints = 300,
                type = BadgeType.MASTERY
            )
            else -> null
        }
    }

    /**
     * 挑战者勋章 - 基于正确率
     */
    private fun checkChallengeBadges(user: User): Badge? {
        if (user.totalAnswered < 20) return null

        val accuracy = user.totalCorrect.toFloat() / user.totalAnswered

        return when {
            accuracy >= 0.9f -> Badge(
                id = "accuracy_expert",
                name = "答题达人",
                emoji = "👑",
                description = "累计答题正确率达到90%",
                requiredPoints = 2000,
                requiredAccuracy = 0.9f,
                type = BadgeType.ACHIEVEMENT
            )
            accuracy >= 0.8f -> Badge(
                id = "accuracy_advanced",
                name = "答题高手",
                emoji = "🎖️",
                description = "累计答题正确率达到80%",
                requiredPoints = 1500,
                requiredAccuracy = 0.8f,
                type = BadgeType.ACHIEVEMENT
            )
            accuracy >= 0.7f -> Badge(
                id = "accuracy_beginner",
                name = "答题能手",
                emoji = "🥉",
                description = "累计答题正确率达到70%",
                requiredPoints = 1000,
                requiredAccuracy = 0.7f,
                type = BadgeType.ACHIEVEMENT
            )
            else -> null
        }
    }

    /**
     * 终极勋章 - 5000积分
     */
    private fun checkGrandMaster(user: User): Badge? {
        return if (user.totalScore >= 5000) {
            Badge(
                id = "grand_master",
                name = "奥数之王",
                emoji = "👑",
                description = "累计获得5000积分",
                requiredPoints = 5000,
                type = BadgeType.SPECIAL
            )
        } else null
    }
}
