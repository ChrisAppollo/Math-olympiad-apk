package com.example.matholympiad.domain.usecase

import com.example.matholympiad.data.local.model.AnswerRecord
import com.example.matholympiad.data.local.model.QuestionType
import com.example.matholympiad.data.local.model.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("SpacedRepetitionAlgorithm 测试")
class SpacedRepetitionAlgorithmTest {

    private val spacedRepetition = SpacedRepetitionAlgorithm()

    @Nested
    @DisplayName("计算下次复习时间")
    inner class CalculateNextReview {
        
        @Test
        @DisplayName("第一次错误应在1天后复习")
        fun `should schedule review in 1 day for first mistake`() {
            // Given
            val wrongAnswer = createWrongAnswer(daysAgo = 0)
            
            // When
            val interval = spacedRepetition.calculateInterval(wrongAnswer, repetitionCount = 0)
            
            // Then
            assertEquals(1, interval) // 1天后复习
        }

        @Test
        @DisplayName("第二次错误应在3天后复习")
        fun `should schedule review in 3 days for second repetition`() {
            // Given
            val wrongAnswer = createWrongAnswer(daysAgo = 1)
            
            // When
            val interval = spacedRepetition.calculateInterval(wrongAnswer, repetitionCount = 1)
            
            // Then
            assertEquals(3, interval)
        }

        @Test
        @DisplayName("第三次错误应在7天后复习")
        fun `should schedule review in 7 days for third repetition`() {
            // Given
            val wrongAnswer = createWrongAnswer(daysAgo = 3)
            
            // When
            val interval = spacedRepetition.calculateInterval(wrongAnswer, repetitionCount = 2)
            
            // Then
            assertEquals(7, interval)
        }

        @Test
        @DisplayName("第四次及以后应在14天后复习")
        fun `should schedule review in 14 days for fourth and beyond`() {
            // Given
            val wrongAnswer = createWrongAnswer(daysAgo = 7)
            
            // When
            val interval = spacedRepetition.calculateInterval(wrongAnswer, repetitionCount = 4)
            
            // Then
            assertEquals(14, interval)
        }
    }

    @Nested
    @DisplayName("优先级计算")
    inner class PriorityCalculation {
        
        @Test
        @DisplayName("高难度错题应有更高优先级")
        fun `high difficulty questions should have higher priority`() {
            // Given
            val difficultWrong = createWrongAnswer(questionDifficulty = 4, daysAgo = 1)
            val easyWrong = createWrongAnswer(questionDifficulty = 1, daysAgo = 1)
            
            // When
            val difficultPriority = spacedRepetition.calculatePriority(difficultWrong, 0, emptyList())
            val easyPriority = spacedRepetition.calculatePriority(easyWrong, 0, emptyList())
            
            // Then
            assertTrue(difficultPriority > easyPriority)
        }

        @Test
        @DisplayName("已过期复习的应有更高优先级")
        fun `overdue reviews should have higher priority`() {
            // Given
            val overdue = createWrongAnswer(daysAgo = 5) // 5天前，应该3天后复习，已过期2天
            val onTime = createWrongAnswer(daysAgo = 2)  // 2天前，应该3天后复习，还有1天
            
            // When
            val overduePriority = spacedRepetition.calculatePriority(overdue, 1, listOf(createReviewLog(daysAgo = 3)))
            val onTimePriority = spacedRepetition.calculatePriority(onTime, 0, emptyList())
            
            // Then
            assertTrue(overduePriority > onTimePriority)
        }

        @Test
        @DisplayName("多次错误的应有更高优先级")
        fun `repeatedly wrong questions should have higher priority`() {
            // Given
            val onceWrong = createWrongAnswer(daysAgo = 1)
            val twiceWrong = createWrongAnswer(daysAgo = 1)
            val repeatHistory = listOf(
                createReviewLog(daysAgo = 10, wasCorrect = false),
                createReviewLog(daysAgo = 7, wasCorrect = false)
            )
            
            // When
            val oncePriority = spacedRepetition.calculatePriority(onceWrong, 0, emptyList())
            val twicePriority = spacedRepetition.calculatePriority(twiceWrong, 0, repeatHistory)
            
            // Then
            assertTrue(twicePriority > oncePriority)
        }
    }

    @Nested
    @DisplayName("错题筛选")
    inner class WrongAnswerFiltering {
        
        @Test
        @DisplayName("应从错题列表中筛选出需要复习的")
        fun `should filter wrong answers needing review`() {
            // Given
            val wrongAnswers = listOf(
                createWrongAnswer(questionId = "q1", daysAgo = 1, correctCount = 0), // 需要复习
                createWrongAnswer(questionId = "q2", daysAgo = 0, correctCount = 0),  // 今天刚错，不需要
                createWrongAnswer(questionId = "q3", daysAgo = 5),                   // 已多次复习且答对，不需要
                createWrongAnswer(questionId = "q4", daysAgo = 2, correctCount = 0) // 需要复习
            )
            
            // When
            val needsReview = spacedRepetition.filterNeedingReview(wrongAnswers, currentTime = System.currentTimeMillis())
            
            // Then
            assertEquals(2, needsReview.size)
            assertTrue(needsReview.any { it.questionId == "q1" })
            assertTrue(needsReview.any { it.questionId == "q4" })
        }

        @Test
        @DisplayName("已掌握的错题不应再复习（连续3次答对）")
        fun `should not include mastered questions`() {
            // Given
            val masteredQuestion = createWrongAnswer(
                daysAgo = 1,
                reviewHistory = listOf(
                    createReviewLog(wasCorrect = true),
                    createReviewLog(wasCorrect = true),
                    createReviewLog(wasCorrect = true)
                )
            )
            
            // When
            val needsReview = spacedRepetition.filterNeedingReview(listOf(masteredQuestion), currentTime = System.currentTimeMillis())
            
            // Then
            assertTrue(needsReview.isEmpty())
        }
    }

    private fun createWrongAnswer(
        questionId: String = "q001",
        daysAgo: Int = 1,
        questionDifficulty: Int = 2,
        correctCount: Int = 0,
        reviewHistory: List<ReviewLog> = emptyList()
    ): WrongAnswerItem {
        return WrongAnswerItem(
            questionId = questionId,
            wrongTime = System.currentTimeMillis() - daysAgo * 24 * 60 * 60 * 1000,
            questionDifficulty = questionDifficulty,
            type = QuestionType.CALCULATION,
            consecutiveCorrectCount = correctCount,
            reviewHistory = reviewHistory
        )
    }

    private fun createReviewLog(
        daysAgo: Int = 1,
        wasCorrect: Boolean = false
    ): ReviewLog {
        return ReviewLog(
            reviewTime = System.currentTimeMillis() - daysAgo * 24 * 60 * 60 * 1000,
            wasCorrect = wasCorrect
        )
    }

    // Data classes to match the UseCase
    data class WrongAnswerItem(
        val questionId: String,
        val wrongTime: Long,
        val questionDifficulty: Int,
        val type: QuestionType,
        val consecutiveCorrectCount: Int = 0,
        val reviewHistory: List<ReviewLog> = emptyList()
    )

    data class ReviewLog(
        val reviewTime: Long,
        val wasCorrect: Boolean
    )
}
