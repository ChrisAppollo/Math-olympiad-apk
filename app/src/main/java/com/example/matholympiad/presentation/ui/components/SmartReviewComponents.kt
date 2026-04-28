package com.example.matholympiad.presentation.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matholympiad.data.local.model.AnswerHistory
import com.example.matholympiad.data.local.model.Question
import com.example.matholympiad.domain.usecase.MasteryLevel
import com.example.matholympiad.domain.usecase.ReviewSchedule
import com.example.matholympiad.presentation.theme.AppColors
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * 智能错题重练界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartReviewScreen(
    todayReviews: List<ReviewItem>,
    upcomingReviews: List<ReviewSchedule>,
    statistics: ReviewStatistics,
    onReviewQuestion: (Question) -> Unit,
    onViewSchedule: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // 顶部统计卡片
        StatisticsHeader(statistics = statistics)
        
        // Tab选择
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = AppColors.PrimaryOrange
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📚 今日复习")
                    if (statistics.todayReviews > 0) {
                        Badge(
                            containerColor = AppColors.AlertRed
                        ) {
                            Text(
                                "${statistics.todayReviews}",
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📅 复习计划")
                }
            }
        }
        
        // 内容区域
        when (selectedTab) {
            0 -> TodayReviewSection(
                reviews = todayReviews,
                onReviewClick = onReviewQuestion
            )
            1 -> ScheduleSection(
                schedules = upcomingReviews,
                onViewClick = onViewSchedule
            )
        }
    }
}

/**
 * 统计头部
 */
@Composable
private fun StatisticsHeader(statistics: ReviewStatistics) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF667eea)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "智能错题本",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Text(
                text = "基于SM-2间隔重复算法",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = "${statistics.totalWrongAnswers}",
                    label = "总错题",
                    icon = "📚"
                )
                StatItem(
                    value = "${statistics.masteredCount}",
                    label = "已掌握",
                    icon = "🎯"
                )
                StatItem(
                    value = "${statistics.averageMemoryStrength}%",
                    label = "记忆强度",
                    icon = "🧠"
                )
                StatItem(
                    value = "${statistics.streakDays}",
                    label = "连续天数",
                    icon = "🔥"
                )
            }
        }
    }
}

/**
 * 统计项
 */
@Composable
private fun StatItem(value: String, label: String, icon: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 20.sp
        )
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

/**
 * 今日复习区域
 */
@Composable
private fun TodayReviewSection(
    reviews: List<ReviewItem>,
    onReviewClick: (Question) -> Unit
) {
    if (reviews.isEmpty()) {
        EmptyReviewState()
    } else {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "今日还有 ${reviews.size} 道错题待复习",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.TextDark
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            items(
                items = reviews,
                key = { it.history.historyId }
            ) { item ->
                ReviewQuestionCard(
                    item = item,
                    onClick = { onReviewClick(item.question) }
                )
            }
        }
    }
}

/**
 * 空状态
 */
@Composable
private fun EmptyReviewState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🎉", fontSize = 80.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "太棒了！",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextDark
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "今日没有待复习的错题",
                fontSize = 16.sp,
                color = AppColors.TextGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "保持这个势头，继续学习！",
                fontSize = 14.sp,
                color = AppColors.SuccessGreen
            )
        }
    }
}

/**
 * 复习题目卡片
 */
@Composable
private fun ReviewQuestionCard(
    item: ReviewItem,
    onClick: () -> Unit
) {
    val masteryColor = when (item.masteryLevel) {
        MasteryLevel.NEW -> Color(0xFFFF6B6B)
        MasteryLevel.LEARNING -> Color(0xFFFFA502)
        MasteryLevel.NEARLY_MASTERED -> Color(0xFF2ED573)
        MasteryLevel.MASTERED -> Color(0xFF3742FA)
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (item.isCurrentReview) 2.dp else 1.dp,
            color = if (item.isCurrentReview) AppColors.PrimaryOrange else Color.LightGray
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 掌握度指示
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(masteryColor.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (item.masteryLevel) {
                            MasteryLevel.NEW -> "📖"
                            MasteryLevel.LEARNING -> "📚"
                            MasteryLevel.NEARLY_MASTERED -> "📝"
                            MasteryLevel.MASTERED -> "✅"
                        },
                        fontSize = 20.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.masteryLevel.displayName,
                        fontSize = 12.sp,
                        color = masteryColor,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = "第${item.history.reviewCount + 1}次复习",
                        fontSize = 14.sp,
                        color = AppColors.TextGray
                    )
                }
                
                // 记忆强度
                MemoryStrengthIndicator(strength = item.memoryStrength)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 题目内容（截断）
            Text(
                text = item.question.content.take(100) + 
                       if (item.question.content.length > 100) "..." else "",
                fontSize = 14.sp,
                color = AppColors.TextDark,
                maxLines = 3
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 底部信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
 // 上次复习时间
 val daysSince = ChronoUnit.DAYS.between(
 LocalDate.parse(
 java.time.Instant.ofEpochMilli(item.history.answeredAt)
     .atZone(java.time.ZoneId.systemDefault())
     .toLocalDate()
     .toString()
 ),
 LocalDate.now()
 )
                
                Text(
                    text = if (daysSince == 0L) "今天添加" else "${daysSince}天前",
                    fontSize = 12.sp,
                    color = AppColors.TextGray
                )
                
                // 开始复习按钮
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (item.isCurrentReview) 
                            AppColors.PrimaryOrange else Color(0xFF9CA3AF)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = item.isCurrentReview
                ) {
                    Text(
                        text = if (item.isCurrentReview) "开始复习" else "等待复习",
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

/**
 * 记忆强度指示器
 */
@Composable
private fun MemoryStrengthIndicator(strength: Int) {
    val color = when {
        strength >= 80 -> Color(0xFF2ED573)
        strength >= 50 -> Color(0xFFFFA502)
        else -> Color(0xFFFF6B6B)
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center
        ) {
 // 背景圆环
 CircularProgressIndicator(
 progress = 1f,
 modifier = Modifier.fillMaxSize(),
 color = Color.LightGray.copy(alpha = 0.3f),
 strokeWidth = 4.dp
 )
 
 // 进度圆环
 CircularProgressIndicator(
 progress = strength / 100f,
 modifier = Modifier.fillMaxSize(),
 color = color,
 strokeWidth = 4.dp
 )
            
            Text(
                text = "$strength",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextDark
            )
        }
        
        Text(
            text = "记忆度",
            fontSize = 10.sp,
            color = AppColors.TextGray
        )
    }
}

/**
 * 复习计划区域
 */
@Composable
private fun ScheduleSection(
    schedules: List<ReviewSchedule>,
    onViewClick: () -> Unit
) {
    if (schedules.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("📅", fontSize = 60.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "暂无待复习的题目",
                    fontSize = 16.sp,
                    color = AppColors.TextGray
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 分组显示
            val grouped = schedules.groupBy { it.daysUntilReview }
            
            grouped.toSortedMap().forEach { (days, items) ->
                item {
                    ScheduleSectionHeader(days = days, count = items.size)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                items(
                    items = items.take(3),
                    key = { it.questionId }
                ) { schedule ->
                    ScheduleItemCard(schedule = schedule)
                }
                
                if (items.size > 3) {
                    item {
                        Text(
                            text = "+${items.size - 3} 更多",
                            fontSize = 13.sp,
                            color = AppColors.PrimaryOrange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { onViewClick() },
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

/**
 * 日程分段标题
 */
@Composable
private fun ScheduleSectionHeader(days: Int, count: Int) {
    val text = when {
        days == 0 -> "今天 · $count 道"
        days == 1 -> "明天 · $count 道"
        days <= 7 -> "${days}天后 · $count 道"
        else -> "${days / 7}周后 · $count 道"
    }
    
    val color = when {
        days == 0 -> AppColors.AlertRed
        days == 1 -> Color(0xFFFFA502)
        days <= 3 -> AppColors.PrimaryOrange
        else -> AppColors.TextGray
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

/**
 * 日程项卡片
 */
@Composable
private fun ScheduleItemCard(schedule: ReviewSchedule) {
    val levelColor = when (schedule.masteryLevel) {
        MasteryLevel.NEW -> Color(0xFFFF6B6B)
        MasteryLevel.LEARNING -> Color(0xFFFFA502)
        MasteryLevel.NEARLY_MASTERED -> Color(0xFF2ED573)
        MasteryLevel.MASTERED -> Color(0xFF3742FA)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 掌握度
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(levelColor.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${schedule.memoryStrength}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = levelColor
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = schedule.questionContent.take(50) + 
                           if (schedule.questionContent.length > 50) "..." else "",
                    fontSize = 13.sp,
                    color = AppColors.TextDark,
                    maxLines = 2
                )
                
                if (schedule.estimatedMasteryDays > 0 && 
                    schedule.masteryLevel != MasteryLevel.MASTERED) {
                    Text(
                        text = "预计${schedule.estimatedMasteryDays}天后掌握",
                        fontSize = 11.sp,
                        color = AppColors.SuccessGreen
                    )
                }
            }
        }
    }
}

/**
 * 复习评价对话框
 * 答完错题后使用
 */
@Composable
fun ReviewQualityDialog(
    onDismiss: () -> Unit,
    onRate: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "💭 这次复习感觉如何？",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "请根据你的记忆程度选择：",
                    fontSize = 14.sp,
                    color = AppColors.TextGray
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 评分按钮
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QualityButton(
                        quality = 0,
                        emoji = "❌",
                        label = "完全没记住",
                        description = "下次明天复习",
                        color = Color(0xFFFF6B6B),
                        onRate = onRate
                    )
                    QualityButton(
                        quality = 3,
                        emoji = "🤔",
                        label = "困难想起",
                        description = "需要努力回想",
                        color = Color(0xFFFFA502),
                        onRate = onRate
                    )
                    QualityButton(
                        quality = 4,
                        emoji = "😊",
                        label = "基本正确",
                        description = "有一点迟疑",
                        color = Color(0xFF2ED573),
                        onRate = onRate
                    )
                    QualityButton(
                        quality = 5,
                        emoji = "🤩",
                        label = "完全掌握",
                        description = "秒答，很轻松",
                        color = Color(0xFF3742FA),
                        onRate = onRate
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

/**
 * 质量评分按钮
 */
@Composable
private fun QualityButton(
    quality: Int,
    emoji: String,
    label: String,
    description: String,
    color: Color,
    onRate: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRate(quality) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = color.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                fontSize = 24.sp
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = color
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = AppColors.TextGray
                )
            }
            
            // 星级
            Row {
                repeat(if (quality == 0) 1 else quality) {
                    Text("⭐", fontSize = 14.sp)
                }
            }
        }
    }
}

/**
 * 复习项数据类
 */
data class ReviewItem(
    val history: AnswerHistory,
    val question: Question,
    val masteryLevel: MasteryLevel,
    val memoryStrength: Int,
    val isCurrentReview: Boolean = false
)

/**
 * 复习统计
 */
data class ReviewStatistics(
    val totalWrongAnswers: Int,
    val todayReviews: Int,
    val upcomingReviews: Int,
    val masteredCount: Int,
    val learningCount: Int,
    val averageMemoryStrength: Int,
    val streakDays: Int
)
