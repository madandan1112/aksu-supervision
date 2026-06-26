package cn.aksu.supervision.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import cn.aksu.supervision.data.local.dao.*
import cn.aksu.supervision.data.local.entity.*

@Database(
    entities = [
        TokenEntity::class,
        TaskEntity::class,
        EnterpriseEntity::class,
        PendingInspectionEntity::class,
        AlertEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tokenDao(): TokenDao
    abstract fun taskDao(): TaskDao
    abstract fun enterpriseDao(): EnterpriseDao
    abstract fun pendingInspectionDao(): PendingInspectionDao
    abstract fun alertDao(): AlertDao
}
