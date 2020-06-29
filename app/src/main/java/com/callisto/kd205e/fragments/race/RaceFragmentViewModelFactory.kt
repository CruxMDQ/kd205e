package com.callisto.kd205e.fragments.race

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.callisto.kd205e.database.RaceDao

class RaceFragmentViewModelFactory(
    private val dataSource: RaceDao,
    private val application: Application) : ViewModelProvider.Factory
{
    // Provided factory method takes same argument as ViewModel?
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T
    {
        // Checks whether there's a matching view model class
        if (modelClass.isAssignableFrom(RaceViewModel::class.java))
        {
            return RaceViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
