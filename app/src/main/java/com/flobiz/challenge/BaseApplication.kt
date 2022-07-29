package com.flobiz.challenge

import android.app.Application
import com.flobiz.challenge.api.StackExchangeApi
import com.flobiz.challenge.db.DataRepository
import com.flobiz.challenge.db.RoomDb

class BaseApplication : Application() {

    val database by lazy { RoomDb.getDatabase(this) }
    val repository by lazy { DataRepository(database.questionDao(), retrofit) }
    val retrofit by lazy { StackExchangeApi.getInstance() }
}