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
    val score: Int = 0,
    val hintShowing: Boolean = false,
    val hintText: String = "",
    val quizCompleted: Boolean = false,
    val showAnimation: Boolean = false,
    val earnedBadges: List<String> = emptyList(),
    val loading: Boolean = false,
    // Stats tracking
    val questionStartTime: Long = System.currentTimeMillis(),
    val lastAnswerTime: Long = 0L,
    val totalCorrectAnswers: Int = 0,
    val currentStreak: Int = 0
)
