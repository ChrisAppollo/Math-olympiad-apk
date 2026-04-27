package com.example.matholympiad.presentation.ui.wronganswers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.matholympiad.presentation.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WrongAnswersScreen(
    onBack: () -> Unit,
    viewModel: WrongAnswersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8E7))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 顶部导航栏
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "📚 错题本",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadWrongAnswers() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "刷新",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AppColors.PrimaryOrange
                )
            )

            // 统计信息
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        value = "${uiState.wrongQuestions.size}",
                        label = "错题总数"
                    )
                    StatItem(
                        value = "${uiState.wrongQuestions.sumOf { it.wrongCount }}",
                        label = "累计错误"
                    )
                    StatItem(
                        value = if (uiState.wrongQuestions.isNotEmpty()) {
                            "${uiState.wrongQuestions.sumOf { it.wrongCount } / uiState.wrongQuestions.size}"
                        } else "0",
                        label = "平均错误次数"
                    )
                }
            }

            // 错题列表
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppColors.PrimaryOrange)
                    }
                }
                uiState.error != null -> {
                    ErrorView(message = uiState.error!!) {
                        viewModel.loadWrongAnswers()
                    }
                }
                uiState.wrongQuestions.isEmpty() -> {
                    EmptyWrongAnswersView()
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.wrongQuestions,
                            key = { it.question.id }
                        ) { item ->
                            WrongAnswerCard(
                                item = item,
                                onClick = { viewModel.selectQuestion(item) }
                            )
                        }
                        // 底部留白
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }

        // 错题详情弹窗
        if (uiState.showDetailDialog && uiState.selectedQuestion != null) {
            WrongAnswerDetailDialog(
                item = uiState.selectedQuestion!!,
                onDismiss = { viewModel.dismissDetailDialog() }
            )
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.PrimaryOrange
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun EmptyWrongAnswersView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFFFFD93D)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "暂无错题记录",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Text(
                "继续保持，答对每一题！",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Red
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                message,
                fontSize = 16.sp,
                color = Color.Red
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.PrimaryOrange
                )
            ) {
                Text("重试")
            }
        }
    }
}

@Composable
private fun WrongAnswerCard(
    item: WrongAnswerItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // 错误次数标签（位于左上角）
        if (item.wrongCount > 1) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(AppColors.PrimaryOrange, RoundedCornerShape(topStart = 12.dp, bottomEnd = 12.dp))
                    .align(Alignment.Start),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "${item.wrongCount}",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 题型标签
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = when (item.question.type) {
                        "ARITHMETIC" -> "🔢 计算题"
                        "LOGIC" -> "🧩 逻辑题"
                        "GEOMETRY" -> "📐 几何题"
                        "WORD" -> "📖 应用题"
                        else -> "📝 ${item.question.type}"
                    },
                    fontSize = 14.sp,
                    color = AppColors.SkyBlue,
                    fontWeight = FontWeight.Medium
                )
                DifficultyBadge(difficulty = item.question.difficulty)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 题目内容
            Text(
                text = item.question.content,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 用户答案和正确答案对比
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "你的答案",
                        fontSize = 12.sp,
                        color = Color.Red
                    )
                    Text(
                        item.userAnswer,
                        fontSize = 16.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        "正确答案",
                        fontSize = 12.sp,
                        color = Color(0xFF4CAF50)
                    )
                    Text(
                        item.correctAnswerText,
                        fontSize = 16.sp,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 错误时间
            Text(
                text = "上次做错: ${formatTime(item.wrongTime)}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun DifficultyBadge(difficulty: Int) {
    val (color, label) = when (difficulty) {
        1 -> Color(0xFF4CAF50) to "简单"
        2 -> Color(0xFF8BC34A) to "较易"
        3 -> Color(0xFFFFC107) to "中等"
        4 -> Color(0xFFFF9800) to "较难"
        else -> Color(0xFFF44336) to "困难"
    }
    Box(
        modifier = Modifier
            .border(1.dp, color, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            label,
            fontSize = 12.sp,
            color = color
        )
    }
}

@Composable
private fun WrongAnswerDetailDialog(
    item: WrongAnswerItem,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "📝 错题详情",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (item.wrongCount > 1) {
                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFFFF6B6B),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "错过${item.wrongCount}次",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 题目
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = item.question.content,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 答案对比
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "你的答案 ❌",
                            fontSize = 14.sp,
                            color = Color.Red
                        )
                        Text(
                            item.userAnswer,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            "正确答案 ✅",
                            fontSize = 14.sp,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            item.correctAnswerText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 解析
                Text(
                    "💡 题目解析",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.PrimaryOrange
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.question.explanation,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.PrimaryOrange
                )
            ) {
                Text("好的，我知道了")
            }
        },
        containerColor = Color.White
    )
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
