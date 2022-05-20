package com.example.myapplication.data.tasks

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.data.Resource
import com.example.myapplication.data.SautiService
import com.example.myapplication.data.db.SautiDb
import com.example.myapplication.data.db.SummonDao
import com.example.myapplication.data.model.*
import com.example.myapplication.utils.FileUtil
import com.example.myapplication.utils.ProgressRequestBody
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import javax.inject.Inject

/**
 * A task that create the search result in the database and fetches the next page, if it has one.
 */
class CreateSummonTask @Inject constructor(
    private val summon: Summon,
    private val sautiService: SautiService,
    private val summonDao: SummonDao,
    private val db: SautiDb,
) : Runnable {
    private val _liveData = MutableLiveData<Resource<Boolean>>()
    val liveData: LiveData<Resource<Boolean>> = _liveData

    override fun run() {

        val newSummon = try {
            val file = File(FileUtil.getPath(Uri(summon.path), this))
            val descTopic: RequestBody = RequestBody.create(MediaType.parse("text/plain"), summon.topic)
            val descDate: RequestBody = RequestBody.create(MediaType.parse("text/plain"), summon.date)
            val descName: RequestBody = RequestBody.create(MediaType.parse("text/plain"), summon.name)
            val descDesc: RequestBody =
                RequestBody.create(MediaType.parse("text/plain"), summon.description )

            val fileBody = ProgressRequestBody(file, "audio", this@AudioUploadFormActivity)
            val filePart = MultipartBody.Part.createFormData("audio", file.name, fileBody)

            val response = sautiService.createSummon(summon).execute()

            when (val apiResponse = ApiResponse.create(response)) {
                is ApiSuccessResponse -> {
                    summonDao.insert(summon)
                    Resource.success(true)
                }
                is ApiEmptyResponse -> {
                    Resource.success(false)
                }
                is ApiErrorResponse -> {
                    Resource.error(apiResponse.errorMessage, true)
                }
            }

        } catch (e: IOException) {
            Resource.error(e.message!!, true)
        }
        _liveData.postValue(newSummon)
    }
}
