package com.example.matholympiad.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matholympiad.data.local.model.Question
import com.example.matholympiad.data.repository.QuestionRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizUiState(
    val currentQuestion: Question? = null,
    val currentQuestionIndex: Int = 0,
    val totalQuestions: Int = 0,
    val score: Int = 0,
    val feedbackShowing: Boolean = false,
    val isCorrect: Boolean? = null,
    val hintShowing: Boolean = false,
    val hintText: String = "",
    val explanation: String = "",
    val encouragement: String = "",
    val quizCompleted: Boolean = false
)

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val questionRepo: QuestionRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    // 用户输入的答案
    var userAnswer by mutableStateOf("")
        private set

    private var questions: List<Question> = emptyList()
    private var correctStreak: Int = 0

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            questions = questionRepo.getAllQuestions()
            if (questions.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        currentQuestion = questions[0],
                        currentQuestionIndex = 0,
                        totalQuestions = questions.size
                    )
                }
            }
        }
    }

    fun onAnswerChanged(answer: String) {
        userAnswer = answer
    }

    fun onSubmitClick() {
        val currentQuestion = _uiState.value.currentQuestion ?: return
        if (userAnswer.isBlank()) return

        val isAnswerCorrect = currentQuestion.checkAnswer(userAnswer)
        
        _uiState.update { state ->
            state.copy(
                feedbackShowing = true,
                isCorrect = isAnswerCorrect,
                explanation = currentQuestion.explanation,
                hintShowing = true, // 提交后自动显示提示
                hintText = currentQuestion.hint
            )
        }

        if (isAnswerCorrect) {
            correctStreak++
            val points = calculatePoints(currentQuestion.difficulty, correctStreak)
            _uiState.update { it.copy(score = it.score + points) }
        } else {
            correctStreak = 0
        }
    }

    fun onNextClick() {
        val currentIndex = _uiState.value.currentQuestionIndex
        
        if (currentIndex >= questions.size - 1) {
            // 答题完成
            _uiState.update {
                it.copy(
                    quizCompleted = true,
                    feedbackShowing = true,
                    isCorrect = true
                )
            }
        } else {
            // 下一题
            val nextIndex = currentIndex + 1
            _uiState.update {
                it.copy(
                    currentQuestion = questions[nextIndex],
                    currentQuestionIndex = nextIndex,
                    feedbackShowing = false,
                    isCorrect = null,
                    hintShowing = false,
                    hintText = "",
                    explanation = "",
                    encouragement = ""
                )
            }
            userAnswer = ""
        }
    }

    fun onHintClick() {
        val currentQuestion = _uiState.value.currentQuestion ?: return
        
        _uiState.update {
            it.copy(
                hintShowing = true,
                hintText = currentQuestion.hint
            )
        }
    }

    fun resetQuiz() {
        correctStreak = 0
        userAnswer = ""
        if (questions.isNotEmpty()) {
            _uiState.update {
                QuizUiState(
                    currentQuestion = questions[0],
                    currentQuestionIndex = 0,
                    totalQuestions = questions.size
                )
            }
        }
    }

    private fun calculatePoints(difficulty: Int, streak: Int): Int {
        val basePoints = difficulty * 10
        val streakBonus = if (streak >= 3) (streak - 2) * 5 else 0
        return basePoints + streakBonus
    }
}
