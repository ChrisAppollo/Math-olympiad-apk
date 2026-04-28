package com.example.matholympiad.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * 答题历史记录实体
 * 记录用户的每一次答题情况，用于错题本和智能复习
 */
@Entity(
    tableName = "answer_history",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Question::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["questionId"]),
        Index(value = ["answeredAt"])
    ]
)
data class AnswerHistory(
    @PrimaryKey(autoGenerate = true)
    val historyId: Long = 0,
    
    val userId: String,           // 用户ID
    val questionId: String,       // 题目ID
    val selectedAnswer: Int,      // 用户选择的答案索引
    val isCorrect: Boolean,       // 是否答对
    val answeredAt: Long,         // 作答时间戳（毫秒）
    val responseTimeMs: Long,     // 答题耗时（毫秒）
    
    // 艾宾浩斯复习相关
    val reviewStage: Int = 0,     // 当前复习阶段（0=新错题，1=首次复习...）
    val nextReviewAt: Long? = null, // 下次复习时间
    val masteryLevel: Int = 0,    // 掌握程度（0-100）
    val reviewCount: Int = 0      // 已复习次数
) {
    companion object {
        /**
         * 艾宾浩斯遗忘曲线复习间隔（天）
         * 对应复习阶段：1天、2天、4天、7天、15天
         */
        val SPACED_REPETITION_DAYS = listOf(1, 2, 4, 7, 15)
        
        /**
         * 最大复习阶段
         */
        const val MAX_REVIEW_STAGE = 5
    }
    
    /**
     * 计算下次复习时间
     */
    fun calculateNextReview(): Long? {
        if (isCorrect) return null
        
        val days = when (reviewStage) {
            0 -> SPACED_REPETITION_DAYS[0]
            1 -> SPACED_REPETITION_DAYS[1]
            2 -> SPACED_REPETITION_DAYS[2]
            3 -> SPACED_REPETITION_DAYS[3]
            4 -> SPACED_REPETITION_DAYS[4]
            else -> return null // 已经完成所有复习
        }
        
        return answeredAt + (days * 24 * 60 * 60 * 1000)
    }
    
    /**
     * 检查今天是否需要复习
     */
    fun isDueForReview(currentTime: Long = System.currentTimeMillis()): Boolean {
        return nextReviewAt != null && currentTime >= nextReviewAt
    }
}
