package com.callisto.kd205e.views.scoresetter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.callisto.kd205e.views.mvvm.CustomViewModel

class ScoreSetterModel: CustomViewModel<ScoreSetterState>
{
    var attributeName = MutableLiveData<String?>()

    var attributeScore = MutableLiveData<Int?>()

    var attributeModifier = MutableLiveData<Int?>()

    var attributeIcon = MutableLiveData<String?>()

    override var state: ScoreSetterState? = null
        get() = ScoreSetterState(
            attributeName.value,
            attributeScore.value,
            attributeModifier.value,
            attributeIcon.value
        )
        set(value) {
            field = value
            restore(value)
        }

    fun getLabel(): LiveData<String?> = attributeName

    fun getScore(): LiveData<Int?> = attributeScore

    fun getModifier(): LiveData<Int?> = attributeModifier

    fun getIcon(): LiveData<String?> = attributeIcon

    fun getFinalScore(): Int {
        return getScore().value!! + getModifier().value!!
    }

    // do functions managing clicks and stuff here

    private fun restore(state: ScoreSetterState?)
    {
        attributeName.value = state?.attributeName
        attributeScore.value = state?.attributeValue
        attributeModifier.value = state?.attributeModifier
        attributeIcon.value = state?.attributeIcon
//        attributeValue.value = state?.hexCode
    }

}