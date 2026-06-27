package com.victorkirui.meetnote

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MeetNoteApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MeetNoteApplication)
            modules(appModule)
        }
    }
}