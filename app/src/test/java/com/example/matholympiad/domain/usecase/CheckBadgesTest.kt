package com.example.matholympiad.domain.usecase

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@DisplayName("CheckBadges 测试")
class CheckBadgesTest {

    private val checkBadges = CheckBadges()

    @Nested
    @DisplayName("积分勋章")
    inner class PointsBadges {
        
        @Test
        @DisplayName("100积分解锁\"小学者\"勋章")
        fun `should unlock scholar badge at 100 points`() {
            // Given
            val user = createTestUser(totalScore = 100)

            // When
            val result = checkBadges(user)

            // Then
            assertTrue(result.contains("scholar"))
        }

        @Test
        @DisplayName("500积分解锁\"算术大师\"勋章")
        fun `should unlock master badge at 500 points`() {
            // Given
            val user = createTestUser(totalScore = 500)

            // When
            val result = checkBadges(user)

            // Then
            assertTrue(result.contains("master"))
        }

        @Test
        @DisplayName("未达门槛时不应解锁")
        fun `should not unlock badge below threshold`() {
            // Given
            val user = createTestUser(totalScore = 50)

            // When
            val result = checkBadges(user)

            // Then
            assertFalse(result.contains("scholar"))
        }
    }

    @Nested
    @DisplayName("连续答题勋章")
    inner class StreakBadges {
        
        @Test
        @DisplayName("连续7天答题解锁\"持之以恒\"勋章")
        fun `should unlock persistent badge at 7 day streak`() {
            // Given
            val user = createTestUser(consecutiveDays = 7)

            // When
            val result = checkBadges(user)

            // Then
            assertTrue(result.contains("persistent"))
        }

        @Test
        @DisplayName("连续30天答题解锁\"学习达人\"勋章")
        fun `should unlock learner badge at 30 day streak`() {
            // Given
            val user = createTestUser(consecutiveDays = 30)

            // When
            val result = checkBadges(user)

            // Then
            assertTrue(result.contains("learner"))
        }
    }

    @Nested
    @DisplayName("答题数量勋章")
    inner class AnswerCountBadges {
        
        @Test
        @DisplayName("累计答对50题解锁\"答题高手\"勋章")
        fun `should unlock expert badge at 50 correct answers`() {
            // Given
            val user = createTestUser(totalCorrect = 50, totalAnswered = 100)

            // When
            val result = checkBadges(user)

            // Then
            assertTrue(result.contains("expert"))
        }

        @Test
        @DisplayName("累计答对100题解锁\"答题王者\"勋章")
        fun `should unlock king badge at 100 correct answers`() {
            // Given
            val user = createTestUser(totalCorrect = 100, totalAnswered = 200)

            // When
            val result = checkBadges(user)

            // Then
            assertTrue(result.contains("king"))
        }
    }

    @Nested
    @DisplayName("满分勋章")
    inner class PerfectScoreBadges {
        
        @Test
        @DisplayName("全对答题解锁\"完美答题\"勋章")
        fun `should unlock perfect badge when all correct`() {
            // Given
            val user = createTestUser(totalCorrect = 3, totalAnswered = 3)

            // When
            val result = checkBadges(user, currentSessionCorrect = 3, currentSessionTotal = 3)

            // Then
            assertTrue(result.contains("perfect"))
        }
    }

    private fun createTestUser(
        totalScore: Int = 0,
        badges: List<String> = emptyList(),
        consecutiveDays: Int = 0,
        totalCorrect: Int = 0,
        totalAnswered: Int = 0
    ): TestUser {
        return TestUser(
            userId = "test_user",
            totalScore = totalScore,
            badges = badges,
            consecutiveDays = consecutiveDays,
            totalCorrect = totalCorrect,
            totalAnswered = totalAnswered
        )
    }
}

data class TestUser(
    val userId: String,
    val totalScore: Int,
    val badges: List<String> = emptyList(),
    val lastLoginDate: String = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
    val consecutiveDays: Int,
    val totalCorrect: Int,
    val totalAnswered: Int
)

class CheckBadges {
    operator fun invoke(
        user: TestUser,
        currentSessionCorrect: Int = 0,
        currentSessionTotal: Int = 0
    ): List<String> {
        val newBadges = mutableListOf<String>()

        // 积分勋章
        if (user.totalScore >= 100 && "scholar" !in user.badges) {
            newBadges.add("scholar")
        }
        if (user.totalScore >= 500 && "master" !in user.badges) {
            newBadges.add("master")
        }

        // 连续答题勋章
        if (user.consecutiveDays >= 7 && "persistent" !in user.badges) {
            newBadges.add("persistent")
        }
        if (user.consecutiveDays >= 30 && "learner" !in user.badges) {
            newBadges.add("learner")
        }

        // 答题数量勋章
        if (user.totalCorrect >= 50 && "expert" !in user.badges) {
            newBadges.add("expert")
        }
        if (user.totalCorrect >= 100 && "king" !in user.badges) {
            newBadges.add("king")
        }

        // 满分勋章
        if (currentSessionCorrect == currentSessionTotal && currentSessionTotal > 0 && "perfect" !in user.badges) {
            newBadges.add("perfect")
        }

        return newBadges
    }
}
