package com.callisto.kd205e.fragments.species

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
import androidx.navigation.fragment.findNavController
import com.callisto.kd205e.R
import com.callisto.kd205e.SpinnerArrayAdapter
import com.callisto.kd205e.database.Kd205eDatabase
import com.callisto.kd205e.database.entities.Trait
import com.callisto.kd205e.database.models.SpeciesModel
import com.callisto.kd205e.databinding.RaceSelectionFragmentBinding
import com.callisto.kd205e.fragments.BaseFragment
import com.callisto.kd205e.fragments.scores.ScoresFragment
import timber.log.Timber

private const val ARG_PARAM1 = "characterId"

class SpeciesFragment : BaseFragment()
{
    companion object
    {
        fun newInstance() =
            SpeciesFragment()

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param characterId ID of the character being edited.
         * @return A new instance of fragment ScoresFragment.
         */
        @JvmStatic
        fun newInstance(characterId: Long) =
            ScoresFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_PARAM1, characterId)
                }
            }
    }

    private var characterId: Long? = null

    // To resolve these things, check the name of the layout used.
    // Source: https://stackoverflow.com/a/57228909
    private lateinit var binding: RaceSelectionFragmentBinding

    private lateinit var viewModel: SpeciesViewModel

    private lateinit var raceSpinnerAdapter: ArrayAdapter<SpeciesModel>
    private lateinit var traitSpinnerAdapter: ArrayAdapter<Trait>

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        arguments?.let {
            characterId = it.getLong(ARG_PARAM1)
        }
    }

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

        val database = Kd205eDatabase.getInstance(application).dao

        val viewModelFactory =
            SpeciesViewModelFactory(
                database,
                application
            )

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(SpeciesViewModel::class.java)

        binding.raceSelectionViewModel = viewModel

        binding.lifecycleOwner = viewLifecycleOwner

        binding.btnPickRaceAgain.setOnClickListener {
            showSpeciesSelectionDialog()
        }

        binding.btnSetAbilityScores.setOnClickListener {
            val action = SpeciesFragmentDirections.actionRaceSelectionFragmentToScoresFragment(viewModel.getCharacterId())
            this.findNavController().navigate(action)
        }

        // TODO This is an ugly hack to allow for back and forth navigation between fragments, find a better way
        if (characterId == 0L)
        {
            viewModel.track()
        }
        else
        {
            viewModel.track(characterId!!)
        }

//        viewModel.track()

        viewModel.speciesList.observe(viewLifecycleOwner, Observer {

//            raceSpinnerAdapter = ArrayAdapter(
//                this.requireContext(),
//                R.layout.row_item,
//                it
//            )
            // FIXED Replaced vanilla adapter with custom adapter to allow for 'Select One' title.
            // Source: https://medium.com/vattenfall-tech/android-spinner-customizations-8b4980fb0ee3
            raceSpinnerAdapter = SpinnerArrayAdapter(
                this.requireContext(),
                it
            )

            // FIXED Find out why the ProgressBar isn't being shown (possible fix: encase main layout into a container and add the ProgressBar as a sibling?)
            viewModel.isLoading.set(false)

            showSpeciesSelectionDialog()
        })

//        viewModel.traitsWithPendingSelections.observe(viewLifecycleOwner, Observer {
//            if (it.isNotEmpty())
        viewModel.activeTrait.observe(viewLifecycleOwner, Observer {
//            if (it.isNotNull())
//            {
                Timber.v("Observing list of traits with pending selections")

                traitSpinnerAdapter = SpinnerArrayAdapter(
                    this.requireContext(),
                    // it
                    viewModel.traitOptionsLD.value!!
                )

                showTraitSelectionDialog()
//            }
        })

        viewModel.summary.observe(viewLifecycleOwner, Observer {
            Timber.v("Observing summary of selections")

            binding.txtDetailsRace.text = it
        })

        return binding.root
    }

    private fun showTraitSelectionDialog()
    {
        val spinner = Spinner(requireContext())

        val alertDialogBuilder = AlertDialog.Builder(requireContext())

        spinner.adapter = traitSpinnerAdapter

        alertDialogBuilder.setTitle("Pick a trait")
        alertDialogBuilder.setMessage("Select a trait from the options available.")

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
                val trait: Trait? = traitSpinnerAdapter.getItem(position)

                if (position != 0)
                {
                    viewModel.onTraitPicked(trait)

                    alertDialog.dismiss()
                }
            }
        }

        alertDialog.show()

        spinner.setSelection(0, false)
    }

    // "Boss, da sauce fer doing dis 'ere fing:
    // https://inducesmile.com/kotlin-source-code/how-to-display-a-spinner-in-alert-dialog-in-kotlin/ "
    private fun showSpeciesSelectionDialog()
    {
        val spinner = Spinner(requireContext())

        val alertDialogBuilder = AlertDialog.Builder(requireContext())

        spinner.adapter = raceSpinnerAdapter

        alertDialogBuilder.setTitle("Pick a race")
        alertDialogBuilder.setMessage("Select a race from the options available.")

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
                val speciesModel: SpeciesModel? = raceSpinnerAdapter.getItem(position)

                if (position != 0)
                {
                    viewModel.onRacePicked(speciesModel)

                    alertDialog.dismiss()
                }
            }
        }

        alertDialog.show()

        spinner.setSelection(0, false)
    }
}