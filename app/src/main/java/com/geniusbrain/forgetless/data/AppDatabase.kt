package com.geniusbrain.forgetless.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [TaskEntity::class, TaskDailyStatusEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun statusDao(): StatusDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "forgetless_database"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // 첫 실행 시 기본 4개 항목 생성
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    initializeDefaultTasks(database)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        private suspend fun initializeDefaultTasks(database: AppDatabase) {
            val taskDao = database.taskDao()
            val defaultTasks = listOf(
                "마스크 챙겨오기",
                "응가역사 숙제하기",
                "과학 숙제하기",
                "사회 숙제하기"
            )
            
            // 중복 생성 방지
            for (title in defaultTasks) {
                if (taskDao.countTasksByTitle(title) == 0) {
                    taskDao.insertTask(
                        TaskEntity(
                            title = title,
                            enabled = true,
                            hour = 8,
                            minute = 0,
                            repeatDays = "1,2,3,4,5", // 월~금
                            note = null
                        )
                    )
                }
            }
        }
    }
}

