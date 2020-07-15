package com.callisto.kd205e.views.mvvm

interface CustomViewModel<T: CustomViewState>
{
    var state: T?
}