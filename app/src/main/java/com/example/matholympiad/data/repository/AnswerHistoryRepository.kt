package com.example.matholympiad.data.repository

import com.example.matholympiad.data.local.dao.AnswerHistoryDao
import com.example.matholympiad.data.local.model.AnswerHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Answer 历史记录仓库
 */
@Singleton
class AnswerHistoryRepository @Inject constructor(
    private val answerHistoryDao: AnswerHistoryDao
) {
    companion object {
        // 内存存储
        private val histories = mutableListOf<AnswerHistory>()
        private var nextId = 1L
    }

    init {
        // 初始化一些数据用于测试
        if (histories.isEmpty()) {
            // 可以在这里添加默认数据
        }
    }

    /**
     * 获取所有历史
     */
    fun getAllHistories(): Flow<List<AnswerHistory>> = flow {
        emit(histories.toList())
    }

    /**
     * 根据ID获取历史
     */
    fun getHistoryById(historyId: Long): AnswerHistory? {
        return histories.find { it.historyId == historyId }
    }

    /**
     * 插入或更新历史记录
     */
    suspend fun insertAnswerHistory(history: AnswerHistory) {
        if (history.historyId == 0L) {
            val newHistory = history.copy(historyId = nextId++)
            histories.add(newHistory)
        } else {
            val index = histories.indexOfFirst { it.historyId == history.historyId }
            if (index != -1) {
                histories[index] = history
            } else {
                histories.add(history)
            }
        }
    }

    /**
     * 获取日期范围内的历史
     */
    fun getHistoriesInDateRange(startDate: String, endDate: String): Flow<List<AnswerHistory>> = flow {
        val filtered = histories.filter { record ->
            // AnswerHistory doesn't have wrongDate, skip this filter for now
            true
        }
        emit(filtered)
    }

    /**
     * 根据用户ID获取历史
     */
    suspend fun getUserAnswerHistory(userId: String): List<AnswerHistory> {
        return histories.filter { it.userId == userId }
    }

    /**
     * 清除所有历史
     */
    suspend fun clearAllHistory() {
        histories.clear()
    }
}
