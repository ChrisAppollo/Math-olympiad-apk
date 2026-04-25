package com.example.matholympiad.data.local.dao

import androidx.room.*
import com.example.matholympiad.data.local.model.AnswerRecord
import kotlinx.coroutines.flow.Flow

/**
 * 答题历史数据访问接口
 */
@Dao
interface AnswerHistoryDao {
    
    @Query("SELECT * FROM answer_history WHERE userId = :userId ORDER BY answerTime DESC")
    suspend fun getUserAnswerHistory(userId: String): List<AnswerRecord>
    
    @Query("SELECT * FROM answer_history WHERE userId = :userId AND questionId IN (:questionIds)")
    suspend fun getAnswersForQuestions(userId: String, questionIds: List<String>): List<AnswerRecord>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswer(answer: AnswerRecord)
    
    @Query("DELETE FROM answer_history WHERE userId = :userId")
    suspend fun clearUserHistory(userId: String)
}