package com.callisto.kd205e.fragments.scores

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.callisto.kd205e.database.Kd205eDao
import com.callisto.kd205e.database.models.*
import com.callisto.kd205e.fragments.BaseViewModel
import com.callisto.kd205e.RandomNumberGod
import com.callisto.kd205e.database.entities.CharacterAttributeTrait
import com.callisto.kd205e.database.entities.Attribute
import com.callisto.kd205e.database.entities.CharacterAbilityScore
import com.callisto.kd205e.database.entities.TraitPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ScoresViewModel
    : BaseViewModel
{
    val database: Kd205eDao

    constructor(database: Kd205eDao, application: Application) : super(application)
    {
        this.database = database
        this._activeCharacterModel = MutableLiveData()
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
    private var _activeCharacterModel: MutableLiveData<CharacterModel>

    private var _characterId: MutableLiveData<Long>

    private var _activeTraitId: MutableLiveData<Long>

    private var _mustDistributePoints: MutableLiveData<Boolean>

    private var _pointsToDistribute: MutableLiveData<Int>

    private var _pointsLeft: MutableLiveData<Int>

    private var _maxPointsPerAttribute: MutableLiveData<Int>

    val activeCharacterModel: LiveData<CharacterModel>
        get() = _activeCharacterModel

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

    private lateinit var attributes: List<Attribute>

    fun track(parameter: Long)
    {
        uiScope.launch {

            _characterId.value = parameter

            _activeCharacterModel.value = setUpCharacterFromDB(characterId.value!!)

            // DONE Build algorithm to get point distribution
            // Algorithm must:
            // - Get all racial traits AND class abilities
            // - Determine if there are any requiring point distribution
            // - Determine how many points must be distributed, and how

            val result = getAbilityPicksForRacialTraits(_activeCharacterModel.value!!.speciesModel!!.race.raceId)

            if (result.isNotEmpty())
            {
                setUpTraitPointAllocation(result)
            }

            isLoading.set(false)
        }
    }

    private fun setUpTraitPointAllocation(result: List<TraitPoints>)
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

    private suspend fun getAbilityPicksForRacialTraits(raceId: Long) : List<TraitPoints>
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

    private suspend fun setUpCharacterFromDB(characterId: Long): CharacterModel?
    {
        return withContext(Dispatchers.IO)
        {
            val dbCharacter = database.getSingleCharacter(characterId)!!

            var scores = database.getScoresForCharacter(characterId)

            // TODO Maybe it would be best to move this into its own method call?
            attributes = database.getAllAttributes()

            if (scores!!.isEmpty())
            {
                for (attribute in attributes)
                {
                    database.insertCharacterScore(
                        CharacterAbilityScore(
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

            val traitList = database.getCharacterTraits(characterId)

            val result = CharacterModel(dbCharacter, charSpecies, scores, mediatorList, traitList)

            result
        }
    }

    private suspend fun refreshCharacter(characterId: Long): CharacterModel?
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

            val traitList = database.getCharacterTraits(characterId)

            val result = CharacterModel(dbCharacter, charSpecies, scores, mediatorList, traitList)

            result
        }
    }

    private suspend fun getCharacterSpecies(raceId: Long): SpeciesModel?
    {
        return withContext(Dispatchers.IO)
        {
            val racesAndAbilityModifiers = database.queryRacialAttributesView()

            Timber.v(racesAndAbilityModifiers.size.toString())

            val mListSpecies = mutableListOf<SpeciesModel>()
            val mapRacesWithMods = racesAndAbilityModifiers.groupBy(
                keySelector = { it.race },
                valueTransform = { it.abilityScoreModifier }
            )

            mapRacesWithMods.forEach {
                mListSpecies.add(SpeciesModel(it.key, it.value))
            }

            var result: SpeciesModel? = null

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
                CharacterAbilityScore(
                    rowToUpdate.scoreId,
                    characterId,
                    attributeId,
                    score
                )
            )

            _activeCharacterModel.postValue(refreshCharacter(characterId))
        }
    }

    fun updateScore(ability: String, score: Int)
    {
        val characterId = activeCharacterModel.value!!.character.characterId

        val attributeId = attributes.find { it.name == ability }!!.attributeId

        uiScope.launch {
            updateCharacterScore(characterId, attributeId, score)
        }
    }

    fun addCharacterTraitAttribute(ability: String)
    {
        val characterId = activeCharacterModel.value!!.character.characterId

        val attributeId = attributes.find { it.name == ability }!!.attributeId

        val traitId = activeTraitId.value!!

        uiScope.launch {
            addCharacterTraitAttributeDB(characterId, attributeId, traitId)
        }

        _pointsLeft.value = (_pointsLeft.value)?.minus(1)
    }

    fun removeCharacterTraitAttribute(ability: String)
    {
        val characterId = activeCharacterModel.value!!.character.characterId

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
            val result: Long = database.insertCharacterAttributeTrait(
                CharacterAttributeTrait(
                    characterId,
                    attributeId,
                    traitId,
                    1
                )
            )

            Timber.v("DB operation result: $result")

            _activeCharacterModel.postValue(refreshCharacter(characterId))
        }
    }

    private suspend fun removeCharacterTraitAttributeDB(characterId: Long, attributeId: Long, traitId: Long)
    {
        return withContext(Dispatchers.IO)
        {
            val result: Int = database.deleteCharacterAttributeTrait(characterId, attributeId, traitId)

            Timber.v("DB operation result: $result")

            _activeCharacterModel.postValue(refreshCharacter(characterId))
        }
    }

    fun onCharacterTraitAttributesConfirmed()
    {
        _mustDistributePoints.value = false
    }
}
