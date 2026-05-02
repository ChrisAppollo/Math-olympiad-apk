package com.example.matholympiad.domain.usecase

import com.example.matholympiad.data.repository.QuestionRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import com.example.matholympiad.data.local.model.AppConstants

class GetTodayQuestions(private val questionRepo: QuestionRepo) {
 
suspend operator fun invoke(): Result<List<String>> = withContext(Dispatchers.IO) {
 try {
 // 首先检查题库是否已初始化，如果没有则等待
 val count = questionRepo.getQuestionCount()
 if (count == 0) {
 // 题库为空，等待短暂时间让import完成
 kotlinx.coroutines.delay(500)
 }
 
 // 获取今日题目ID列表
 val questions = questionRepo.getAllQuestions()
 val todayQuestionIds = if (questions.isNotEmpty()) {
 questions.shuffled().take(AppConstants.DAILY_QUESTION_COUNT).map { it.id }
 } else {
 // 如果仍为空，使用备用题目
 getFallbackQuestionIds()
 }
 
 Result.success(todayQuestionIds)
 } catch (e: Exception) {
 Result.failure(e)
 }
 }
 
 private fun getFallbackQuestionIds(): List<String> {
 // 内置备份题目ID，确保总有题可答
 return (1..AppConstants.DAILY_QUESTION_COUNT).map { String.format("q%03d", it) }
 }
}
