package com.example.matholympiad.data.remote.service

import com.example.matholympiad.data.remote.model.AIExplanationRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * AI服务接口
 */
interface AIService {
    /**
     * 获取流式讲解
     */
    suspend fun getStreamingExplanation(request: AIExplanationRequest): Flow<String>
    
    /**
     * 获取知识点讲解
     */
    suspend fun getConceptExplanation(
        topic: String,
        subtopic: String?,
        difficulty: String
    ): String
    
    /**
     * 获取个性化建议
     */
    suspend fun getPersonalizedAdvice(
        weakTopics: List<String>,
        recentAccuracy: Float,
        streakDays: Int
    ): String
}
