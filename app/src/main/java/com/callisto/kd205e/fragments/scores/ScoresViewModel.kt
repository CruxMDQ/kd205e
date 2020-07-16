package com.callisto.kd205e.fragments.scores

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.callisto.kd205e.database.Kd205eDao
import com.callisto.kd205e.database.model.*
import com.callisto.kd205e.fragments.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

private const val STR = "Strength"
private const val DEX = "Dexterity"
private const val CON = "Constitution"
private const val INT = "Intelligence"
private const val WIS = "Wisdom"
private const val CHA = "Charisma"


class ScoresViewModel
(
    val database: Kd205eDao,
    application: Application
) : BaseViewModel(application)
{
    var activeCharacter = MutableLiveData<Character>()

    var _baseStrength = MutableLiveData<Int>()
    var _baseDexterity = MutableLiveData<Int>()
    var _baseConstitution = MutableLiveData<Int>()
    var _baseIntelligence = MutableLiveData<Int>()
    var _baseWisdom = MutableLiveData<Int>()
    var _baseCharisma = MutableLiveData<Int>()

    var _modStrength = MutableLiveData<Int>()
    var _modDexterity = MutableLiveData<Int>()
    var _modConstitution = MutableLiveData<Int>()
    var _modIntelligence = MutableLiveData<Int>()
    var _modWisdom = MutableLiveData<Int>()
    var _modCharisma = MutableLiveData<Int>()

    var _finalStrength = MutableLiveData<Int>()
    var _finalDexterity = MutableLiveData<Int>()
    var _finalConstitution = MutableLiveData<Int>()
    var _finalIntelligence = MutableLiveData<Int>()
    var _finalWisdom = MutableLiveData<Int>()
    var _finalCharisma = MutableLiveData<Int>()

    val baseStrength: LiveData<Int>
        get() = _baseStrength

    val modStrength: LiveData<Int>
        get() = _modStrength
    
    val finalStrength: LiveData<Int>
        get() = _finalStrength

    val baseDexterity: LiveData<Int>
        get() = _baseDexterity

    val modDexterity: LiveData<Int>
        get() = _modDexterity

    val finalDexterity: LiveData<Int>
        get() = _finalDexterity

    val baseConstitution: LiveData<Int>
        get() = _baseConstitution

    val modConstitution: LiveData<Int>
        get() = _modConstitution

    val finalConstitution: LiveData<Int>
        get() = _finalConstitution

    val baseIntelligence: LiveData<Int>
        get() = _baseIntelligence

    val modIntelligence: LiveData<Int>
        get() = _modIntelligence

    val finalIntelligence: LiveData<Int>
        get() = _finalIntelligence

    val baseWisdom: LiveData<Int>
        get() = _baseWisdom

    val modWisdom: LiveData<Int>
        get() = _modWisdom

    val finalWisdom: LiveData<Int>
        get() = _finalWisdom

    val baseCharisma: LiveData<Int>
        get() = _baseCharisma

    val modCharisma: LiveData<Int>
        get() = _modCharisma

    val finalCharisma: LiveData<Int>
        get() = _finalCharisma

    private lateinit var attributes: List<DBAttribute>

    fun track(characterId: Long)
    {
        uiScope.launch {

//            getAttributes()

            activeCharacter.value = setUpCharacterFromDB(characterId)
            
            _baseStrength.value = activeCharacter.value?.getBaseScore(STR)!!
            _baseDexterity.value = activeCharacter.value?.getBaseScore(DEX)!!
            _baseConstitution.value = activeCharacter.value?.getBaseScore(CON)!!
            _baseIntelligence.value = activeCharacter.value?.getBaseScore(INT)!!
            _baseWisdom.value = activeCharacter.value?.getBaseScore(WIS)!!
            _baseCharisma.value = activeCharacter.value?.getBaseScore(CHA)!!

            _modStrength.value = activeCharacter.value?.getModifierScore(STR)!!
            _modDexterity.value = activeCharacter.value?.getModifierScore(DEX)!!
            _modConstitution.value = activeCharacter.value?.getModifierScore(CON)!!
            _modIntelligence.value = activeCharacter.value?.getModifierScore(INT)!!
            _modWisdom.value = activeCharacter.value?.getModifierScore(WIS)!!
            _modCharisma.value = activeCharacter.value?.getModifierScore(CHA)!!

            _finalStrength.value = activeCharacter.value?.getFinalScore(STR)!!
            _finalDexterity.value = activeCharacter.value?.getFinalScore(DEX)!!
            _finalConstitution.value = activeCharacter.value?.getFinalScore(CON)!!
            _finalIntelligence.value = activeCharacter.value?.getFinalScore(INT)!!
            _finalWisdom.value = activeCharacter.value?.getFinalScore(WIS)!!
            _finalCharisma.value = activeCharacter.value?.getFinalScore(CHA)!!
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

    private suspend fun updateCharacterScore(characterId: Long, attributeId: Long, score: Int)
    {
        return withContext(Dispatchers.IO)
        {
            // TODO THIS IS AWFUL. I shouldn't have to fetch an entire row from the DB to know what to update. I need to figure out how to build an object to pass to the DAO for the update to work WITHOUT initializing the primary key field.
            val rowToUpdate = database.getScoreForCharacter(characterId, attributeId)

            database.updateCharacterScore(
                DBAbilityScore(rowToUpdate.scoreId, characterId, attributeId, score)
            )

            activeCharacter.postValue(refreshCharacter(characterId))
        }
    }

    fun updateScore(ability: String, score: Int)
    {
        val characterId = activeCharacter.value!!.character.characterId

        val attributeId = attributes.find { it.name == ability }!!.attributeId

        uiScope.launch {
            updateCharacterScore(characterId, attributeId, score)

            when(ability)
            {
                STR -> {
                    _baseStrength.postValue(score)
                    _finalStrength.postValue(activeCharacter.value?.getFinalScore(STR)!!)
                }
                DEX -> {
                    _baseDexterity.postValue(score)
                    _finalDexterity.postValue(activeCharacter.value?.getFinalScore(DEX)!!)
                }
                CON -> {
                    _baseConstitution.postValue(score)
                    _finalConstitution.postValue(activeCharacter.value?.getFinalScore(CON)!!)
                }
                INT -> {
                    _baseIntelligence.postValue(score)
                    _finalIntelligence.postValue(activeCharacter.value?.getFinalScore(INT)!!)
                }
                WIS -> {
                    _baseWisdom.postValue(score)
                    _finalWisdom.postValue(activeCharacter.value?.getFinalScore(WIS)!!)
                }
                CHA -> {
                    _baseCharisma.postValue(score)
                    _finalCharisma.postValue(activeCharacter.value?.getFinalScore(CHA)!!)
                }
            }
        }
    }

    private fun roll4d6MinusLowest(): Int
    {
        val rolls = arrayOf((1..6).random(), (1..6).random(), (1..6).random(), (1..6).random())

        return rolls.sum() - rolls.min()!!
    }
}
