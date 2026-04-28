package com.example.matholympiad.data.repository

import com.example.matholympiad.data.local.model.AnswerRecord
import com.example.matholympiad.data.local.model.Question
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 每周统计仓库
 */
@Singleton
class WeeklyStatisticsRepository @Inject constructor() {
    
    /**
     * 获取每周统计
     */
    fun getWeeklyStatistics(histories: List<AnswerRecord>): WeeklyStatistics {
        val total = histories.size
        val correct = histories.count { it.isCorrect }
        val accuracy = if (total > 0) correct.toFloat() / total else 0f
        
        return WeeklyStatistics(
            totalQuestions = total,
            correctCount = correct,
            accuracy = accuracy,
            totalStudyMinutes = histories.size * 2 // 估算每题2分钟
        )
    }
    
    /**
     * 获取平均答题时间
     */
    fun getAverageAnswerTime(histories: List<AnswerRecord>): Int {
        if (histories.isEmpty()) return 0
        return histories.map { it.answerTime }.average().toInt()
    }
}

/**
 * 每周统计数据
 */
data class WeeklyStatistics(
    val totalQuestions: Int,
    val correctCount: Int,
    val accuracy: Float,
    val totalStudyMinutes: Int
)
