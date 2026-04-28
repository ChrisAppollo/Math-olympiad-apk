package com.example.matholympiad.domain.usecase

import com.example.matholympiad.data.local.model.AnswerRecord
import com.example.matholympiad.data.local.model.User
import com.example.matholympiad.data.repository.UserRepo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("SubmitAnswerUseCase 测试")
class SubmitAnswerUseCaseTest {

 private lateinit var userRepo: UserRepo
 private lateinit var submitAnswerUseCase: SubmitAnswerUseCase

 @BeforeEach
 fun setup() {
 userRepo = mockk(relaxed = true)
 submitAnswerUseCase = SubmitAnswerUseCase(userRepo)
 }

 @Test
 @DisplayName("答对题目时应该增加积分")
 fun `when answer is correct, should add point`() = runTest {
 // Given
 coEvery { userRepo.addPoints(1) } returns Unit

 // When
 val result = submitAnswerUseCase(
 questionId = "q001",
 selectedAnswer = 0,
 isCorrect = true
 )

 // Then
 assertTrue(result.isSuccess)
 val (pointsEarned, _) = result.getOrNull()!!
 assertEquals(1, pointsEarned)
 coVerify { userRepo.addPoints(1) }
 }

 @Test
 @DisplayName("答错题时不应增加积分")
 fun `when answer is wrong, should not add point`() = runTest {
 // Given
 coEvery { userRepo.addPoints(0) } returns Unit

 // When
 val result = submitAnswerUseCase(
 questionId = "q001",
 selectedAnswer = 1,
 isCorrect = false
 )

 // Then
 assertTrue(result.isSuccess)
 val (pointsEarned, _) = result.getOrNull()!!
 assertEquals(0, pointsEarned)
 coVerify { userRepo.addPoints(0) }
 }

 @Test
 @DisplayName("应该返回答题记录")
 fun `should return answer record`() = runTest {
 // When
 val result = submitAnswerUseCase(
 questionId = "q001",
 selectedAnswer = 0,
 isCorrect = true
 )

 // Then
 assertTrue(result.isSuccess)
 val (_, record) = result.getOrNull()!!
 assertEquals("q001", record.questionId)
 assertEquals(0, record.selectedAnswer)
 assertTrue(record.isCorrect)
 }

 @Test
 @DisplayName("发生异常时返回失败结果")
 fun `should return failure on exception`() = runTest {
 // Given
 coEvery { userRepo.addPoints(any()) } throws RuntimeException("DB Error")

 // When
 val result = submitAnswerUseCase(
 questionId = "q001",
 selectedAnswer = 0,
 isCorrect = true
 )

 // Then
 assertTrue(result.isFailure)
 }
}
