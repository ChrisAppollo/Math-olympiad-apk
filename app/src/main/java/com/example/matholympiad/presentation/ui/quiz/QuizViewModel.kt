package com.example.matholympiad.presentation.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matholympiad.data.local.model.Question
import com.example.matholympiad.data.repository.QuestionRepo
import com.example.matholympiad.data.repository.UserRepo
import com.example.matholympiad.domain.usecase.EncouragementGenerator
import com.example.matholympiad.domain.usecase.SubmitAnswerUseCase
import com.google.gson.Gson
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
    private val encouragementGenerator: EncouragementGenerator,
    private val checkBadges: com.example.matholympiad.domain.usecase.CheckBadges
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState

    private var currentQuestions = listOf<Question>()

    init {
        loadQuizQuestions()
    }

    /**
     * 抓取下题库数据
     * - 有缓存就走缓存
     * - 没有缓存抓取线上的数据
     * - 数据库的缓存题库每天都要更新，实现定时刷新的逻辑
     */
    private fun loadQuizQuestions(retryCount: Int = 3) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(loading = true)
                
                // 重试机制：如果题库为空，等待后重试
                var attempts = 0
                var questions: List<Question>
                
                do {
                    questions = questionRepo.getAllQuestions()
                    if (questions.isNotEmpty()) break
                    attempts++
                    if (attempts < retryCount) {
                        kotlinx.coroutines.delay(300) // 等待300ms让题库初始化
                    }
                } while (attempts < retryCount)
                
                // 如果仍无数据，使用备用题目
                currentQuestions = if (questions.isNotEmpty()) {
                    questions.shuffled().take(3)
                } else {
                    getFallbackQuestions()
                }
                
                _uiState.value = QuizUiState(
                    totalQuestions = currentQuestions.size,
                    currentQuestion = if (currentQuestions.isNotEmpty()) currentQuestions[0] else null,
                    score = 0,
                    loading = false
                )
            } catch (e: Exception) {
                e.printStackTrace()
                // 使用备用题目
                currentQuestions = getFallbackQuestions()
                _uiState.value = QuizUiState(
                    totalQuestions = currentQuestions.size,
                    currentQuestion = if (currentQuestions.isNotEmpty()) currentQuestions[0] else null,
                    score = 0,
                    loading = false
                )
            }
        }
    }
 
    /**
     * 备用题目：当数据库加载失败时使用
     */
    private val gson = Gson()
    
    private fun getFallbackQuestions(): List<Question> {
        return listOf(
            Question(
                id = "fallback_001",
                content = "1+2+3+...+10的和是多少？",
                options = gson.toJson(listOf("55", "50", "45", "60")),
                correctAnswer = 0,
                explanation = "等差数列求和公式：(首项+末项)×项数÷2 = (1+10)×10÷2 = 55",
                type = "ARITHMETIC",
                difficulty = 2
            ),
            Question(
                id = "fallback_002", 
                content = "鸡兔同笼，共有头10个，腿32条，鸡有几只？",
                options = gson.toJson(listOf("4只", "5只", "6只", "3只")),
                correctAnswer = 0,
                explanation = "假设全是兔应有40条腿，多8条。每只兔换鸡少2条腿，鸡=8÷2=4只",
                type = "ARITHMETIC",
                difficulty = 2
            ),
            Question(
                id = "fallback_003",
                content = "找规律：2, 5, 11, 23, 47, ?",
                options = gson.toJson(listOf("95", "96", "93", "94")),
                correctAnswer = 0,
                explanation = "规律：前一个数×2+1。47×2+1=95",
                type = "LOGIC",
                difficulty = 3
            )
        )
    }

    fun selectAnswer(answerIndex: Int) {
        val currentState = _uiState.value
        if (currentState.feedbackShowing) return
        _uiState.value = currentState.copy(selectedAnswer = answerIndex)
    }

    fun showHint() {
        val state = _uiState.value
        val question = state.currentQuestion ?: return
        
        viewModelScope.launch {
            // 提示功能：显示题目解析的提示版本
            val hint = when {
                question.explanation.contains("=") -> {
                    // 提取等式左边作为提示
                    val parts = question.explanation.split("=")
                    if (parts.size > 1) "提示：需要计算 ${parts[0].trim()}" else "提示：仔细读题"
                }
                question.type == "LOGIC" -> "提示：观察规律，找重复模式"
                question.type == "GRAPHIC" -> "提示：注意图形的变化规律"
                else -> "提示：仔细审题，一步步思考"
            }
            
            _uiState.value = state.copy(
                hintShowing = true,
                hintText = hint
            )
        }
    }

    fun dismissHint() {
        val state = _uiState.value
        _uiState.value = state.copy(hintShowing = false)
    }

    fun submitAnswer() {
        val state = _uiState.value
        val question = state.currentQuestion ?: return
        val selectedAnswer = state.selectedAnswer ?: return

        viewModelScope.launch {
            try {
                val isCorrect = selectedAnswer == question.correctAnswer
                val pointsEarned = if (isCorrect) 10 else 0
                val answerTime = System.currentTimeMillis() - state.questionStartTime

                submitAnswerUseCase(
                    questionId = question.id,
                    selectedAnswer = selectedAnswer,
                    isCorrect = isCorrect
                )
                
                // 检查新解锁的勋章
                val newlyUnlocked = checkBadges()

                _uiState.value = state.copy(
                    feedbackShowing = true,
                    isCorrect = isCorrect,
                    explanation = question.explanation,
                    hintShowing = false,
                    showAnimation = true,
                    encouragement = if (isCorrect) {
                        encouragementGenerator.getCorrectEncouragement()
                    } else {
                        "别灰心，正确答案是：${question.getOptionsList()[question.correctAnswer]}"
                    },
                    score = state.score + pointsEarned,
                    earnedBadges = newlyUnlocked.map { it.id },
                    lastAnswerTime = answerTime,
                    totalCorrectAnswers = if (isCorrect) state.totalCorrectAnswers + 1 else state.totalCorrectAnswers,
                    currentStreak = if (isCorrect) state.currentStreak + 1 else 0
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
                encouragement = "",
                hintShowing = false,
                hintText = "",
                showAnimation = false,
                questionStartTime = System.currentTimeMillis()
            )
        } else {
            // Quiz completed - show completion screen
            _uiState.value = currentState.copy(
                currentQuestion = null,
                feedbackShowing = true,
                isCorrect = true, // Mark as completion
                encouragement = "🎉 今日闯关完成！\n获得 ${currentState.score} 积分\n正确率: ${(currentState.totalCorrectAnswers * 100 / 3)}%",
                quizCompleted = true
            )
        }
    }

    fun completeQuiz() {
        // Reset for next quiz
        viewModelScope.launch {
            loadQuizQuestions()
        }
    }

    fun resetQuiz() {
        viewModelScope.launch {
            _uiState.value = QuizUiState()
            loadQuizQuestions()
        }
    }
}
