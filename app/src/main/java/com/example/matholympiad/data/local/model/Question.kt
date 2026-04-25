package com.example.matholympiad.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 题目数据模型
 * 存储于 questions 表
 */
@Entity(tableName = "questions")
data class Question(
    @PrimaryKey val id: String,
    val content: String,          // 题目内容
    val options: List<String>,     // 选项列表（最多 4 个）
    val correctAnswer: Int,        // 正确答案索引（0-3）
    val explanation: String,       // 解析说明
    val type: QuestionType,        // 题型：CALCULATION/LOGIC/GRAPHIC
    val difficulty: Int            // 难度等级 1-5
)

/**
 * 题型枚举
 */
enum class QuestionType {
    CALCULATION,   // 计算题
    LOGIC,         // 逻辑推理题
    GRAPHIC        // 图形题
}
