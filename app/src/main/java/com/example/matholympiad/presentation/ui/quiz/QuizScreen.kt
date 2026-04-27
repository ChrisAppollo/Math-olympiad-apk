package com.example.matholympiad.presentation.ui.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matholympiad.presentation.theme.AppColors
import com.example.matholympiad.presentation.ui.components.CorrectAnswerAnimation
import com.example.matholympiad.presentation.ui.components.WrongAnswerAnimation
import com.example.matholympiad.presentation.ui.components.QuizCompletionAnimation
import com.example.matholympiad.presentation.ui.components.BadgeUnlockedAnimation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    uiState: QuizUiState,
    onAnswerSelected: (Int) -> Unit,
    onSubmitClick: () -> Unit,
    onNextClick: () -> Unit,
    onHintClick: () -> Unit,
    onBackClick: () -> Unit
) {
    // Debug: Log UI state changes
    LaunchedEffect(uiState) {
        android.util.Log.d("QuizScreen", "UI State: feedback=${uiState.feedbackShowing}, selected=${uiState.selectedAnswer}, hint=${uiState.hintShowing}")
    }
    
    // 动画状态
    var showCorrectAnimation by remember { mutableStateOf(false) }
    var showWrongAnimation by remember { mutableStateOf(false) }
    var showCompletionAnimation by remember { mutableStateOf(false) }
    
    // 监听答题结果触发动画
    LaunchedEffect(uiState.feedbackShowing, uiState.isCorrect, uiState.quizCompleted) {
        if (uiState.feedbackShowing) {
            if (uiState.quizCompleted) {
                showCompletionAnimation = true
            } else if (uiState.isCorrect == true) {
                showCorrectAnimation = true
            } else if (uiState.isCorrect == false) {
                showWrongAnimation = true
            }
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = AppColors.BackgroundGray,
            topBar = {
                TopAppBar(
                    title = { 
                        Text("答题闯关", color = AppColors.TextDark)
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Text("←", fontSize = 24.sp, color = AppColors.TextDark)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.White)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 进度指示器
                QuizProgressIndicator(
                    currentQuestion = uiState.currentQuestionIndex + 1,
                    totalQuestions = uiState.totalQuestions,
                    score = uiState.score
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 题目区域
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AppColors.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (uiState.currentQuestion != null) {
                            Text(
                                text = uiState.currentQuestion.content,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextDark
                            )
                        } else if (uiState.quizCompleted) {
                            Text(
                                text = "今日答题完成！",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.PrimaryOrange
                            )
                        } else {
                            Text(
                                text = "加载题目中...",
                                style = MaterialTheme.typography.titleLarge,
                                color = AppColors.TextGray
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 选项区域（仅在未完成时显示）
                if (!uiState.quizCompleted && uiState.currentQuestion != null) {
                    val optionsList = uiState.currentQuestion.getOptionsList()
                    optionsList.forEachIndexed { index, option ->
                        val isSelected = uiState.selectedAnswer == index
                        val showResult = uiState.feedbackShowing
                        val isCorrectAnswer = index == uiState.currentQuestion.correctAnswer
                        
                        val backgroundColor = when {
                            showResult && isCorrectAnswer -> AppColors.SuccessGreen
                            showResult && isSelected && !isCorrectAnswer -> AppColors.AlertRed
                            isSelected -> AppColors.SkyBlue
                            else -> AppColors.White
                        }
                        
                        val contentColor = when {
                            showResult && (isCorrectAnswer || (isSelected && !isCorrectAnswer)) -> Color.White
                            isSelected -> Color.White
                            else -> AppColors.TextDark
                        }
                        
                        // 使用 Box + clickable 确保点击响应
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(vertical = 4.dp)
                                .background(backgroundColor, RoundedCornerShape(12.dp))
                                .then(
                                    if (!showResult) {
                                        Modifier.clickable {
                                            android.util.Log.d("QuizScreen", "Option clicked: $index")
                                            onAnswerSelected(index)
                                        }
                                    } else Modifier
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${('A'.code + index).toChar()}.",
                                    fontSize = 16.sp,
                                    color = contentColor.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Text(
                                    text = option,
                                    fontSize = 18.sp,
                                    color = contentColor,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                when {
                                    showResult && isCorrectAnswer -> Text("✅", fontSize = 20.sp)
                                    showResult && isSelected && !isCorrectAnswer -> Text("❌", fontSize = 20.sp)
                                    isSelected && !showResult -> Text("●", fontSize = 12.sp, color = contentColor)
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 提示卡片
                AnimatedVisibility(
                    visible = uiState.hintShowing && uiState.hintText.isNotBlank(),
                    enter = fadeIn() + expandVertically()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = AppColors.WarmGold.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "💡 提示",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.hintText,
                                fontSize = 14.sp,
                                color = AppColors.TextGray
                            )
                        }
                    }
                }
                
                // 反馈区域
                AnimatedVisibility(
                    visible = uiState.feedbackShowing && !uiState.quizCompleted && uiState.explanation.isNotBlank(),
                    enter = fadeIn() + expandVertically()
                ) {
                    FeedbackCard(
                        isCorrect = uiState.isCorrect == true,
                        explanation = uiState.explanation,
                        encouragement = uiState.encouragement
                    )
                }
                
                // 占位器顶到底部
                Spacer(modifier = Modifier.weight(1f))
                
                // 底部操作按钮
                if (!uiState.quizCompleted) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 提示按钮
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .background(AppColors.WarmGold, RoundedCornerShape(12.dp))
                                .then(
                                    if (!uiState.feedbackShowing) {
                                        Modifier.clickable {
                                            android.util.Log.d("QuizScreen", "Hint button clicked")
                                            onHintClick()
                                        }
                                    } else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💡 提示", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppColors.TextDark)
                        }
                        
                        val submitBgColor = when {
                            uiState.feedbackShowing && uiState.isCorrect == true -> AppColors.SuccessGreen
                            uiState.feedbackShowing && uiState.isCorrect == false -> AppColors.AlertRed
                            else -> AppColors.PrimaryOrange
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .background(submitBgColor, RoundedCornerShape(12.dp))
                                .clickable {
                                    if (uiState.feedbackShowing) {
                                        onNextClick()
                                    } else {
                                        onSubmitClick()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (uiState.feedbackShowing) {
                                    if (uiState.isCorrect == true) "下一题 →" else "继续 →"
                                } else "提交答案",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                } else {
                    // 答题完成按钮
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(AppColors.PrimaryOrange, RoundedCornerShape(12.dp))
                            .clickable { onBackClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("返回首页 🏠", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
            
            // 动画叠加层
            if (showCorrectAnimation) {
                CorrectAnswerAnimation(
                    onAnimationEnd = { showCorrectAnimation = false }
                )
            }
            
            if (showWrongAnimation) {
                WrongAnswerAnimation(
                    onAnimationEnd = { showWrongAnimation = false }
                )
            }
            
            if (showCompletionAnimation) {
                QuizCompletionAnimation(
                    totalScore = uiState.score,
                    onAnimationEnd = { showCompletionAnimation = false }
                )
            }
        }
    }
}

@Composable
private fun QuizProgressIndicator(
    currentQuestion: Int,
    totalQuestions: Int,
    score: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "题目 $currentQuestion / $totalQuestions",
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.TextDark
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🏆", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${score}分",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.PrimaryOrange
                )
            }
        }
    }
}

@Composable
private fun FeedbackCard(
    isCorrect: Boolean,
    explanation: String,
    encouragement: String
) {
    val backgroundColor = if (isCorrect) AppColors.SuccessGreen.copy(alpha = 0.1f) 
        else AppColors.AlertRed.copy(alpha = 0.1f)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = if (isCorrect) "🎉 回答正确！" else "😔 回答错误",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isCorrect) AppColors.SuccessGreen else AppColors.AlertRed
            )
            
            if (encouragement.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = encouragement,
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppColors.TextDark
                )
            }
            
            if (explanation.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "📖 解析：$explanation",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextGray
                )
            }
        }
    }
}
