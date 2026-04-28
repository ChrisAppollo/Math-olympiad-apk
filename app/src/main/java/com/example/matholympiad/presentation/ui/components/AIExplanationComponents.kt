package com.example.matholympiad.presentation.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matholympiad.domain.usecase.AIExplanation
import com.example.matholympiad.domain.usecase.KeyPoint
import com.example.matholympiad.domain.usecase.SimilarProblem
import com.example.matholympiad.domain.usecase.Step
import com.example.matholympiad.presentation.theme.AppColors
import kotlinx.coroutines.launch

/**
 * AI 讲题助手弹窗
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AIExplanationDialog(
    explanation: AIExplanation?,
    questionContent: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (explanation == null) {
        // 加载状态
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("🤖 AI讲题助手") },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = AppColors.PrimaryOrange)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("AI正在分析题目...")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("关闭")
                }
            }
        )
        return
    }

    // 当前显示的步骤索引
    var currentStepIndex by remember { mutableStateOf(0) }
    var showKeyPoints by remember { mutableStateOf(false) }
    var showTips by remember { mutableStateOf(false) }
    var showSimilarProblems by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
            .fillMaxWidth(0.95f)
            .fillMaxHeight(0.9f),
        shape = RoundedCornerShape(24.dp),
        containerColor = Color(0xFFF8F9FA),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🤖",
                    fontSize = 28.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI讲题助手",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                
                // 模式切换按钮
                IconButton(
                    onClick = { showTips = !showTips },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (showTips) AppColors.PrimaryOrange.copy(alpha = 0.2f) else Color.Transparent,
                            CircleShape
                        )
                ) {
 Icon(
 imageVector = Icons.Outlined.Send,
                    contentDescription = "解题技巧",
                    tint = if (showTips) AppColors.PrimaryOrange else Color.Gray
                )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // 题目预览
                QuestionPreviewCard(content = questionContent)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 内容区域
                AnimatedContent(
                    targetState = when {
                        showTips -> "tips"
                        showSimilarProblems -> "similar"
                        showKeyPoints -> "keyPoints"
                        else -> "steps"
                    },
                    transitionSpec = { 
                        fadeIn(animationSpec = tween(300)) + 
                        slideInHorizontally { it / 2 } with
                        fadeOut(animationSpec = tween(150)) + 
                        slideOutHorizontally { -it / 2 }
                    },
                    label = "content_switch"
                ) { contentType ->
                    when (contentType) {
                        "tips" -> TipsSection(tips = explanation.tips)
                        "similar" -> SimilarProblemsSection(problems = explanation.similarProblems)
                        "keyPoints" -> KeyPointsSection(keyPoints = explanation.keyPoints)
                        else -> StepByStepSection(
                            steps = explanation.stepByStepGuide,
                            currentStep = currentStepIndex,
                            onStepSelected = { currentStepIndex = it }
                        )
                    }
                }
            }
        },
        confirmButton = {
            // 底部导航按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
NavigationButton(
            icon = Icons.Filled.Menu,
            label = "步骤",
            isSelected = !showKeyPoints && !showTips && !showSimilarProblems,
            onClick = {
                showKeyPoints = false
                showTips = false
                showSimilarProblems = false
            }
        )
                
 NavigationButton(
 icon = Icons.Outlined.Home,
 label = "知识点",
                    isSelected = showKeyPoints,
                    onClick = {
                        showKeyPoints = true
                        showTips = false
                        showSimilarProblems = false
                    }
                )
                
if (explanation.similarProblems.isNotEmpty()) {
 NavigationButton(
 icon = Icons.Outlined.Menu,
                    label = "相似题",
                    isSelected = showSimilarProblems,
                    onClick = {
                        showSimilarProblems = true
                        showKeyPoints = false
                        showTips = false
                    }
                )
            }
                
                TextButton(onClick = onDismiss) {
                    Text(
                        "关闭",
                        color = AppColors.TextGray
                    )
                }
            }
        }
    )
}

/**
 * 题目预览卡片
 */
@Composable
private fun QuestionPreviewCard(content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📝 题目",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.PrimaryOrange
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                fontSize = 14.sp,
                color = AppColors.TextDark,
                lineHeight = 20.sp
            )
        }
    }
}

/**
 * 分步讲解区域
 */
@Composable
private fun StepByStepSection(
    steps: List<Step>,
    currentStep: Int,
    onStepSelected: (Int) -> Unit
) {
    Column {
        // 进度指示器
        StepProgressIndicator(
            totalSteps = steps.size,
            currentStep = currentStep
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 步骤列表
        steps.forEachIndexed { index, step ->
            AnimatedVisibility(
                visible = index <= currentStep,
                enter = fadeIn() + expandVertically()
            ) {
                StepCard(
                    step = step,
                    isActive = index == currentStep,
                    isCompleted = index < currentStep,
                    onClick = { onStepSelected(index) }
                )
            }
            if (index < steps.size - 1) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        
        // 下一步按钮
        if (currentStep < steps.size - 1) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onStepSelected(currentStep + 1) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.PrimaryOrange
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("下一步 👉")
            }
        } else {
            // 全部完成提示
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.SuccessGreen.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🎉", fontSize = 40.sp)
                    Text(
                        text = "讲解完成！",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.SuccessGreen
                    )
                    Text(
                        text = "你学会了吗？去练习一下吧~",
                        fontSize = 14.sp,
                        color = AppColors.TextGray
                    )
                }
            }
        }
    }
}

/**
 * 步骤卡片
 */
@Composable
private fun StepCard(
    step: Step,
    isActive: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    val borderColor = when {
        isActive -> AppColors.PrimaryOrange
        isCompleted -> AppColors.SuccessGreen
        else -> Color.LightGray
    }
    
    val backgroundColor = when {
        isActive -> AppColors.PrimaryOrange.copy(alpha = 0.05f)
        isCompleted -> AppColors.SuccessGreen.copy(alpha = 0.05f)
        else -> Color.White
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isActive) { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isActive) 2.dp else 1.dp,
            color = borderColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 步骤标题行
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 步骤编号
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(borderColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "${step.order}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = step.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) AppColors.PrimaryOrange else AppColors.TextDark
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 步骤内容
            Text(
                text = step.content,
                fontSize = 14.sp,
                color = AppColors.TextDark,
                lineHeight = 20.sp
            )
            
            // 公式
            if (!step.formula.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Text(
                        text = "📐 ${step.formula}",
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        color = AppColors.SkyBlue,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // 视觉提示
            if (!step.visualHint.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💡", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = step.visualHint,
                        fontSize = 13.sp,
                        color = AppColors.TextGray,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}

/**
 * 步骤进度指示器
 */
@Composable
private fun StepProgressIndicator(totalSteps: Int, currentStep: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until totalSteps) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(
                        when {
                            i < currentStep -> AppColors.SuccessGreen
                            i == currentStep -> AppColors.PrimaryOrange
                            else -> Color.LightGray
                        },
                        RoundedCornerShape(2.dp)
                    )
            )
            if (i < totalSteps - 1) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "进度 ${currentStep + 1}/${totalSteps}",
            fontSize = 12.sp,
            color = AppColors.TextGray
        )
        
        val progress = (currentStep + 1) * 100 / totalSteps
        Text(
            text = "$progress%",
            fontSize = 12.sp,
            color = AppColors.PrimaryOrange,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * 知识点区域
 */
@Composable
private fun KeyPointsSection(keyPoints: List<KeyPoint>) {
    Column {
        Text(
            text = "🎯 关键知识点",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextDark
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        keyPoints.forEach { keyPoint ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3E0)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = keyPoint.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.PrimaryOrange
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = keyPoint.description,
                        fontSize = 14.sp,
                        color = AppColors.TextDark
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * 解题技巧区域
 */
@Composable
private fun TipsSection(tips: List<String>) {
    Column {
        Text(
            text = "💡 解题技巧",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextDark
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        tips.forEachIndexed { index, tip ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E9)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "${index + 1}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.SuccessGreen,
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                AppColors.SuccessGreen.copy(alpha = 0.2f),
                                CircleShape
                            )
                            .wrapContentSize(Alignment.Center)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = tip,
                        fontSize = 14.sp,
                        color = AppColors.TextDark,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * 相似题目区域
 */
@Composable
private fun SimilarProblemsSection(problems: List<SimilarProblem>) {
    Column {
        Text(
            text = "📚 相似题目推荐",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextDark
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        problems.forEach { problem ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📖", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = problem.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.SkyBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = problem.description,
                        fontSize = 13.sp,
                        color = AppColors.TextGray
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * 底部导航按钮
 */
@Composable
private fun NavigationButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) AppColors.PrimaryOrange else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (isSelected) AppColors.PrimaryOrange else Color.Gray
        )
    }
}

/**
 * AI讲解按钮
 * 在答题界面使用
 */
@Composable
fun AIExplanationButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier.scale(scale),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF7C4DFF)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🤖", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "AI讲题",
                fontSize = 16.sp
            )
        }
    }
}
