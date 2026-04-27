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
 // 创建新表
 database.execSQL("""
 CREATE TABLE IF NOT EXISTS questions (
 id TEXT PRIMARY KEY NOT NULL,
 content TEXT NOT NULL,
 options TEXT NOT NULL DEFAULT '[]',
 correctAnswer INTEGER NOT NULL,
 explanation TEXT NOT NULL,
 type TEXT NOT NULL DEFAULT 'ARITHMETIC',
 difficulty INTEGER NOT NULL
 )
 """)
 }
 }
}
