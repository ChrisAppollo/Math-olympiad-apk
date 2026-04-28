package com.example.matholympiad.domain.usecase

import com.example.matholympiad.data.local.model.AnswerRecord
import com.example.matholympiad.data.repository.UserRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SubmitAnswerUseCase(private val userRepo: UserRepo) {
    
 suspend operator fun invoke(
 questionId: String,
 selectedAnswer: Int?,
 isCorrect: Boolean
 ): Result<Pair<Int, AnswerRecord>> = withContext(Dispatchers.IO) {
 try {
 val pointsEarned = if (isCorrect) 10 else 0
 
 // 增加积分并更新今日完成题数
 userRepo.addPoints(pointsEarned)
 userRepo.updateTodayCompletedCount(userRepo.getDefaultUser().todayQuestionsCompleted + 1)
 
 // 更新用户答题统计（修复问题3）
 userRepo.updateUserStats(UserRepo.DEFAULT_USER_ID, isCorrect)
 
 val answerRecord = AnswerRecord(
 questionId = questionId,
 selectedAnswer = selectedAnswer,
 isCorrect = isCorrect,
 answerTime = System.currentTimeMillis()
 )
 
 Result.success(Pair(pointsEarned, answerRecord))
 } catch (e: Exception) {
 Result.failure(e)
 }
 }
}
