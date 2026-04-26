package com.example.matholympiad.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

/**
 * 题型枚举
 */
enum class QuestionType {
    CALCULATION,    // 计算题
    LOGIC,          // 逻辑推理题
    GRAPHIC         // 图形题
}

/**
 * 题目数据模型
 * 存储于 questions 表
 */
@Entity(tableName = "questions")
@TypeConverters(UserTypeConverters::class)
data class Question(
    @PrimaryKey val id: String,
    val content: String,                    // 题目内容
    val options: String = "[]",             // 选项列表 JSON 字符串
    val correctAnswer: Int,                 // 正确答案索引（0-3）
    val explanation: String,                // 解析说明
    val type: String = "CALCULATION",      // 题型：CALCULATION/LOGIC/GRAPHIC
    val difficulty: Int                     // 难度等级 1-5
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
}
