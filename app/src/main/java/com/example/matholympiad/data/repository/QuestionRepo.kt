package com.example.matholympiad.data.repository

import com.example.matholympiad.data.local.dao.QuestionDao
import com.example.matholympiad.data.local.model.AppConstants
import com.example.matholympiad.data.local.model.Question
import com.example.matholympiad.data.local.model.QuestionType
import com.example.matholympiad.data.local.model.UserTypeConverters
import kotlinx.coroutines.flow.Flow
import java.io.File
import org.json.JSONObject

/**
 * 题库数据仓库
 */
class QuestionRepo(private val questionDao: QuestionDao) {
 
 /**
 * 从 JSON 文件加载题库
 */
 suspend fun loadQuestionsFromJson(filePath: String): Boolean {
 return try {
 val file = File(filePath)
 if (!file.exists()) {
 false
 } else {
 val jsonContent = file.readText()
 val jsonArray = org.json.JSONArray(jsonContent)
 val questions = mutableListOf<Question>()
 
 for (i in 0 until jsonArray.length()) {
 val obj = jsonArray.getJSONObject(i)
 val question = Question(
 id = obj.getString("id"),
 content = obj.getString("content"),
 options = UserTypeConverters().stringListToString(jsonArrayToStringList(obj.getJSONArray("options"))),
 correctAnswer = obj.getInt("correctAnswer"),
 explanation = obj.getString("explanation"),
 type = obj.getString("type"),
 difficulty = obj.getInt("difficulty")
 )
 questions.add(question)
 }
 
 questionDao.insertQuestions(questions)
 true
 }
 } catch (e: Exception) {
 false
 }
 }
 
 private fun jsonArrayToStringList(array: org.json.JSONArray): List<String> {
 return (0 until array.length()).map { array.getString(it) }
 }
 
 /**
 * 按题型随机抽取题目
 */
 suspend fun getRandomQuestionsByType(type: QuestionType, count: Int): List<Question> {
 return questionDao.getRandomQuestionsByType(type, count)
 }
 
 /**
 * 获取所有题目
 */
suspend fun getAllQuestions(): List<Question> {
 return questionDao.getAllQuestions()
 }

 /**
 * 获取题目数量
 */
 suspend fun getQuestionCount(): Int {
 return questionDao.getQuestionCount()
 }

/**
 * 获取题库统计信息
 */
 suspend fun getQuestionStats(): Map<String, Int> {
 val allQuestions = questionDao.getAllQuestions()
 return allQuestions.groupBy { it.type }.mapValues { it.value.size }
 }

 /**
 * 获取今日闯关题目
 */
 suspend fun getTodayQuestions(): List<Question> {
 // 从所有题目中随机取指定数量
 val allQuestions = questionDao.getAllQuestions()
 return if (allQuestions.size <= AppConstants.DAILY_QUESTION_COUNT) {
 allQuestions
 } else {
 allQuestions.shuffled().take(AppConstants.DAILY_QUESTION_COUNT)
 }
 }
}