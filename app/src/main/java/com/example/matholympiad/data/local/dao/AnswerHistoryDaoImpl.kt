package com.example.matholympiad.data.local.dao

import com.example.matholympiad.data.local.model.AnswerRecord

/**
 * 答题历史数据访问实现（内存存储）
 */
class AnswerHistoryDaoImpl : AnswerHistoryDao {
 private val historyMap = mutableMapOf<String, MutableList<AnswerRecord>>()
 
 override suspend fun getUserAnswerHistory(userId: String): List<AnswerRecord> {
 return historyMap[userId]?.toList() ?: emptyList()
 }
 
 override suspend fun getAnswersForQuestions(userId: String, questionIds: List<String>): List<AnswerRecord> {
 return historyMap[userId]?.filter { it.questionId in questionIds } ?: emptyList()
 }
 
 override suspend fun insertAnswer(answer: AnswerRecord, userId: String) {
 val list = historyMap.getOrPut(userId) { mutableListOf() }
 list.add(0, answer) // 新记录放前面
 }
 
 override suspend fun clearUserHistory(userId: String) {
 historyMap.remove(userId)
 }
}