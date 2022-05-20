package com.example.myapplication.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

import com.example.myapplication.data.model.Summon

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
