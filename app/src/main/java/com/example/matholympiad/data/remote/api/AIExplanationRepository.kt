package com.example.matholympiad.data.remote.api

import com.example.matholympiad.data.remote.model.AIExplanationRequest
import com.example.matholympiad.data.remote.model.AIExplanationResponse
import com.example.matholympiad.data.remote.service.AIService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AI讲题功能仓库
 */
@Singleton
class AIExplanationRepository @Inject constructor(
    private val aiService: AIService
) {
    /**
     * 获取AI讲解（流式）
     */
    fun getStreamingExplanation(
        questionId: String,
        questionContent: String,
        userAnswer: Int,
        correctAnswer: Int,
        difficulty: String
    ): Flow<String> = flow {
        try {
            val request = AIExplanationRequest(
                questionId = questionId,
                questionContent = questionContent,
                userAnswer = userAnswer,
                correctAnswer = correctAnswer,
                difficulty = difficulty
            )
            
            aiService.getStreamingExplanation(request).collect { chunk ->
                emit(chunk)
            }
        } catch (e: Exception) {
            emit("抱歉，AI讲解服务暂时不可用。让我为你解析这道题：\n\n")
            emit(generateFallbackExplanation(questionContent, correctAnswer, userAnswer))
        }
    }
    
    /**
     * 获取AI知识点讲解
     */
    suspend fun getConceptExplanation(
        topic: String,
        subtopic: String?,
        difficulty: String
    ): Result<String> {
        return try {
            val result = aiService.getConceptExplanation(topic, subtopic, difficulty)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取个性化学习建议
     */
    suspend fun getPersonalizedAdvice(
        weakTopics: List<String>,
        recentAccuracy: Float,
        streakDays: Int
    ): Result<String> {
        return try {
            val result = aiService.getPersonalizedAdvice(weakTopics, recentAccuracy, streakDays)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 生成备用讲解（当AI服务不可用时）
     */
    private fun generateFallbackExplanation(
        questionContent: String,
        correctAnswer: Int,
        userAnswer: Int
    ): String {
        return buildString {
            append("【题目分析】\n")
            append("这道题考查的是对相关知识点的理解和应用。")
            if (userAnswer != correctAnswer) {
                append("\n\n你选择的是选项${'A' + userAnswer}，但正确答案是选项${'A' + correctAnswer}。")
            }
            append("\n\n【正确答案】选项${'A' + correctAnswer}\n")
            append("\n【解析思路】\n")
            append("1. 首先仔细阅读题目，理解题意\n")
            append("2. 分析每个选项的含义\n")
            append("3. 运用相关知识点进行推理\n")
            append("4. 得出正确结论\n")
            append("\n【相关知识点】\n")
            append("这道题目涉及的基础概念很重要，建议你：\n")
            append("• 回顾相关的基础知识\n")
            append("• 多做类似的练习题\n")
            append("• 查阅详细的解题步骤\n")
        }
    }
}
