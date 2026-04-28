package com.example.matholympiad.data.remote.model

/**
 * AI讲解请求
 */
data class AIExplanationRequest(
    val questionId: String,
    val questionContent: String,
    val userAnswer: Int,
    val correctAnswer: Int,
    val difficulty: String,
    val questionType: String = "",
    val tags: List<String> = emptyList()
)

/**
 * AI讲解响应
 */
data class AIExplanationResponse(
    val explanation: String,
    val keyConcepts: List<String>,
    val relatedQuestions: List<String>,
    val recommendations: List<String>,
    val difficultyAnalysis: String
)

/**
 * 知识点请求
 */
data class ConceptRequest(
    val topic: String,
    val subtopic: String?,
    val difficulty: String
)

/**
 * 学习建议请求
 */
data class LearningAdviceRequest(
    val weakTopics: List<String>,
    val recentAccuracy: Float,
    val streakDays: Int
)
