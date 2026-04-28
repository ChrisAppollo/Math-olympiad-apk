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
import java.time.temporal.ChronoUnit

@Composable
fun LearningReportCard(
 period: ReportPeriod,
 onPeriodChange: (ReportPeriod) -> Unit,
 stats: LearningStats,
 modifier: Modifier = Modifier
) {
 Card(
 modifier = modifier
 .fillMaxWidth()
 .padding(16.dp),
 shape = RoundedCornerShape(16.dp),
 colors = CardDefaults.cardColors(containerColor = Color.White)
 ) {
 Column(
 modifier = Modifier.padding(20.dp)
 ) {
 ReportHeader(period, onPeriodChange)
 ReportStats(stats)
 }
 }
}

@Composable
private fun ReportHeader(
 period: ReportPeriod,
 onPeriodChange: (ReportPeriod) -> Unit
) {
 Row(
 modifier = Modifier.fillMaxWidth(),
 horizontalArrangement = Arrangement.SpaceBetween,
 verticalAlignment = Alignment.CenterVertically
 ) {
 Text(
 text = when (period) {
 ReportPeriod.WEEKLY -> "本周学习"
 ReportPeriod.MONTHLY -> "本月学习"
 ReportPeriod.YEARLY -> "年度学习"
 },
 fontSize = 20.sp,
 fontWeight = FontWeight.Bold
 )
 
 PeriodSelector(period, onPeriodChange)
 }
}

@Composable
private fun PeriodSelector(
 current: ReportPeriod,
 onSelect: (ReportPeriod) -> Unit
) {
 Row(
 horizontalArrangement = Arrangement.spacedBy(8.dp)
 ) {
 ReportPeriod.values().forEach { period ->
 Box(
 modifier = Modifier
 .clip(RoundedCornerShape(8.dp))
 .background(if (period == current) AppColors.PrimaryOrange else Color.LightGray.copy(alpha = 0.3f))
 .clickable { onSelect(period) }
 .padding(horizontal = 12.dp, vertical = 6.dp)
 ) {
 Text(
 text = when (period) {
 ReportPeriod.WEEKLY -> "周"
 ReportPeriod.MONTHLY -> "月"
 ReportPeriod.YEARLY -> "年"
 },
 fontSize = 14.sp,
 color = if (period == current) Color.White else Color.Gray
 )
 }
 }
 }
}

@Composable
private fun ReportStats(stats: LearningStats) {
 Spacer(modifier = Modifier.height(16.dp))
 
 Row(
 modifier = Modifier.fillMaxWidth(),
 horizontalArrangement = Arrangement.SpaceEvenly
 ) {
 StatCard("做题总量", "${stats.totalQuestions}")
 StatCard("正确率", "${(stats.accuracy * 100).toInt()}%", stats.accuracy)
 StatCard("连续学习", "${stats.streakDays}天")
 StatCard("错题复习", "${stats.wrongQuestionCount}")
 }
 
 Spacer(modifier = Modifier.height(16.dp))
 TypeStatsCard(stats)
}

@Composable
private fun StatCard(
 label: String,
 value: String,
 accuracy: Float? = null
) {
 Column(
 horizontalAlignment = Alignment.CenterHorizontally
 ) {
 if (accuracy != null) {
 Box(
 modifier = Modifier.size(60.dp),
 contentAlignment = Alignment.Center
 ) {
 CircularProgressIndicator(
 progress = 1f,
 modifier = Modifier.fillMaxSize(),
 color = Color.LightGray.copy(alpha = 0.3f),
 strokeWidth = 6.dp
 )
 CircularProgressIndicator(
 progress = accuracy,
 modifier = Modifier.fillMaxSize(),
 color = AppColors.PrimaryOrange,
 strokeWidth = 6.dp
 )
 Text(
 text = "${(accuracy * 100).toInt()}%",
 fontSize = 14.sp,
 fontWeight = FontWeight.Bold
 )
 }
 } else {
 Text(
 text = value,
 fontSize = 24.sp,
 fontWeight = FontWeight.Bold,
 color = AppColors.TextDark
 )
 }
 
 Text(
 text = label,
 fontSize = 12.sp,
 color = Color.Gray
 )
 }
}

@Composable
private fun TypeStatsCard(stats: LearningStats) {
 Card(
 modifier = Modifier.fillMaxWidth(),
 shape = RoundedCornerShape(12.dp),
 colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.2f))
 ) {
 Column(modifier = Modifier.padding(12.dp)) {
 Text(
 text = "题型分布",
 fontSize = 14.sp,
 fontWeight = FontWeight.Medium
 )
 
 Spacer(modifier = Modifier.height(8.dp))
 
 Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
 TypeStatItem("计算", "${stats.calculationProgress}/10", AppColors.PrimaryOrange)
 TypeStatItem("逻辑", "${stats.logicProgress}/10", Color(0xFF2196F3))
 TypeStatItem("图形", "${stats.geometryProgress}/10", Color(0xFF4CAF50))
 }
 }
 }
}

@Composable
private fun TypeStatItem(label: String, value: String, color: Color) {
 Column(horizontalAlignment = Alignment.CenterHorizontally) {
 Text(
 text = value,
 fontSize = 16.sp,
 fontWeight = FontWeight.Bold,
 color = color
 )
 Text(
 text = label,
 fontSize = 12.sp,
 color = Color.Gray
 )
 }
}

@Composable
fun LearningPlanCard(
 title: String,
 target: String,
 description: String,
 deadline: String,
 progress: Float,
 onStart: () -> Unit,
 modifier: Modifier = Modifier
) {
 Card(
 modifier = modifier
 .fillMaxWidth()
 .padding(horizontal = 16.dp, vertical = 8.dp),
 shape = RoundedCornerShape(12.dp),
 colors = CardDefaults.cardColors(containerColor = Color.White)
 ) {
 Column(
 modifier = Modifier.padding(16.dp)
 ) {
 Row(
 modifier = Modifier.fillMaxWidth(),
 horizontalArrangement = Arrangement.SpaceBetween,
 verticalAlignment = Alignment.CenterVertically
 ) {
 Column(modifier = Modifier.weight(1f)) {
 Text(
 text = title,
 fontSize = 16.sp,
 fontWeight = FontWeight.SemiBold
 )
 Text(
 text = target,
 fontSize = 14.sp,
 color = Color.Gray
 )
 }
 
 if (deadline.isNotEmpty()) {
 Surface(
 shape = RoundedCornerShape(8.dp),
 color = Color(0xFFFFE4B5),
 modifier = Modifier.padding(start = 8.dp)
 ) {
 Text(
 text = "剩${deadline}",
 fontSize = 12.sp,
 modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
 color = Color(0xFF8B6914)
 )
 }
 }
 }
 
 Spacer(modifier = Modifier.height(12.dp))
 
 LinearProgressIndicator(
 progress = progress,
 modifier = Modifier.fillMaxWidth()
 .height(8.dp)
 .clip(RoundedCornerShape(4.dp)),
 color = AppColors.PrimaryOrange
 )
 
 Spacer(modifier = Modifier.height(8.dp))
 
 Row(
 modifier = Modifier.fillMaxWidth(),
 horizontalArrangement = Arrangement.SpaceBetween
 ) {
 Text(
 text = description,
 fontSize = 13.sp,
 color = Color.Gray,
 modifier = Modifier.weight(1f)
 )
 Text(
 text = "${(progress * 100).toInt()}%",
 fontSize = 14.sp,
 fontWeight = FontWeight.Bold
 )
 }
 
 Spacer(modifier = Modifier.height(12.dp))
 
 Button(
 onClick = onStart,
 modifier = Modifier.fillMaxWidth(),
 shape = RoundedCornerShape(8.dp)
 ) {
 Text("开始学习")
 }
 }
 }
}

enum class ReportPeriod {
 WEEKLY,
 MONTHLY,
 YEARLY
}

data class LearningStats(
 val totalQuestions: Int = 0,
 val accuracy: Float = 0f,
 val streakDays: Int = 0,
 val wrongQuestionCount: Int = 0,
 val calculationProgress: Int = 0,
 val logicProgress: Int = 0,
 val geometryProgress: Int = 0
)
