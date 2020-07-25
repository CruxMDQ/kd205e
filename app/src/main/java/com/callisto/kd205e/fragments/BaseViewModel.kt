package com.callisto.kd205e.fragments

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

abstract class BaseViewModel
    : AndroidViewModel
{
    //    val database
    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(application: Application) : super(application)
    {
        this.isLoading = ObservableBoolean()
        this.viewModelJob = Job()
        this.uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

        isLoading.set(true)
    }

    @Suppress("JoinDeclarationAndAssignment")
    val isLoading: ObservableBoolean

    private var viewModelJob: Job

    protected val uiScope: CoroutineScope

    override fun onCleared()
    {
        super.onCleared()
        viewModelJob.cancel()
    }
}
