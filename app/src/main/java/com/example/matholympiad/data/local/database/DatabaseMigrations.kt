package com.example.matholympiad.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * 数据库迁移脚本
 */
object DatabaseMigrations {
    
    // Migration 1 -> 2: 添加 totalAnswered 和 totalCorrect 列到 users 表
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // 添加 totalAnswered 列（默认值为0）
            database.execSQL("ALTER TABLE users ADD COLUMN totalAnswered INTEGER NOT NULL DEFAULT 0")
            // 添加 totalCorrect 列（默认值为0）
            database.execSQL("ALTER TABLE users ADD COLUMN totalCorrect INTEGER NOT NULL DEFAULT 0")
        }
    }
    
 // Migration 2 -> 3: 重建questions表以修复options格式
 val MIGRATION_2_3 = object : Migration(2, 3) {
 override fun migrate(database: SupportSQLiteDatabase) {
 // 删除旧表
 database.execSQL("DROP TABLE IF EXISTS questions")
 // 创建新表 - 包含所有字段
 database.execSQL("""
 CREATE TABLE IF NOT EXISTS questions (
 id TEXT PRIMARY KEY NOT NULL,
 content TEXT NOT NULL,
 options TEXT NOT NULL DEFAULT '[]',
 correctAnswer INTEGER NOT NULL DEFAULT -1,
 correctAnswerText TEXT NOT NULL DEFAULT '',
 explanation TEXT NOT NULL DEFAULT '',
 hint TEXT NOT NULL DEFAULT '',
 type TEXT NOT NULL DEFAULT 'CALCULATION',
 module TEXT NOT NULL DEFAULT '',
 topic TEXT NOT NULL DEFAULT '',
 difficulty INTEGER NOT NULL DEFAULT 2
 )
 """)
 }
 }
    
// Migration 3 -> 4: 添加新字段支持填空题
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 添加填空题答案字段
        database.execSQL("ALTER TABLE questions ADD COLUMN correctAnswerText TEXT NOT NULL DEFAULT ''")
        // 添加提示字段
        database.execSQL("ALTER TABLE questions ADD COLUMN hint TEXT NOT NULL DEFAULT ''")
        // 添加模块字段
        database.execSQL("ALTER TABLE questions ADD COLUMN module TEXT NOT NULL DEFAULT ''")
        // 添加主题字段
        database.execSQL("ALTER TABLE questions ADD COLUMN topic TEXT NOT NULL DEFAULT ''")
    }
}

// Migration 4 -> 5: 添加错题本表（answer_history）
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 创建 answer_history 表
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS answer_history (
                historyId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                userId TEXT NOT NULL,
                questionId TEXT NOT NULL,
                selectedAnswer INTEGER NOT NULL,
                isCorrect INTEGER NOT NULL,
                answeredAt INTEGER NOT NULL,
                responseTimeMs INTEGER NOT NULL,
                reviewStage INTEGER NOT NULL DEFAULT 0,
                nextReviewAt INTEGER,
                masteryLevel INTEGER NOT NULL DEFAULT 0,
                reviewCount INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE,
                FOREIGN KEY (questionId) REFERENCES questions(id) ON DELETE CASCADE
            )
        """)
        
        // 创建索引
        database.execSQL("CREATE INDEX IF NOT EXISTS index_answer_history_userId ON answer_history(userId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_answer_history_questionId ON answer_history(questionId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_answer_history_answeredAt ON answer_history(answeredAt)")
    }
}
}
