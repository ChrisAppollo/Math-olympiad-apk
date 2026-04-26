package com.example.matholympiad.presentation.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matholympiad.presentation.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(uiState: ProfileUiState) {
    Scaffold(
        containerColor = AppColors.BackgroundGray,
        topBar = {
            TopAppBar(
                title = { Text("👤 我的资料", color = AppColors.TextDark) },
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
            
            // 积分卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.PrimaryOrange)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🏆",
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "总积分",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.WarmGold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${uiState.totalScore}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.White
                    )
                    Text(
                        text = "可解锁更多勋章!",
                        fontSize = 12.sp,
                        color = AppColors.WarmGold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 勋章统计
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Card(
                    modifier = Modifier.weight(1f).height(100.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🏅", fontSize = 32.sp)
                        Text("已获勋章", style = MaterialTheme.typography.bodySmall, color = AppColors.TextDark)
                        Text("${uiState.badgesCount}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = AppColors.SkyBlue)
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Card(
                    modifier = Modifier.weight(1f).height(100.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🔥", fontSize = 32.sp)
                        Text("连续打卡", style = MaterialTheme.typography.bodySmall, color = AppColors.TextDark)
                        Text("${uiState.streakCount}天", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = AppColors.AlertRed)
                    }
                }
            }
        }
    }
}
