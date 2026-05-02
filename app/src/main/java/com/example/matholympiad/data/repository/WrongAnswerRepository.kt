package com.example.matholympiad.data.repository

import com.example.matholympiad.data.local.dao.WrongAnswerDao
import com.example.matholympiad.data.local.model.AnswerHistory
import com.example.matholympiad.data.local.model.Question
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.Flow

/**
 * 错题本 Repository - 管理错题和艾宾浩斯复习逻辑
 */
class WrongAnswerRepository(private val wrongAnswerDao: WrongAnswerDao) {
    
    /**
     * 获取所有错题
     */
    fun getAllWrongAnswers(userId: String): Flow<List<AnswerHistory>> {
        return wrongAnswerDao.getAllWrongAnswers(userId)
    }
    
    /**
     * 获取今天需要复习的错题
     */
    fun getDueReviewQuestions(userId: String): Flow<List<AnswerHistory>> {
        return wrongAnswerDao.getDueReviewQuestions(userId)
    }
    
    /**
     * 获取特定题目的答题历史
     */
    fun getQuestionHistory(userId: String, questionId: String): Flow<List<AnswerHistory>> {
        return wrongAnswerDao.getQuestionHistory(userId, questionId)
    }
    
    /**
     * 统计错题数量
     */
    fun getWrongAnswerCount(userId: String): Flow<Int> {
        return wrongAnswerDao.getWrongAnswerCount(userId)
    }
    
    /**
     * 统计待复习数量
     */
    fun getDueReviewCount(userId: String): Flow<Int> {
        return wrongAnswerDao.getDueReviewCount(userId)
    }
    
    /**
     * 记录答题结果
     */
    suspend fun recordAnswer(
        userId: String,
        questionId: String,
        selectedAnswer: Int,
        isCorrect: Boolean,
        responseTimeMs: Long
    ): Long {
        val history = AnswerHistory(
            userId = userId,
            questionId = questionId,
            selectedAnswer = selectedAnswer,
            isCorrect = isCorrect,
            answeredAt = System.currentTimeMillis(),
            responseTimeMs = responseTimeMs,
            reviewStage = if (isCorrect) 0 else 1,
            nextReviewAt = if (isCorrect) null else calculateNextReviewTime(0),
            masteryLevel = if (isCorrect) 100 else 0,
            reviewCount = 0
        )
        return wrongAnswerDao.insertAnswerHistory(history)
    }
    
/**
 * 更新复习状态（艾宾浩斯算法核心）
 */
suspend fun markAsReviewed(userId: String, questionId: String, wasCorrect: Boolean) {
    val histories = wrongAnswerDao.getQuestionHistory(userId, questionId).firstOrNull()
    val currentHistory = histories?.firstOrNull() ?: return
    
    val newStage = if (wasCorrect) {
        // 答对：提升阶段
        minOf(currentHistory.reviewStage + 1, AnswerHistory.MAX_REVIEW_STAGE)
    } else {
        // 答错：重置到初始阶段
        1
    }
    
    val newMasteryLevel = calculateMasteryLevel(newStage, wasCorrect)
    val newNextReviewAt = if (newStage < AnswerHistory.MAX_REVIEW_STAGE) {
        calculateNextReviewTime(newStage)
    } else {
        null // 已完成所有复习阶段
    }
    
    wrongAnswerDao.updateReviewStatus(
        userId = userId,
        questionId = questionId,
        newStage = newStage,
        newNextReviewAt = newNextReviewAt,
            newMasteryLevel = newMasteryLevel
        )
    }
    
    /**
     * 计算下次复习时间（艾宾浩斯间隔重复）
     */
    private fun calculateNextReviewTime(stage: Int): Long {
        val days = when (stage) {
            0 -> 1  // 初次错题：1天后复习
            1 -> 2  // 首次复习：2天后
            2 -> 4  // 二次复习：4天后
            3 -> 7  // 三次复习：7天后
            4 -> 15 // 四次复习：15天后
            else -> 30 // 巩固阶段：30天后
        }
        return System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000L)
    }
    
    /**
     * 计算掌握程度（0-100）
     */
    private fun calculateMasteryLevel(stage: Int, wasCorrect: Boolean): Int {
        if (!wasCorrect) return 0
        
        // 基于复习阶段计算掌握度
        val baseMastery = when (stage) {
            0 -> 20
            1 -> 40
            2 -> 60
            3 -> 80
            4 -> 95
            else -> 100
        }
        
        // 根据复习次数微调
        val adjustment = (stage * 2).coerceAtMost(10)
        return (baseMastery + adjustment).coerceIn(0, 100)
    }
    
    /**
     * 删除错题
     */
    suspend fun deleteWrongAnswer(userId: String, questionId: String) {
        wrongAnswerDao.deleteWrongAnswer(userId, questionId)
    }
}
