package com.callisto.kd205e.fragments.roll

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.callisto.kd205e.Difficulty

class RollViewModel : ViewModel()
{
    val mDifficulties = listOf(
        Difficulty("Very easy", 5),
        Difficulty("Easy", 10),
        Difficulty("Medium", 15),
        Difficulty("Hard", 20),
        Difficulty("Very hard", 25),
        Difficulty("Nearly impossible", 30)
    )

    private val _rollBase = MutableLiveData<Int>()

    val rollBase: LiveData<Int>
        get() = _rollBase

    private val _rollAdjusted = MutableLiveData<Int>()

    val rollAdjusted: LiveData<Int>
        get() = _rollAdjusted

    private val _modifier = MutableLiveData<Int>()

    private val modifier: LiveData<Int>
        get() = _modifier

    private val _difficulty = MutableLiveData<Difficulty>()

    val difficulty: LiveData<Difficulty>
        get() = _difficulty

    init {
        _modifier.value = 0
    }

    fun onBtnRollClicked()
    {
        _rollBase.value = rollD20()

        _rollAdjusted.value = rollBase.value!!.plus(modifier.value!!)
    }

    fun onModifierChanged(modifier: String)
    {
        val mod = modifier.toInt()

        if (mod != _modifier.value)
            _modifier.value = mod
    }

    fun onDifficultySelected(item: Difficulty?) {
        _difficulty.value = item
    }

    private fun rollD20(): Int
    {
        @Suppress("JoinDeclarationAndAssignment")
        val iResult : Int

        iResult = (1..20).random()

        return iResult
    }
}