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
)

enum class BadgeType {
    BEGINNER,   // 新手勋章
    ACHIEVEMENT, // 成就勋章
    STREAK,     // 连续勋章
    MASTERY,    // 精通勋章
    REVIEW,     // 复习勋章
    SPECIAL     // 特殊勋章
}

/**
 * 所有预定义勋章配置 — 与 UI 层 Badges 保持一致
 */
object Badges {

    fun getAllBadges(): List<Badge> = listOf(
        Badge(
            id = "first_quiz",
            name = "初次尝试",
            emoji = "🎯",
            description = "完成第一次答题",
            requiredPoints = 0,
            type = BadgeType.BEGINNER
        ),
        Badge(
            id = "perfect_score",
            name = "满分高手",
            emoji = "💯",
            description = "单日全部答对",
            requiredPoints = 0,
            requiredAccuracy = 1.0f,
            type = BadgeType.ACHIEVEMENT
        ),
        Badge(
            id = "streak_3",
            name = "三日坚持",
            emoji = "🔥",
            description = "连续3天完成每日答题",
            requiredPoints = 0,
            requiredStreak = 3,
            type = BadgeType.STREAK
        ),
        Badge(
            id = "streak_7",
            name = "一周坚持",
            emoji = "👑",
            description = "连续7天完成每日答题",
            requiredPoints = 0,
            requiredStreak = 7,
            type = BadgeType.STREAK
        ),
        Badge(
            id = "streak_30",
            name = "月度之星",
            emoji = "⭐",
            description = "连续30天完成每日答题",
            requiredPoints = 0,
            requiredStreak = 30,
            type = BadgeType.STREAK
        ),
        Badge(
            id = "review_master",
            name = "复习达人",
            emoji = "📖",
            description = "当天完成错题复习",
            requiredPoints = 0,
            type = BadgeType.REVIEW
        ),
        Badge(
            id = "math_wizard",
            name = "数学奇才",
            emoji = "🧮",
            description = "累计答对50道题目",
            requiredPoints = 0,
            type = BadgeType.MASTERY
        ),
        Badge(
            id = "master",
            name = "数学大师",
            emoji = "🎓",
            description = "累计获得1000积分",
            requiredPoints = 1000,
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
