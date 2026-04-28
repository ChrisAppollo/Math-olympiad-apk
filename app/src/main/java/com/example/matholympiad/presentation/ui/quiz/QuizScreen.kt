package com.example.matholympiad.presentation.ui.quiz

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.matholympiad.presentation.viewmodel.QuizUiState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matholympiad.presentation.theme.AppColors
import com.example.matholympiad.presentation.ui.components.CorrectAnswerAnimation
import com.example.matholympiad.presentation.ui.components.WrongAnswerAnimation
import com.example.matholympiad.presentation.ui.components.QuizCompletionAnimation

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun QuizScreen(
    uiState: QuizUiState,
    userAnswer: String,
    onAnswerChanged: (String) -> Unit,
    onSubmitClick: () -> Unit,
    onNextClick: () -> Unit,
    onHintClick: () -> Unit,
    onBackClick: () -> Unit
) {
    // Debug: Log UI state changes
    LaunchedEffect(uiState) {
        android.util.Log.d("QuizScreen", "UI State: feedback=${uiState.feedbackShowing}, answer='$userAnswer', hint=${uiState.hintShowing}")
    }

    // 动画状态
    var showCorrectAnimation by remember { mutableStateOf(false) }
    var showWrongAnimation by remember { mutableStateOf(false) }
    var showCompletionAnimation by remember { mutableStateOf(false) }

    // 键盘控制
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    // 监听答题结果触发动画
    LaunchedEffect(uiState.feedbackShowing, uiState.isCorrect, uiState.quizCompleted) {
        if (uiState.feedbackShowing) {
            if (uiState.quizCompleted) {
                showCompletionAnimation = true
            } else if (uiState.isCorrect == true) {
                showCorrectAnimation = true
            } else if (uiState.isCorrect == false) {
                showWrongAnimation = true
            }
        }
    }

    // 新题目加载时自动聚焦输入框
    LaunchedEffect(uiState.currentQuestionIndex) {
        if (!uiState.feedbackShowing && !uiState.quizCompleted) {
            focusRequester.requestFocus()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = AppColors.BackgroundGray,
            topBar = {
                TopAppBar(
                    title = {
                        Text("答题闯关", color = AppColors.TextDark)
                    },
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 进度指示器
                QuizProgressIndicator(
                    currentQuestion = uiState.currentQuestionIndex + 1,
                    totalQuestions = uiState.totalQuestions,
                    score = uiState.score
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                        if (uiState.currentQuestion != null) {
                            // 显示模块和主题标签
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (uiState.currentQuestion.module.isNotBlank()) {
                                    ModuleTag(uiState.currentQuestion.module)
                                }
                                if (uiState.currentQuestion.topic.isNotBlank()) {
                                    TopicTag(uiState.currentQuestion.topic)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = uiState.currentQuestion.content,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextDark
                            )

                            // 显示题目类型提示
                            if (uiState.currentQuestion.isMultipleChoice()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "（选择题）",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AppColors.TextGray
                                )
                            }
                        } else if (uiState.quizCompleted) {
                            Text(
                                text = "今日答题完成！",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.PrimaryOrange
                            )
                        } else {
                            Text(
                                text = "加载题目中...",
                                style = MaterialTheme.typography.titleLarge,
                                color = AppColors.TextGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 回答区域（仅在未完成时显示）
                if (!uiState.quizCompleted && uiState.currentQuestion != null) {
                    // 如果是选择题，显示选项
                    if (uiState.currentQuestion.isMultipleChoice()) {
                        MultipleChoiceOptions(
                            options = uiState.currentQuestion.getOptionsList(),
                            selectedAnswer = userAnswer.toIntOrNull() ?: -1,
                            feedbackShowing = uiState.feedbackShowing,
                            correctAnswer = uiState.currentQuestion.correctAnswer,
                            onOptionSelected = { index ->
                                onAnswerChanged(index.toString())
                            }
                        )
                    } else {
                        // 填空题：显示输入框
                        AnswerInputField(
                            value = userAnswer,
                            onValueChange = onAnswerChanged,
                            enabled = !uiState.feedbackShowing,
                            focusRequester = focusRequester,
                            onDone = {
                                keyboardController?.hide()
                                if (userAnswer.isNotBlank()) {
                                    onSubmitClick()
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 提示卡片
                AnimatedVisibility(
                    visible = uiState.hintShowing && uiState.hintText.isNotBlank(),
                    enter = fadeIn() + expandVertically()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = AppColors.WarmGold.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "💡 提示",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.hintText,
                                fontSize = 14.sp,
                                color = AppColors.TextGray
                            )
                        }
                    }
                }

                // 反馈区域
                AnimatedVisibility(
                    visible = uiState.feedbackShowing && !uiState.quizCompleted && uiState.explanation.isNotBlank(),
                    enter = fadeIn() + expandVertically()
                ) {
                    FeedbackCard(
                        isCorrect = uiState.isCorrect == true,
                        explanation = uiState.explanation,
                        encouragement = uiState.encouragement,
                        correctAnswer = uiState.currentQuestion?.correctAnswerText ?: ""
                    )
                }

                // 占位器顶到底部
                Spacer(modifier = Modifier.weight(1f))

                // 底部操作按钮
                if (!uiState.quizCompleted) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 提示按钮
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .background(
                                    if (uiState.hintShowing) AppColors.TextGray else AppColors.WarmGold,
                                    RoundedCornerShape(12.dp)
                                )
                                .then(
                                    if (!uiState.feedbackShowing) {
                                        Modifier.clickable {
                                            android.util.Log.d("QuizScreen", "Hint button clicked")
                                            onHintClick()
                                        }
                                    } else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (uiState.hintShowing) "已显示 💡" else "💡 提示",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextDark
                            )
                        }

                        val submitBgColor = when {
                            uiState.feedbackShowing && uiState.isCorrect == true -> AppColors.SuccessGreen
                            uiState.feedbackShowing && uiState.isCorrect == false -> AppColors.AlertRed
                            else -> AppColors.PrimaryOrange
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .background(submitBgColor, RoundedCornerShape(12.dp))
                                .clickable {
                                    if (uiState.feedbackShowing) {
                                        onNextClick()
                                    } else {
                                        keyboardController?.hide()
                                        onSubmitClick()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (uiState.feedbackShowing) {
                                    if (uiState.isCorrect == true) "下一题 →" else "继续 →"
                                } else "提交答案",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                } else {
                    // 答题完成按钮
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(AppColors.PrimaryOrange, RoundedCornerShape(12.dp))
                            .clickable { onBackClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("返回首页 🏠", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // 动画叠加层
            if (showCorrectAnimation) {
                CorrectAnswerAnimation(
                    onAnimationEnd = { showCorrectAnimation = false }
                )
            }

            if (showWrongAnimation) {
                WrongAnswerAnimation(
                    onAnimationEnd = { showWrongAnimation = false }
                )
            }

            if (showCompletionAnimation) {
                QuizCompletionAnimation(
                    totalScore = uiState.score,
                    onAnimationEnd = { showCompletionAnimation = false }
                )
            }
        }
    }
}

@Composable
private fun ModuleTag(module: String) {
    Box(
        modifier = Modifier
            .background(AppColors.PrimaryOrange.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = module,
            fontSize = 12.sp,
            color = AppColors.PrimaryOrange,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TopicTag(topic: String) {
    Box(
        modifier = Modifier
            .background(AppColors.SkyBlue.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = topic,
            fontSize = 12.sp,
            color = AppColors.SkyBlue,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnswerInputField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    focusRequester: FocusRequester,
    onDone: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        label = { Text("请输入答案") },
        placeholder = { Text("在此输入你的答案...") },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppColors.PrimaryOrange,
            focusedLabelColor = AppColors.PrimaryOrange,
            disabledBorderColor = AppColors.TextGray.copy(alpha = 0.3f),
            disabledTextColor = AppColors.TextGray
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDone() }
        )
    )
}

@Composable
private fun MultipleChoiceOptions(
    options: List<String>,
    selectedAnswer: Int,
    feedbackShowing: Boolean,
    correctAnswer: Int,
    onOptionSelected: (Int) -> Unit
) {
    options.forEachIndexed { index, option ->
        val isSelected = selectedAnswer == index
        val isCorrectAnswer = index == correctAnswer

        val backgroundColor = when {
            feedbackShowing && isCorrectAnswer -> AppColors.SuccessGreen
            feedbackShowing && isSelected && !isCorrectAnswer -> AppColors.AlertRed
            isSelected -> AppColors.SkyBlue
            else -> AppColors.White
        }

        val contentColor = when {
            feedbackShowing && (isCorrectAnswer || (isSelected && !isCorrectAnswer)) -> Color.White
            isSelected -> Color.White
            else -> AppColors.TextDark
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(vertical = 4.dp)
                .background(backgroundColor, RoundedCornerShape(12.dp))
                .then(
                    if (!feedbackShowing) {
                        Modifier.clickable {
                            android.util.Log.d("QuizScreen", "Option clicked: $index")
                            onOptionSelected(index)
                        }
                    } else Modifier
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${('A'.code + index).toChar()}.",
                    fontSize = 16.sp,
                    color = contentColor.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = option,
                    fontSize = 18.sp,
                    color = contentColor,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                when {
                    feedbackShowing && isCorrectAnswer -> Text("✅", fontSize = 20.sp)
                    feedbackShowing && isSelected && !isCorrectAnswer -> Text("❌", fontSize = 20.sp)
                    isSelected && !feedbackShowing -> Text("●", fontSize = 12.sp, color = contentColor)
                }
            }
        }
    }
}

@Composable
private fun QuizProgressIndicator(
    currentQuestion: Int,
    totalQuestions: Int,
    score: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "题目 $currentQuestion / $totalQuestions",
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.TextDark
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🏆", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${score}分",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.PrimaryOrange
                )
            }
        }
    }
}

@Composable
private fun FeedbackCard(
    isCorrect: Boolean,
    explanation: String,
    encouragement: String,
    correctAnswer: String
) {
    val backgroundColor = if (isCorrect) AppColors.SuccessGreen.copy(alpha = 0.1f)
    else AppColors.AlertRed.copy(alpha = 0.1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = if (isCorrect) "🎉 回答正确！" else "😔 回答错误",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isCorrect) AppColors.SuccessGreen else AppColors.AlertRed
            )

            if (!isCorrect && correctAnswer.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "✅ 正确答案：$correctAnswer",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppColors.SuccessGreen,
                    fontWeight = FontWeight.Medium
                )
            }

            if (encouragement.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = encouragement,
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppColors.TextDark
                )
            }

            if (explanation.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "📖 解析：$explanation",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextGray
                )
            }
        }
    }
}
