package com.example.matholympiad.domain.usecase

import com.example.matholympiad.data.local.model.AnswerHistory
import com.example.matholympiad.data.repository.WrongAnswerRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.Flow

/**
 * 获取今日待复习错题
 */
class GetTodayReviewQuestions(
    private val wrongAnswerRepository: WrongAnswerRepository
) {
    operator fun invoke(userId: String): Flow<List<AnswerHistory>> {
        return wrongAnswerRepository.getDueReviewQuestions(userId)
    }
}

/**
 * 获取所有错题列表
 */
class GetAllWrongAnswers(
    private val wrongAnswerRepository: WrongAnswerRepository
) {
    operator fun invoke(userId: String): Flow<List<AnswerHistory>> {
        return wrongAnswerRepository.getAllWrongAnswers(userId)
    }
}

/**
 * 获取错题统计
 */
class GetWrongAnswerStats(
    private val wrongAnswerRepository: WrongAnswerRepository
) {
    suspend fun getStats(userId: String): WrongAnswerStats {
        val totalCount = wrongAnswerRepository.getWrongAnswerCount(userId).firstOrNull() ?: 0
        val dueCount = wrongAnswerRepository.getDueReviewCount(userId).firstOrNull() ?: 0
        
        return WrongAnswerStats(
            totalWrongAnswers = totalCount,
            dueForReview = dueCount
        )
    }
}

/**
 * 错题统计数据类
 */
data class WrongAnswerStats(
    val totalWrongAnswers: Int,
    val dueForReview: Int
)

/**
 * 标记题目为已复习
 */
class MarkAsReviewed(
    private val wrongAnswerRepository: WrongAnswerRepository
) {
    suspend operator fun invoke(userId: String, questionId: String, wasCorrect: Boolean) {
        wrongAnswerRepository.markAsReviewed(userId, questionId, wasCorrect)
    }
}

/**
 * 记录答题结果
 */
class RecordAnswerResult(
    private val wrongAnswerRepository: WrongAnswerRepository
) {
    suspend operator fun invoke(
        userId: String,
        questionId: String,
        selectedAnswer: Int,
        isCorrect: Boolean,
        responseTimeMs: Long
    ) {
        wrongAnswerRepository.recordAnswer(
            userId = userId,
            questionId = questionId,
            selectedAnswer = selectedAnswer,
            isCorrect = isCorrect,
            responseTimeMs = responseTimeMs
        )
    }
}

/**
 * 删除错题
 */
class DeleteWrongAnswer(
    private val wrongAnswerRepository: WrongAnswerRepository
) {
    suspend operator fun invoke(userId: String, questionId: String) {
        wrongAnswerRepository.deleteWrongAnswer(userId, questionId)
    }
}
