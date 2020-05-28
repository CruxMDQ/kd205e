package com.callisto.kd205e.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

abstract class BaseActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        Timber.i("onCreate called")
    }

    override fun onStart()
    {
        super.onStart()

        Timber.i("onStart called")
    }

    override fun onResume()
    {
        super.onResume()

        Timber.i("onResume called")
    }

    override fun onPause()
    {
        super.onPause()

        Timber.i("onPause called")
    }

    override fun onStop()
    {
        super.onStop()

        Timber.i("onStop called")
    }

    override fun onDestroy()
    {
        super.onDestroy()

        Timber.i("onDestroy called")
    }

    override fun onRestart()
    {
        super.onRestart()

        Timber.i("onRestart called")
    }
}