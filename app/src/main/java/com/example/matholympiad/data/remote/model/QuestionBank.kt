package com.example.matholympiad.data.remote.model

import com.example.matholympiad.data.local.model.QuestionType
import com.google.gson.annotations.SerializedName
import java.util.UUID

/**
 * 与新题库JSON格式对应的数据类
 */
data class QuestionBank(
    val title: String,
    val description: String,
    @SerializedName("total_count")
    val totalCount: Int,
    val modules: List<String>,
    val problems: List<Problem>
)

data class Problem(
    val id: Int,
    val module: String,
    val topic: String,
    val difficulty: String,
    val question: String,
    val hint: String,
    val answer: String
)

/**
 * 模块映射：中文 -> QuestionType
 */
fun String.toQuestionType(): QuestionType {
    return when (this) {
        "计算" -> QuestionType.CALCULATION
        "计数" -> QuestionType.COUNTING
        "数论" -> QuestionType.NUMBER_THEORY
        "几何" -> QuestionType.GEOMETRY
        "应用题" -> QuestionType.WORD_PROBLEM
        "行程" -> QuestionType.TRAVEL
        "组合" -> QuestionType.COMBINATORICS
        else -> QuestionType.CALCULATION
    }
}

/**
 * 难度映射：中文 -> 数字
 */
fun String.toDifficultyLevel(): Int {
    return when (this) {
        "简单" -> 1
        "中等" -> 3
        "困难" -> 5
        else -> 2
    }
}

/**
 * 将Problem转换为Question
 */
fun Problem.toQuestion(): com.example.matholympiad.data.local.model.Question {
    return com.example.matholympiad.data.local.model.Question(
        id = "q${id.toString().padStart(3, '0')}",
        content = question,
        options = "[]", // 填空题无选项
        correctAnswer = -1, // -1表示填空题
        correctAnswerText = answer,
        explanation = answer, // 用答案作为解析，或后续添加更详细的解析
        hint = hint,
        type = module.toQuestionType().name,
        module = module,
        topic = topic,
        difficulty = difficulty.toDifficultyLevel()
    )
}
