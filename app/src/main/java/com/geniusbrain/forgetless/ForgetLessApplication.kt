package com.geniusbrain.forgetless

import android.app.Application
import com.geniusbrain.forgetless.data.AppDatabase
import com.geniusbrain.forgetless.data.Repository

class ForgetLessApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val repository: Repository by lazy {
        Repository(database.taskDao(), database.statusDao())
    }
}

