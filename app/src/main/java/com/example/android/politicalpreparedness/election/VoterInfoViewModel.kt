package com.example.android.politicalpreparedness.election

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.repository.ElectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VoterInfoViewModel(application: Application, val election: Election) : ViewModel() {

    private val database = ElectionDatabase.getInstance(application)
    private val network = CivicsApi.retrofitService
    private val repository = ElectionRepository(database)
    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo
    private val _isElectionInDb = MutableLiveData<Boolean>()
    val isElectionInDb: LiveData<Boolean>
        get() = _isElectionInDb

    init {
        viewModelScope.launch {
            _voterInfo.value = network.getVoterInfo(getCountryAndState(election.division), election.id.toInt()).await()
            isElectionInDb()
        }
    }

    suspend fun isElectionInDb(){
        withContext(Dispatchers.IO){
            _isElectionInDb.postValue(database.electionDao.exists(election.id.toInt()))
        }
    }

    private fun getCountryAndState(division: String): String{
        val country = division.substringAfter("country:","")
            .substringBefore("/")
        var state = division.substringAfter("state:","")
            .substringBefore("/")
        if(state == "") state = "ca"

        return "$country/$state"
    }

    fun followUnfollowElection(election: Election){
        viewModelScope.launch {
            if(_isElectionInDb.value == false) {
                repository.saveElection(election)
                _isElectionInDb.value = true
            }
            else {
                repository.deleteElection(election)
                _isElectionInDb.value = false
            }
        }
    }
}