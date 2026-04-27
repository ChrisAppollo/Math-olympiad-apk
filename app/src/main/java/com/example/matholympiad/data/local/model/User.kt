package com.example.matholympiad.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 答题记录
 */
data class AnswerRecord(
    val questionId: String,
    val selectedAnswer: Int?,       // 用户选择的选项索引（null 表示未选择）
    val isCorrect: Boolean,          // 是否正确
    val answerTime: Long             // 答题时间戳
)

/**
 * 用户数据模型
 * 存储于 users 表
 */
@Entity(tableName = "users")
@TypeConverters(UserTypeConverters::class)
data class User(
    @PrimaryKey val userId: String,
    var totalScore: Int = 0, // 总积分累计
    var badges: String = "[]", // 已解锁勋章 ID 列表（JSON字符串）
    var streakCount: Int = 0, // 连续打卡天数
    val lastLoginDate: String, // "2026-04-25"
    var todayQuestionsCompleted: Int = 0, // 今日已完成题数
    val answerHistory: String = "[]", // 历史答题记录（JSON字符串）
    var totalAnswered: Int = 0, // 累计答题数量
    var totalCorrect: Int = 0 // 累计正确答题数量
){
    // 辅助方法：获取徽章列表
    fun getBadgesList(): List<String> {
        return UserTypeConverters().stringToStringList(badges)
    }

    // 辅助方法：返回带有新徽章列表的用户副本
    fun withBadgesList(list: List<String>): User {
        return this.copy(badges = UserTypeConverters().stringListToString(list))
    }

    // 辅助方法：获取答题历史
    fun getAnswerHistoryList(): List<AnswerRecord> {
        return UserTypeConverters().stringToAnswerRecordList(answerHistory)
    }

    // 辅助方法：返回带有新答题历史的用户副本
    fun withAnswerHistoryList(list: List<AnswerRecord>): User {
        return this.copy(answerHistory = UserTypeConverters().answerRecordListToString(list))
    }
}

/**
 * 类型转换器
 */
class UserTypeConverters {
    private val gson = Gson()

    @TypeConverter
    fun stringListToString(list: List<String>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun stringToStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun answerRecordListToString(list: List<AnswerRecord>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun stringToAnswerRecordList(value: String): List<AnswerRecord> {
        val listType = object : TypeToken<List<AnswerRecord>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
}

/**
 * 今日临时题目表
 */
@Entity(tableName = "today_questions")
data class TodayQuestion(
    @PrimaryKey val questionId: String,
    val indexInToday: Int // 今日第几题（0-2）
)
