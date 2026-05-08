package com.victorkirui.meetnote.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MetPerson::class, MyProfile::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    companion object{
        const val DATABASE_NAME = "AppDatabase"
    }

    abstract fun metPersonDao(): MetPersonDao
    abstract fun myProfile(): MyProfileDao
}