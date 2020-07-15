package com.callisto.kd205e.views.base

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import com.callisto.kd205e.views.mvvm.*

/**
 * Article source: https://medium.com/@matthias.c.siegmund/mvvm-architecture-for-custom-views-on-android-b5636cb6be26
 *
 * To avoid implementing stuff like state recreation over and over again for each Custom View,
 * an abstract class like this one can handle the boiler plate.
 */
abstract class CustomLinearLayout<V: CustomViewState, T: CustomViewModel<V>>
    : LinearLayout, CustomView<V, T>
{
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)

    override fun onAttachedToWindow()
    {
        super.onAttachedToWindow()

        val lifeCycleOwner = context as? LifecycleOwner ?: throw LifecycleOwnerNotFoundException()

        onLifecycleOwnerAttached(lifeCycleOwner)
    }

    override fun onSaveInstanceState() = CustomViewStateWrapper(super.onSaveInstanceState(), viewModel.state)

    @Suppress("UNCHECKED_CAST")
    override fun onRestoreInstanceState(state: Parcelable?)
    {
        if (state is CustomViewStateWrapper)
        {
            viewModel.state = state.state as V

            super.onRestoreInstanceState(state.superState)
        }
    }
}