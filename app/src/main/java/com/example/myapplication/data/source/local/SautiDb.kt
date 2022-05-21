package com.example.myapplication.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

import com.example.myapplication.data.model.Summon
import com.example.myapplication.data.source.local.SummonDao

/**
 * Main database description.
 */
@Database(
    entities = [
        Summon::class],
    version = 1,
    exportSchema = false
)
abstract class SautiDb : RoomDatabase() {

    abstract fun summonDao(): SummonDao
}
