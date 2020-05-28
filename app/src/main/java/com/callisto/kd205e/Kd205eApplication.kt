package com.callisto.kd205e

import android.app.Application
import timber.log.Timber

class Kd205eApplication : Application()
{
    override fun onCreate()
    {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}