package com.example.matholympiad.presentation.ui.quiz

import com.example.matholympiad.data.local.model.Question
import com.example.matholympiad.data.repository.QuestionRepo
import com.example.matholympiad.data.repository.UserRepo
import com.example.matholympiad.domain.model.SubmitAnswerResult
import com.example.matholympiad.domain.usecase.CheckBadges
import com.example.matholympiad.domain.usecase.EncouragementGenerator
import com.example.matholympiad.domain.usecase.SubmitAnswerUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("QuizViewModel 测试")
class QuizViewModelTest {

    private lateinit var userRepo: UserRepo
    private lateinit var questionRepo: QuestionRepo
    private lateinit var submitAnswerUseCase: SubmitAnswerUseCase
    private lateinit var encouragementGenerator: EncouragementGenerator
    private lateinit var checkBadges: CheckBadges
    private lateinit var viewModel: QuizViewModel

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        userRepo = mockk()
        questionRepo = mockk()
        submitAnswerUseCase = mockk()
        encouragementGenerator = mockk()
        checkBadges = mockk()
        
        // 默认模拟数据
        coEvery { questionRepo.getAllQuestions() } returns createTestQuestions(5)
        coEvery { encouragementGenerator.getCorrectEncouragement() } returns "太棒了！"
        coEvery { checkBadges() } returns emptyList()

        viewModel = QuizViewModel(
            userRepo, questionRepo, submitAnswerUseCase, 
            encouragementGenerator, checkBadges
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Nested
    @DisplayName("初始化")
    inner class Initialization {
        
        @Test
        @DisplayName("初始化时应该加载题目")
        fun `should load questions on init`() = runTest(testDispatcher) {
            // Then
            val state = viewModel.uiState.value
            assertEquals(3, state.totalQuestions)
            assertNotNull(state.currentQuestion)
            assertFalse(state.loading)
        }

        @Test
        @DisplayName("题库为空时应使用备用题目")
        fun `should use fallback questions when database empty`() = runTest(testDispatcher) {
            // Given
            coEvery { questionRepo.getAllQuestions() } returns emptyList()
            
            // When
            val newViewModel = QuizViewModel(
                userRepo, questionRepo, submitAnswerUseCase,
                encouragementGenerator, checkBadges
            )
            
            // Then
            val state = newViewModel.uiState.value
            assertEquals(3, state.totalQuestions) // 3个备用题
            assertNotNull(state.currentQuestion)
        }
    }

    @Nested
    @DisplayName("选择答案")
    inner class SelectAnswer {
        
        @Test
        @DisplayName("可以选择答案并更新状态")
        fun `should update selected answer`() = runTest(testDispatcher) {
            // When
            viewModel.selectAnswer(2)
            
            // Then
            assertEquals(2, viewModel.uiState.value.selectedAnswer)
        }

        @Test
        @DisplayName("已显示反馈时不应允许选择新答案")
        fun `should not allow answer selection when feedback showing`() = runTest(testDispatcher) {
            // Given - 先提交答案进入反馈状态
            viewModel.selectAnswer(0)
            coEvery { 
                submitAnswerUseCase(any(), any(), any(), any()) 
            } returns SubmitAnswerResult(true, 10, emptyList())
            viewModel.submitAnswer()
            testDispatcher.scheduler.advanceUntilIdle()
            
            // When
            viewModel.selectAnswer(3)
            
            // Then - 选择的答案应保持为第一次选择的
            assertEquals(0, viewModel.uiState.value.selectedAnswer)
        }
    }

    @Nested
    @DisplayName("提交答案")
    inner class SubmitAnswer {
        
        @Test
        @DisplayName("答对时应该更新分数和显示鼓励")
        fun `should update score and show encouragement when correct`() = runTest(testDispatcher) {
            // Given
            viewModel.selectAnswer(0) // 正确答案索引为0
            coEvery { 
                submitAnswerUseCase(any(), any(), any(), any()) 
            } returns SubmitAnswerResult(
                isCorrect = true,
                newScore = 10,
                newBadgesUnlocked = emptyList()
            )
            
            // When
            viewModel.submitAnswer()
            testDispatcher.scheduler.advanceUntilIdle()
            
            // Then
            val state = viewModel.uiState.value
            assertTrue(state.feedbackShowing)
            assertTrue(state.isCorrect == true)
            assertEquals(10, state.score)
        }

        @Test
        @DisplayName("答错时应该显示正确答案")
        fun `should show correct answer when wrong`() = runTest(testDispatcher) {
            // Given
            val question = createQuestion("q1", 0)
            viewModel.selectAnswer(1) // 选择错误答案
            coEvery { 
                submitAnswerUseCase(any(), any(), any(), any()) 
            } returns SubmitAnswerResult(
                isCorrect = false,
                newScore = 0,
                newBadgesUnlocked = emptyList()
            )
            
            // When
            viewModel.submitAnswer()
            testDispatcher.scheduler.advanceUntilIdle()
            
            // Then
            val state = viewModel.uiState.value
            assertTrue(state.feedbackShowing)
            assertFalse(state.isCorrect == true)
            assertTrue(state.encouragement.contains("正确答案是"))
        }

        @Test
        @DisplayName("提交时应该记录答题")
        fun `should record answer when submitted`() = runTest(testDispatcher) {
            // Given
            viewModel.selectAnswer(0)
            
            // When
            viewModel.submitAnswer()
            testDispatcher.scheduler.advanceUntilIdle()
            
            // Then
            coVerify { 
                submitAnswerUseCase(any(), any(), any(), any())
            }
        }

        @Test
        @DisplayName("新解锁的勋章应该被显示")
        fun `should display newly unlocked badges`() = runTest(testDispatcher) {
            // Given
            viewModel.selectAnswer(0)
            coEvery { 
                submitAnswerUseCase(any(), any(), any(), any()) 
            } returns SubmitAnswerResult(
                isCorrect = true,
                newScore = 10,
                newBadgesUnlocked = listOf("perfect_score")
            )
            
            // When
            viewModel.submitAnswer()
            testDispatcher.scheduler.advanceUntilIdle()
            
            // Then
            val state = viewModel.uiState.value
            assertTrue(state.earnedBadges.contains("perfect_score"))
        }
    }

    @Nested
    @DisplayName("下一题")
    inner class NextQuestion {
        
        @Test
        @DisplayName("切换到下一题时应重置状态")
        fun `should reset state when next question`() = runTest(testDispatcher) {
            // Given
            viewModel.selectAnswer(0)
            coEvery { 
                submitAnswerUseCase(any(), any(), any(), any()) 
            } returns SubmitAnswerResult(true, 10, emptyList())
            viewModel.submitAnswer()
            testDispatcher.scheduler.advanceUntilIdle()
            
            // When
            viewModel.nextQuestion()
            testDispatcher.scheduler.advanceUntilIdle()
            
            // Then
            val state = viewModel.uiState.value
            assertFalse(state.feedbackShowing)
            assertNull(state.selectedAnswer)
            assertFalse(state.hintShowing)
        }

        @Test
        @DisplayName("答完所有题目后应该显示完成状态")
        fun `should show completion after all questions`() = runTest(testDispatcher) {
            // Given - 模拟完成3道题
            repeat(3) {
                viewModel.selectAnswer(0)
                coEvery { 
                    submitAnswerUseCase(any(), any(), any(), any()) 
                } returns SubmitAnswerResult(true, 10, emptyList())
                viewModel.submitAnswer()
                testDispatcher.scheduler.advanceUntilIdle()
                viewModel.nextQuestion()
                testDispatcher.scheduler.advanceUntilIdle()
            }
            
            // Then
            val state = viewModel.uiState.value
            assertTrue(state.quizCompleted)
            assertTrue(state.encouragement.contains("闯关完成"))
        }
    }

    @Nested
    @DisplayName("提示功能")
    inner class HintFeature {
        
        @Test
        @DisplayName("显示提示应该更新UI状态")
        fun `should show hint when requested`() = runTest(testDispatcher) {
            // Given
            val question = createQuestion("q1", 0, explanation = "答案是5")
            coEvery { questionRepo.getAllQuestions() } returns listOf(question)
            
            // When
            viewModel.showHint()
            testDispatcher.scheduler.advanceUntilIdle()
            
            // Then
            val state = viewModel.uiState.value
            assertTrue(state.hintShowing)
            assertTrue(state.hintText.isNotEmpty())
        }

        @Test
        @DisplayName("关闭提示应该更新UI状态")
        fun `should dismiss hint`() = runTest(testDispatcher) {
            // Given
            viewModel.showHint()
            testDispatcher.scheduler.advanceUntilIdle()
            
            // When
            viewModel.dismissHint()
            
            // Then
            assertFalse(viewModel.uiState.value.hintShowing)
        }
    }

    private fun createTestQuestions(count: Int): List<Question> {
        return (1..count).map { index ->
            createQuestion("q$index", 0)
        }
    }

    private fun createQuestion(
        id: String, 
        correctIndex: Int,
        explanation: String = "解释"
    ): Question {
        return Question(
            id = id,
            content = "Test question $id",
            options = "[\"A\", \"B\", \"C\", \"D\"]",
            correctAnswer = correctIndex,
            explanation = explanation,
            type = "CALCULATION",
            difficulty = 2
        )
    }
}

// Mock data class for SubmitAnswerResult
data class SubmitAnswerResult(
    val isCorrect: Boolean,
    val newScore: Int,
    val newBadgesUnlocked: List<String>
)
