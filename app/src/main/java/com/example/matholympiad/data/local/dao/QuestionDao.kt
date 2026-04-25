package com.example.matholympiad.data.local.dao

import androidx.room.*
import com.example.matholympiad.data.local.model.Question
import com.example.matholympiad.data.local.model.QuestionType
import kotlinx.coroutines.flow.Flow

/**
 * 题目数据访问接口
 */
@Dao
interface QuestionDao {
    
    @Query("SELECT * FROM questions")
    suspend fun getAllQuestions(): List<Question>
    
    @Query("SELECT * FROM questions WHERE type = :type ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomQuestionsByType(type: QuestionType, count: Int): List<Question>
    
    @Query("SELECT * FROM questions WHERE id IN (:questionIds)")
    suspend fun getQuestionsByIds(questionIds: List<String>): List<Question>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<Question>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodayQuestions(todayQuestions: List<TodayQuestion>)
    
    @Query("SELECT * FROM today_questions")
    suspend fun getTodayQuestions(): List<TodayQuestion>
    
    @Query("DELETE FROM today_questions")
    suspend fun clearTodayQuestions()
}

