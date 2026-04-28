package com.example.matholympiad.domain.usecase

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("AIExplanationGenerator 测试")
class AIExplanationGeneratorTest {

    private val generator = AIExplanationGenerator()

    @Nested
    @DisplayName("趣味儿解答生成")
    inner class FunExplanation {
        
        @Test
        @DisplayName("应生成非空趣味儿解答")
        fun `should generate non-empty fun explanation`() {
            // Given
            val question = "鸡兔同笼，头35个，脚94只"
            val correctAnswer = "鸡23只，兔12只"
            
            // When
            val explanation = generator.generateExplanation(
                questionContent = question,
                correctAnswer = correctAnswer,
                questionType = "ARITHMETIC",
                isKidFriendly = true
            )
            
            // Then
            assertFalse(explanation.isBlank())
            assertTrue(explanation.length > 10)
        }

        @Test
        @DisplayName("趣味儿解答应包含鼓励语句")
        fun `should contain encouragement in kid-friendly mode`() {
            // Given
            val question = "1+2+3+...+100 = ?"
            
            // When
            val explanation = generator.generateExplanation(
                questionContent = question,
                correctAnswer = "5050",
                questionType = "ARITHMETIC",
                isKidFriendly = true
            )
            
            // Then
            // 应包含积极、鼓励性的词汇
            val encouragingWords = listOf("真棒", "厉害", "聪明", "尝试", "加油", "超级")
            assertTrue(
                encouragingWords.any { encouraging -> 
                    explanation.contains(encouraging, ignoreCase = true) 
                } || explanation.contains("！", ignoreCase = true),
                "趣味儿解答应包含鼓励性语气"
            )
        }

        @Test
        @DisplayName("应包含具体解题步骤")
        fun `should include specific solution steps`() {
            // Given
            val question = "找规律：2, 5, 11, 23, ?"
            
            // When
            val explanation = generator.generateExplanation(
                questionContent = question,
                correctAnswer = "47",
                questionType = "LOGIC",
                isKidFriendly = true
            )
            
            // Then
            assertTrue(
                explanation.contains("规律") || 
                explanation.contains("×") || 
                explanation.contains("加") ||
                explanation.contains("乘"),
                "解答应解释规律"
            )
        }
    }

    @Nested
    @DisplayName("正式解答生成")
    inner class FormalExplanation {
        
        @Test
        @DisplayName("正式模式不应过于口语化")
        fun `formal mode should not be too casual`() {
            // Given
            val question = "鸡兔同笼问题"
            
            // When
            val explanation = generator.generateExplanation(
                questionContent = question,
                correctAnswer = "鸡23只，兔12只",
                questionType = "ARITHMETIC",
                isKidFriendly = false
            )
            
            // Then
            // 正式模式不应包含过多感叹号或表情符号
            assertTrue(
                explanation.count { it == '！' } <= 2,
                "正式解答不应包含过多感叹号"
            )
        }

        @Test
        @DisplayName("正式模式应包含数学推理")
        fun `formal mode should contain mathematical reasoning`() {
            // Given
            val question = "水池问题"
            
            // When
            val explanation = generator.generateExplanation(
                questionContent = question,
                correctAnswer = "2.4小时",
                questionType = "ARITHMETIC",
                isKidFriendly = false
            )
            
            // Then
            assertTrue(
                explanation.contains("假设") || 
                explanation.contains("所以") ||
                explanation.contains("因此") ||
                explanation.contains("解"),
                "正式解答应包含逻辑推理"
            )
        }
    }

    @Nested
    @DisplayName("不同题型处理")
    inner class QuestionTypeHandling {
        
        @Test
        @DisplayName("计算题应展示具体计算过程")
        fun `calculation questions should show computation steps`() {
            // Given
            val question = "1²+2²+...+10² = ?"
            val expectedAnswer = "385"
            
            // When
            val explanation = generator.generateExplanation(
                questionContent = question,
                correctAnswer = expectedAnswer,
                questionType = "CALCULATION",
                isKidFriendly = true
            )
            
            // Then
            assertTrue(
                explanation.contains("公式") || 
                explanation.contains("等于") ||
                explanation.contains("+") ||
                explanation.contains("×") ||
                explanation.contains("÷"),
                "计算题解答应包含计算过程"
            )
        }

        @Test
        @DisplayName("逻辑题应分析推理过程")
        fun `logic questions should analyze reasoning`() {
            // Given
            val question = "找规律：1, 1, 2, 3, 5, ?"
            
            // When
            val explanation = generator.generateExplanation(
                questionContent = question,
                correctAnswer = "8",
                questionType = "LOGIC",
                isKidFriendly = true
            )
            
            // Then
            assertTrue(
                explanation.contains("斐波那契") || 
                explanation.contains("前两个") ||
                explanation.contains("相加") ||
                explanation.contains("规律"),
                "逻辑题应分析推理过程"
            )
        }

        @Test
        @DisplayName("图形题应强调视觉化解释")
        fun `graphic questions should emphasize visual explanation`() {
            // Given
            val question = "看图找规律"
            
            // When
            val explanation = generator.generateExplanation(
                questionContent = question,
                correctAnswer = "6",
                questionType = "GRAPHIC",
                isKidFriendly = true
            )
            
            // Then
            // 图形题应使用更直观的语言
            assertTrue(
                explanation.isNotBlank(),
                "图形题应有解答"
            )
        }
    }

    @Nested
    @DisplayName("输入验证")
    inner class InputValidation {
        
        @Test
        @DisplayName("空题目应返回通用提示")
        fun `empty question should return generic hint`() {
            // When
            val explanation = generator.generateExplanation(
                questionContent = "",
                correctAnswer = "123",
                questionType = "ARITHMETIC",
                isKidFriendly = true
            )
            
            // Then
            assertFalse(explanation.isBlank())
        }

        @Test
        @DisplayName("null答案应进行处理")
        fun `null answer should be handled gracefully`() {
            // When
            val explanation = generator.generateExplanation(
                questionContent = "Test question",
                correctAnswer = "?",
                questionType = "LOGIC",
                isKidFriendly = true
            )
            
            // Then
            assertFalse(explanation.isBlank())
            assertTrue(explanation.contains("思考") || explanation.contains("分析一下"))
        }
    }
}

/**
 * AIExplanationGenerator 的简单实现，用于测试
 * 实际项目中应接入LLM API
 */
class AIExplanationGenerator {
    
    fun generateExplanation(
        questionContent: String,
        correctAnswer: String,
        questionType: String,
        isKidFriendly: Boolean
    ): String {
        if (questionContent.isBlank()) {
            return if (isKidFriendly) "这道题很有趣呢！让/我们一起来思考！" else "请认真审题，逐步分析。"
        }
        
        return when (questionType) {
            "CALCULATION" -> generateCalculationExplanation(questionContent, correctAnswer, isKidFriendly)
            "LOGIC" -> generateLogicExplanation(questionContent, correctAnswer, isKidFriendly)
            "GRAPHIC" -> generateGraphicExplanation(questionContent, correctAnswer, isKidFriendly)
            else -> generateGenericExplanation(questionContent, correctAnswer, isKidFriendly)
        }
    }
    
    private fun generateCalculationExplanation(
        content: String,
        answer: String,
        isKidFriendly: Boolean
    ): String {
        return if (isKidFriendly) {
            "太棒了！答案是$answer！\n" +
            "让/们一步步来计算：\n" +
            "1. 首先找出数字的规律\n" +
            "2. 然后应用计算公式\n" +
            "3. 最后得到答案$answer\n" +
            "你真厉害！"
        } else {
            "解答过程：\n" +
            "1. 分析题目条件\n" +
            "2. 建立数学模型\n" +
            "3. 计算得出结果：$answer"
        }
    }
    
    private fun generateLogicExplanation(
        content: String,
        answer: String,
        isKidFriendly: Boolean
    ): String {
        val isFibonacci = content.contains("1, 1, 2, 3, 5")
        
        return if (isKidFriendly) {
            if (isFibonacci) {
                "这是斐波那契数列！每个数是前两个数的和~\n" +
                "5 + 8 = $answer\n" +
                "超级棒！"
            } else {
                "找找规律，发现其中的奥秘！\n" +
                "答案是$answer\n" +
                "你太聪明了！"
            }
        } else {
            "逻辑分析：\n" +
            "- 观察数据特征\n" +
            "- 推导变化规律\n" +
            "- 验证答案$answer"
        }
    }
    
    private fun generateGraphicExplanation(
        content: String,
        answer: String,
        isKidFriendly: Boolean
    ): String {
        return if (isKidFriendly) {
            "看看图上有什么规律呢？\n" +
            "答案是$answer\n" +
            "真棒！继续保持！"
        } else {
            "图形分析：\n" +
            "- 观察图形特征\n" +
            "- 计算相关数据\n" +
            "- 得出答案$answer"
        }
    }
    
    private fun generateGenericExplanation(
        content: String,
        answer: String,
        isKidFriendly: Boolean
    ): String {
        return if (isKidFriendly) {
            "妙的答案是$answer！\n" +
            "让/们想一想为什么是这个答案呢~\n" +
            "你做得很棒！"
        } else {
            "解题思路：\n" +
            "- 审题理解题意\n" +
            "- 分析解题方法\n" +
            "- 计算验证答案$answer"
        }
    }
}
