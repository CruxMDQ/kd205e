package com.callisto.kd205e.views.scoresetter

import com.callisto.kd205e.views.mvvm.CustomViewState
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScoreSetterState(
    val attributeName: String?,
    val attributeValue: Int?,
    val attributeModifier: Int?,
    val attributeIcon: String?
): CustomViewState