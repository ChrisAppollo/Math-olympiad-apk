package com.example.matholympiad.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.matholympiad.data.local.dao.*
import com.example.matholympiad.data.local.model.AnswerRecord
import com.example.matholympiad.data.local.model.Badge
import com.example.matholympiad.data.local.model.Question
import com.example.matholympiad.data.local.model.TodayQuestion
import com.example.matholympiad.data.local.model.User

/**
 * Room 数据库主类
 */
@Database(
    entities = [
        User::class,
        Question::class,
        AnswerRecord::class,
        Badge::class,
        TodayQuestion::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun questionDao(): QuestionDao
    abstract fun answerHistoryDao(): AnswerHistoryDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "math_olympiad_database"
                )
                .fallbackToDestructiveMigration()  // 支持数据库版本升级
                .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
}
