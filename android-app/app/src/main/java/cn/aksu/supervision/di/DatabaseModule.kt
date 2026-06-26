package cn.aksu.supervision.di

import android.content.Context
import androidx.room.Room
import cn.aksu.supervision.data.local.AppDatabase
import cn.aksu.supervision.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "aksu_supervision.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideTokenDao(db: AppDatabase): TokenDao = db.tokenDao()

    @Provides
    fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()

    @Provides
    fun provideEnterpriseDao(db: AppDatabase): EnterpriseDao = db.enterpriseDao()

    @Provides
    fun providePendingInspectionDao(db: AppDatabase): PendingInspectionDao = db.pendingInspectionDao()

    @Provides
    fun provideAlertDao(db: AppDatabase): AlertDao = db.alertDao()
}
