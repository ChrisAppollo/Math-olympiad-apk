package com.example.matholympiad.domain.usecase

import com.example.matholympiad.data.local.model.Question
import javax.inject.Inject

/**
 * AI 讲题助手
 * 生成题目讲解内容
 */
class AIExplanationGenerator @Inject constructor() {

    /**
     * 生成AI讲解
     * @param question 题目
     * @param isSimpleMode 是否简化模式（针对低龄用户）
     * @return AI讲解内容
     */
    fun generateExplanation(question: Question, isSimpleMode: Boolean = false): AIExplanation {
        val baseExplanation = question.explanation

        // 根据题型生成不同的讲解方式
        val stepByStep = when (getQuestionType(question)) {
            "鸡兔同笼" -> generateChickenRabbitExplanation(question)
            "等差数列" -> generateArithmeticSequenceExplanation(question)
            "找规律" -> generatePatternExplanation(question)
            "应用题" -> generateWordProblemExplanation(question)
            else -> generateGenericExplanation(question)
        }

        // 关键知识点提炼
        val keyPoints = extractKeyPoints(question)

        // 相似题目推荐
        val similarQuestions = generateSimilarProblemHints(question)

        // 针对不同难度级别调整讲解
        val adaptedExplanation = if (isSimpleMode) {
            adaptForYoungLearners(stepByStep)
        } else {
            stepByStep
        }

        return AIExplanation(
            originalExplanation = baseExplanation,
            stepByStepGuide = adaptedExplanation,
            keyPoints = keyPoints,
            similarProblems = similarQuestions,
            tips = generateTips(question),
            difficulty = question.difficulty
        )
    }

    /**
     * 鸡兔同笼题型讲解
     */
    private fun generateChickenRabbitExplanation(question: Question): List<Step> {
        return listOf(
            Step(
                order = 1,
                title = "理解题意",
                content = "题目告诉我们头的总数和腿的总数，需要找出鸡和兔各有多少只。",
                visualHint = "画图：画几个圆圈代表头"
            ),
            Step(
                order = 2,
                title = "假设法",
                content = "假设全是鸡，计算应该有2×头数的腿。",
                formula = "假设腿数 = 头数 × 2"
            ),
            Step(
                order = 3,
                title = "找差距",
                content = "计算实际腿数与假设腿数的差。",
                formula = "腿数差 = 实际腿数 - 假设腿数"
            ),
            Step(
                order = 4,
                title = "换兔子",
                content = "每把一只鸡换成兔子，腿数增加2条。所以兔子数量 = 腿数差 ÷ 2",
                formula = "兔子数 = 腿数差 ÷ 2"
            ),
            Step(
                order = 5,
                title = "求鸡的数量",
                content = "鸡的数量 = 总头数 - 兔子数量",
                formula = "鸡数 = 头数 - 兔子数"
            )
        )
    }

    /**
     * 等差数列题型讲解
     */
    private fun generateArithmeticSequenceExplanation(question: Question): List<Step> {
        return listOf(
            Step(
                order = 1,
                title = "识别数列类型",
                content = "这是一道等差数列求和问题，数列中相邻两个数的差相等。",
                visualHint = "观察：1,2,3...后项-前项=1"
            ),
            Step(
                order = 2,
                title = "确定参数",
                content = "找出首项、末项和项数",
                formula = "首项a₁, 末项aₙ, 项数n"
            ),
            Step(
                order = 3,
                title = "应用公式",
                content = "使用高斯求和公式：和 = (首项 + 末项) × 项数 ÷ 2",
                formula = "S = (a₁ + aₙ) × n ÷ 2"
            ),
            Step(
                order = 4,
                title = "代入计算",
                content = "把具体数字代入公式计算结果",
                formula = "例如：(1+100)×100÷2 = 5050"
            )
        )
    }

    /**
     * 找规律题型讲解
     */
    private fun generatePatternExplanation(question: Question): List<Step> {
        return listOf(
            Step(
                order = 1,
                title = "观察数列",
                content = "仔细观察数列中数字的变化规律。",
                visualHint = "写下来：看相邻数的关系"
            ),
            Step(
                order = 2,
                title = "找关系",
                content = "计算相邻数的差、商，或寻找其他规律。",
                formula = "差 = 后项 - 前项"
            ),
            Step(
                order = 3,
                title = "验证规律",
                content = "用找到的规律验证前面几项是否正确。",
                visualHint = "检查前几项是否符合"
            ),
            Step(
                order = 4,
                title = "预测下一项",
                content = "用确定的规律计算下一项的值。"
            )
        )
    }

    /**
     * 应用题讲解
     */
    private fun generateWordProblemExplanation(question: Question): List<Step> {
        return listOf(
            Step(
                order = 1,
                title = "读题理解",
                content = "仔细阅读题目，找出已知条件和要求的问题。",
                visualHint = "圈出：已知量和未知量"
            ),
            Step(
                order = 2,
                title = "分析关系",
                content = "找出数量之间的关系，确定解题思路。"
            ),
            Step(
                order = 3,
                title = "列式计算",
                content = "根据分析列出算式并逐步计算。"
            ),
            Step(
                order = 4,
                title = "检验答案",
                content = "把答案代入原题验证是否正确。"
            )
        )
    }

    /**
     * 通用讲解
     */
    private fun generateGenericExplanation(question: Question): List<Step> {
        return listOf(
            Step(
                order = 1,
                title = "理解题目",
                content = "仔细阅读题目，理解要求和条件。"
            ),
            Step(
                order = 2,
                title = "分析思路",
                content = question.explanation.substringBefore("。", question.explanation)
            ),
            Step(
                order = 3,
                title = "计算求解",
                content = "根据思路进行计算。"
            ),
            Step(
                order = 4,
                title = "验证答案",
                content = "检查答案是否正确。"
            )
        )
    }

    /**
     * 提取关键知识点
     */
    private fun extractKeyPoints(question: Question): List<KeyPoint> {
        val keyPoints = mutableListOf<KeyPoint>()

        when {
            question.content.contains("鸡") && question.content.contains("兔") -> {
                keyPoints.add(KeyPoint("假设法", "通过假设全部为某一种，再计算差值求解"))
                keyPoints.add(KeyPoint("头腿关系", "鸡2条腿，兔4条腿"))
            }
            question.content.contains("和") && question.content.contains("...") -> {
                keyPoints.add(KeyPoint("等差数列求和", "(首项+末项)×项数÷2"))
                keyPoints.add(KeyPoint("高斯算法", "配对求和法"))
            }
            question.content.contains("规律") || question.content.contains(",") -> {
                keyPoints.add(KeyPoint("找规律", "观察相邻数的关系"))
            }
            else -> {
                keyPoints.add(KeyPoint("审题", "仔细阅读理解题意"))
                keyPoints.add(KeyPoint("分步计算", "复杂问题分步解决"))
            }
        }

        return keyPoints
    }

    /**
     * 生成解题技巧
     */
    private fun generateTips(question: Question): List<String> {
        return when {
            question.content.contains("鸡") && question.content.contains("兔") -> listOf(
                "画图帮助理解：用圆圈代表头",
                "记住：鸡2条腿，兔4条腿",
                "假设法可以假设全是鸡或全是兔",
                "解完后代入验证"
            )
            question.content.contains("和") && question.content.contains("...") -> listOf(
                "等差数列：相邻数差相等",
                "项数 = (末项-首项)÷公差 + 1",
                "奇数个等差数列，中间数=平均数",
                "配对法让小数配大数"
            )
            else -> listOf(
                "仔细读题，找出所有条件",
                "复杂问题分步解决",
                "计算后要检验答案",
                "错题要总结原因"
            )
        }
    }

    /**
     * 生成相似题目提示
     */
    private fun generateSimilarProblemHints(question: Question): List<SimilarProblem> {
        return when {
            question.content.contains("鸡") && question.content.contains("兔") -> listOf(
                SimilarProblem("停车场问题", "车轮问题，类似鸡兔同笼思路"),
                SimilarProblem("买邮票问题", "不同面值邮票的张数"),
                SimilarProblem("投篮得分问题", "2分球和3分球的个数")
            )
            question.content.contains("和") && question.content.contains("...") -> listOf(
                SimilarProblem("偶数数列求和", "2+4+6+...+100"),
                SimilarProblem("奇数数列求和", "1+3+5+...+99"),
                SimilarProblem("等差数列项数", "已知和求项数")
            )
            else -> emptyList()
        }
    }

    /**
     * 针对低龄学习者调整讲解
     */
    private fun adaptForYoungLearners(steps: List<Step>): List<Step> {
        return steps.map { step ->
            step.copy(
                content = simplifyLanguage(step.content),
                visualHint = step.visualHint ?: "画图帮助你理解哦~"
            )
        }
    }

    /**
     * 简化语言
     */
    private fun simplifyLanguage(text: String): String {
        return text
            .replace("假设", " pretend ")
            .replace("计算", "算一算")
            .replace("确定", "找出")
            .replace("应用", "使用")
            .replace("代入", "把数字放进")
            .trim()
    }

    /**
     * 获取题型
     */
    private fun getQuestionType(question: Question): String {
        return when {
            question.content.contains("鸡") && question.content.contains("兔") -> "鸡兔同笼"
            question.content.contains(",") || question.content.contains("...") -> {
                when {
                    question.content.contains("规律") -> "找规律"
                    question.content.contains("和") -> "等差数列"
                    else -> "找规律"
                }
            }
            question.content.contains("小明") || question.content.contains("甲乙") -> "应用题"
            else -> "一般"
        }
    }
}

/**
 * AI讲解数据类
 */
data class AIExplanation(
    val originalExplanation: String,
    val stepByStepGuide: List<Step>,
    val keyPoints: List<KeyPoint>,
    val similarProblems: List<SimilarProblem>,
    val tips: List<String>,
    val difficulty: Int
)

/**
 * 解题步骤
 */
data class Step(
    val order: Int,
    val title: String,
    val content: String,
    val formula: String? = null,
    val visualHint: String? = null
)

/**
 * 关键知识点
 */
data class KeyPoint(
    val name: String,
    val description: String
)

/**
 * 相似题目
 */
data class SimilarProblem(
    val title: String,
    val description: String
)
