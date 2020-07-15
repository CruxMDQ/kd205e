package com.callisto.kd205e.fragments.species

import android.app.Application
import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.callisto.kd205e.database.Kd205eDao
import com.callisto.kd205e.database.model.*
import com.callisto.kd205e.fragments.BaseViewModel
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.StringBuilder

class SpeciesViewModel
(
    val database: Kd205eDao,
    application: Application
) : BaseViewModel(application)
{
    var races = MutableLiveData<List<RaceModifierPair>>()

    private var selectedRace = MutableLiveData<Species>()

    val pickClassButtonEnabled = Transformations.map(selectedRace) {
        it != null
    }

    private var characterId = MutableLiveData<Long>()

    val selectedRaceString = Transformations.map(selectedRace) {
        selectedRace ->
        formatRace(selectedRace)
    }

    private fun formatRace(species: Species): Spanned
    {
        val sb = StringBuilder()
        sb.apply {
            for (abilityScoreModifier in species.abilityModifiers) {
                append(abilityScoreModifier.toString())
                append("<br>")
            }
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
        } else {
            HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }

    fun track()
    {
        uiScope.launch {
            characterId.value = createNewCharacter()

            races.value = getRacesWithModsFromDB()
        }
    }

    private suspend fun getSpecies(): List<Species>?
    {
        return withContext(Dispatchers.IO)
        {
            val racesAndAbilityModifiers = database.queryRacialAttributesView()

            Timber.v(racesAndAbilityModifiers.size.toString())

            val mListSpecies = mutableListOf<Species>()
            val mapRacesWithMods = racesAndAbilityModifiers.groupBy(
                keySelector = { it.race },
                valueTransform = { it.abilityScoreModifier }
            )

            mapRacesWithMods.forEach {
                mListSpecies.add(Species(it.key, it.value))
            }

            Timber.v("Races retrieved: %s", mListSpecies.size.toString())

            mListSpecies
        }
    }

    private suspend fun getRacesWithModsFromDB(): List<RaceModifierPair>?
    {
        return withContext(Dispatchers.IO)
        {
            val racesAndAbilityModifiers = database.queryRacialAttributesView()

            Timber.v(racesAndAbilityModifiers.size.toString())

            racesAndAbilityModifiers
        }
    }

    private suspend fun createNewCharacter() : Long
    {
        return withContext(Dispatchers.IO)
        {
            // TODO Fix insertion logic to avoid assigning a race to an empty character
            val newCharacterId = database.createNewCharacter(DBCharacter(1))

            Timber.v(newCharacterId.toString())

            newCharacterId
        }
    }

    fun onRacePicked(item: Species?)
    {
        selectedRace.value = item

        uiScope.launch {
            updateCharacter(characterId.value!!, selectedRace.value!!.race.raceId)
        }
    }

    private suspend fun updateCharacter(characterId: Long, raceId: Long)
    {
        return withContext(Dispatchers.IO)
        {
            database.updateCharacter(
                DBCharacter(characterId, raceId)
            )

            val character = database.getSingleCharacter(characterId)
            val race = database.getSingleRace(character?.raceId!!)

            Timber.v(character.characterId.toString())
            Timber.v(race!!.name)
        }
    }

    fun getCharacterId(): Long
    {
        return characterId.value!!
    }
}