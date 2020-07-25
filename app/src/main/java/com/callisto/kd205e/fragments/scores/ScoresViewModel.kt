package com.callisto.kd205e.fragments.scores

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.callisto.kd205e.database.Kd205eDao
import com.callisto.kd205e.database.model.*
import com.callisto.kd205e.fragments.BaseViewModel
import com.callisto.kd205e.RandomNumberGod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception

class ScoresViewModel
    : BaseViewModel
{
    val database: Kd205eDao

    constructor(database: Kd205eDao, application: Application) : super(application)
    {
        this.database = database
        this._activeCharacter = MutableLiveData()
        this._characterId = MutableLiveData()
        this._activeTraitId = MutableLiveData()

        this._mustDistributePoints = MutableLiveData()
        this._pointsToDistribute = MutableLiveData()
        this._maxPointsPerAttribute = MutableLiveData()
        this._pointsLeft = MutableLiveData()

        this._mustDistributePoints.value = false
    }

    // FIXED Fix this so it's accessible via LiveData.
    // Directly accessing MutableLiveData is bad practice.
    private var _activeCharacter: MutableLiveData<Character>

    private var _characterId: MutableLiveData<Long>

    private var _activeTraitId: MutableLiveData<Long>

    private var _mustDistributePoints: MutableLiveData<Boolean>

    private var _pointsToDistribute: MutableLiveData<Int>

    private var _pointsLeft: MutableLiveData<Int>

    private var _maxPointsPerAttribute: MutableLiveData<Int>

    val activeCharacter: LiveData<Character>
        get() = _activeCharacter

    val characterId: LiveData<Long>
        get() = _characterId

    val activeTraitId: LiveData<Long>
        get() = _activeTraitId

    val mustDistributePoints: LiveData<Boolean>
        get() = _mustDistributePoints

    val pointsToDistribute: LiveData<Int>
        get() = _pointsToDistribute

    val pointsLeft: LiveData<Int>
        get() = _pointsLeft

    val maxPointsPerAttribute: LiveData<Int>
        get() = _maxPointsPerAttribute

    private lateinit var attributes: List<DBAttribute>

    fun track(parameter: Long)
    {
        uiScope.launch {

            _characterId.value = parameter

            _activeCharacter.value = setUpCharacterFromDB(characterId.value!!)

            val result = getAbilityPicksForRacialTraits(_activeCharacter.value!!.species!!.race.raceId)

            // DONE Build algorithm to get point distribution
            // Algorithm must:
            // - Get all racial traits AND class abilities
            // - Determine if there are any requiring point distribution
            // - Determine how many points must be distributed, and how
            if (result.isNotEmpty())
            {
                setUpTraitPointAllocation(result)
            }

            isLoading.set(false)
        }
    }

    private fun setUpTraitPointAllocation(result: List<DBTraitPoints>)
    {
        Timber.v(result.size.toString())

        // Preserving code in case it turns out I have to support distributing points for multiple traits at once
//        _maxPointsPerAttribute.value = result.maxBy { it.picks }!!.picks
//        _pointsToDistribute.value = result.sumBy { it.points }
        _maxPointsPerAttribute.value = result.first().points

        _pointsToDistribute.value = result.first().picks

        _activeTraitId.value = result.first().traitId

        _pointsLeft.value = _pointsToDistribute.value

        // Since this triggers UI interactions, all other LiveData operations must be done first if the UI is to read them
        _mustDistributePoints.value = true
    }

    private suspend fun getAbilityPicksForRacialTraits(raceId: Long) : List<DBTraitPoints>
    {
        return withContext(Dispatchers.IO)
        {
            database.getAbilityPicksForRacialTraits(raceId)
        }
    }

//    private fun getAttributes()
//    {
//        uiScope.launch {
//            suspend { attributes = database.checkAllAttributes() }
//        }
//    }

    private suspend fun setUpCharacterFromDB(characterId: Long): Character?
    {
        return withContext(Dispatchers.IO)
        {
            val dbCharacter = database.getSingleCharacter(characterId)!!

            var scores = database.getScoresForCharacter(characterId)

            // TODO Maybe it would be best to move this into its own method call?
            attributes = database.checkAllAttributes()

            if (scores!!.isEmpty())
            {
                for (attribute in attributes)
                {
                    database.insertCharacterScore(
                        DBAbilityScore(
                            characterId,
                            attribute.attributeId,
                            RandomNumberGod.roll4d6MinusLowest()
                        )
                    )
                }

                scores = database.getScoresForCharacter(characterId)
            }

//            val characterAttributeTraits: List<DBCharacterAttributeTrait> = database.getCharacterAttributeTraits(characterId)

            val charSpecies = getCharacterSpecies(dbCharacter.raceId)

            val mediatorList = ArrayList<AbilityTraitModifier>()

            for (item in database.getCharacterAttributeTraits(characterId))
            {
                mediatorList.add(AbilityTraitModifier(attributes.find { it.attributeId == item.fAttributeId }!!.name, item.value))
            }

            val result = Character(dbCharacter, charSpecies, scores, mediatorList)

            result
        }
    }

    private suspend fun refreshCharacter(characterId: Long): Character?
    {
        return withContext(Dispatchers.IO)
        {
            val dbCharacter = database.getSingleCharacter(characterId)!!

            val scores = database.getScoresForCharacter(characterId)

//            val characterAttributeTraits: List<DBCharacterAttributeTrait> = database.getCharacterAttributeTraits(characterId)

            val charSpecies = getCharacterSpecies(dbCharacter.raceId)

            val mediatorList = ArrayList<AbilityTraitModifier>()

            for (item in database.getCharacterAttributeTraits(characterId))
            {
                mediatorList.add(AbilityTraitModifier(attributes.find { it.attributeId == item.fAttributeId }!!.name, item.value))
            }

            val result = Character(dbCharacter, charSpecies, scores, mediatorList)

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

            _activeCharacter.postValue(refreshCharacter(characterId))
        }
    }

    fun updateScore(ability: String, score: Int)
    {
        val characterId = activeCharacter.value!!.character.characterId

        val attributeId = attributes.find { it.name == ability }!!.attributeId

        uiScope.launch {
            updateCharacterScore(characterId, attributeId, score)
        }
    }

    fun addCharacterTraitAttribute(ability: String)
    {
        val characterId = activeCharacter.value!!.character.characterId

        val attributeId = attributes.find { it.name == ability }!!.attributeId

        val traitId = activeTraitId.value!!

        uiScope.launch {
            addCharacterTraitAttributeDB(characterId, attributeId, traitId)
        }

        _pointsLeft.value = (_pointsLeft.value)?.minus(1)
    }

    fun removeCharacterTraitAttribute(ability: String)
    {
        val characterId = activeCharacter.value!!.character.characterId

        val attributeId = attributes.find { it.name == ability }!!.attributeId

        val traitId = activeTraitId.value!!

        uiScope.launch {
            removeCharacterTraitAttributeDB(characterId, attributeId, traitId)
        }

        _pointsLeft.value = (_pointsLeft.value)?.plus(1)
    }

    private suspend fun addCharacterTraitAttributeDB(characterId: Long, attributeId: Long, traitId: Long)
    {
        return withContext(Dispatchers.IO)
        {
            val result: Long = database.insertCharacterAttributeTrait(DBCharacterAttributeTrait(characterId, attributeId, traitId, 1))

            Timber.v("DB operation result: $result")

            _activeCharacter.postValue(refreshCharacter(characterId))
        }
    }

    private suspend fun removeCharacterTraitAttributeDB(characterId: Long, attributeId: Long, traitId: Long)
    {
        return withContext(Dispatchers.IO)
        {
            val result: Int = database.qDeleteCharacterAttributeTrait(characterId, attributeId, traitId)

            Timber.v("DB operation result: $result")

            _activeCharacter.postValue(refreshCharacter(characterId))
        }
    }

    fun onCharacterTraitAttributesConfirmed()
    {
        _mustDistributePoints.value = false
    }
}
