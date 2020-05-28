package com.callisto.kd205e.activities

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.callisto.kd205e.R
import com.callisto.kd205e.databinding.ActivityMainBinding

class MainActivity : BaseActivity()
{
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.btnRoll.setOnClickListener { onBtnRollClicked() }
    }

    private fun onBtnRollClicked()
    {
        binding.lblRoll.setText(rollDice().toString())
    }

    private fun rollDice(): Int
    {
        var iResult : Int

        iResult = (1..20).random()

        return iResult
    }
}
