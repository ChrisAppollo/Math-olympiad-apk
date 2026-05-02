package com.example.matholympiad.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matholympiad.data.local.model.Question
import com.example.matholympiad.data.repository.QuestionRepo
import com.example.matholympiad.data.repository.UserRepo
import com.example.matholympiad.domain.usecase.CheckBadges
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
 val lastQuestionPoints: Int = 0, // 记录上一题获得的积分
 val feedbackShowing: Boolean = false,
 val isCorrect: Boolean? = null,
 val hintShowing: Boolean = false,
 val hintText: String = "",
 val explanation: String = "",
 val encouragement: String = "",
 val quizCompleted: Boolean = false,
 val savedProgress: Boolean = false // 新增：已保存进度状态
)

@HiltViewModel
class QuizViewModel @Inject constructor(
 private val questionRepo: QuestionRepo,
 private val userRepo: UserRepo,
 private val checkBadges: CheckBadges
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
 questions = questionRepo.getTodayQuestions() // 只加载3道今日题目
 if (questions.isNotEmpty()) {
 _uiState.update {
 it.copy(
 currentQuestion = questions[0],
 currentQuestionIndex = 0,
 totalQuestions = questions.size // 这里会显示3
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
 
 // 计算并获得本题的积分
 val earnedPoints = if (isAnswerCorrect) {
 correctStreak++
 calculatePoints(currentQuestion.difficulty, correctStreak)
 } else {
 correctStreak = 0
 0
 }
 
 // 保存积分到用户资料
 viewModelScope.launch {
 userRepo.addPoints(earnedPoints)
 userRepo.updateTodayCompletedCount(_uiState.value.currentQuestionIndex + 1)
 // 更新答题统计（用于正确率计算）
 userRepo.updateUserStats(UserRepo.DEFAULT_USER_ID, isAnswerCorrect)
 
 // 检查并解锁新勋章
 val user = userRepo.getDefaultUser()
 val newBadges = checkBadges(user)
 if (newBadges.isNotEmpty()) {
 // TODO: 可以显示勋章解锁提示
 }
 }
 
 _uiState.update { state ->
 state.copy(
 feedbackShowing = true,
 isCorrect = isAnswerCorrect,
 score = state.score + earnedPoints,
 lastQuestionPoints = earnedPoints, // 记录本题的积分
 explanation = currentQuestion.explanation,
 hintShowing = true,
 hintText = currentQuestion.hint
 )
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

 fun onEarlyFinishClick() {
 // 保存当前答题进度（已完成的题目数）
 val completedCount = _uiState.value.currentQuestionIndex
 viewModelScope.launch {
 userRepo.updateTodayCompletedCount(completedCount)
 }
 
 // 标记为已保存进度并重置
 _uiState.update {
 it.copy(
 savedProgress = true,
 quizCompleted = true // 标记为完成以触发返回首页逻辑
 )
 }
 }

 fun resetQuiz() {
 correctStreak = 0
 userAnswer = ""
 loadQuestions() // 重新加载今日3道题目
 }

    private fun calculatePoints(difficulty: Int, streak: Int): Int {
        val basePoints = difficulty * 10
        val streakBonus = if (streak >= 3) (streak - 2) * 5 else 0
        return basePoints + streakBonus
    }
}
