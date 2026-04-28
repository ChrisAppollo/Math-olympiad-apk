package com.example.matholympiad.presentation.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.example.matholympiad.presentation.theme.AppColors
import java.time.LocalDate

/**
 * 排行榜组件
 */

/**
 * 排行榜用户
 */
data class LeaderboardUser(
    val userId: String,
    val nickname: String,
    val avatar: String,
    val score: Int,
    val rank: Int,
    val streakDays: Int = 0,
    val weeklyProgress: Int = 0
)

/**
 * 排行榜头部
 */
@Composable
fun LeaderboardHeader(
    userRank: Int,
    userScore: Int,
    totalParticipants: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2)
                    )
                )
            )
            .padding(24.dp)
    ) {
        // 标题
        Text(
            text = "🏆 全球排行榜",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        Text(
            text = "与全球数学爱好者一较高下",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // 用户排名卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.15f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 排名图标
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val rankIcon = when {
                        userRank <= 3 -> "🥇"
                        userRank <= 10 -> "🥈"
                        userRank <= 100 -> "🥉"
                        else -> "📊"
                    }
                    Text(
                        text = rankIcon,
                        fontSize = 28.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "我的排名",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "#$userRank",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "超过 ${((totalParticipants - userRank).coerceAtLeast(0) * 100 / totalParticipants.coerceAtLeast(1))}% 的用户",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
                
                // 积分
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "积分",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "$userScore",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                }
            }
        }
    }
}

/**
 * 前三名领奖台
 */
@Composable
fun TopThreePodium(
    topUsers: List<LeaderboardUser>,
    onUserClick: (LeaderboardUser) -> Unit
) {
    if (topUsers.size < 3) return
    
    val first = topUsers[0]
    val second = topUsers[1]
    val third = topUsers[2]
    
 Row(
 modifier = Modifier
 .fillMaxWidth()
 .height(200.dp)
 .padding(horizontal = 16.dp),
 horizontalArrangement = Arrangement.SpaceEvenly,
 verticalAlignment = Alignment.Bottom
 ) {
        // 第二名
        PodiumItem(
            rank = 2,
            user = second,
            height = 120.dp,
            color = Color(0xFFC0C0C0),
            onClick = { onUserClick(second) }
        )
        
        // 第一名
        PodiumItem(
            rank = 1,
            user = first,
            height = 160.dp,
            color = Color(0xFFFFD700),
            onClick = { onUserClick(first) },
            isFirst = true
        )
        
        // 第三名
        PodiumItem(
            rank = 3,
            user = third,
            height = 100.dp,
            color = Color(0xFFCD7F32),
            onClick = { onUserClick(third) }
        )
    }
}

/**
 * 领奖台项
 */
@Composable
private fun PodiumItem(
    rank: Int,
    user: LeaderboardUser,
    height: Dp,
    color: Color,
    onClick: () -> Unit,
    isFirst: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "crown")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isFirst) 5f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        // 皇冠（第一名）
        if (isFirst) {
            Text(
                text = "👑",
                fontSize = 32.sp,
                modifier = Modifier.offset(y = (-floatAnim).dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        
        // 头像
        Box(
            modifier = Modifier
                .size(if (isFirst) 64.dp else 48.dp)
                .background(color.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.avatar.take(2),
                fontSize = if (isFirst) 24.sp else 18.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 昵称
        Text(
            text = user.nickname,
            fontSize = if (isFirst) 16.sp else 14.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.TextDark
        )
        
        // 积分
        Text(
            text = "${user.score}",
            fontSize = if (isFirst) 18.sp else 14.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 领奖台柱
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(height)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            color.copy(alpha = 0.8f),
                            color
                        )
                    ),
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$rank",
                fontSize = if (isFirst) 48.sp else 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

/**
 * 排行榜列表
 */
@Composable
fun LeaderboardList(
    users: List<LeaderboardUser>,
    currentUserId: String,
    onUserClick: (LeaderboardUser) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(users) { user ->
            LeaderboardItem(
                user = user,
                isCurrentUser = user.userId == currentUserId,
                onClick = { onUserClick(user) }
            )
        }
    }
}

/**
 * 排行榜项
 */
@Composable
private fun LeaderboardItem(
    user: LeaderboardUser,
    isCurrentUser: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isCurrentUser -> Color(0xFFFFF3E0)
        user.rank <= 3 -> Color(0xFFFFFBF0)
        else -> Color.White
    }
    
    val rankColor = when (user.rank) {
        1 -> Color(0xFFFFD700)
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> Color(0xFF9CA3AF)
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = if (isCurrentUser) {
            androidx.compose.foundation.BorderStroke(
                width = 2.dp,
                color = AppColors.PrimaryOrange
            )
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 排名
            Text(
                text = "#${user.rank}",
                fontSize = 18.sp,
                fontWeight = if (user.rank <= 3) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.Center,
                color = rankColor
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // 头像
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(rankColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.avatar.take(2),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = rankColor
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // 昵称和连胜
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = user.nickname,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextDark
                    )
                    if (isCurrentUser) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = " (我)",
                            fontSize = 12.sp,
                            color = AppColors.PrimaryOrange
                        )
                    }
                }
                
                if (user.streakDays > 0) {
                    Text(
                        text = "🔥 连胜 ${user.streakDays} 天",
                        fontSize = 12.sp,
                        color = AppColors.AlertRed
                    )
                }
            }
            
            // 积分
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🔥 ", fontSize = 14.sp)
                Text(
                    text = "${user.score}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isCurrentUser -> AppColors.PrimaryOrange
                        user.rank <= 3 -> rankColor
                        else -> AppColors.TextDark
                    }
                )
            }
        }
    }
}

/**
 * 排行榜Tab
 */
@Composable
fun LeaderboardTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.White,
        contentColor = AppColors.PrimaryOrange
    ) {
        Tab(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🏆 总榜")
            }
        }
        Tab(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🔥 连胜榜")
            }
        }
        Tab(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📈 进步榜")
            }
        }
    }
}

/**
 * 个人成就显示
 */
@Composable
fun PersonalAchievements(
    medalsCount: Int,
    currentStreak: Int,
    bestStreak: Int,
    totalAnswered: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        AchievementItem(
            icon = "🏅",
            value = "$medalsCount",
            label = "勋章"
        )
        AchievementItem(
            icon = "🔥",
            value = "$currentStreak",
            label = "连胜"
        )
        AchievementItem(
            icon = "🏆",
            value = "$bestStreak",
            label = "最佳"
        )
        AchievementItem(
            icon = "📚",
            value = "$totalAnswered",
            label = "答题"
        )
    }
}

/**
 * 成就项
 */
@Composable
private fun AchievementItem(
    icon: String,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 24.sp
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextDark
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = AppColors.TextGray
        )
    }
}
