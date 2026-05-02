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
 id = "first_quiz",  // 与 ProfileUiState 一致
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
 description = "单日满分完成所有题目",
 requiredPoints = 0,
 requiredAccuracy = 1.0f,
 type = BadgeType.ACHIEVEMENT
 ),
 Badge(
 id = "streak_3",
 name = "三日坚持",
 emoji = "🔥",
 description = "连续 3 天完成每日答题",
 requiredPoints = 0,
 requiredStreak = 3,
 type = BadgeType.STREAK
 ),
 Badge(
 id = "streak_7",
 name = "一周坚持",
 emoji = "🔥",
 description = "连续 7 天完成每日答题",
 requiredPoints = 0,
 requiredStreak = 7,
 type = BadgeType.STREAK
 ),
 Badge(
 id = "streak_30",
 name = "月度之星",
 emoji = "⭐",
 description = "连续 30 天完成每日答题",
 requiredPoints = 0,
 requiredStreak = 30,
 type = BadgeType.STREAK
 ),
 Badge(
 id = "math_wizard",
 name = "数学奇才",
 emoji = "🔢",
 description = "累计答对 50 道题目",
 requiredPoints = 500,
 type = BadgeType.MASTERY
 ),
 Badge(
 id = "master",
 name = "数学大师",
 emoji = "🎓",
 description = "累计获得 1000 积分",
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
