package com.example.matholympiad.data.repository

import com.example.matholympiad.data.local.dao.UserDao
import com.example.matholympiad.data.local.model.User
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

@DisplayName("UserRepo 测试")
class UserRepoTest {

    private lateinit var userDao: UserDao
    private lateinit var userRepo: UserRepo

    @BeforeEach
    fun setup() {
        userDao = mockk(relaxed = true)
        userRepo = UserRepo(userDao)
    }

    @Nested
    @DisplayName("获取用户")
    inner class GetUser {
        
        @Test
        @DisplayName("应该返回默认用户")
        fun `should return default user`() = runTest {
            // Given
            val testUser = createTestUser(totalScore = 100)
            coEvery { userDao.getDefaultUser() } returns testUser

            // When
            val result = userRepo.getDefaultUser()

            // Then
            assertEquals(100, result.totalScore)
        }

        @Test
        @DisplayName("不存在用户时应该创建新用户")
        fun `should create user if not exists`() = runTest {
            // Given
            coEvery { userDao.getDefaultUser() } returns null

            // When
            val result = userRepo.getOrCreateUser()

            // Then
            coVerify { userDao.insertUser(any()) }
            assertNotNull(result)
        }
    }

    @Nested
    @DisplayName("更新积分")
    inner class UpdateScore {
        
        @Test
        @DisplayName("答对应该增加积分")
        fun `should add score when correct`() = runTest {
            // Given
            val user = createTestUser(totalScore = 50)
            coEvery { userDao.getDefaultUser() } returns user

            // When
            userRepo.updateScore(points = 10, isCorrect = true)

            // Then
            coVerify { 
                userDao.updateUser(match { it.totalScore == 60 })
            }
        }

        @Test
        @DisplayName("答错不减积分")
        fun `should not subtract score when wrong`() = runTest {
            // Given
            val user = createTestUser(totalScore = 50)
            coEvery { userDao.getDefaultUser() } returns user

            // When
            userRepo.updateScore(points = 10, isCorrect = false)

            // Then
            coVerify { 
                userDao.updateUser(match { it.totalScore == 50 })
            }
        }

        @Test
        @DisplayName("答对应该增加答题统计")
        fun `should increment counts when correct`() = runTest {
            // Given
            val user = createTestUser(totalAnswered = 10, totalCorrect = 8)
            coEvery { userDao.getDefaultUser() } returns user

            // When
            userRepo.updateScore(points = 10, isCorrect = true)

            // Then
            coVerify { 
                userDao.updateUser(match { 
                    it.totalAnswered == 11 && it.totalCorrect == 9
                })
            }
        }
    }

    @Nested
    @DisplayName("勋章系统")
    inner class BadgeSystem {
        
        @Test
        @DisplayName("解锁新勋章应该添加到用户徽章列表")
        fun `should add badge to user's list`() = runTest {
            // Given
            val user = createTestUser(badges = listOf("first_steps"))
            coEvery { userDao.getDefaultUser() } returns user

            // When
            userRepo.unlockBadge("quick_learner")

            // Then
            coVerify { 
                userDao.updateUser(match { 
                    "quick_learner" in it.getBadgesList()
                })
            }
        }

        @Test
        @DisplayName("重复解锁勋章不应重复添加")
        fun `should not add duplicate badges`() = runTest {
            // Given
            val user = createTestUser(badges = listOf("first_steps"))
            coEvery { userDao.getDefaultUser() } returns user

            // When
            userRepo.unlockBadge("first_steps")

            // Then - 确保不更新数据库（因为徽章已存在）
            coVerify(exactly = 0) { userDao.updateUser(any()) }
        }
    }

    private fun createTestUser(
        totalScore: Int = 0,
        badges: List<String> = emptyList(),
        totalAnswered: Int = 0,
        totalCorrect: Int = 0
    ): User {
        return User(
            userId = "test_user",
            totalScore = totalScore,
            badges = "[${badges.joinToString(",") { "\"$it\"" }}]",
            lastLoginDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
            totalAnswered = totalAnswered,
            totalCorrect = totalCorrect
        )
    }
}
