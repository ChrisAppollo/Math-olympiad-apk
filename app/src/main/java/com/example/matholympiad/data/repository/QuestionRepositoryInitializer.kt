package com.example.matholympiad.data.repository

import android.content.Context
import com.example.matholympiad.data.local.dao.QuestionDao
import com.example.matholympiad.data.util.JsonQuestionImporter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 题库初始化管理器
 * 在应用启动时自动导入题库
 * 
 * @param context 应用上下文
 * @param questionDao 题目数据访问对象
 */
class QuestionRepositoryInitializer @Inject constructor(
 @ApplicationContext private val context: Context,
 private val questionDao: QuestionDao
) {
    
    /**
     * 检查题库是否已有数据，如果没有则从assets导入
     */
    suspend fun initializeIfNeeded() {
        withContext(Dispatchers.IO) {
            val existingCount = questionDao.getQuestionCount()
            if (existingCount == 0) {
                // 题库为空，从assets导入默认题库
                importDefaultQuestions()
            }
        }
    }
    
    /**
     * 强制重新导入题库（用于更新题库）
     */
    suspend fun forceReimport() {
        withContext(Dispatchers.IO) {
            questionDao.deleteAllQuestions()
            importDefaultQuestions()
        }
    }
    
    /**
     * 从assets导入默认30道奥数题
     */
    private suspend fun importDefaultQuestions() {
        val questions = try {
            JsonQuestionImporter.importFromAssets(context, "questions.json")
        } catch (e: Exception) {
            // 如果文件不存在，使用内置备用题库
            getFallbackQuestions()
        }
        
        if (questions.isNotEmpty()) {
            questionDao.insertQuestions(questions)
        }
    }
    
    /**
     * 内置备用题库（当assets文件读取失败时使用）
     */
    private fun getFallbackQuestions(): List<com.example.matholympiad.data.local.model.Question> {
        return listOf(
            createQuestion("fallback_001", "1+2+3+...+10=？", listOf("55", "50", "45", "60"), 0, "等差数列求和：(1+10)×10÷2=55"),
            createQuestion("fallback_002", "鸡兔同笼，头10腿32，鸡几只？", listOf("4", "5", "6", "3"), 0, "假设全是兔40条腿，多8条，每只兔换鸡少2条，鸡=8÷2=4只")
        )
    }
    
    private fun createQuestion(
        id: String,
        content: String,
        options: List<String>,
        correct: Int,
        explanation: String
    ): com.example.matholympiad.data.local.model.Question {
        return com.example.matholympiad.data.local.model.Question(
            id = id,
            content = content,
            options = com.example.matholympiad.data.local.model.UserTypeConverters().stringListToString(options),
            correctAnswer = correct,
            explanation = explanation,
            type = "ARITHMETIC",
            difficulty = 2
        )
    }
}