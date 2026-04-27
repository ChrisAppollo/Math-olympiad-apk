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
 * 勋章数据模型
 */
data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val emoji: String,
    val isUnlocked: Boolean = false,
    val unlockDate: String? = null,
    val requiredPoints: Int = 0
)

/**
 * 预定义勋章列表
 */
object Badges {
    fun getAllBadges(): List<Badge> = listOf(
        Badge(
            id = "first_quiz",
            name = "初次尝试",
            description = "完成第一次答题",
            emoji = "🎯",
            requiredPoints = 0
        ),
        Badge(
            id = "perfect_score",
            name = "满分大师",
            description = "单次全部答对",
            emoji = "💯",
            requiredPoints = 100
        ),
        Badge(
            id = "math_wizard",
            name = "数学天才",
            description = "累计答对50题",
            emoji = "🧮",
            requiredPoints = 500
        ),
        Badge(
            id = "streak_3",
            name = "三日连胜",
            description = "连续三天打卡",
            emoji = "🔥",
            requiredPoints = 300
        ),
        Badge(
            id = "streak_7",
            name = "一周王者",
            description = "连续七天打卡",
            emoji = "👑",
            requiredPoints = 700
        ),
        Badge(
            id = "collector",
            name = "收集达人",
            description = "解锁5个勋章",
            emoji = "🏆",
            requiredPoints = 500
        ),
        Badge(
            id = "quick_solver",
            name = "闪电答题",
            description = "快速答题5题",
            emoji = "⚡",
            requiredPoints = 200
        ),
        Badge(
            id = "master",
            name = "数学大师",
            description = "累计获得1000积分",
            emoji = "🎓",
            requiredPoints = 1000
        )
    )
}
