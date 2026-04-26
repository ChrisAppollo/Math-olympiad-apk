package com.example.matholympiad.presentation.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matholympiad.data.local.model.Question
import com.example.matholympiad.data.repository.QuestionRepo
import com.example.matholympiad.data.repository.UserRepo
import com.example.matholympiad.domain.usecase.EncouragementGenerator
import com.example.matholympiad.domain.usecase.SubmitAnswerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val questionRepo: QuestionRepo,
    private val submitAnswerUseCase: SubmitAnswerUseCase,
    private val encouragementGenerator: EncouragementGenerator
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState

    private var currentQuestions = listOf<Question>()

    init {
        loadQuizQuestions()
    }

    private fun loadQuizQuestions() {
        viewModelScope.launch {
            try {
                currentQuestions = questionRepo.getAllQuestions().shuffled().take(3)
                _uiState.value = QuizUiState(
                    totalQuestions = currentQuestions.size,
                    currentQuestion = if (currentQuestions.isNotEmpty()) currentQuestions[0] else null,
                    score = 0
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = QuizUiState(
                    totalQuestions = 0,
                    encouragement = "加载题目失败，请重试"
                )
            }
        }
    }

    fun selectAnswer(answerIndex: Int) {
        val currentState = _uiState.value
        if (currentState.feedbackShowing) return
        _uiState.value = currentState.copy(selectedAnswer = answerIndex)
    }

    fun submitAnswer() {
        val state = _uiState.value
        val question = state.currentQuestion ?: return
        val selectedAnswer = state.selectedAnswer ?: return

        viewModelScope.launch {
            try {
                val isCorrect = selectedAnswer == question.correctAnswer
                val pointsEarned = if (isCorrect) 10 else 0

                submitAnswerUseCase(
                    questionId = question.id,
                    selectedAnswer = selectedAnswer,
                    isCorrect = isCorrect
                )

                _uiState.value = state.copy(
                    feedbackShowing = true,
                    isCorrect = isCorrect,
                    explanation = question.explanation,
                    encouragement = if (isCorrect) {
                        encouragementGenerator.getCorrectEncouragement()
                    } else {
                        "别灰心，这道题解析：${question.explanation}"
                    },
                    score = state.score + pointsEarned
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = state.copy(
                    feedbackShowing = true,
                    encouragement = "提交出错，请重试"
                )
            }
        }
    }

    fun nextQuestion() {
        val currentState = _uiState.value
        val currentIndex = currentState.currentQuestionIndex

        if (currentIndex < currentQuestions.size - 1) {
            _uiState.value = currentState.copy(
                currentQuestionIndex = currentIndex + 1,
                currentQuestion = currentQuestions.getOrNull(currentIndex + 1),
                selectedAnswer = null,
                feedbackShowing = false,
                isCorrect = null,
                explanation = "",
                encouragement = ""
            )
        } else {
            // Quiz completed
            _uiState.value = currentState.copy(
                currentQuestion = null,
                feedbackShowing = true,
                encouragement = "今日闯关完成！获得 ${currentState.score} 积分~"
            )
        }
    }
}
