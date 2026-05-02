package com.example.matholympiad.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.matholympiad.data.local.model.AnswerHistory
import kotlinx.coroutines.flow.Flow

/**
 * 错题本 DAO - 基于艾宾浩斯遗忘曲线的复习管理
 */
@Dao
interface WrongAnswerDao {
    
    /**
     * 获取用户的所有错题（答错的题目）
     */
    @Query("""
        SELECT * FROM answer_history 
        WHERE userId = :userId AND isCorrect = 0
        ORDER BY answeredAt DESC
    """)
    fun getAllWrongAnswers(userId: String): Flow<List<AnswerHistory>>
    
    /**
     * 获取今天需要复习的错题
     */
    @Query("""
        SELECT * FROM answer_history 
        WHERE userId = :userId 
        AND isCorrect = 0 
        AND nextReviewAt IS NOT NULL 
        AND nextReviewAt <= :currentTime
        ORDER BY nextReviewAt ASC
    """)
    fun getDueReviewQuestions(userId: String, currentTime: Long = System.currentTimeMillis()): Flow<List<AnswerHistory>>
    
    /**
     * 获取特定题目的答题历史
     */
    @Query("""
        SELECT * FROM answer_history 
        WHERE userId = :userId AND questionId = :questionId
        ORDER BY answeredAt DESC
    """)
    fun getQuestionHistory(userId: String, questionId: String): Flow<List<AnswerHistory>>
    
    /**
     * 统计错题数量
     */
    @Query("SELECT COUNT(*) FROM answer_history WHERE userId = :userId AND isCorrect = 0")
    fun getWrongAnswerCount(userId: String): Flow<Int>
    
    /**
     * 统计待复习的错题数量
     */
    @Query("""
        SELECT COUNT(*) FROM answer_history 
        WHERE userId = :userId 
        AND isCorrect = 0 
        AND nextReviewAt IS NOT NULL 
        AND nextReviewAt <= :currentTime
    """)
    fun getDueReviewCount(userId: String, currentTime: Long = System.currentTimeMillis()): Flow<Int>
    
    /**
     * 插入答题记录
     */
    @Insert
    suspend fun insertAnswerHistory(history: AnswerHistory): Long
    
    /**
     * 更新答题记录（用于更新复习状态）
     */
    @Update
    suspend fun updateAnswerHistory(history: AnswerHistory)
    
    /**
     * 更新题目的复习状态
     */
    @Query("""
        UPDATE answer_history 
        SET reviewStage = :newStage, 
            nextReviewAt = :newNextReviewAt,
            masteryLevel = :newMasteryLevel,
            reviewCount = reviewCount + 1
        WHERE userId = :userId AND questionId = :questionId AND 
            answeredAt = (
                SELECT MAX(answeredAt) 
                FROM answer_history 
                WHERE userId = :userId AND questionId = :questionId
            )
    """)
    suspend fun updateReviewStatus(
        userId: String,
        questionId: String,
        newStage: Int,
        newNextReviewAt: Long?,
        newMasteryLevel: Int
    )
    
    /**
     * 删除错题记录
     */
    @Query("DELETE FROM answer_history WHERE userId = :userId AND questionId = :questionId")
    suspend fun deleteWrongAnswer(userId: String, questionId: String)
}
