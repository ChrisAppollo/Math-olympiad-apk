package com.example.matholympiad.presentation.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matholympiad.data.local.model.Question
import com.example.matholympiad.data.repository.UserRepo
import com.example.matholympiad.domain.usecase.EncouragementGenerator
import com.example.matholympiad.domain.usecase.SubmitAnswerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

class QuizViewModel(
    private val userRepo: UserRepo,
    private val submitAnswerUseCase: SubmitAnswerUseCase,
    private val encouragementGenerator: EncouragementGenerator
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState
    
    var currentQuestions = listOf<Question>()
        private set
    
    fun initializeQuiz(questions: List<Question>) {
        currentQuestions = questions
        _uiState.value = QuizUiState(
            totalQuestions = 3,
            currentQuestion = if (questions.isNotEmpty()) questions[0] else null,
            score = 0
        )
    }
    
    fun selectAnswer(answerIndex: Int) {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(selectedAnswer = answerIndex)
    }
    
    fun submitAnswer() {
        val state = _uiState.value
        val question = state.currentQuestion ?: return
        val selectedAnswer = state.selectedAnswer ?: return
        
        viewModelScope.launch {
            val isCorrect = selectedAnswer == question.correctAnswer
            val pointsEarned = if (isCorrect) 1 else 0
            
            submitAnswerUseCase(
                questionId = question.id,
                selectedAnswer = selectedAnswer,
                isCorrect = isCorrect
            ).onSuccess { (points, answerRecord) ->
                _uiState.value = state.copy(
                    feedbackShowing = true,
                    isCorrect = isCorrect,
                    explanation = question.explanation,
                    encouragement = if (isCorrect) {
                        encouragementGenerator.getCorrectEncouragement()
                    } else {
                        "别灰心，这道题解析：${question.explanation}"
                    },
                    score = state.score + points
                )
            }
        }
    }
    
    fun nextQuestion() {
        val currentState = _uiState.value
        if (currentState.currentQuestionIndex < currentQuestions.size - 1) {
            _uiState.value = currentState.copy(
                currentQuestionIndex = currentState.currentQuestionIndex + 1,
                currentQuestion = currentQuestions[currentState.currentQuestionIndex + 1],
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
                encouragement = "今日闯关完成！积分已保存~"
            )
        }
    }
}
