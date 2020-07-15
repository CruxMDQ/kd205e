package com.callisto.kd205e.views.mvvm

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class CustomViewStateWrapper(
    val superState: Parcelable?,
    val state: CustomViewState?
) : Parcelable