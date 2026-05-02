package com.example.matholympiad.presentation.ui.home

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
import androidx.compose.ui.unit.sp
import com.example.matholympiad.presentation.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onQuizClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onWrongAnswersClick: () -> Unit = {}
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
                    val progress = uiState.todayCompleted.toFloat().coerceIn(0f, 1f) / uiState.maxTodayQuestions.coerceAtLeast(1)
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxWidth().height(12.dp),
                        color = AppColors.SkyBlue,
                        trackColor = AppColors.BackgroundGray
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "已完成 ${uiState.todayCompleted}/${uiState.maxTodayQuestions} 题",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppColors.TextGray
                        )
                        Text(
                            text = "${uiState.totalScore} 积分",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.PrimaryOrange
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 开始答题按钮
            Button(
                onClick = onQuizClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryOrange),
                shape = RoundedCornerShape(16.dp),
                enabled = uiState.isQuizAvailable && !uiState.loading
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (uiState.isQuizAvailable) "🚀 开始闯关" else "今日已完成",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (uiState.isQuizAvailable) {
                        Text(
                            text = "还有 ${uiState.maxTodayQuestions - uiState.todayCompleted} 道题等你挑战",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
 Spacer(modifier = Modifier.height(16.dp))
 
 // 错题本按钮
 OutlinedButton(
     onClick = onWrongAnswersClick,
     modifier = Modifier
         .fillMaxWidth()
         .height(56.dp),
     shape = RoundedCornerShape(12.dp)
 ) {
     Text("📚 错题本", style = MaterialTheme.typography.titleMedium)
 }
 
 Spacer(modifier = Modifier.height(16.dp))
 
 // 个人资料按钮
            OutlinedButton(
                onClick = onProfileClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("👤 个人资料", style = MaterialTheme.typography.titleMedium)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
 // 徽章展示
 Card(
 modifier = Modifier.fillMaxWidth(),
 shape = RoundedCornerShape(12.dp),
 colors = CardDefaults.cardColors(containerColor = AppColors.White)
 ) {
 Column(
 modifier = Modifier.padding(16.dp)
 ) {
 Text(
 text = "我的勋章",
 style = MaterialTheme.typography.titleMedium,
 color = AppColors.TextDark
 )
 Spacer(modifier = Modifier.height(8.dp))
 if (uiState.badgesCount > 0) {
 // 显示已解锁勋章的图标
 Row(
 modifier = Modifier.fillMaxWidth(),
 horizontalArrangement = Arrangement.spacedBy(8.dp),
 verticalAlignment = Alignment.CenterVertically
 ) {
 uiState.getUnlockedBadgeInfos().take(4).forEach { badge ->
 Text(
 text = badge.emoji,
 fontSize = 32.sp
 )
 }
 if (uiState.badgesCount > 4) {
 Text(
 text = "+${uiState.badgesCount - 4}",
 fontSize = 18.sp,
 color = AppColors.WarmGold
 )
 }
 }
 } else {
 Text(
 text = "暂无勋章，加油闯关吧！",
 style = MaterialTheme.typography.bodyMedium,
 color = AppColors.TextGray
 )
 }
 }
 }
}
    }
}
