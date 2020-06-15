package com.callisto.kd205e.fragments.race

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RaceViewModel : ViewModel()
{
    private val _objectToJson = MutableLiveData<String>()

    val objectToJson: LiveData<String>
        get() = _objectToJson

    fun onStringChanged(param: String)
    {
        _objectToJson.value = param
    }
}