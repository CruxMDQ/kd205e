package com.callisto.kd205e.fragments.scores

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.callisto.kd205e.R
import com.callisto.kd205e.database.Kd205eDatabase
import com.callisto.kd205e.database.models.CharacterModel
import com.callisto.kd205e.databinding.ScoresFragmentBinding
import com.callisto.kd205e.databinding.ViewScoreSetterBinding
import com.callisto.kd205e.fragments.BaseFragment
import kotlinx.android.synthetic.main.dialog_set_score.view.*
import timber.log.Timber

private const val ARG_PARAM1 = "characterId"

private const val STR = "Strength"
private const val DEX = "Dexterity"
private const val CON = "Constitution"
private const val INT = "Intelligence"
private const val WIS = "Wisdom"
private const val CHA = "Charisma"

private const val ABILITY_BASE_MIN = 3
private const val ABILITY_BASE_MAX = 18
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

    private var characterId: Long? = null

    private lateinit var binding: ScoresFragmentBinding

    private lateinit var viewModel: ScoresViewModel

    private val listOfSetters: List<ViewScoreSetterBinding>
        get()
        {
            return listOf(
                binding.scoreSetterStrength, binding.scoreSetterDexterity,
                binding.scoreSetterConstitution, binding.scoreSetterIntelligence,
                binding.scoreSetterWisdom, binding.scoreSetterCharisma
            )
        }

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

        viewModel.track(characterId!!)

        observeActiveCharacter()

        observeIfPointsNeedToBeDistributed()

        setClickListenersOnScoreSetters()

        setGoBackButtonLogic()

        return binding.root
    }

    private fun observeIfPointsNeedToBeDistributed()
    {
        // TODO Introduce check for character abilities requiring point distribution here

        viewModel.mustDistributePoints.observe(viewLifecycleOwner, Observer {
            Timber.v("Must distribute points: %s", it)

            if (it!!)
            {
                // FIXED Fix this: as it stands, this returns null
                if (viewModel.maxPointsPerAttribute.value == 1)
                {
                    displayCheckboxesOnScoreSetters()
                }
            }
            else
            {
                hideCheckboxesOnScoreSetters()
            }
        })
    }

    private fun observeActiveCharacter()
    {
        viewModel.activeCharacterModel.observe(viewLifecycleOwner, Observer
        {
            for (attribute in it.abilityScores!!)
            {
                when (attribute.name)
                {
                    STR -> prepareScoreSetter(
                        binding.scoreSetterStrength,
                        it,
                        STR,
                        resources.getDrawable(R.drawable.icons8_strength, null)
                    )
                    DEX -> prepareScoreSetter(
                        binding.scoreSetterDexterity,
                        it,
                        DEX,
                        resources.getDrawable(R.drawable.icons8_dexterity, null)
                    )
                    CON -> prepareScoreSetter(
                        binding.scoreSetterConstitution,
                        it,
                        CON,
                        resources.getDrawable(R.drawable.icons8_constitution, null)
                    )
                    INT -> prepareScoreSetter(
                        binding.scoreSetterIntelligence,
                        it,
                        INT,
                        resources.getDrawable(R.drawable.icons8_intelligence, null)
                    )
                    WIS -> prepareScoreSetter(
                        binding.scoreSetterWisdom,
                        it,
                        WIS,
                        resources.getDrawable(R.drawable.icons8_wisdom, null)
                    )
                    CHA -> prepareScoreSetter(
                        binding.scoreSetterCharisma,
                        it,
                        CHA,
                        resources.getDrawable(R.drawable.icons8_charisma, null)
                    )
                }
            }
        })
    }

    private fun setGoBackButtonLogic()
    {
        binding.btnGoBackToSpeciesSelection.setOnClickListener {
            val action =
                ScoresFragmentDirections.actionScoresFragmentToRaceSelectionFragment(viewModel.characterId.value!!)
            this.findNavController().navigate(action)
        }
    }

    private fun displayCheckboxesOnScoreSetters()
    {
        for (item in listOfSetters)
        {
            item.chkSelectStat.visibility = View.VISIBLE

            item.txtPlus.visibility = View.GONE
        }
    }

    private fun hideCheckboxesOnScoreSetters()
    {
        for (item in listOfSetters)
        {
            item.chkSelectStat.visibility = View.GONE

            item.txtPlus.visibility = View.VISIBLE
        }
    }

    private fun setClickListenersOnScoreSetters()
    {
        for (item in listOfSetters)
        {
            item.panelContainer.setOnClickListener { spawnCustomEditingDialogBox(item.panelContainer.tag.toString()) }
        }
    }

    private fun spawnCustomEditingDialogBox(ability: String)
    {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_set_score, null)

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Change $ability score")

        val dialog = alertDialogBuilder.show()

        dialogView.input.addTextChangedListener(object : TextWatcher
        {
            override fun afterTextChanged(s: Editable?)
            {
                try
                {
                    val score = s.toString().toInt()

                    if (score > ABILITY_BASE_MAX || score < ABILITY_BASE_MIN)
                    {
                        dialogView.txtInfo.visibility = View.VISIBLE

                        dialogView.btnSetNewScoreValue.isEnabled = false
                    }
                    else
                    {
                        dialogView.txtInfo.visibility = View.INVISIBLE

                        dialogView.btnSetNewScoreValue.isEnabled = true
                    }
                }
                catch(e: NumberFormatException) { }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        dialogView.btnSetNewScoreValue.setOnClickListener {
            val score = dialogView.input.text.toString().toInt()

            viewModel.updateScore(ability, score)

            dialog.dismiss()
        }
    }

    private fun prepareScoreSetter(
        view: ViewScoreSetterBinding,
        characterModel: CharacterModel,
        ability: String,
        iconDrawable: Drawable?
    )
    {
        val base = characterModel.getBaseScore(ability).toString()
        val final = characterModel.getFinalScore(ability).toString()

        val modifier = characterModel.getModifierScore(ability)
        val trait = characterModel.getTraitModifiers(ability)

        val bonus = modifier + trait

        view.bind(
            base,
            bonus.toString(),
            final,
            iconDrawable
        )

        view.panelContainer.tag = ability

        view.chkSelectStat.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
            {
                viewModel.addCharacterTraitAttribute(ability)
            }
            else
            {
                viewModel.removeCharacterTraitAttribute(ability)
            }
        }
    }

    private fun ViewScoreSetterBinding.bind(
        base: String,
        modifier: String,
        final: String,
        iconDrawable: Drawable?
    )
    {
        txtBaseScore.text = base
        txtBonus.text = modifier
        txtFinalScore.text = final
        imgAttributeIcon.setImageDrawable(iconDrawable)
    }
}

