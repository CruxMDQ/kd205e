package com.callisto.kd205e.fragments.scores

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.callisto.kd205e.database.Kd205eDao
import com.callisto.kd205e.database.model.*
import com.callisto.kd205e.fragments.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ScoresViewModel
(
    val database: Kd205eDao,
    application: Application
) : BaseViewModel(application)
{
    var activeCharacter = MutableLiveData<Character>()

    var strength = MutableLiveData<AbilityScore>()
    var dexterity = MutableLiveData<AbilityScore>()
    var constitution = MutableLiveData<AbilityScore>()
    var intelligence = MutableLiveData<AbilityScore>()
    var wisdom = MutableLiveData<AbilityScore>()
    var charisma = MutableLiveData<AbilityScore>()

    private lateinit var attributes: List<DBAttribute>

    fun track(characterId: Long)
    {
        uiScope.launch {

//            getAttributes()

            activeCharacter.value = setUpCharacterFromDB(characterId)
        }
    }

    private fun getAttributes()
    {
        uiScope.launch {
            suspend { attributes = database.checkAllAttributes() }
        }
    }

    private suspend fun setUpCharacterFromDB(characterId: Long): Character?
    {
        return withContext(Dispatchers.IO)
        {
            val dbCharacter = database.getSingleCharacter(characterId)!!

            attributes = database.checkAllAttributes()
            //            val dbRace = database.getSingleRace(dbCharacter.raceId)

            for (attribute in attributes)
            {
                database.insertCharacterScore(
                    DBAbilityScore(characterId, attribute.attributeId, roll4d6MinusLowest())
                )
            }

            val scores = database.getScoresForCharacter(characterId)

            val charSpecies = getCharacterSpecies(dbCharacter.raceId)

            val result = Character(dbCharacter, charSpecies, scores)

            result
        }
    }

    private suspend fun refreshCharacter(characterId: Long): Character?
    {
        return withContext(Dispatchers.IO)
        {
            val dbCharacter = database.getSingleCharacter(characterId)!!

            val scores = database.getScoresForCharacter(characterId)

            val charSpecies = getCharacterSpecies(dbCharacter.raceId)

            val result = Character(dbCharacter, charSpecies, scores)

            result
        }
    }

    private suspend fun getCharacterSpecies(raceId: Long): Species?
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

            var result: Species? = null

            for (species in mListSpecies)
            {
                if (species.race.raceId == raceId)
                {
                    result = species
                    break
                }
            }

            result
        }
    }

    private fun roll4d6MinusLowest(): Int
    {
        val rolls = arrayOf((1..6).random(), (1..6).random(), (1..6).random(), (1..6).random())

        return rolls.sum() - rolls.min()!!
    }

    fun updateCharacterScore(ability: String, score: Int)
    {
        val characterId = activeCharacter.value!!.character.characterId

        val attributeId = attributes.find { it.name == ability }!!.attributeId

//        val abilityScoreUpdated = activeCharacter.value?.abilityScores?.find { ability == it.name }
//
//        abilityScoreUpdated!!.value = score

        uiScope.launch {
            suspend {
                database.updateCharacterScore(
                    DBAbilityScore(characterId, attributeId, score)
                )

                activeCharacter.value = refreshCharacter(characterId)
            }
        }
    }
}
