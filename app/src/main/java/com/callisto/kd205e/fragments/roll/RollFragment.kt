package com.callisto.kd205e.fragments.roll

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.callisto.kd205e.R
import com.callisto.kd205e.databinding.FragmentRollBinding
import com.callisto.kd205e.fragments.BaseFragment
import timber.log.Timber
import java.lang.NumberFormatException

// NEVER STORE REFERENCES TO FRAGMENTS, ACTIVITIES OR VIEWS. THEY DON'T SURVIVE CONFIG CHANGES.
class RollFragment : BaseFragment()
{
    // Oi, boss, dat 'persistenz' fing needs it?
    companion object
    {
        fun newInstance() = RollFragment()
    }

    // Binder gubbinz. Resolves views and does a lot of brain-urtin' stuff behind the scenes.
    private lateinit var binding: FragmentRollBinding

    // Model object, as in the MVC pattern. Contains all biz logic.
    private lateinit var viewModel: RollViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_roll,
            container,
            false
        )

        viewModel = ViewModelProvider(this).get(RollViewModel::class.java)

        binding.rollViewModel = viewModel

        binding.lifecycleOwner = viewLifecycleOwner

        binding.editModifier.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val param = binding.editModifier.text.toString()

                try
                {
                    viewModel.onModifierChanged(param)
                }
                catch (e: NumberFormatException)
                {
                    Timber.tag("RollFragment").e(e)

                    viewModel.onModifierChanged("0")
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        return binding.root
    }
}