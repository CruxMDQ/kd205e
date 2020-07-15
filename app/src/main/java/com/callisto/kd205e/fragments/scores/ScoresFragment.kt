package com.callisto.kd205e.fragments.scores

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.callisto.kd205e.R
import com.callisto.kd205e.database.Kd205eDatabase
import com.callisto.kd205e.database.model.Character
import com.callisto.kd205e.databinding.ScoresFragmentBinding
import com.callisto.kd205e.databinding.ViewScoreSetterBinding
import com.callisto.kd205e.fragments.BaseFragment

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "characterId"

/**
 * A simple [Fragment] subclass.
 * Use the [ScoresFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScoresFragment : BaseFragment()
{
    companion object
    {
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
    /*
    * private fun setListeners() {

   val clickableViews: List<View> =
       listOf(boxOneText, boxTwoText, boxThreeText,
boxFourText, boxFiveText, rootConstraintLayout,
redButton, greenButton, yellowButton
)
    */
    private var characterId: Long? = null

    private lateinit var binding: ScoresFragmentBinding

    private lateinit var viewModel: ScoresViewModel

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        arguments?.let {
            characterId = it.getLong(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.scores_fragment,
            container,
            false
        )

        val application = requireNotNull(this.activity).application

        val database = Kd205eDatabase.getInstance(application).dao

        val viewModelFactory =
            ScoresViewModelFactory(
                database,
                application
            )

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(ScoresViewModel::class.java)

        binding.scoresViewModel = viewModel

        binding.lifecycleOwner = viewLifecycleOwner

        val abilityStr = getString(R.string.ability_strength)
        val abilityDex = getString(R.string.ability_dexterity)
        val abilityCon = getString(R.string.ability_constitution)
        val abilityInt = getString(R.string.ability_intelligence)
        val abilityWis = getString(R.string.ability_wisdom)
        val abilityCha = getString(R.string.ability_charisma)

        viewModel.track(characterId!!)

        viewModel.strength.observe(viewLifecycleOwner, Observer {
            bindScoreSetter(
                binding.scoreSetterStrength,
                viewModel.activeCharacter.value!!,
                it.name
            )
        })

        viewModel.activeCharacter.observe(viewLifecycleOwner, Observer
        {
            for (attribute in it.abilityScores!!)
            {
                when (attribute.name)
                {
                    abilityStr -> bindScoreSetter(
                        binding.scoreSetterStrength,
                        it,
                        abilityStr
                    )
                    abilityDex -> bindScoreSetter(
                        binding.scoreSetterDexterity,
                        it,
                        abilityDex
                    )
                    abilityCon -> bindScoreSetter(
                        binding.scoreSetterConstitution,
                        it,
                        abilityCon
                    )
                    abilityInt -> bindScoreSetter(
                        binding.scoreSetterIntelligence,
                        it,
                        abilityInt
                    )
                    abilityWis -> bindScoreSetter(
                        binding.scoreSetterWisdom,
                        it,
                        abilityWis
                    )
                    abilityCha -> bindScoreSetter(
                        binding.scoreSetterCharisma,
                        it,
                        abilityCha
                    )
                }
            }
        })

        val abilitySetters = listOf(
            binding.scoreSetterStrength, binding.scoreSetterDexterity,
            binding.scoreSetterConstitution, binding.scoreSetterIntelligence,
            binding.scoreSetterWisdom, binding.scoreSetterCharisma
        )

        for (item in abilitySetters)
        {
            item.panelContainer.setOnClickListener { spawnEditingDialogBox(item.panelContainer.tag.toString()) }
        }
        return binding.root
    }

    private fun spawnEditingDialogBox(ability: String)
    {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())

        alertDialogBuilder.setTitle("Change $ability score")

        val input = EditText(requireContext())
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        input.layoutParams = params

        alertDialogBuilder.setView(input)

        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            val score = input.text.toString().toInt()

            viewModel.updateCharacterScore(ability, score)

            dialog.dismiss()
        }

        val dialog = alertDialogBuilder.create()

        dialog.show()
    }

    private fun bindScoreSetter(
        view: ViewScoreSetterBinding,
        character: Character,
        ability: String
    )
    {
        view.bind(
            character.getBaseScore(ability).toString(),
            character.getModifierScore(ability).toString(),
            character.getFinalScore(ability).toString()
        )

        view.panelContainer.tag = ability
    }

    // TODO Pick up from here
    private fun ViewScoreSetterBinding.bind(base: String, modifier: String, final: String)
    {
        txtBaseScore.text = base
        txtBonus.text = modifier
        txtFinalScore.text = final
    }
}

