package com.example.matholympiad.data.util

import android.content.Context
import com.example.matholympiad.data.local.model.Question
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

/**
 * JSON题库导入工具
 */
object JsonQuestionImporter {
 
 private val gson = Gson()
 
 /**
 * 从assets加载题库
 */
 fun importFromAssets(context: Context, fileName: String = "questions.json"): List<Question> {
 return try {
 val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
 parseJsonQuestions(jsonString)
 } catch (e: Exception) {
 e.printStackTrace()
 emptyList()
 }
 }
 
 /**
 * 从字符串解析题库
 */
 fun parseJsonQuestions(jsonString: String): List<Question> {
 return try {
 val jsonArray = JSONArray(jsonString)
 val questions = mutableListOf<Question>()
 
 for (i in 0 until jsonArray.length()) {
 val obj = jsonArray.getJSONObject(i)
 
 // 解析options数组
 val optionsArray = obj.getJSONArray("options")
 val optionsList = mutableListOf<String>()
 for (j in 0 until optionsArray.length()) {
 optionsList.add(optionsArray.getString(j))
 }
 // 使用Gson序列化，与Question.getOptionsList()保持一致
 val optionsJson = gson.toJson(optionsList)
 
 val question = Question(
 id = obj.getString("id"),
 content = obj.getString("content"),
 options = optionsJson,
 correctAnswer = obj.getInt("correctAnswer"),
 explanation = obj.getString("explanation"),
 type = obj.optString("type", "ARITHMETIC"),
 difficulty = obj.optInt("difficulty", 2)
 )
 questions.add(question)
 }
 questions
 } catch (e: Exception) {
 e.printStackTrace()
 emptyList()
 }
 }
 
 /**
 * 从文件路径加载题库
 */
 fun importFromFile(context: Context, filePath: String): List<Question> {
 return try {
 val file = java.io.File(filePath)
 if (!file.exists()) return emptyList()
 
 val jsonString = file.readText()
 parseJsonQuestions(jsonString)
 } catch (e: Exception) {
 e.printStackTrace()
 emptyList()
 }
 }
 
 /**
 * 导出题库到JSON字符串
 */
 fun exportToJson(questions: List<Question>): String {
 val jsonArray = JSONArray()
 
 for (q in questions) {
 val obj = JSONObject().apply {
 put("id", q.id)
 put("content", q.content)
 put("options", JSONArray(q.getOptionsList()))
 put("correctAnswer", q.correctAnswer)
 put("explanation", q.explanation)
 put("type", q.type)
 put("difficulty", q.difficulty)
 }
 jsonArray.put(obj)
 }
 
 return jsonArray.toString(2)
 }
 
 /**
 * 保存题库到文件
 */
 fun saveToFile(context: Context, questions: List<Question>, fileName: String = "custom_questions.json"): Boolean {
 return try {
 val file = java.io.File(context.getExternalFilesDir(null), fileName)
 file.writeText(exportToJson(questions))
 true
 } catch (e: Exception) {
 e.printStackTrace()
 false
 }
 }
}
