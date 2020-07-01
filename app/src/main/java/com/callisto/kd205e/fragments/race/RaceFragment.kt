package com.callisto.kd205e.fragments.race

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.callisto.kd205e.R
import com.callisto.kd205e.database.Kd205eDatabase
import com.callisto.kd205e.database.model.RaceModifierPair
import com.callisto.kd205e.database.model.Species
import com.callisto.kd205e.databinding.RaceSelectionFragmentBinding
import com.callisto.kd205e.experimental.ExperimentalDatabase
import com.callisto.kd205e.fragments.BaseFragment
import timber.log.Timber

class RaceFragment : BaseFragment()
{
    companion object
    {
        fun newInstance() =
            RaceFragment()
    }

    // To resolve these things, check the name of the layout used.
    // Source: https://stackoverflow.com/a/57228909
    private lateinit var binding: RaceSelectionFragmentBinding

    private lateinit var viewModel: RaceViewModel

    private lateinit var raceSpinnerAdapter: ArrayAdapter<Species>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.race_selection_fragment,
            container,
            false
        )

        val application = requireNotNull(this.activity).application

        val database = Kd205eDatabase.getInstance(application).raceDao

        val viewModelFactory = RaceFragmentViewModelFactory(database, application)

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(RaceViewModel::class.java)

        binding.raceSelectionViewModel = viewModel

        binding.lifecycleOwner = viewLifecycleOwner

        binding.btnPickRaceAgain.setOnClickListener {
            showDialogSpinner()
        }

        viewModel.onStartTracking()

        viewModel.races.observe(viewLifecycleOwner, Observer {
            response ->
            if (!response.isNullOrEmpty())
            {
                raceSpinnerAdapter = ArrayAdapter(
                    this.requireContext(),
                    R.layout.row_item,
                    getSpeciesForSpinner(response)
                )

                showDialogSpinner()
            }
        })

        return binding.root
    }

    // TODO find a way to move this out of the fragment and into the model
    private fun getSpeciesForSpinner(response: List<RaceModifierPair>): MutableList<Species>
    {
        val mListSpecies = mutableListOf<Species>()
        val mapRacesWithMods = response.groupBy(
            keySelector = { it.race },
            valueTransform = { it.abilityScoreModifier }
        )

        mapRacesWithMods.forEach {
            mListSpecies.add(Species(it.key, it.value))
        }

        Timber.v("Races retrieved: %s", mListSpecies.size.toString())

        return mListSpecies
    }

    // "Boss, da sauce fer doing dis 'ere fing:
    // https://inducesmile.com/kotlin-source-code/how-to-display-a-spinner-in-alert-dialog-in-kotlin/ "
    private fun showDialogSpinner()
    {
        val spinner = Spinner(requireContext())

        val alertDialogBuilder = AlertDialog.Builder(requireContext())

        spinner.adapter = raceSpinnerAdapter

        spinner.setSelection(0, false)

        alertDialogBuilder.setTitle("Pick a race")
        alertDialogBuilder.setMessage("Select a race from the options shown.")

        alertDialogBuilder.setView(spinner)

        val alertDialog = alertDialogBuilder.create()

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onNothingSelected(parent: AdapterView<*>?) { }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            )
            {
                // FIXED Find out how to stop this from firing when the adapter is set
                // Source: https://stackoverflow.com/a/37561529
                val species: Species? = raceSpinnerAdapter.getItem(position)

                viewModel.onRacePicked(species)

                alertDialog.dismiss()
            }
        }

        alertDialog.show()
    }
}