package com.example.matholympiad.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

/**
 * 题型枚举 - 七大奥数模块
 */
enum class QuestionType {
    CALCULATION,    // 计算
    COUNTING,       // 计数
    NUMBER_THEORY,  // 数论
    GEOMETRY,       // 几何
    WORD_PROBLEM,   // 应用题
    TRAVEL,         // 行程
    COMBINATORICS   // 组合
}

/**
 * 题目数据模型
 * 存储于 questions 表
 */
@Entity(tableName = "questions")
@TypeConverters(UserTypeConverters::class)
data class Question(
    @PrimaryKey val id: String,
    val content: String,        // 题目内容
    val options: String = "[]", // 选项列表 JSON 字符串（填空题为空）
    val correctAnswer: Int = -1, // 正确答案索引（-1表示填空题）
    val correctAnswerText: String = "", // 填空题正确答案文本
    val explanation: String,    // 解析说明
    val hint: String = "",      // 答题提示
    val type: String = "CALCULATION", // 题型
    val module: String = "",    // 所属模块
    val topic: String = "",     // 细分主题
    val difficulty: Int         // 难度等级 1-5
) {
    // 获取选项列表
    fun getOptionsList(): List<String> {
        return UserTypeConverters().stringToStringList(options)
    }

    // 设置选项列表 - 返回一个新的 Question 实例
    fun withOptionsList(list: List<String>): Question {
        return this.copy(options = UserTypeConverters().stringListToString(list))
    }

    // 获取题型
    fun getQuestionType(): QuestionType {
        return try {
            QuestionType.valueOf(type)
        } catch (e: IllegalArgumentException) {
            QuestionType.CALCULATION
        }
    }

    // 设置题型 - 返回一个新的 Question 实例
    fun withQuestionType(qType: QuestionType): Question {
        return this.copy(type = qType.name)
    }

    // 是否为选择题
    fun isMultipleChoice(): Boolean {
        return correctAnswer >= 0 && getOptionsList().isNotEmpty()
    }

    // 是否为填空题
    fun isFillInBlank(): Boolean {
        return !isMultipleChoice()
    }

    // 验证答案（支持模糊匹配）
    fun checkAnswer(userAnswer: String): Boolean {
        if (isMultipleChoice()) {
            // 选择题：尝试解析为索引
            return try {
                val index = userAnswer.toInt()
                index == correctAnswer
            } catch (e: NumberFormatException) {
                false
            }
        }
        // 填空题：进行文本模糊匹配
        return fuzzyMatch(userAnswer.trim(), correctAnswerText.trim())
    }

    // 模糊匹配算法
    private fun fuzzyMatch(userAnswer: String, correctAnswer: String): Boolean {
        // 完全匹配
        if (userAnswer.equals(correctAnswer, ignoreCase = true)) {
            return true
        }
        
        // 去除空格后匹配
        val normalizedUser = userAnswer.replace("\\s+".toRegex(), "")
        val normalizedCorrect = correctAnswer.replace("\\s+".toRegex(), "")
        if (normalizedUser.equals(normalizedCorrect, ignoreCase = true)) {
            return true
        }
        
        // 提取数字部分匹配（适用于答案包含额外说明的情况）
        val userNumbers = extractNumbers(userAnswer)
        val correctNumbers = extractNumbers(correctAnswer)
        if (userNumbers.isNotEmpty() && correctNumbers.isNotEmpty()) {
            // 如果提取的数字相同，认为是正确的
            if (userNumbers == correctNumbers) {
                return true
            }
        }
        
        // 包含关系（用户的答案是正确答案的一部分，或反之）
        if (correctAnswer.contains(userAnswer, ignoreCase = true) ||
            userAnswer.contains(correctAnswer, ignoreCase = true)) {
            return true
        }
        
        return false
    }

    // 提取字符串中的所有数字
    private fun extractNumbers(text: String): List<String> {
        val regex = "\\d+".toRegex()
        return regex.findAll(text).map { it.value }.toList()
    }
}
