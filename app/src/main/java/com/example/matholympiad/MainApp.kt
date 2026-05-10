package com.example.matholympiad

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.SupervisorJob
import com.example.matholympiad.data.local.database.AppDatabase
import com.example.matholympiad.data.repository.QuestionRepo
import java.io.File

@HiltAndroidApp
class MainApp : Application() {
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
 override fun onCreate() {
 super.onCreate()
 Log.d("MainApp", "Application onCreate started")
 
 // 启动初始化协程
 applicationScope.launch {
 try {
 initializeDatabase()
 } catch (e: Exception) {
 Log.e("MainApp", "Failed to initialize database", e)
 }
 }
 }
 
 private suspend fun initializeDatabase() {
 val db = AppDatabase.getDatabase(this)
 val questionDao = db.questionDao()
 val repo = QuestionRepo(questionDao)
 
 // 检查数据库是否为空
 val count = questionDao.getQuestionCount()
 Log.d("MainApp", "Current question count in database: $count")
        
        if (count == 0) {
            Log.d("MainApp", "Database is empty, loading questions from assets...")
            // 从 assets 加载 JSON
            val filePath = "${this.filesDir.parent}/databases/questions.json"
            
            // 先尝试从 assets 复制到内部存储
            try {
                assets.open("questions.json").use { input ->
                    val outputFile = File(filesDir, "questions.json")
                    outputFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                    Log.d("MainApp", "Copied questions.json to internal storage")
                }
                
// 加载到数据库
val jsonFilePath = File(filesDir, "questions.json").absolutePath
val success = repo.loadQuestionsFromJson(jsonFilePath)
Log.d("MainApp", "JSON load result: $success")

if (success) {
val newCount = questionDao.getQuestionCount()
Log.d("MainApp", "New question count: $newCount")
}
            } catch (e: Exception) {
                Log.e("MainApp", "Error loading questions", e)
            }
        } else {
            Log.d("MainApp", "Database already has $count questions, skipping initialization")
        }
    }
}
