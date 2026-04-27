package com.example.matholympiad.presentation.ui.wronganswers

import com.example.matholympiad.data.local.model.Question

/**
 * 错题本界面状态
 */
data class WrongAnswersUiState(
    val wrongQuestions: List<WrongAnswerItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedQuestion: WrongAnswerItem? = null,
    val showDetailDialog: Boolean = false
)

/**
 * 错题条目（带答题记录信息）
 */
data class WrongAnswerItem(
    val question: Question,
    val userAnswer: String,
    val wrongTime: Long,
    val wrongCount: Int = 1
) {
    val correctAnswerText: String
        get() = question.getOptionsList()[question.correctAnswer]
}
