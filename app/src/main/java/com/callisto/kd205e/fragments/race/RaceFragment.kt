package com.callisto.kd205e.fragments.race

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.callisto.kd205e.R
import com.callisto.kd205e.databinding.RaceSelectionFragmentBinding
import com.callisto.kd205e.fragments.BaseFragment
import com.callisto.kd205e.model.Attribute
import com.callisto.kd205e.model.Race
import com.callisto.kd205e.model.Trait
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.io.IOException

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.race_selection_fragment,
            container,
            false
        )

        viewModel = ViewModelProvider(this).get(RaceViewModel::class.java)

        binding.raceSelectionViewModel = viewModel

        binding.lifecycleOwner = viewLifecycleOwner

//        buildJsonByHand()

        getRacesFromJsonAssetFile()

        return binding.root
    }

    // TODO Move this to some kind of library class?
    private fun getJsonDataFromAsset(context: Context, fileName: String): String?
    {
        val jsonString: String

        try
        {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        }
        catch (ioException: IOException)
        {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

    // Source: http://bezkoder.com/kotlin-android-read-json-file-assets-gson/
    private fun getRacesFromJsonAssetFile(): Set<Race>
    {
        // JSON source files must be stored under src/main/assets
        val jsonFileString = getJsonDataFromAsset(requireContext(), "races.json")
        Timber.v(jsonFileString)

        val gson = Gson()

        val setRacesType = object : TypeToken<Set<Race>>()
        {}.type

        val setRaces: Set<Race> = gson.fromJson(jsonFileString, setRacesType)

        for (Race in setRaces)
        {
            Timber.v(Race.name)
        }

        return setRaces
    }

    private fun buildJsonByHand()
    {
        val gson = Gson()

        val str = Attribute("Strength", 2)
        val dex = Attribute("Dexterity", 2)
        val con = Attribute("Constitution", 2)
        val int = Attribute("Intelligence", 2)
        val cha = Attribute("Charisma", 2)

        val strHalf = Attribute("Strength", 1)
        val dexHalf = Attribute("Dexterity", 1)
        val conHalf = Attribute("Constitution", 1)
        val intHalf = Attribute("Intelligence", 1)
        val wisHalf = Attribute("Wisdom", 1)
        val chaHalf = Attribute("Charisma", 1)

        val halfElfPoints = Trait("Ability score picks", 2)

        val dragonborn = Race("Dragonborn", "Dragonborn", setOf(str, chaHalf))

        val dwarfHill = Race("Dwarf, hill", "Dwarven", setOf(con, wisHalf))

        val elfHigh = Race("Elf, high", "High-elven", setOf(dex, intHalf))

        val elfWood = Race("Elf, wood", "Wood-elven", setOf(dex, wisHalf))

        val elfDrow = Race("Elf, drow", "Drow", setOf(dex, chaHalf))

        val halfElf = Race("Half-elf", "Half-elven", setOf(cha), setOf(halfElfPoints))

        val halflingLightfoot = Race("Halfling, lightfoot", "Halfling", setOf(dex, chaHalf))

        val halflingStout = Race("Halfling, stout", "Halfling", setOf(dex, conHalf))

        val halfOrc = Race("Half-orc", "Half-orcish", setOf(str, conHalf))

        val human =
            Race("Human", "Human", setOf(strHalf, dexHalf, conHalf, intHalf, wisHalf, chaHalf))

        val gnomeForest = Race("Gnome, forest", "Gnome", setOf(int, dexHalf))

        val gnomeRock = Race("Gnome, rock", "Gnome", setOf(int, conHalf))

        val races = setOf(
            dragonborn,
            dwarfHill,
            elfHigh,
            elfDrow,
            elfWood,
            halfElf,
            halflingLightfoot,
            halflingStout,
            halfOrc,
            human,
            gnomeForest,
            gnomeRock
        )

        val jsonTut: String = gson.toJson(races)

        viewModel.onStringChanged(jsonTut)

        Timber.v(jsonTut)
    }
}