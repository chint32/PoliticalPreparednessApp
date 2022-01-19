package com.example.android.politicalpreparedness.representative

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel(): ViewModel() {

    private val network = CivicsApi.retrofitService

    val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    fun getRepresentivesForAddress(address: String){
        viewModelScope.launch {
            val repList = ArrayList<Representative>()
            val result = network.getRepresentative(address).await()
            for(i in result.offices.indices){
                repList.add(
                    Representative(
                    result.officials[i],
                    result.offices[i]
                    )
                )
            }

            _representatives.value = repList
        }
    }


}
