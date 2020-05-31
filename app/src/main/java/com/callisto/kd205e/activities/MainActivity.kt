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
        val iD20 = rollD20()

        binding.txtBaseRoll.text = iD20.toString()

        val sMod : String = binding.editModifier.text.toString()

        val iResult = iD20 + sMod.toInt()

        binding.lblResult.text = iResult.toString()
    }

    private fun rollD20(): Int
    {
        val iResult : Int

        iResult = (1..20).random()

        return iResult
    }
}
