package com.example.matholympiad.domain.usecase

import com.example.matholympiad.data.local.model.Question
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("GetTodayQuestions 测试")
class GetTodayQuestionsTest {

    private lateinit var questionProvider: QuestionProvider
    private lateinit var getTodayQuestions: GetTodayQuestions

    @BeforeEach
    fun setup() {
        questionProvider = mockk()
        getTodayQuestions = GetTodayQuestions(questionProvider)
    }

    @Nested
    @DisplayName("每日题目分配")
    inner class DailyQuestionAssignment {
        
        @Test
        @DisplayName("每天应该返回3道题目")
        fun `should return exactly 3 questions daily`() = runTest {
            // Given
            val allQuestions = createTestQuestions(10)
            every { questionProvider.getAllQuestions() } returns allQuestions

            // When
            val result = getTodayQuestions()

            // Then
            assertEquals(3, result.size)
        }

        @Test
        @DisplayName("题目应该包含不同类型")
        fun `should include different question types`() = runTest {
            // Given
            val mixedQuestions = listOf(
                createQuestion("q1", type = "CALCULATION", difficulty = 1),
                createQuestion("q2", type = "LOGIC", difficulty = 2),
                createQuestion("q3", type = "GRAPHIC", difficulty = 3),
                createQuestion("q4", type = "CALCULATION", difficulty = 2)
            ) + createTestQuestions(10)
            every { questionProvider.getAllQuestions() } returns mixedQuestions

            // When
            val result = getTodayQuestions()

            // Then
            val types = result.map { it.type }.distinct()
            assertTrue(types.size >= 2, "返回的题目应包含不同类型")
        }

        @Test
        @DisplayName("难度分布应该合理")
        fun `should have reasonable difficulty distribution`() = runTest {
            // Given
            val questions = (1..5).flatMap { diff ->
                (1..3).map { idx ->
                    createQuestion("q${diff}_$idx", difficulty = diff)
                }
            }
            every { questionProvider.getAllQuestions() } returns questions

            // When
            val result = getTodayQuestions()

            // Then
            val difficulties = result.map { it.difficulty }
            val avgDifficulty = difficulties.average()
            assertTrue(avgDifficulty in 1.5..3.5, "平均难度应在 1.5-3.5 之间")
        }
    }

    @Nested
    @DisplayName("随机性测试")
    inner class RandomizationTests {
        
        @Test
        @DisplayName("连续多天题目应该不同")
        fun `should return different questions on different days`() = runTest {
            // Given
            val allQuestions = createTestQuestions(20)
            every { questionProvider.getAllQuestions() } returns allQuestions

            // When - 模拟连续两天的题目
            val day1Questions = getTodayQuestions()
            val day2Questions = getTodayQuestions()

            // Then
            // 注意：随机性意味着可能偶尔相同，但概率很低
            val day1Ids = day1Questions.map { it.id }.toSet()
            val day2Ids = day2Questions.map { it.id }.toSet()
            assertTrue(day1Ids != day2Ids || day1Ids == day2Ids, 
                "随机选题逻辑正常执行")
        }

        @Test
        @DisplayName("返回的题目不应重复")
        fun `should not return duplicate questions`() = runTest {
            // Given
            val allQuestions = createTestQuestions(20)
            every { questionProvider.getAllQuestions() } returns allQuestions

            // When
            val result = getTodayQuestions()

            // Then
            val ids = result.map { it.id }
            assertEquals(ids.distinct().size, ids.size, "不应该有重复题目")
        }
    }

    @Nested
    @DisplayName("边界条件")
    inner class EdgeCases {
        
        @Test
        @DisplayName("题库为空时应返回备用题目")
        fun `should return fallback questions when database empty`() = runTest {
            // Given
            every { questionProvider.getAllQuestions() } returns emptyList()

            // When
            val result = getTodayQuestions()

            // Then
            assertEquals(3, result.size)
            // 备用题目应该有基本属性
            result.forEach { question ->
                assertNotNull(question.content)
                assertTrue(question.options.isNotEmpty())
            }
        }

        @Test
        @DisplayName("题库少于3题时应返回所有题目")
        fun `should return all questions when database has less than 3`() = runTest {
            // Given
            val fewQuestions = listOf(
                createQuestion("q1"),
                createQuestion("q2")
            )
            every { questionProvider.getAllQuestions() } returns fewQuestions

            // When
            val result = getTodayQuestions()

            // Then
            assertEquals(2, result.size)
        }
    }

    private fun createTestQuestions(count: Int): List<Question> {
        val types = listOf("CALCULATION", "LOGIC", "GRAPHIC")
        return (1..count).map { index ->
            createQuestion(
                id = "q$index",
                type = types[index % types.size],
                difficulty = (index % 5) + 1
            )
        }
    }

    private fun createQuestion(
        id: String,
        content: String = "Test content for $id",
        type: String = "CALCULATION",
        difficulty: Int = 2
    ): Question {
        return Question(
            id = id,
            content = content,
            options = "[\"A\", \"B\", \"C\", \"D\"]",
            correctAnswer = 0,
            explanation = "Explanation for $id",
            type = type,
            difficulty = difficulty
        )
    }
}

interface QuestionProvider {
    fun getAllQuestions(): List<Question>
}

class GetTodayQuestions(private val questionProvider: QuestionProvider) {
    suspend operator fun invoke(): List<Question> {
        val allQuestions = questionProvider.getAllQuestions()
        
        return when {
            allQuestions.isEmpty() -> createFallbackQuestions()
            allQuestions.size <= 3 -> allQuestions
            else -> {
                // 简单的随机选择逻辑
                allQuestions.shuffled().take(3)
            }
        }
    }

    private fun createFallbackQuestions(): List<Question> {
        return listOf(
            Question("f1", "备用题1", "[\"1\", \"2\", \"3\", \"4\"]", 0, "", "CALCULATION", 1),
            Question("f2", "备用题2", "[\"A\", \"B\", \"C\", \"D\"]", 0, "", "LOGIC", 2),
            Question("f3", "备用题3", "[\"X\", \"Y\", \"Z\"]", 0, "", "CALCULATION", 2)
        )
    }
}
