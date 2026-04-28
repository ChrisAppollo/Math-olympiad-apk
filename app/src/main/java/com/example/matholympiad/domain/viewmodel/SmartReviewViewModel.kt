package com.example.matholympiad.domain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matholympiad.data.local.model.AnswerHistory
import com.example.matholympiad.data.local.model.Question
import com.example.matholympiad.data.repository.AnswerHistoryRepository
import com.example.matholympiad.domain.usecase.MasteryLevel
import com.example.matholympiad.domain.usecase.ReviewSchedule
import com.example.matholympiad.domain.usecase.SpacedRepetitionAlgorithm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * 智能错题重练ViewModel
 */
@HiltViewModel
class SmartReviewViewModel @Inject constructor(
    private val answerHistoryRepository: AnswerHistoryRepository,
    private val spacedRepetition: SpacedRepetitionAlgorithm
) : ViewModel() {

    private val _uiState = MutableStateFlow(SmartReviewUiState())
    val uiState: StateFlow<SmartReviewUiState> = _uiState.asStateFlow()

    private val _reviewToday = MutableStateFlow<Int?>(null)
    val reviewToday: StateFlow<Int?> = _reviewToday.asStateFlow()

    init {
        loadTodayReviews()
    }

    /**
     * 加载今日复习列表
     */
    fun loadTodayReviews() {
        viewModelScope.launch {
            try {
            answerHistoryRepository.getAllHistories()
                .collect { histories ->
                    val pendingReviews = spacedRepetition.getTodayReviewQuestions(histories)
                        .filter { it.masteryLevel < 100 }
                    
                    _uiState.update { state ->
                        state.copy(
                            todayReviews = pendingReviews,
                            isLoading = false
                        )
                    }
                    
                    _reviewToday.value = pendingReviews.size
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    /**
     * 开始复习一道错题
     */
    fun startReview(historyId: Long) {
        viewModelScope.launch {
            answerHistoryRepository.getHistoryById(historyId)
                ?.let { history ->
                    _uiState.update { state ->
                        state.copy(
                            currentReviewing = history
                        )
                    }
                }
        }
    }

    /**
     * 完成复习并评分
     * @param quality 答题质量 (0-5)
     */
    fun completeReview(history: AnswerHistory, quality: Int) {
        viewModelScope.launch {
            val updatedHistory = spacedRepetition.calculateNextReview(history, quality)
            
            // 更新历史记录
            answerHistoryRepository.insertAnswerHistory(updatedHistory)
            
            _uiState.update { state ->
                state.copy(
                    currentReviewing = null,
                    reviewCompleted = true
                )
            }
            
            // 重新加载列表
            loadTodayReviews()
        }
    }

    /**
     * 获取错题掌握度
     */
    fun getMasteryLevel(history: AnswerHistory): MasteryLevel {
        return spacedRepetition.calculateMasteryLevel(history)
    }

    /**
     * 计算记忆强度
     */
    fun getMemoryStrength(history: AnswerHistory): Int {
        return spacedRepetition.calculateMemoryStrength(history)
    }

    /**
     * 清除完成状态
     */
    fun clearCompletionState() {
        _uiState.update { it.copy(reviewCompleted = false) }
    }
}

/**
 * 智能复习UI状态
 */
data class SmartReviewUiState(
    val isLoading: Boolean = true,
    val todayReviews: List<AnswerHistory> = emptyList(),
    val upcomingReviews: List<AnswerHistory> = emptyList(),
    val currentReviewing: AnswerHistory? = null,
    val reviewCompleted: Boolean = false,
    val error: String? = null
)
