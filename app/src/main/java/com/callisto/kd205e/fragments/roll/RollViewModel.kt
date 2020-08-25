package com.callisto.kd205e.fragments.roll

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.callisto.kd205e.database.models.Difficulty
import com.callisto.kd205e.RandomNumberGod

class RollViewModel : ViewModel()
{
    val mDifficulties = listOf(
        Difficulty("Very easy", 5),
        Difficulty("Easy", 10),
        Difficulty("Medium", 15),
        Difficulty("Hard", 20),
        Difficulty("Very hard", 25),
        Difficulty("Nearly impossible", 30),
        Difficulty("Custom", 0)
    )

    private val _rollBase = MutableLiveData<Int>()

    val rollBase: LiveData<Int>
        get() = _rollBase

    private val _rollAdjusted = MutableLiveData<Int>()

    val rollAdjusted: LiveData<Int>
        get() = _rollAdjusted

    private val _modifier = MutableLiveData<Int>()

    val modifier: LiveData<Int>
        get() = _modifier

    init {
        _modifier.value = 0
    }

    fun onBtnRollClicked()
    {
        _rollBase.value = RandomNumberGod.rollD20()

        _rollAdjusted.value = rollBase.value!!.plus(modifier.value!!)
    }

    fun onModifierChanged(param: String)
    {
        val mod = param.toInt()

        if (mod != _modifier.value)
            _modifier.value = mod
    }

    fun onDifficultySelected(param: Difficulty?) {
        _dc.value = param!!.value
    }

    fun onDifficultySetManually(param: String)
    {
        val difficulty = param.toInt()

        _dc.value = difficulty
    }

    val _dc = MutableLiveData<Int>()

    val dc: LiveData<Int>
        get() = _dc
}