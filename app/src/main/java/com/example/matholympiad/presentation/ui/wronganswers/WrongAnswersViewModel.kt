package com.example.matholympiad.presentation.ui.wronganswers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matholympiad.data.local.model.AnswerHistory
import com.example.matholympiad.domain.usecase.DeleteWrongAnswer
import com.example.matholympiad.domain.usecase.GetAllWrongAnswers
import com.example.matholympiad.domain.usecase.GetTodayReviewQuestions
import com.example.matholympiad.domain.usecase.GetWrongAnswerStats
import com.example.matholympiad.domain.usecase.MarkAsReviewed
import com.example.matholympiad.domain.usecase.RecordAnswerResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 错题本 UI 状态
 */
data class WrongAnswersUiState(
    val isLoading: Boolean = false,
    val wrongAnswers: List<AnswerHistory> = emptyList(),
    val reviewQuestions: List<AnswerHistory> = emptyList(),
    val totalWrongCount: Int = 0,
    val dueReviewCount: Int = 0,
    val error: String? = null,
    val showReviewMode: Boolean = false,
    val currentReviewQuestion: AnswerHistory? = null,
    val reviewIndex: Int = 0,
    val reviewTotal: Int = 0,
    val reviewSuccess: Boolean? = null
)

/**
 * 错题本 ViewModel
 */
class WrongAnswersViewModel(
    private val getAllWrongAnswers: GetAllWrongAnswers,
    private val getTodayReviewQuestions: GetTodayReviewQuestions,
    private val getWrongAnswerStats: GetWrongAnswerStats,
    private val markAsReviewed: MarkAsReviewed,
    private val recordAnswerResult: RecordAnswerResult,
    private val deleteWrongAnswer: DeleteWrongAnswer
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(WrongAnswersUiState())
    val uiState: StateFlow<WrongAnswersUiState> = _uiState.asStateFlow()
    
    private var currentUserId: String = "user_001" // TODO: 从用户认证获取
    
    init {
        loadWrongAnswers()
        loadReviewQuestions()
        loadStats()
    }
    
    /**
     * 加载所有错题
     */
    private fun loadWrongAnswers() {
        viewModelScope.launch {
            getAllWrongAnswers(currentUserId).collectLatest { questions ->
                _uiState.value = _uiState.value.copy(wrongAnswers = questions)
            }
        }
    }
    
    /**
     * 加载今日待复习题目
     */
    private fun loadReviewQuestions() {
        viewModelScope.launch {
            getTodayReviewQuestions(currentUserId).collectLatest { questions ->
                _uiState.value = _uiState.value.copy(reviewQuestions = questions)
            }
        }
    }
    
/**
 * 加载统计信息
 */
private fun loadStats() {
    viewModelScope.launch {
        try {
            val stats = getWrongAnswerStats.getStats(currentUserId)
            _uiState.value = _uiState.value.copy(
                totalWrongCount = stats.totalWrongAnswers,
                dueReviewCount = stats.dueForReview
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(error = e.message)
        }
    }
}
    
    /**
     * 开始复习模式
     */
    fun startReviewMode() {
        val questions = _uiState.value.reviewQuestions
        if (questions.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(
                showReviewMode = true,
                currentReviewQuestion = questions[0],
                reviewIndex = 0,
                reviewTotal = questions.size
            )
        }
    }
    
    /**
     * 提交复习答案
     */
    fun submitReviewAnswer(selectedAnswer: Int) {
        val currentQuestion = _uiState.value.currentReviewQuestion ?: return
        
        viewModelScope.launch {
            // TODO: 这里需要获取题目的正确答案来对比
            // 暂时模拟为答对
            val isCorrect = true
            
            // 记录答题结果
            recordAnswerResult(
                userId = currentUserId,
                questionId = currentQuestion.questionId,
                selectedAnswer = selectedAnswer,
                isCorrect = isCorrect,
                responseTimeMs = 0
            )
            
            // 更新复习状态
            markAsReviewed(
                userId = currentUserId,
                questionId = currentQuestion.questionId,
                wasCorrect = isCorrect
            )
            
            // 更新 UI 状态
            _uiState.value = _uiState.value.copy(reviewSuccess = isCorrect)
            
            // 延迟后进入下一题
            kotlinx.coroutines.delay(1000)
            nextReviewQuestion()
        }
    }
    
    /**
     * 进入下一题
     */
    private fun nextReviewQuestion() {
        val currentIndex = _uiState.value.reviewIndex
        val questions = _uiState.value.reviewQuestions
        
        if (currentIndex + 1 < questions.size) {
            _uiState.value = _uiState.value.copy(
                reviewIndex = currentIndex + 1,
                currentReviewQuestion = questions[currentIndex + 1],
                reviewSuccess = null
            )
        } else {
            // 复习完成
            exitReviewMode()
            loadReviewQuestions() // 刷新列表
        }
    }
    
    /**
     * 退出复习模式
     */
    fun exitReviewMode() {
        _uiState.value = _uiState.value.copy(
            showReviewMode = false,
            currentReviewQuestion = null,
            reviewIndex = 0,
            reviewTotal = 0,
            reviewSuccess = null
        )
    }
    
    /**
     * 删除错题
     */
    fun deleteQuestion(questionId: String) {
        viewModelScope.launch {
            try {
                deleteWrongAnswer(currentUserId, questionId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}
