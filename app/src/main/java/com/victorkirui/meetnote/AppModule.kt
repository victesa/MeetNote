package com.victorkirui.meetnote

import androidx.room.Room
import com.victorkirui.meetnote.data.AppDatabase
import com.victorkirui.meetnote.repository.MetPersonRepository
import com.victorkirui.meetnote.repository.MyProfileRepository
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    //DAOs
    single { get<AppDatabase>().metPersonDao() }
    single { get<AppDatabase>().myProfile() }

    //Repositories
    single { MetPersonRepository(get()) }
    single { MyProfileRepository(get()) }


}