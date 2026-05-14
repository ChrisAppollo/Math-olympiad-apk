package com.example.matholympiad.presentation.ui.profile

import com.example.matholympiad.data.local.model.User

/**
 * 个人资料界面状态
 */
data class ProfileUiState(
    val loading: Boolean = false,
    val user: User? = null,
    val totalScore: Int = 0,
    val badgesCount: Int = 0,
    val streakCount: Int = 0,
    val badges: List<Badge> = emptyList(),
    val showBadgeDetail: Badge? = null,
    val answeredQuestionsCount: Int = 0,
    val correctRate: Float = 0f
)

/**
 * 勋章数据模型（UI 展示用）
 */
data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val emoji: String,
    val isUnlocked: Boolean = false,
    val unlockDate: String? = null,
    val unlockCondition: String = ""  // 解锁条件描述（如"连续3天打卡"而非"需要300积分"）
)

/**
 * 预定义勋章列表 — 与数据层 Badges 保持一致
 */
object Badges {
    fun getAllBadges(): List<Badge> = listOf(
        Badge(
            id = "first_quiz",
            name = "初次尝试",
            description = "完成第一次答题",
            emoji = "🎯",
            unlockCondition = "完成1次答题即可解锁"
        ),
        Badge(
            id = "perfect_score",
            name = "满分高手",
            description = "单日全部答对",
            emoji = "💯",
            unlockCondition = "当日所有题目全部答对"
        ),
        Badge(
            id = "streak_3",
            name = "三日坚持",
            description = "连续3天完成每日答题",
            emoji = "🔥",
            unlockCondition = "连续3天打卡"
        ),
        Badge(
            id = "streak_7",
            name = "一周坚持",
            description = "连续7天完成每日答题",
            emoji = "👑",
            unlockCondition = "连续7天打卡"
        ),
        Badge(
            id = "streak_30",
            name = "月度之星",
            description = "连续30天完成每日答题",
            emoji = "⭐",
            unlockCondition = "连续30天打卡"
        ),
        Badge(
            id = "review_master",
            name = "复习达人",
            description = "当天完成错题复习",
            emoji = "📖",
            unlockCondition = "当天完成至少1次错题复习"
        ),
        Badge(
            id = "math_wizard",
            name = "数学奇才",
            description = "累计答对50道题目",
            emoji = "🧮",
            unlockCondition = "累计答对50题"
        ),
        Badge(
            id = "master",
            name = "数学大师",
            description = "累计获得1000积分",
            emoji = "🎓",
            unlockCondition = "累计获得1000积分"
        )
    )
}
