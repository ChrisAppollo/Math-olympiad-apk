package com.example.matholympiad.presentation.ui.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.matholympiad.presentation.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    uiState: QuizUiState,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Scaffold(
        containerColor = AppColors.BackgroundGray,
        topBar = {
            TopAppBar(
                title = { 
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(uiState.totalQuestions) { index ->
                            Box(
                                modifier = Modifier
                                    .size(if (index < uiState.currentQuestionIndex + 1) 24.dp else 20.dp)
                                    .background(
                                        color = if (index == uiState.currentQuestionIndex) AppColors.PrimaryOrange else AppColors.BackgroundGray,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    fontSize = if (index < uiState.currentQuestionIndex + 1) 12.sp else 10.sp,
                                    color = AppColors.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
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
            Spacer(modifier = Modifier.height(8.dp))
            
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
                    Text(
                        text = uiState.currentQuestion?.content ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextDark
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 选项区域
            uiState.currentQuestion?.options?.forEachIndexed { index, option ->
                val isSelected = uiState.selectedAnswer == index
                Button(
                    onClick = { /* Handle selection */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(vertical = 4.dp),
                    colors = if (isSelected) {
                        ButtonDefaults.buttonColors(containerColor = AppColors.SkyBlue)
                    } else {
                        ButtonDefaults.buttonColors(containerColor = AppColors.White)
                    },
                    shape = RoundedCornerShape(12.dp),
                    enabled = !uiState.feedbackShowing
                ) {
                    Text(
                        text = option,
                        fontSize = 18.sp,
                        color = if (isSelected) AppColors.White else AppColors.TextDark,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 底部操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { /* Hint */ },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.WarmGold),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !uiState.feedbackShowing
                ) {
                    Text("💡 提示", style = MaterialTheme.typography.titleMedium, color = AppColors.TextDark)
                }
                
                Button(
                    onClick = { /* Submit */ },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (uiState.isCorrect == true) AppColors.SuccessGreen else AppColors.PrimaryOrange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (uiState.feedbackShowing) "下一题" else "✅ 提交",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.White
                    )
                }
            }
        }
    }
}
