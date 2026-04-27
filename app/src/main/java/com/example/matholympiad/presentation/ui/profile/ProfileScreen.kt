package com.example.matholympiad.presentation.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matholympiad.presentation.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onBackClick: () -> Unit
) {
    var selectedBadge by remember { mutableStateOf<Badge?>(null) }
    
    Scaffold(
        containerColor = AppColors.BackgroundGray,
        topBar = {
            TopAppBar(
                title = { Text("👤 我的资料", color = AppColors.TextDark) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回",
                            tint = AppColors.TextDark
                        )
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
                    Text("🏆", fontSize = 48.sp)
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
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "解锁更多勋章吧!",
                        fontSize = 12.sp,
                        color = AppColors.WarmGold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 统计卡片
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    emoji = "🏅",
                    label = "已获勋章",
                    value = "${uiState.badgesCount}",
                    color = AppColors.SkyBlue,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    emoji = "🔥",
                    label = "连续打卡",
                    value = "${uiState.streakCount}天",
                    color = AppColors.AlertRed,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    emoji = "📊",
                    label = "正确率",
                    value = "${(uiState.correctRate * 100).toInt()}%",
                    color = AppColors.SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 勋章墙标题
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏅 勋章墙",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextDark
                )
                Text(
                    text = "${uiState.badges.filter { it.isUnlocked }.size}/${uiState.badges.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextGray
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 勋章网格
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.badges) { badge ->
                    BadgeItem(
                        badge = badge,
                        onClick = { selectedBadge = badge }
                    )
                }
            }
        }
        
        // 勋章详情弹窗
        AnimatedVisibility(
            visible = selectedBadge != null,
            enter = fadeIn() + scaleIn(initialScale = 0.8f)
        ) {
            selectedBadge?.let { badge ->
                BadgeDetailDialog(
                    badge = badge,
                    onDismiss = { selectedBadge = null }
                )
            }
        }
    }
}

@Composable
fun StatCard(
    emoji: String,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextGray
            )
        }
    }
}

@Composable
fun BadgeItem(
    badge: Badge,
    onClick: () -> Unit
) {
    val alpha by animateFloatAsState(
        targetValue = if (badge.isUnlocked) 1f else 0.4f,
        label = "badgeAlpha"
    )
    
    Column(
        modifier = Modifier
            .padding(4.dp)
            .alpha(alpha)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 勋章图标
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    color = if (badge.isUnlocked) AppColors.WarmGold.copy(alpha = 0.2f) 
                            else AppColors.Gray,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = badge.emoji,
                fontSize = 32.sp
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // 勋章名称
        Text(
            text = badge.name,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = if (badge.isUnlocked) AppColors.TextDark else AppColors.TextGray,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun BadgeDetailDialog(
    badge: Badge,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (badge.isUnlocked) AppColors.WarmGold.copy(alpha = 0.95f)
                                else AppColors.White
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 锁定图标（如果未解锁）
                if (!badge.isUnlocked) {
                    Text(
                        text = "🔒",
                        fontSize = 32.sp
                    )
                } else {
                    Text(
                        text = "✨",
                        fontSize = 32.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = badge.emoji,
                    fontSize = 64.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = badge.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextDark
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = badge.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppColors.TextGray,
                    textAlign = TextAlign.Center
                )
                
                if (!badge.isUnlocked) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "需要 ${badge.requiredPoints} 积分",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.AlertRed
                    )
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "🏆 已获得",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.SuccessGreen
                    )
                    badge.unlockDate?.let {
                        Text(
                            text = "解锁日期: $it",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.TextGray
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.PrimaryOrange
                    )
                ) {
                    Text("关闭")
                }
            }
        }
    }
}
