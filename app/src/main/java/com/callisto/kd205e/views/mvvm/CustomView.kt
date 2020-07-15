package com.callisto.kd205e.views.mvvm

import androidx.lifecycle.LifecycleOwner

/**
 * Article source: https://medium.com/@matthias.c.siegmund/mvvm-architecture-for-custom-views-on-android-b5636cb6be26
 *
 * Defines the basics needed to turn a Custom View into a self-contained component starting with a
 * [ViewModel].
 *
 * Said [ViewModel] will be the place where the Business Logic is and where some [LiveData]
 * representing a certain UI state will be exposed.
 *
 * Observing the [ViewModel]'s [LiveData] requires a [LifecycleOwner], hence a method defined to
 * pass said [LifecycleOwner] - obtained through the [Context] of the custom view, requiring the
 * parent to implement [LifecycleOwner].
 *
 * We cannot use the [ViewModel] to take care of state recreation, so the Custom View must do it.
 **/
interface CustomView<V: CustomViewState, T: CustomViewModel<V>>
{
    val viewModel: T

    fun onLifecycleOwnerAttached(lifecycleOwner: LifecycleOwner)
}