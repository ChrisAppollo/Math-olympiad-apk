package com.example.matholympiad.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.matholympiad.presentation.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onQuizClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLeaderboardClick: () -> Unit
) {
    Scaffold(
        containerColor = AppColors.BackgroundGray
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 积分进度条区域
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "今日闯关进度",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.TextDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 彩虹渐变进度条
                    LinearProgressIndicator(
                        progress = { uiState.todayCompleted.toFloat() / uiState.maxTodayQuestions },
                        modifier = Modifier.fillMaxWidth().height(12.dp),
                        shape = RoundedCornerShape(6.dp),
                        brush = Brush.horizontalGradient(
                            colors = listOf(AppColors.SkyBlue, AppColors.SuccessGreen, AppColors.WarmGold)
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${uiState.todayCompleted} / ${uiState.maxTodayQuestions}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextDark
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 大尺寸今日闯关按钮
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.PrimaryOrange)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🚀",
                        fontSize = 64.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "今日闯关",
                        style = MaterialTheme.typography.headlineMedium,
                        color = AppColors.White,
                        fontWeight = FontWeight.Bold
                    )
                    if (uiState.isQuizAvailable) {
                        Text(
                            text = "点击开始答题",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.WarmGold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else {
                        Text(
                            text = "今日已完成，明天继续加油！",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.White,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 勋章预览区域
            Text(
                text = "🏅 最新勋章",
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.TextDark
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 简单展示 3 个勋章图标
                for (i in 0 until minOf(uiState.badgesCount, 3)) {
                    Card(
                        modifier = Modifier.size(80.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(text = "🏅", fontSize = 32.sp)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 排行榜入口
            Button(
                onClick = onLeaderboardClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.SkyBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "⭐ 排行榜 TOP20",
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.White
                )
            }
        }
    }
}
