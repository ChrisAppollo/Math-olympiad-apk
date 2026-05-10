package com.example.matholympiad.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
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
 
 @ColumnInfo(name = "userId")
 val userId: String,
 
 @ColumnInfo(name = "questionId")
 val questionId: String,
 
 @ColumnInfo(name = "selectedAnswer")
 val selectedAnswer: Int,
 
 @ColumnInfo(name = "isCorrect", typeAffinity = ColumnInfo.INTEGER)
 val isCorrect: Boolean,
 
 @ColumnInfo(name = "answeredAt")
 val answeredAt: Long,
 
 @ColumnInfo(name = "responseTimeMs")
 val responseTimeMs: Long,
 
 @ColumnInfo(name = "reviewStage")
 val reviewStage: Int = 0,
 
 @ColumnInfo(name = "nextReviewAt")
 val nextReviewAt: Long? = null,
 
 @ColumnInfo(name = "masteryLevel")
 val masteryLevel: Int = 0,
 
 @ColumnInfo(name = "reviewCount")
 val reviewCount: Int = 0
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
