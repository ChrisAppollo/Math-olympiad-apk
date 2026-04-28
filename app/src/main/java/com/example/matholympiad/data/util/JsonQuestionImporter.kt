package com.example.matholympiad.data.util

import android.content.Context
import com.example.matholympiad.data.local.model.Question
import com.example.matholympiad.data.remote.model.QuestionBank
import com.example.matholympiad.data.remote.model.toQuestion
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

/**
 * JSON题库导入工具 - 支持新题库格式
 */
object JsonQuestionImporter {

    private val gson = Gson()

    /**
     * 从assets加载新格式题库
     */
    fun importFromAssets(context: Context, fileName: String = "questions_new.json"): List<Question> {
        return try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            parseNewFormatQuestions(jsonString)
        } catch (e: Exception) {
            android.util.Log.e("JsonQuestionImporter", "Failed to load from assets: ${e.message}")
            // 尝试加载旧格式
            try {
                val jsonString = context.assets.open("questions.json").bufferedReader().use { it.readText() }
                parseJsonQuestions(jsonString)
            } catch (e2: Exception) {
                e2.printStackTrace()
                emptyList()
            }
        }
    }

    /**
     * 解析新格式题库（带有modules和problems字段）
     */
    fun parseNewFormatQuestions(jsonString: String): List<Question> {
        return try {
            val questionBank = gson.fromJson(jsonString, QuestionBank::class.java)
            questionBank.problems.map { it.toQuestion() }
        } catch (e: Exception) {
            android.util.Log.e("JsonQuestionImporter", "Failed to parse new format: ${e.message}")
            // 回退到旧格式解析
            parseJsonQuestions(jsonString)
        }
    }

    /**
     * 从字符串解析旧格式题库（选择题格式）
     */
    fun parseJsonQuestions(jsonString: String): List<Question> {
        return try {
            val jsonArray = JSONArray(jsonString)
            val questions = mutableListOf<Question>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)

                // 解析options数组（如果存在）
                val optionsList = mutableListOf<String>()
                if (obj.has("options")) {
                    val optionsArray = obj.getJSONArray("options")
                    for (j in 0 until optionsArray.length()) {
                        optionsList.add(optionsArray.getString(j))
                    }
                }
                val optionsJson = gson.toJson(optionsList)

                // 判断是选择题还是填空题
                val isMultipleChoice = optionsList.isNotEmpty() && obj.has("correctAnswer")
                val correctAnswer = if (isMultipleChoice) obj.getInt("correctAnswer") else -1
                val correctAnswerText = if (!isMultipleChoice && obj.has("answer")) {
                    obj.getString("answer")
                } else {
                    obj.optString("correctAnswerText", "")
                }

                val question = Question(
                    id = obj.getString("id"),
                    content = obj.getString("content"),
                    options = optionsJson,
                    correctAnswer = correctAnswer,
                    correctAnswerText = correctAnswerText,
                    explanation = obj.optString("explanation", ""),
                    hint = obj.optString("hint", ""),
                    type = obj.optString("type", "CALCULATION"),
                    module = obj.optString("module", ""),
                    topic = obj.optString("topic", ""),
                    difficulty = obj.optInt("difficulty", 2)
                )
                questions.add(question)
            }
            questions
        } catch (e: Exception) {
            android.util.Log.e("JsonQuestionImporter", "Failed to parse old format: ${e.message}")
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
            // 尝试新格式，失败则尝试旧格式
            parseNewFormatQuestions(jsonString)
        } catch (e: Exception) {
            android.util.Log.e("JsonQuestionImporter", "Failed to load from file: ${e.message}")
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
                put("correctAnswerText", q.correctAnswerText)
                put("explanation", q.explanation)
                put("hint", q.hint)
                put("type", q.type)
                put("module", q.module)
                put("topic", q.topic)
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
