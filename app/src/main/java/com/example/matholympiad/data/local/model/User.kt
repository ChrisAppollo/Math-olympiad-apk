package com.example.matholympiad.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class AnswerRecord(
    val questionId: String,
    val selectedAnswer: Int?,      // 用户选择的选项索引（null 表示未选择）
    val isCorrect: Boolean,        // 是否正确
    val answerTime: Long           // 答题时间戳
)

/**
 * 用户数据模型
 * 存储于 users 表
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey val userId: String,
    var totalScore: Int = 0,        // 总积分累计
    var badges: List<String> = emptyList(),  // 已解锁勋章 ID 列表
    var streakCount: Int = 0,       // 连续打卡天数
    val lastLoginDate: String,      // "2026-04-25"
    var todayQuestionsCompleted: Int = 0,  // 今日已完成题数
    val answerHistory: List<AnswerRecord> = emptyList()  // 历史答题记录（今日）
)

/**
 * 勋章数据模型
 * 存储于 badges 表
 */
@Entity(tableName = "badges")
data class Badge(
    @PrimaryKey val id: String,      // "beginner", "persistence_7days"
    val name: String,                // "新手入门"
    val description: String,         // 获取条件说明
    val unlockConditions: Map<String, Int>,  // {"minStreakDays": 7}
    val iconResId: Int               // 资源 ID（图标图片）
)

/**
 * 今日临时题目表
 */
@Entity(tableName = "today_questions")
data class TodayQuestion(
    @PrimaryKey val questionId: String,
    val indexInToday: Int            // 今日第几题（0-2）
)
