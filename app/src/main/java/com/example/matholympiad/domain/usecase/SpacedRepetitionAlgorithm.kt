package com.example.matholympiad.domain.usecase

import com.example.matholympiad.data.local.model.AnswerHistory
import com.example.matholympiad.data.local.model.Question
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

/**
 * SM-2 间隔重复算法实现
 * 用于错题智能重练系统
 */
class SpacedRepetitionAlgorithm @Inject constructor() {

    companion object {
        // 最小间隔天数
        const val MIN_INTERVAL = 1

        // 最大间隔天数
        const val MAX_INTERVAL = 365

        // 初始容易度因子
        const val DEFAULT_EASE_FACTOR = 2.5

        // 最小容易度因子
        const val MIN_EASE_FACTOR = 1.3
    }

    /**
     * 计算下次复习日期
     * @param history 错题历史记录
     * @param quality 答题质量 (0-5):
     * 0-2: 完全没记住/错误
     * 3: 困难但答对
     * 4: 基本答对
     * 5: 轻松答对
     * @return 更新后的历史记录
     */
    fun calculateNextReview(
        history: AnswerHistory,
        quality: Int
    ): AnswerHistory {
        // 答题质量标准化到 0-5
        val normalizedQuality = quality.coerceIn(0, 5)

        // AnswerHistory uses reviewCount instead of repetitionCount
        val repetitionCount = history.reviewCount
        // AnswerHistory uses masteryLevel / 100 as ease factor approximation
        val previousEaseFactor = (history.masteryLevel / 100.0).coerceAtLeast(0.5) * 5.0
        // AnswerHistory doesn't have intervalDays, use reviewStage as proxy
        val previousInterval = if (history.reviewStage > 0) history.reviewStage else 1

        // 新的容易度因子
        val newEaseFactor = calculateNewEaseFactor(
            previousEaseFactor,
            normalizedQuality
        )

        // 新的间隔天数
        val newInterval = calculateNewInterval(
            repetitionCount,
            previousInterval,
            newEaseFactor,
            normalizedQuality
        )

        // 新的重复次数
        val newRepetitionCount = if (normalizedQuality >= 3) {
            repetitionCount + 1
        } else {
            // 答错或困难，重置次数但保留记录
            0
        }

        // Calculate new review stage (capped at max)
        val newReviewStage = min(newRepetitionCount, AnswerHistory.MAX_REVIEW_STAGE)

        // 计算下次复习时间 (milliseconds from now)
        val nextReviewAt = System.currentTimeMillis() + (newInterval * 24L * 60 * 60 * 1000)

        // Determine if mastered (3+ reviews with good quality)
        val isMastered = newRepetitionCount >= 3 && normalizedQuality >= 4

        return history.copy(
            reviewCount = newRepetitionCount,
            reviewStage = newReviewStage,
            nextReviewAt = nextReviewAt,
            masteryLevel = if (isMastered) 100 else min(history.masteryLevel + (normalizedQuality * 10), 99)
        )
    }

    /**
     * 计算新的容易度因子
     */
    private fun calculateNewEaseFactor(
        currentEaseFactor: Double,
        quality: Int
    ): Double {
        // SM-2算法公式
        // EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
        val newEF = currentEaseFactor + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02))

        // 限制最小值
        return max(newEF, MIN_EASE_FACTOR)
    }

    /**
     * 计算新的间隔天数
     */
    private fun calculateNewInterval(
        repetitionCount: Int,
        previousInterval: Int,
        easeFactor: Double,
        quality: Int
    ): Int {
        return when {
            quality < 3 -> {
                // 答错或困难，重置间隔
                MIN_INTERVAL
            }
            repetitionCount == 0 -> {
                // 第一次答对
                1
            }
            repetitionCount == 1 -> {
                // 第二次答对
                6
            }
            else -> {
                // 之后按公式计算
                val newInterval = (previousInterval * easeFactor).toInt()
                min(newInterval, MAX_INTERVAL)
            }
        }
    }

    /**
     * Get today's review questions from AnswerHistory list
     */
    fun getTodayReviewQuestions(histories: List<AnswerHistory>): List<AnswerHistory> {
        val currentTime = System.currentTimeMillis()
        return histories.filter { history ->
            history.nextReviewAt != null &&
                    history.nextReviewAt <= currentTime &&
                    history.masteryLevel < 100 // Not fully mastered
        }.sortedBy { it.nextReviewAt }
    }

    /**
     * 获取即将到期的错题（未来7天）
     */
    fun getUpcomingReviews(histories: List<AnswerHistory>): List<AnswerHistory> {
        val currentTime = System.currentTimeMillis()
        val nextWeek = currentTime + (7L * 24 * 60 * 60 * 1000)

        return histories.filter { history ->
            history.nextReviewAt != null &&
                    history.nextReviewAt in (currentTime + 1)..nextWeek &&
                    history.masteryLevel < 100
        }
    }

    /**
     * 计算错题掌握度
     */
    fun calculateMasteryLevel(history: AnswerHistory): MasteryLevel {
        return when {
            history.masteryLevel >= 100 -> MasteryLevel.MASTERED
            history.reviewCount >= 2 -> MasteryLevel.NEARLY_MASTERED
            history.reviewCount >= 1 -> MasteryLevel.LEARNING
            else -> MasteryLevel.NEW
        }
    }

    /**
     * 计算记忆强度 (0-100%)
     */
    fun calculateMemoryStrength(history: AnswerHistory): Int {
        if (history.masteryLevel >= 100) return 100

        val baseStrength = when (history.reviewCount) {
            0 -> 0
            1 -> 30
            2 -> 60
            else -> min(90 + (history.reviewCount - 3) * 3, 99)
        }

        // Calculate days since last review using reviewStage as proxy
        val decay = if (history.reviewStage > 0) min(history.reviewStage * 5, 50) else 0
        return max(baseStrength - decay, 0)
    }

    /**
     * 预估完全掌握所需天数
     */
    fun estimateMasteryDays(history: AnswerHistory): Int {
        if (history.masteryLevel >= 100) return 0

        val remainingReviews = max(3 - history.reviewCount, 0)
        var estimatedDays = 0
        var interval = max(history.reviewStage, 1)
        val currentEaseFactor = (history.masteryLevel / 100.0).coerceAtLeast(0.5) * 5.0

        repeat(remainingReviews) {
            estimatedDays += interval
            interval = (interval * currentEaseFactor).toInt()
        }

        return estimatedDays
    }
}

/**
 * 掌握度等级
 */
enum class MasteryLevel(val displayName: String, val color: Int) {
    NEW("新错题", 0xFFFF6B6B.toInt()),
    LEARNING("学习中", 0xFFFFA502.toInt()),
    NEARLY_MASTERED("即将掌握", 0xFF2ED573.toInt()),
    MASTERED("已掌握", 0xFF3742FA.toInt())
}

/**
 * 错题复习计划
 */
data class ReviewSchedule(
    val questionId: String,
    val questionContent: String,
    val nextReviewDate: LocalDate,
    val daysUntilReview: Int,
    val masteryLevel: MasteryLevel,
    val memoryStrength: Int,
    val estimatedMasteryDays: Int
)

/**
 * 复习统计
 */
data class ReviewStatistics(
    val totalWrongAnswers: Int,
    val todayReviews: Int,
    val upcomingReviews: Int,
    val masteredCount: Int,
    val learningCount: Int,
    val averageMemoryStrength: Int,
    val streakDays: Int
)
