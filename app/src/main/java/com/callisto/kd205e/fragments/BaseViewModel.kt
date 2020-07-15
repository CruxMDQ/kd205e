package com.callisto.kd205e.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

abstract class BaseViewModel
(
//    val database
    application: Application
) : AndroidViewModel(application)
{
    private var viewModelJob = Job()

    protected val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared()
    {
        super.onCleared()
        viewModelJob.cancel()
    }

//    abstract fun track()
}
