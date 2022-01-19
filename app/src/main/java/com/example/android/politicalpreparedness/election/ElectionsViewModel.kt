package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.ElectionRepository
import kotlinx.coroutines.launch

class ElectionsViewModel(application: Application): ViewModel() {

    private val TAG = "ElectionsViewModel"

    private val database = ElectionDatabase.getInstance(application)
    private val network = CivicsApi.retrofitService
    private val repository = ElectionRepository(database)

    val savedElections = repository.electionsFromDb

    val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    init {
        getElectionsFromApi()
    }

    fun getElectionsFromApi(){
        viewModelScope.launch {
            val result = network.getElections().await()
            _upcomingElections.value = result.elections
            Log.d(TAG, "remoteData  -  " + result.toString())


        }
    }
}