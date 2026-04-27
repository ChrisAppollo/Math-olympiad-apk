package com.example.matholympiad.presentation.ui.wronganswers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matholympiad.data.local.dao.AnswerHistoryDao
import com.example.matholympiad.data.local.model.Question
import com.example.matholympiad.data.repository.QuestionRepo
import com.example.matholympiad.data.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WrongAnswersViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val questionRepo: QuestionRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(WrongAnswersUiState())
    val uiState: StateFlow<WrongAnswersUiState> = _uiState

    init {
        loadWrongAnswers()
    }

    /**
     * 加载错题记录
     */
    fun loadWrongAnswers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val user = userRepo.getDefaultUser()
                val allQuestions = questionRepo.getAllQuestions()
                
                // 获取答错的记录
                val wrongAnswerHistory = user.getAnswerHistoryList().filter { !it.isCorrect }
                
                // 分组统计错误次数
                val groupedByQuestion = wrongAnswerHistory.groupBy { it.questionId }
                
                // 转换为错题条目
                val wrongItems = groupedByQuestion.map { (questionId, records) ->
                    val question = allQuestions.find { it.id == questionId }
                    val latestRecord = records.maxByOrNull { it.answerTime }
                    
                    WrongAnswerItem(
                        question = question ?: Question(
                            id = questionId,
                            content = "题目已删除",
                            options = "[]",
                            correctAnswer = 0,
                            explanation = "该题目已被移除",
                            type = "UNKNOWN",
                            difficulty = 1
                        ),
                        userAnswer = latestRecord?.selectedAnswer?.let { 
                            question?.getOptionsList()?.getOrNull(it) ?: "未知"
                        } ?: "未作答",
                        wrongTime = latestRecord?.answerTime ?: System.currentTimeMillis(),
                        wrongCount = records.size
                    )
                }.sortedByDescending { it.wrongTime }

                _uiState.value = WrongAnswersUiState(
                    wrongQuestions = wrongItems,
                    isLoading = false
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = WrongAnswersUiState(
                    isLoading = false,
                    error = "加载错题记录失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 选择查看错题详情
     */
    fun selectQuestion(item: WrongAnswerItem) {
        _uiState.value = _uiState.value.copy(
            selectedQuestion = item,
            showDetailDialog = true
        )
    }

    /**
     * 关闭详情弹窗
     */
    fun dismissDetailDialog() {
        _uiState.value = _uiState.value.copy(
            showDetailDialog = false
        )
    }
}
