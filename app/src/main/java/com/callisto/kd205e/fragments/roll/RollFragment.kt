package com.callisto.kd205e.fragments.roll

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.callisto.kd205e.R
import com.callisto.kd205e.databinding.FragmentRollBinding
import com.callisto.kd205e.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_roll.*
import timber.log.Timber

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

        val arrayAdapter = ArrayAdapter(
            this.requireContext(),
            R.layout.row_item,
            viewModel.mDifficulties
        )

        binding.spDifficulty.adapter = arrayAdapter

        binding.spDifficulty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onNothingSelected(parent: AdapterView<*>?) { /*TODO("Not yet implemented")*/ }

            override fun onItemSelected
            (
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = arrayAdapter.getItem(position)

                if (item!!.title == "Custom")
                {
                    editDifficulty.visibility = View.VISIBLE
                    txtDifficulty.visibility = View.INVISIBLE
                }
                else
                {
                    viewModel.onDifficultySelected(item)
                }
            }
        }

        binding.editDifficulty.addTextChangedListener(object : TextWatcher
        {
            override fun afterTextChanged(s: Editable?) {
                val param = editDifficulty.text.toString()

                try
                {
                    viewModel.onDifficultySetManually(param)
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

        binding.editDifficulty.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if(keyCode == KeyEvent.KEYCODE_ENTER)
            {
                txtDifficulty.visibility = View.VISIBLE
                editDifficulty.visibility = View.INVISIBLE

                val view = activity?.currentFocus
                view?.let { v ->
                    val imm =
                        context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(v.windowToken, 0)
                }
                return@OnKeyListener true
            }
            false
        })

        binding.txtModifier.setOnClickListener {
            txtModifier.visibility = View.INVISIBLE
            editModifier.visibility = View.VISIBLE

            // BUG: "Um, boss, dis 'ere fing don't open when s'posed ta."
            // FIX: *KUFF* "'Ere'z yer solushun, ya git! Add that... urr... 'InputMethodManager' gubbin."
            editModifier.text.clear()
            editModifier.requestFocus()

            // Source: stackoverflow.com/questions/1109022
            val view = activity?.currentFocus
            view?.let { v ->
                val imm =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.showSoftInput(view, 0)
            }
        }

        binding.editModifier.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val param = editModifier.text.toString()

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

        binding.editModifier.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if(keyCode == KeyEvent.KEYCODE_ENTER)
            {
                txtModifier.visibility = View.VISIBLE
                editModifier.visibility = View.INVISIBLE

                // Source: stackoverflow.com/questions/1109022
                val view = activity?.currentFocus
                view?.let { vi ->
                    val imm =
                        context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(vi.windowToken, 0)
                }
                return@OnKeyListener true
            }
            false
        })

        binding.txtResult.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (viewModel.rollAdjusted.value!! >= viewModel.dc.value!!)
                {
                    // Source: https://stackoverflow.com/questions/31842983/
                    txtResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.my_green))
                }
                else
                {
                    txtResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.my_red))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        return binding.root
    }
}