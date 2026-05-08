package com.victorkirui.meetnote.repository

import com.victorkirui.meetnote.data.MetPerson
import com.victorkirui.meetnote.data.MetPersonDao
import kotlinx.coroutines.flow.Flow

class MetPersonRepository(private val metPersonDao: MetPersonDao) {

    fun getAllSavedContacts(): Flow<List<MetPerson>>{
        return metPersonDao.getAllSavedContacts()
    }

    suspend fun saveContact(metPerson: MetPerson){
        metPersonDao.save(metPerson)
    }

    fun getContactById(id: Long): Flow<MetPerson?>{
        return metPersonDao.getById(id)
    }

    suspend fun deleteContact(metPerson: MetPerson){
        metPersonDao.delete(metPerson)
    }
}