package com.example.myapplication.data.db

import android.util.SparseIntArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.data.model.Summon

/**
 * Interface for database access on Summon related operations.
 */
@Dao
abstract class SummonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(vararg summons: Summon)

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    abstract fun insertContributors(contributors: List<Contributor>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSummons(summons: List<Summon>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun createSummonIfNotExists(summon: Summon): Long

    @Query("SELECT * FROM summon WHERE name = :name")
    abstract fun load(name: String): LiveData<Summon>

//    @Query(
//        """
//        SELECT login, avatarUrl, summonName, summonOwner, contributions FROM contributor
//        WHERE summonName = :name AND summonOwner = :owner
//        ORDER BY contributions DESC"""
//    )
//    abstract fun loadContributors(owner: String, name: String): LiveData<List<Contributor>>

//    @Query(
//        """
//        SELECT * FROM Summon
//        WHERE owner_login = :owner
//        ORDER BY stars DESC"""
//    )
//    abstract fun loadSummons(owner: String): LiveData<List<Summon>>

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    abstract fun insert(result: RepoSearchResult)

//    @Query("SELECT * FROM SummonSearchResult WHERE `query` = :query")
//    abstract fun search(query: String): LiveData<SummonSearchResult?>

    fun loadOrdered(summonIds: List<Int>): LiveData<List<Summon>> {
        val order = SparseIntArray()
        summonIds.withIndex().forEach {
            order.put(it.value, it.index)
        }
        return loadById(summonIds).map { repositories ->
            repositories.sortedWith(compareBy { order.get(it.id) })
        }
    }

    @Query("SELECT * FROM Summon WHERE id in (:summonIds)")
    protected abstract fun loadById(summonIds: List<Int>): LiveData<List<Summon>>

//    @Query("SELECT * FROM SummonSearchResult WHERE `query` = :query")
//    abstract fun findSearchResult(query: String): SummonSearchResult?
}
