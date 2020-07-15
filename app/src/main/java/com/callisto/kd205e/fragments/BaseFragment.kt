package com.callisto.kd205e.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import timber.log.Timber

abstract class BaseFragment : Fragment()
{
    private var fragmentTag: String = ""
    
    override fun onAttach(context: Context) {
        super.onAttach(context)

        // This abortion of a code was found here: https://stackoverflow.com/a/45433261
        fragmentTag = this.javaClass.asSubclass(this.javaClass).simpleName

        Timber.tag(fragmentTag).i("onAttach called")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag(fragmentTag).i("onCreate called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        Timber.tag(fragmentTag).i("onCreateView called")
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Timber.tag(fragmentTag).i("onActivityCreated called")
    }

    override fun onStart() {
        super.onStart()
        Timber.tag(fragmentTag).i("onStart called")
    }

    override fun onResume() {
        super.onResume()
        Timber.tag(fragmentTag).i("onResume called")
    }

    override fun onPause() {
        super.onPause()
        Timber.tag(fragmentTag).i("onPause called")
    }

    override fun onStop() {
        super.onStop()
        Timber.tag(fragmentTag).i("onStop called")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        Timber.tag(fragmentTag).i("onViewCreated called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.tag(fragmentTag).i("onDestroyView called")
    }

    override fun onDetach() {
        super.onDetach()
        Timber.tag(fragmentTag).i("onDetach called")
    }
}