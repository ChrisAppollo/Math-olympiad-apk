package com.example.matholympiad.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 勋章实体类 - Room 数据库版本
 */
@Entity(tableName = "badges")
data class Badge(
    @PrimaryKey val id: String,
    val name: String,
    val emoji: String,
    val description: String,
    val requiredPoints: Int,
    val requiredStreak: Int = 0, // 连续答对天数要求
    val requiredAccuracy: Float = 0f, // 正确率要求（如0.8表示80%）
    val type: BadgeType,
    var isUnlocked: Boolean = false
) {
    companion object {
        // 预定义勋章ID
        const val FIRST_STEPS = "first_steps"
        const val QUICK_LEARNER = "quick_learner"
        const val PERFECT_SCORE = "perfect_score"
        const val DAILY_STREAK_7 = "daily_streak_7"
        const val DAILY_STREAK_30 = "daily_streak_30"
        const val MASTER_ARITHMETIC = "master_arithmetic"
        const val MASTER_LOGIC = "master_logic"
        const val SPEED_DEMON = "speed_demon"
        const val CHALLENGE_ACCEPTED = "challenge_accepted"
        const val GRAND_MASTER = "grand_master"
    }
}

enum class BadgeType {
    BEGINNER, // 新手勋章
    ACHIEVEMENT, // 成就勋章
    STREAK, // 连续勋章
    MASTERY, // 精通勋章
    SPECIAL // 特殊勋章
}

/**
 * 所有预定义勋章配置
 */
object Badges {

    fun getAllBadges(): List<Badge> = listOf(
        Badge(
            id = Badge.FIRST_STEPS,
            name = "初次尝试",
            emoji = "🌱",
            description = "完成第一次答题",
            requiredPoints = 0,
            type = BadgeType.BEGINNER
        ),
        Badge(
            id = Badge.QUICK_LEARNER,
            name = "小学者",
            emoji = "📚",
            description = "累计获得100积分",
            requiredPoints = 100,
            type = BadgeType.ACHIEVEMENT
        ),
        Badge(
            id = Badge.PERFECT_SCORE,
            name = "满分高手",
            emoji = "💯",
            description = "单日满分完成所有题目",
            requiredPoints = 0,
            requiredAccuracy = 1.0f,
            type = BadgeType.ACHIEVEMENT
        ),
        Badge(
            id = Badge.DAILY_STREAK_7,
            name = "坚持一周",
            emoji = "🔥",
            description = "连续7天完成每日答题",
            requiredPoints = 0,
            requiredStreak = 7,
            type = BadgeType.STREAK
        ),
        Badge(
            id = Badge.DAILY_STREAK_30,
            name = "月度之星",
            emoji = "⭐",
            description = "连续30天完成每日答题",
            requiredPoints = 0,
            requiredStreak = 30,
            type = BadgeType.STREAK
        ),
        Badge(
            id = Badge.MASTER_ARITHMETIC,
            name = "算术大师",
            emoji = "🔢",
            description = "累计答对50道计算题",
            requiredPoints = 500,
            type = BadgeType.MASTERY
        ),
        Badge(
            id = Badge.MASTER_LOGIC,
            name = "逻辑天才",
            emoji = "🧩",
            description = "累计答对30道逻辑题",
            requiredPoints = 300,
            type = BadgeType.MASTERY
        ),
        Badge(
            id = Badge.SPEED_DEMON,
            name = "答题闪电侠",
            emoji = "⚡",
            description = "单题用时少于10秒",
            requiredPoints = 0,
            type = BadgeType.SPECIAL
        ),
        Badge(
            id = Badge.CHALLENGE_ACCEPTED,
            name = "挑战者",
            emoji = "🏆",
            description = "累计挑战100道题目",
            requiredPoints = 1000,
            type = BadgeType.SPECIAL
        ),
        Badge(
            id = Badge.GRAND_MASTER,
            name = "奥数大师",
            emoji = "👑",
            description = "累计获得5000积分",
            requiredPoints = 5000,
            type = BadgeType.SPECIAL
        )
    )

    /**
     * 根据ID获取勋章配置
     */
    fun getBadgeById(id: String): Badge? {
        return getAllBadges().find { it.id == id }
    }
}
