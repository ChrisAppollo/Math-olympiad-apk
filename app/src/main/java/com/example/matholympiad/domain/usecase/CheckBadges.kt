package com.example.matholympiad.domain.usecase

import com.example.matholympiad.data.local.model.Badge
import com.example.matholympiad.data.local.model.BadgeType
import com.example.matholympiad.data.local.model.User
import com.example.matholympiad.data.repository.UserRepo
import javax.inject.Inject

/**
 * 检查并解锁勋章
 * 在每次答题完成后或复习完成后调用
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

        // 逐个检查所有勋章（不再使用 when 短路）
        val allBadgeChecks = listOfNotNull(
            checkFirstQuiz(currentUser),
            checkPerfectScore(currentUser),
            checkStreak3(currentUser),
            checkStreak7(currentUser),
            checkStreak30(currentUser),
            checkMathWizard(currentUser),
            checkMaster(currentUser)
        )

        // 收集新解锁的勋章
        allBadgeChecks.forEach { badge ->
            if (badge.id !in currentBadges) {
                newlyUnlocked.add(badge)
                // 更新用户勋章列表
                userRepo.addBadges(listOf(badge.id))
            }
        }

        return newlyUnlocked
    }

    /**
     * 检查复习达人勋章 — 单独调用，在复习完成时触发
     */
    suspend fun checkReviewBadge(): Badge? {
        val currentUser = userRepo.getDefaultUser()
        val currentBadges = currentUser.getBadgesList().toSet()

        if ("review_master" !in currentBadges) {
            val badge = Badge(
                id = "review_master",
                name = "复习达人",
                emoji = "📖",
                description = "当天完成错题复习",
                requiredPoints = 0,
                type = BadgeType.REVIEW,
                isUnlocked = true
            )
            userRepo.addBadges(listOf("review_master"))
            return badge
        }
        return null
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
     * 满分勋章 — 100% 正确率
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
                description = "单日全部答对",
                requiredPoints = 0,
                requiredAccuracy = 1.0f,
                type = BadgeType.ACHIEVEMENT,
                isUnlocked = true
            )
        } else null
    }

    /**
     * 连续3天勋章 — 独立检查，不再与 streak_7/streak_30 短路
     */
    private fun checkStreak3(user: User): Badge? {
        return if (user.streakCount >= 3) {
            Badge(
                id = "streak_3",
                name = "三日坚持",
                emoji = "🔥",
                description = "连续3天完成每日答题",
                requiredPoints = 0,
                requiredStreak = 3,
                type = BadgeType.STREAK,
                isUnlocked = true
            )
        } else null
    }

    /**
     * 连续7天勋章
     */
    private fun checkStreak7(user: User): Badge? {
        return if (user.streakCount >= 7) {
            Badge(
                id = "streak_7",
                name = "一周坚持",
                emoji = "👑",
                description = "连续7天完成每日答题",
                requiredPoints = 0,
                requiredStreak = 7,
                type = BadgeType.STREAK,
                isUnlocked = true
            )
        } else null
    }

    /**
     * 连续30天勋章
     */
    private fun checkStreak30(user: User): Badge? {
        return if (user.streakCount >= 30) {
            Badge(
                id = "streak_30",
                name = "月度之星",
                emoji = "⭐",
                description = "连续30天完成每日答题",
                requiredPoints = 0,
                requiredStreak = 30,
                type = BadgeType.STREAK,
                isUnlocked = true
            )
        } else null
    }

    /**
     * 精通勋章 — 累计答对 50 题
     */
    private fun checkMathWizard(user: User): Badge? {
        return if (user.totalCorrect >= 50) {
            Badge(
                id = "math_wizard",
                name = "数学奇才",
                emoji = "🧮",
                description = "累计答对50道题目",
                requiredPoints = 0,
                type = BadgeType.MASTERY,
                isUnlocked = true
            )
        } else null
    }

    /**
     * 大师勋章 — 1000 积分
     */
    private fun checkMaster(user: User): Badge? {
        return if (user.totalScore >= 1000) {
            Badge(
                id = "master",
                name = "数学大师",
                emoji = "🎓",
                description = "累计获得1000积分",
                requiredPoints = 1000,
                type = BadgeType.SPECIAL,
                isUnlocked = true
            )
        } else null
    }
}
