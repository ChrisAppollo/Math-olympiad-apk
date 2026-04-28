package com.example.matholympiad.di

import android.content.Context
import androidx.room.Room
import com.example.matholympiad.data.local.database.AppDatabase
import com.example.matholympiad.data.local.database.DatabaseMigrations
import com.example.matholympiad.data.local.dao.UserDao
import com.example.matholympiad.data.local.dao.QuestionDao
import com.example.matholympiad.data.local.dao.AnswerHistoryDao
import com.example.matholympiad.data.local.dao.AnswerHistoryDaoImpl
import com.example.matholympiad.data.repository.QuestionRepo
import com.example.matholympiad.data.repository.UserRepo
import com.example.matholympiad.domain.usecase.CheckBadges
import com.example.matholympiad.domain.usecase.EncouragementGenerator
import com.example.matholympiad.domain.usecase.GetTodayQuestions
import com.example.matholympiad.domain.usecase.SubmitAnswerUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
@Provides
@Singleton
fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
return Room.databaseBuilder(
context.applicationContext,
AppDatabase::class.java,
"math_olympiad_database"
)
.addMigrations(DatabaseMigrations.MIGRATION_1_2, DatabaseMigrations.MIGRATION_2_3, DatabaseMigrations.MIGRATION_3_4)
.fallbackToDestructiveMigration()
.build()
}
    
    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase) = database.userDao()
    
    @Provides
    @Singleton
    fun provideQuestionDao(database: AppDatabase) = database.questionDao()
    
 @Provides
 @Singleton
 fun provideAnswerHistoryDao(): AnswerHistoryDao = AnswerHistoryDaoImpl()
    
    @Provides
    @Singleton
    fun provideUserRepo(userDao: UserDao) = UserRepo(userDao)
    
    @Provides
    @Singleton
    fun provideQuestionRepo(questionDao: QuestionDao) = QuestionRepo(questionDao)
    
    @Provides
    @Singleton
    fun provideGetTodayQuestions(questionRepo: QuestionRepo) = GetTodayQuestions(questionRepo)
    
    @Provides
    @Singleton
    fun provideSubmitAnswerUseCase(userRepo: UserRepo) = SubmitAnswerUseCase(userRepo)
    
    @Provides
    @Singleton
    fun provideCheckBadges(userRepo: UserRepo) = CheckBadges(userRepo)
    
    @Provides
    @Singleton
    fun provideEncouragementGenerator() = EncouragementGenerator()
}
