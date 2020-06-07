package com.callisto.kd205e.activities

import android.os.Bundle
import com.callisto.kd205e.R
import com.callisto.kd205e.fragments.roll.RollFragment

class MainActivity : BaseActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null)
        {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, RollFragment.newInstance())
                .commitNow()
        }
    }
}
