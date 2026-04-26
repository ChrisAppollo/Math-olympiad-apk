package com.example.matholympiad.data.local.dao

import androidx.room.*
import com.example.matholympiad.data.local.model.User
import com.example.matholympiad.data.local.model.UserTypeConverters
import kotlinx.coroutines.flow.Flow

/**
 * 用户数据访问接口
 */
@Dao
interface UserDao {
    
    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    suspend fun getUser(userId: String): User?
    
    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    fun getUserFlow(userId: String): Flow<User?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)
    
    @Update
    suspend fun updateUser(user: User)
    
    @Query("UPDATE users SET totalScore = totalScore + :points WHERE userId = :userId")
    suspend fun addPoints(userId: String, points: Int)
    
    @Query("UPDATE users SET todayQuestionsCompleted = :count WHERE userId = :userId")
    suspend fun updateTodayCompletedCount(userId: String, count: Int)
    
    @Query("UPDATE users SET streakCount = :streakCount, lastLoginDate = :lastLoginDate WHERE userId = :userId")
    suspend fun updateStreak(userId: String, streakCount: Int, lastLoginDate: String)
    
    @Transaction
    suspend fun updateUserBadges(userId: String, newBadgeIds: List<String>) {
        val user = getUser(userId) ?: throw IllegalArgumentException("User not found")
        val currentBadges = user.getBadgesList()
        val updatedBadges = (currentBadges + newBadgeIds).distinct()
        val converters = UserTypeConverters()
        updateUser(user.copy(badges = converters.stringListToString(updatedBadges)))
    }
}
