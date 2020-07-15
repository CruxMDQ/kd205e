package com.callisto.kd205e.fragments.species

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.callisto.kd205e.database.Kd205eDao

class SpeciesViewModelFactory(
    private val dataSource: Kd205eDao,
    private val application: Application) : ViewModelProvider.Factory
{
    // Provided factory method takes same argument as ViewModel?
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T
    {
        // Checks whether there's a matching view model class
        if (modelClass.isAssignableFrom(SpeciesViewModel::class.java))
        {
            return SpeciesViewModel(
                dataSource,
                application
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
