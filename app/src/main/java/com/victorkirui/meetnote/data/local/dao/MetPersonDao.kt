package com.victorkirui.meetnote.data.local.dao

/*
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.victorkirui.meetnote.data.local.entity.MetPerson
import kotlinx.coroutines.flow.Flow

@Dao
interface MetPersonDao {

    @Insert
    suspend fun save(metPerson: MetPerson)

    @Query("SELECT * FROM MetPerson ORDER BY metOn DESC")
    fun getAllSavedContacts(): Flow<List<MetPerson>>

    @Query("SELECT * FROM metperson WHERE id = :id")
    fun getById(id: Long): Flow<MetPerson?>

    @Delete
    suspend fun delete(person: MetPerson)

}
*/