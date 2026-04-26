package com.example.matholympiad.domain.usecase

import com.example.matholympiad.data.repository.QuestionRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetTodayQuestions(private val questionRepo: QuestionRepo) {
    
    suspend operator fun invoke(): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            // 获取今日题目ID列表
            val questions = questionRepo.getAllQuestions()
            val todayQuestionIds = questions.shuffled().take(3).map { it.id }
            
            Result.success(todayQuestionIds)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
