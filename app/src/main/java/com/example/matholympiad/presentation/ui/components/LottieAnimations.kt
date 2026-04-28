package com.example.matholympiad.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.example.matholympiad.presentation.theme.AppColors
import kotlinx.coroutines.delay

/**
 * Lottie正确答题动画
 */
@Composable
fun CorrectAnswerAnimation(
 modifier: Modifier = Modifier,
 points: Int = 10,
 onAnimationEnd: () -> Unit = {}
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/correct.json"))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        speed = 1.0f
    )
    
    // 动画完成后关闭
    val isPlaying = progress < 1f
    LaunchedEffect(progress) {
        if (!isPlaying) {
            delay(300) // 短暂停留后关闭
            onAnimationEnd()
        }
    }
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 半透明背景
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )
        
        // 动画内容
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(32.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Lottie动画
                Box(
                    modifier = Modifier.size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "太棒了！",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.SuccessGreen
                )
                
Spacer(modifier = Modifier.height(8.dp))
 
 Text(
 text = "回答正确！\n+$points 积分",
 fontSize = 16.sp,
 color = AppColors.TextGray,
 textAlign = TextAlign.Center
 )
            }
        }
    }
}

/**
 * Lottie错误答题动画
 */
@Composable
fun WrongAnswerAnimation(
    modifier: Modifier = Modifier,
    onAnimationEnd: () -> Unit = {}
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/wrong.json"))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        speed = 1.0f
    )
    
    val isPlaying = progress < 1f
    LaunchedEffect(progress) {
        if (!isPlaying) {
            delay(300)
            onAnimationEnd()
        }
    }
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(32.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Lottie动画
                Box(
                    modifier = Modifier.size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "别灰心！",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.AlertRed
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "继续学习\n下次一定答对！",
                    fontSize = 16.sp,
                    color = AppColors.TextGray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Lottie闯关完成动画（使用原生Compose动画，因为完成动画复杂用Lottie需要额外资源）
 */
@Composable
fun QuizCompletionAnimation(
    totalScore: Int,
    modifier: Modifier = Modifier,
    onAnimationEnd: () -> Unit = {}
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("lottie/correct.json")
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 0.8f
    )
    
    LaunchedEffect(Unit) {
        delay(3000)
        onAnimationEnd()
    }
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Lottie动画作为烟花效果背景
                Box(
                    modifier = Modifier.size(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "闯关完成！",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.PrimaryOrange
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AppColors.WarmGold.copy(alpha = 0.2f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🏆 本次获得",
                            fontSize = 16.sp,
                            color = AppColors.TextGray
                        )
                        Text(
                            text = "$totalScore 积分",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.PrimaryOrange
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "明天继续挑战新知识！",
                    fontSize = 16.sp,
                    color = AppColors.TextGray
                )
            }
        }
    }
}

/**
 * 勋章解锁动画
 */
@Composable
fun BadgeUnlockedAnimation(
    badgeName: String,
    modifier: Modifier = Modifier,
    onAnimationEnd: () -> Unit = {}
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("lottie/correct.json")
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        speed = 1.2f
    )
    
    val isPlaying = progress < 1f
    LaunchedEffect(progress) {
        if (!isPlaying) {
            delay(500)
            onAnimationEnd()
        }
    }
    
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            )
            
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.WarmGold.copy(alpha = 0.95f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Lottie庆祝动画
                    Box(
                        modifier = Modifier.size(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "🏅",
                        fontSize = 60.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "获得新勋章！",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextDark
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = badgeName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextDark
                    )
                }
            }
        }
    }
}
