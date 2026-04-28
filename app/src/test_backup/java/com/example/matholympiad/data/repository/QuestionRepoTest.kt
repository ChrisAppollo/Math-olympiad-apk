package com.example.matholympiad.data.repository

import com.example.matholympiad.data.local.dao.QuestionDao
import com.example.matholympiad.data.local.model.Question
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("QuestionRepo 测试")
class QuestionRepoTest {

    private lateinit var questionDao: QuestionDao
    private lateinit var questionRepo: QuestionRepo

    @BeforeEach
    fun setup() {
        questionDao = mockk(relaxed = true)
        questionRepo = QuestionRepo(questionDao)
    }

    @Nested
    @DisplayName("获取题目")
    inner class GetQuestions {
        
        @Test
        @DisplayName("应该返回所有题目")
        fun `should return all questions`() = runTest {
            // Given
            val testQuestions = listOf(
                createQuestion("q1"),
                createQuestion("q2"),
                createQuestion("q3")
            )
            coEvery { questionDao.getAllQuestions() } returns testQuestions

            // When
            val result = questionRepo.getAllQuestions()

            // Then
            assertEquals(3, result.size)
        }

        @Test
        @DisplayName("空题库应该返回空列表")
        fun `should return empty list when no questions`() = runTest {
            // Given
            coEvery { questionDao.getAllQuestions() } returns emptyList()

            // When
            val result = questionRepo.getAllQuestions()

            // Then
            assertTrue(result.isEmpty())
        }

        @Test
        @DisplayName("应该可以获取特定ID的题目")
        fun `should get question by id`() = runTest {
            // Given
            val question = createQuestion("q1", content = "Test content")
            coEvery { questionDao.getQuestionById("q1") } returns question

            // When
            val result = questionRepo.getQuestionById("q1")

            // Then
            assertNotNull(result)
            assertEquals("Test content", result?.content)
        }

        @Test
        @DisplayName("不存在的ID应该返回null")
        fun `should return null for non-existent id`() = runTest {
            // Given
            coEvery { questionDao.getQuestionById("nonexistent") } returns null

            // When
            val result = questionRepo.getQuestionById("nonexistent")

            // Then
            assertNull(result)
        }
    }

    @Nested
    @DisplayName("随机题目")
    inner class RandomQuestions {
        
        @Test
        @DisplayName("应该返回指定数量的随机题目")
        fun `should return specified count of random questions`() = runTest {
            // Given
            val allQuestions = (1..10).map { createQuestion("q$it") }
            coEvery { questionDao.getAllQuestions() } returns allQuestions

            // When
            val result = questionRepo.getRandomQuestions(3)

            // Then
            assertEquals(3, result.size)
        }

        @Test
        @DisplayName("当数量超过题库时返回全部题目")
        fun `should return all when count exceeds database`() = runTest {
            // Given
            val allQuestions = (1..5).map { createQuestion("q$it") }
            coEvery { questionDao.getAllQuestions() } returns allQuestions

            // When
            val result = questionRepo.getRandomQuestions(10)

            // Then
            assertEquals(5, result.size)
        }

        @Test
        @DisplayName("返回的题目不应重复")
        fun `should not return duplicate questions`() = runTest {
            // Given
            val allQuestions = (1..20).map { createQuestion("q$it") }
            coEvery { questionDao.getAllQuestions() } returns allQuestions

            // When
            val result = questionRepo.getRandomQuestions(10)

            // Then
            val ids = result.map { it.id }
            assertEquals(ids.distinct().size, ids.size)
        }
    }

    @Nested
    @DisplayName("按类型筛选")
    inner class FilterByType {
        
        @Test
        @DisplayName("应该可以按类型获取题目")
        fun `should get questions by type`() = runTest {
            // Given
            val calculationQuestions = listOf(
                createQuestion("q1", type = "CALCULATION"),
                createQuestion("q2", type = "CALCULATION")
            )
            coEvery { questionDao.getQuestionsByType("CALCULATION") } returns calculationQuestions

            // When
            val result = questionRepo.getQuestionsByType("CALCULATION")

            // Then
            assertEquals(2, result.size)
            result.forEach { assertEquals("CALCULATION", it.type) }
        }

        @Test
        @DisplayName("应该可以按难度获取题目")
        fun `should get questions by difficulty`() = runTest {
            // Given
            val hardQuestions = listOf(
                createQuestion("q1", difficulty = 4),
                createQuestion("q2", difficulty = 5)
            )
            coEvery { questionDao.getQuestionsByDifficulty(4, 5) } returns hardQuestions

            // When
            val result = questionRepo.getQuestionsByDifficulty(min = 4, max = 5)

            // Then
            assertEquals(2, result.size)
        }
    }

    @Nested
    @DisplayName("插入题目")
    inner class InsertQuestions {
        
        @Test
        @DisplayName("应该可以插入新题目")
        fun `should insert new question`() = runTest {
            // Given
            val newQuestion = createQuestion("q_new")

            // When
            questionRepo.insertQuestion(newQuestion)

            // Then
            coVerify { questionDao.insertQuestion(newQuestion) }
        }

        @Test
        @DisplayName("应该可以批量插入题目")
        fun `should insert multiple questions`() = runTest {
            // Given
            val questions = listOf(
                createQuestion("q1"),
                createQuestion("q2"),
                createQuestion("q3")
            )

            // When
            questionRepo.insertQuestions(questions)

            // Then
            coVerify { questionDao.insertQuestions(questions) }
        }
    }

    private fun createQuestion(
        id: String,
        content: String = "Test content",
        type: String = "CALCULATION",
        difficulty: Int = 2
    ): Question {
        return Question(
            id = id,
            content = content,
            options = "[\"A\", \"B\", \"C\", \"D\"]",
            correctAnswer = 0,
            explanation = "Test explanation",
            type = type,
            difficulty = difficulty
        )
    }
}
