package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveElection(election: Election)

    @Query("select * from election_table")
    fun getElections(): LiveData<List<Election>>

    @Query("select * from election_table where id = :electionId")
    fun getElection(electionId: String): LiveData<Election>

    @Query("delete from election_table where id = :electionId")
    fun deleteElection(electionId: String)

    @Query("delete from election_table")
    fun clearSavedElections()

    @Query("SELECT EXISTS (SELECT 1 FROM election_table WHERE id = :id)")
    fun exists(id: Int): Boolean
}