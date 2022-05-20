/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.myapplication.data.source

import androidx.lifecycle.LiveData
import com.example.myapplication.data.model.Summon

/**
 * Main entry point for accessing summons data.
 */
interface SummonsDataSource {

    fun observeSummons(): LiveData<Result<List<Summon>>>

    suspend fun getSummons(): Result<List<Summon>>

    suspend fun refreshSummons()

    fun observeSummon(summonId: String): LiveData<Result<Summon>>

    suspend fun getSummon(summonId: String): Result<Summon>

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
