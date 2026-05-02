package com.example.matholympiad.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.matholympiad.data.local.dao.QuestionDao
import com.example.matholympiad.data.local.dao.UserDao
import com.example.matholympiad.data.local.dao.WrongAnswerDao
import com.example.matholympiad.data.local.model.Badge
import com.example.matholympiad.data.local.model.Question
import com.example.matholympiad.data.local.model.TodayQuestion
import com.example.matholympiad.data.local.model.User
import com.example.matholympiad.data.local.model.AnswerHistory

/**
 * Room 数据库主类
 */
@Database(
    entities = [
        User::class,
        Question::class,
        Badge::class,
        TodayQuestion::class,
        AnswerHistory::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun questionDao(): QuestionDao
    abstract fun wrongAnswerDao(): WrongAnswerDao
 
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
        .addMigrations(
            DatabaseMigrations.MIGRATION_1_2,
            DatabaseMigrations.MIGRATION_2_3,
            DatabaseMigrations.MIGRATION_3_4,
            DatabaseMigrations.MIGRATION_4_5
        )
 .fallbackToDestructiveMigration() // 作为后备方案
 .build()
 
                INSTANCE = instance
                instance
            }
        }
    }
}
