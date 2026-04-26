package com.example.matholympiad.presentation.ui.quiz

import com.example.matholympiad.data.local.model.Question

/**
 * 答题界面状态
 */
data class QuizUiState(
    val currentQuestionIndex: Int = 0,
    val totalQuestions: Int = 3,
    val currentQuestion: Question? = null,
    val selectedAnswer: Int? = null,
    val feedbackShowing: Boolean = false,
    val isCorrect: Boolean? = null,
    val explanation: String = "",
    val encouragement: String = "",
    val score: Int = 0
)
