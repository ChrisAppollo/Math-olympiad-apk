package com.example.matholympiad.presentation.ui.wronganswers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matholympiad.data.local.model.AnswerHistory
import com.example.matholympiad.presentation.ui.wronganswers.WrongAnswersUiState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 错题本主界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WrongAnswersScreen(
    viewModel: WrongAnswersViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState: WrongAnswersUiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("错题本") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (uiState.reviewQuestions.isNotEmpty() && !uiState.showReviewMode) {
                        IconButton(onClick = { viewModel.startReviewMode() }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "开始复习")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.showReviewMode -> {
                WrongAnswerReviewMode(
                    uiState = uiState,
                    onAnswerSubmit = { viewModel.submitReviewAnswer(it) },
                    onExit = { viewModel.exitReviewMode() }
                )
            }
            else -> {
                WrongAnswersList(
                    uiState = uiState,
                    onQuestionClick = { /* TODO: 显示题目详情 */ },
                    onDelete = { viewModel.deleteQuestion(it) }
                )
            }
        }
    }
}

/**
 * 错题列表界面
 */
@Composable
fun WrongAnswersList(
    uiState: WrongAnswersUiState,
    onQuestionClick: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 统计卡片
        StatsCard(
            totalWrong = uiState.totalWrongCount,
            dueReview = uiState.dueReviewCount
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 待复习提醒
        if (uiState.dueReviewCount > 0) {
            ReviewReminderCard(
                count = uiState.dueReviewCount,
                onStartReview = { /* TODO: 触发复习 */ }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // 错题列表
        if (uiState.wrongAnswers.isEmpty()) {
            EmptyState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.wrongAnswers) { history ->
                    WrongAnswerItem(
                        history = history,
                        onClick = { onQuestionClick(history.questionId) },
                        onDelete = { onDelete(history.questionId) }
                    )
                }
            }
        }
    }
}

/**
 * 统计卡片
 */
@Composable
fun StatsCard(totalWrong: Int, dueReview: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            label = "总错题",
            value = totalWrong,
            color = Color(0xFF2196F3)
        )
        
        StatItem(
            label = "待复习",
            value = dueReview,
            color = if (dueReview > 0) Color(0xFFFF9800) else Color(0xFF4CAF50)
        )
    }
}

@Composable
fun StatItem(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

/**
 * 复习提醒卡片
 */
@Composable
fun ReviewReminderCard(count: Int, onStartReview: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onStartReview),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3E0)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = Color(0xFFFF9800),
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "有 $count 道题目需要复习",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "基于艾宾浩斯记忆曲线，及时复习效果更好",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Button(onClick = onStartReview) {
                Text("开始复习")
            }
        }
    }
}

/**
 * 错题项
 */
@Composable
fun WrongAnswerItem(
    history: AnswerHistory,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
verticalAlignment = Alignment.CenterVertically
) {
    // 图标
    Icon(
        imageVector = Icons.Default.Info,
        contentDescription = null,
        tint = Color(0xFFF44336),
        modifier = Modifier.size(32.dp)
    )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // 信息
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "错题 #${history.historyId}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatTime(history.answeredAt),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "复习阶段：${history.reviewStage}/${AnswerHistory.MAX_REVIEW_STAGE}",
                    fontSize = 12.sp,
                    color = Color(0xFF607D8B)
                )
            }
            
            // 删除按钮
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = Color.Gray
                )
            }
        }
    }
}

/**
 * 复习模式界面
 */
@Composable
fun WrongAnswerReviewMode(
    uiState: WrongAnswersUiState,
    onAnswerSubmit: (Int) -> Unit,
    onExit: () -> Unit
) {
    val currentQuestion = uiState.currentReviewQuestion
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 进度指示器
        Text(
            text = "复习进度：${uiState.reviewIndex + 1} / ${uiState.reviewTotal}",
            fontSize = 16.sp,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (currentQuestion != null) {
            // 题目卡片
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "请回忆这道题的答案",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "题目 ID: ${currentQuestion.questionId}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 答案选项（这里需要获取题目详情）
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(0, 1, 2, 3).forEach { index ->
                    OutlinedButton(
                        onClick = { onAnswerSubmit(index) },
                        modifier = Modifier
                            .width(80.dp)
                            .height(80.dp)
                    ) {
                        Text("选项${index + 1}")
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // 退出按钮
        if (uiState.reviewSuccess != null) {
            Button(
                onClick = onExit,
                modifier = Modifier.width(200.dp)
            ) {
                Text("退出复习")
            }
        }
    }
}

/**
 * 空状态
 */
@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "太棒了！",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "目前没有错题记录",
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}

/**
 * 格式化时间
 */
private fun formatTime(timestamp: Long): String {
    val dateTime = LocalDateTime.ofInstant(
        java.time.Instant.ofEpochMilli(timestamp),
        java.time.ZoneId.systemDefault()
    )
    return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
}
