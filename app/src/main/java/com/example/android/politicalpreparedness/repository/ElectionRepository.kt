package com.example.android.politicalpreparedness.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectionRepository(
    private val database: ElectionDatabase
) {

    val electionsFromDb = database.electionDao.getElections()

    suspend fun saveElection(election: Election){
        withContext(Dispatchers.IO){
            database.electionDao.saveElection(election)
        }
    }

    suspend fun deleteElection(election: Election){
        withContext(Dispatchers.IO){
            database.electionDao.deleteElection(election.id)
        }
    }


}