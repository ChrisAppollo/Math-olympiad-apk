package com.example.matholympiad.data.local.dao

import com.example.matholympiad.data.local.model.AnswerRecord

/**
 * 答题历史数据访问接口（内存存储，非持久化）
 */
interface AnswerHistoryDao {
 suspend fun getUserAnswerHistory(userId: String): List<AnswerRecord>
 suspend fun getAnswersForQuestions(userId: String, questionIds: List<String>): List<AnswerRecord>
 suspend fun insertAnswer(answer: AnswerRecord, userId: String)
 suspend fun clearUserHistory(userId: String)
}