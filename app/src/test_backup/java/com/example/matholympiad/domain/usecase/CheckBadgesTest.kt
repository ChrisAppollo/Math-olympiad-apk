package com.example.matholympiad.domain.usecase

import com.example.matholympiad.data.local.model.Badge
import com.example.matholympiad.data.local.model.User
import com.example.matholympiad.data.repository.UserRepo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@DisplayName("CheckBadgesUseCase 测试")
class CheckBadgesTest {

    private lateinit var userRepo: UserRepo
    private lateinit var checkBadges: CheckBadges

    @BeforeEach
    fun setup() {
        userRepo = mockk(relaxed = true)
        checkBadges = CheckBadges(userRepo)
    }

    @Nested
    @DisplayName("点数勋章")
    inner class PointsBadges {
        
        @Test
        @DisplayName("达到100积分解锁小学者勋章")
        fun `should unlock quick_learner at 100 points`() = runTest {
            // Given
            val user = createTestUser(totalScore = 99, badges = emptyList())
            coEvery { userRepo.getCurrentUser() } returns user
            coEvery { userRepo.updateUser(any()) } returns Unit

            // When
            val newBadges = checkBadges.checkNewlyUnlockedBadges(user.copy(totalScore = 100))

            // Then
            assertTrue("quick_learner" in newBadges)
        }

        @Test
        @DisplayName("达到5000积分解锁奥数大师勋章")
        fun `should unlock grand_master at 5000 points`() = runTest {
            // Given
            val user = createTestUser(totalScore = 4999, badges = emptyList())

            // When
            val newBadges = checkBadges.checkNewlyUnlockedBadges(user.copy(totalScore = 5000))

            // Then
            assertTrue("grand_master" in newBadges)
        }

        @Test
        @DisplayName("已有勋章不应重复解锁")
        fun `should not unlock already earned badges`() = runTest {
            // Given
            val user = createTestUser(
                totalScore = 200,
                badges = listOf("quick_learner")
            )

            // When
            val newBadges = checkBadges.checkNewlyUnlockedBadges(user)

            // Then
            assertFalse("quick_learner" in newBadges)
        }
    }

    @Nested
    @DisplayName("首次答题勋章")
    inner class FirstAnswerBadge {
        
        @Test
        @DisplayName("完成第一次答题解锁初次尝试勋章")
        fun `should unlock first_steps on first answer`() = runTest {
            // Given
            val user = createTestUser(totalAnswered = 0, badges = emptyList())

            // When
            val newBadges = checkBadges.checkNewlyUnlockedBadges(user.copy(totalAnswered = 1))

            // Then
            assertTrue("first_steps" in newBadges)
        }
    }

    @Nested
    @DisplayName("正确率勋章")
    inner class AccuracyBadge {
        
        @Test
        @DisplayName("单日满分解锁满分高手勋章")
        fun `should unlock perfect_score at 100% accuracy`() = runTest {
            // Given
            val user = createTestUser(
                todayQuestionsCompleted = 3,
                totalCorrect = 3,
                totalAnswered = 3, // 当天3题全对
                badges = emptyList()
            )

            // When
            val newBadges = checkBadges.checkNewlyUnlockedBadges(user)

            // Then
            assertTrue("perfect_score" in newBadges)
        }

        @Test
        @DisplayName("未当天全对不应解锁满分高手")
        fun `should not unlock perfect_score without 100% accuracy`() = runTest {
            // Given
            val user = createTestUser(
                totalCorrect = 2,
                totalAnswered = 3,
                badges = emptyList()
            )

            // When
            val newBadges = checkBadges.checkNewlyUnlockedBadges(user)

            // Then
            assertFalse("perfect_score" in newBadges)
        }
    }

    @Nested
    @DisplayName("连续打卡勋章")
    inner class StreakBadges {
        
        @Test
        @DisplayName("连续7天解锁坚持一周勋章")
        fun `should unlock daily_streak_7 at 7 days streak`() = runTest {
            // Given
            val user = createTestUser(streakCount = 7, badges = emptyList())

            // When
            val newBadges = checkBadges.checkNewlyUnlockedBadges(user)

            // Then
            assertTrue("daily_streak_7" in newBadges)
        }

        @Test
        @DisplayName("连续30天解锁月度之星勋章")
        fun `should unlock daily_streak_30 at 30 days streak`() = runTest {
            // Given
            val user = createTestUser(streakCount = 30, badges = emptyList())

            // When
            val newBadges = checkBadges.checkNewlyUnlockedBadges(user)

            // Then
            assertTrue("daily_streak_30" in newBadges)
        }
    }

    @Test
    @DisplayName("解锁的勋章应保存到用户数据中")
    fun `should save unlocked badges to user`() = runTest {
        // Given
        val user = createTestUser(totalScore = 100, badges = emptyList())
        coEvery { userRepo.getCurrentUser() } returns user
        coEvery { userRepo.updateUser(any()) } returns Unit

        // When
        checkBadges.checkAndUnlockBadges(user)

        // Then
        coVerify { 
            userRepo.updateUser(match { 
                "quick_learner" in it.getBadgesList()
            }) 
        }
    }

    private fun createTestUser(
        totalScore: Int = 0,
        totalAnswered: Int = 0,
        totalCorrect: Int = 0,
        streakCount: Int = 0,
        todayQuestionsCompleted: Int = 0,
        badges: List<String> = emptyList()
    ): User {
        return User(
            userId = "test_user",
            totalScore = totalScore,
            badges = "[${badges.joinToString(",") { "\"$it\"" }}]",
            streakCount = streakCount,
            lastLoginDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
            todayQuestionsCompleted = todayQuestionsCompleted,
            totalAnswered = totalAnswered,
            totalCorrect = totalCorrect
        )
    }
}
