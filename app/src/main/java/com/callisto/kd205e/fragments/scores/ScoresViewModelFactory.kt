package com.callisto.kd205e.fragments.scores

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.callisto.kd205e.database.Kd205eDao

class ScoresViewModelFactory(
    private val dataSource: Kd205eDao,
    private val application: Application) : ViewModelProvider.Factory
{
    // Provided factory method takes same argument as ViewModel?
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T
    {
        // Checks whether there's a matching view model class
        if (modelClass.isAssignableFrom(ScoresViewModel::class.java))
        {
            return ScoresViewModel(
                dataSource,
                application
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
