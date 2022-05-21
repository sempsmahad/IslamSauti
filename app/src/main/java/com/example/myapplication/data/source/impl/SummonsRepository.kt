package com.example.myapplication.data.source.impl

import androidx.lifecycle.LiveData
import com.example.myapplication.AppExecutors
import com.example.myapplication.data.SautiService
import com.example.myapplication.data.source.local.SautiDb
import com.example.myapplication.data.source.local.SummonDao
import com.example.myapplication.data.model.Summon
import com.example.myapplication.data.Resource
import com.example.myapplication.data.tasks.CreateSummonTask
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummonsRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val db: SautiDb,
    private val summonDao: SummonDao,
    private val sautiService: SautiService
) {
    private val allSummons by lazy {
        summonDao.getSummons()
    }

    fun getSavedSummons() = allSummons

    fun saveSummon(summon: Summon) {
        createSummon(summon)
    }

//    fun createSession(session: Session): LiveData<Resource<Session>> {
//        return object : NetworkBoundResource<Session, Session>(appExecutors) {
//            override fun saveCallResult(item: Session) = dao.insert(item)
//
//
//            override fun shouldFetch(data: Session?): Boolean = data == null
//
//
//            override fun loadFromDb(): LiveData<Session> = dao.getSession(session.id)
//
//
//            override fun createCall() = service.createSession(session)
//
//
//        }.asLiveData()
//    }

    fun createSummon(summon: Summon): LiveData<Resource<Boolean>> {
        val createSummonTask = CreateSummonTask(
            summon = summon,
            service = service,
        )
        appExecutors.networkIO().execute(createSummonTask)
        return createSummonTask.liveData
    }


    suspend fun updateSummon(request: Summon) {
        dao.updateSummon(request)
    }


    suspend fun deleteSummons() {
        dao.clearSummons()
    }

}