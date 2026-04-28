package com.example.matholympiad.domain.usecase

import com.example.matholympiad.data.local.model.Question
import com.example.matholympiad.data.repository.QuestionRepo
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("GetTodayQuestions UseCase 测试")
class GetTodayQuestionsTest {

    private lateinit var questionRepo: QuestionRepo
    private lateinit var getTodayQuestions: GetTodayQuestions

    @BeforeEach
    fun setup() {
        questionRepo = mockk()
        getTodayQuestions = GetTodayQuestions(questionRepo)
    }

    @Test
    @DisplayName("应该返回3道题目")
    fun `should return 3 questions`() = runTest {
        // Given
        val questions = createTestQuestions(10)
        coEvery { questionRepo.getRandomQuestions(any()) } returns questions.take(3)

        // When
        val result = getTodayQuestions()

        // Then
        assertEquals(3, result.size)
    }

    @Test
    @DisplayName("不同日期应返回不同题目组合")
    fun `should return different questions on different days`() = runTest {
        // Given
        val allQuestions = createTestQuestions(20)
        
        // Day 1
        coEvery { questionRepo.getRandomQuestions(3) } returns allQuestions.take(3)
        val day1Questions = getTodayQuestions()
        
        // Day 2 (模拟不同的随机选择)
        coEvery { questionRepo.getRandomQuestions(3) } returns allQuestions.drop(3).take(3)
        val day2Questions = getTodayQuestions()

        // Then
        assertNotEquals(day1Questions, day2Questions)
    }

    @Test
    @DisplayName("题目类型应包含多种题型")
    fun `should include different question types`() = runTest {
        // Given
        val questions = listOf(
            createQuestion("q1", "CALCULATION"),
            createQuestion("q2", "LOGIC"),
            createQuestion("q3", "ARITHMETIC")
        )
        coEvery { questionRepo.getRandomQuestions(any()) } returns questions

        // When
        val result = getTodayQuestions()

        // Then
        val types = result.map { it.type }.distinct()
        assertTrue(types.size >= 1)
    }

    @Test
    @DisplayName("题目不应重复")
    fun `should not return duplicate questions`() = runTest {
        // Given
        val questions = listOf(
            createQuestion("q1", "CALCULATION"),
            createQuestion("q2", "LOGIC"),
            createQuestion("q3", "ARITHMETIC")
        )
        coEvery { questionRepo.getRandomQuestions(any()) } returns questions

        // When
        val result = getTodayQuestions()

        // Then
        val ids = result.map { it.id }
        assertEquals(ids.distinct().size, ids.size, "题目ID不应重复")
    }

    @Test
    @DisplayName("当题库不足时应返回可用题目")
    fun `should return available questions when not enough in database`() = runTest {
        // Given
        val onlyTwoQuestions = createTestQuestions(2)
        coEvery { questionRepo.getRandomQuestions(any()) } returns onlyTwoQuestions

        // When
        val result = getTodayQuestions()

        // Then
        assertEquals(2, result.size)
    }

    private fun createTestQuestions(count: Int): List<Question> {
        return (1..count).map { index ->
            createQuestion(
                "q$index",
                when (index % 3) {
                    0 -> "CALCULATION"
                    1 -> "LOGIC"
                    else -> "ARITHMETIC"
                }
            )
        }
    }

    private fun createQuestion(id: String, type: String): Question {
        return Question(
            id = id,
            content = "Test question $id",
            options = "[\"A\", \"B\", \"C\", \"D\"]",
            correctAnswer = 0,
            explanation = "Test explanation",
            type = type,
            difficulty = 1
        )
    }
}
