package com.example.myapplication.data.source

import androidx.lifecycle.LiveData
import com.example.myapplication.data.Result
import com.example.myapplication.data.model.Summon

/**
 * Interface to the data layer.
 */
interface SummonsRepository {

    fun observeSummons(): LiveData<Result<List<Summon>>>

    suspend fun getSummons(forceUpdate: Boolean = false): Result<List<Summon>>

    suspend fun refreshSummons()

    fun observeSummon(summonId: String): LiveData<Result<Summon>>

    suspend fun getSummon(summonId: String, forceUpdate: Boolean = false): Result<Summon>

    suspend fun refreshSummon(summonId: String)

    suspend fun saveSummon(summon: Summon)

    suspend fun completeSummon(summon: Summon)

    suspend fun completeSummon(summonId: String)

    suspend fun activateSummon(summon: Summon)

    suspend fun activateSummon(summonId: String)

    suspend fun clearCompletedSummons()

    suspend fun deleteAllSummons()

    suspend fun deleteSummon(summonId: String)
}
