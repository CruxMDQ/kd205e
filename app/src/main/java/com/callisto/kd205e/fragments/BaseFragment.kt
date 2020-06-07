package com.callisto.kd205e.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import timber.log.Timber

abstract class BaseFragment : Fragment()
{
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.tag("TitleFragment").i("onAttach called")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag("TitleFragment").i("onCreate called")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Timber.tag("TitleFragment").i("onActivityCreated called")
    }

    override fun onStart() {
        super.onStart()
        Timber.tag("TitleFragment").i("onStart called")
    }

    override fun onResume() {
        super.onResume()
        Timber.tag("TitleFragment").i("onResume called")
    }

    override fun onPause() {
        super.onPause()
        Timber.tag("TitleFragment").i("onPause called")
    }

    override fun onStop() {
        super.onStop()
        Timber.tag("TitleFragment").i("onStop called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.tag("TitleFragment").i("onDestroyView called")
    }

    override fun onDetach() {
        super.onDetach()
        Timber.tag("TitleFragment").i("onDetach called")
    }
}