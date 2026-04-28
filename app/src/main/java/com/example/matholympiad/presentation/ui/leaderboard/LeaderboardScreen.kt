package com.example.matholympiad.presentation.ui.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matholympiad.data.local.model.LeaderboardEntry
import com.example.matholympiad.presentation.theme.AppColors


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    entries: List<LeaderboardEntry>,
    myRank: Int,
    myScore: Int,
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = AppColors.BackgroundGray,
        topBar = {
            TopAppBar(
                title = { Text("🏆 排行榜", color = AppColors.TextDark) },
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
                .padding(16.dp)
        ) {
            // 我的排名卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.PrimaryOrange)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🏅 我",
                        fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "排名第 $myRank",
                                style = MaterialTheme.typography.titleMedium,
                                color = AppColors.White
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$myScore 积分",
                                style = MaterialTheme.typography.titleMedium,
                                color = AppColors.WarmGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 排行榜列表
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "排行榜",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.TextDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(entries) { entry ->
                            LeaderboardItem(entry = entry)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardItem(entry: LeaderboardEntry) {
    val backgroundColor = when (entry.rank) {
        1 -> AppColors.WarmGold
        2 -> AppColors.SoftPink
        3 -> AppColors.SkyBlue
        else -> AppColors.BackgroundGray
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (entry.rank) {
                    1 -> "🥇"
                    2 -> "🥈"
                    3 -> "🥉"
                    else -> "${entry.rank}."
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = entry.userName,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${entry.score} 积分",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = AppColors.PrimaryOrange
            )
            Text(
                text = "🔥 ${entry.streakDays}天",
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextGray
            )
        }
    }
}
