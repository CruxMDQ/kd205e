package com.callisto.kd205e.fragments.species

import android.app.Application
import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.callisto.kd205e.database.Kd205eDao
import com.callisto.kd205e.database.models.*
import com.callisto.kd205e.database.entities.Character
import com.callisto.kd205e.database.entities.CharacterTrait
import com.callisto.kd205e.database.entities.Trait
import com.callisto.kd205e.fragments.BaseViewModel
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.StringBuilder

class SpeciesViewModel
    : BaseViewModel
{
    val database: Kd205eDao

    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(database: Kd205eDao, application: Application) : super(application)
    {
        this.database = database

        this.characterId = MutableLiveData()

        this.selectedRace = MutableLiveData()

        this.activeCharacter = MutableLiveData()

        this.speciesList = MutableLiveData()

        this.traitsWithSelectionsCompleted = ArrayList()
        this.traitsWithSelectionsCompletedLD = MutableLiveData()
        this.traitsWithSelectionsCompletedLD.value = traitsWithSelectionsCompleted

        this.traitsWithPendingSelections = ArrayList()
        this.traitsWithPendingSelectionsLD = MutableLiveData()
        this.traitsWithPendingSelectionsLD.value = traitsWithPendingSelections

        this.traitOptions = ArrayList()
        this.traitOptionsLD = MutableLiveData()
        this.traitOptionsLD.value = traitOptions

        this._activeTrait = MutableLiveData()

        this.setAbilityScoresEnabled = Transformations.map(selectedRace) {
            it != null
        }

        this.characterString = Transformations.map(activeCharacter) {
            formatCharacter(it)
        }

        this.summary = MutableLiveData()
    }

    // TODO Figure out how to construct a LiveData field to observe instead of observing this
    var speciesList: MutableLiveData<List<SpeciesModel>>

    private var characterId: MutableLiveData<Long>

    private var selectedRace: MutableLiveData<SpeciesModel>

    private var activeCharacter: MutableLiveData<CharacterModel>

    val setAbilityScoresEnabled: LiveData<Boolean>

    val characterString: LiveData<Spanned>

    var summary: MutableLiveData<Spanned>

    // TRAIT FINGZ

    private var traitsWithSelectionsCompleted: ArrayList<Trait>

    private var traitsWithSelectionsCompletedLD: MutableLiveData<List<Trait>>

    private var traitsWithPendingSelections: ArrayList<Trait>

    private var traitsWithPendingSelectionsLD: MutableLiveData<List<Trait>>

    private var traitOptions: ArrayList<Trait>

    var traitOptionsLD: MutableLiveData<List<Trait>>

    private var _activeTrait: MutableLiveData<Trait>

    val activeTrait: LiveData<Trait>
        get() = _activeTrait

    // TODO Fix problem: if there are traits with options AND traits without, only the first will be printed
    // Pseudocode:
    // - Get list of traits for selected species
    // - Check if there are any requiring options
    // - If yes:
    // -- Get list of traits on character object
    // -- For each of those, find out if they belong in the array of options for a trait requiring picks
    // -- If yes, add this character trait to the list of traits to print AND do not add its master
    // - If no:
    // -- Add this racial trait to the list of traits to print
    // PROBLEM: Database queries require launching this on a separate scope
    private fun formatCharacter(characterModel: CharacterModel): Spanned
    {
        // This constructs the output
        val sb = StringBuilder()

        // Actual species to print
        val speciesModel = characterModel.speciesModel

        try
        {
            sb.apply {

                // Header for ability score modifiers
                append("Ability score modifiers: ")
                append("<br>")
                append("<br>")

                // Print each modifier on the list
                for (abilityScoreModifier in speciesModel!!.abilityModifiers) {
                    append(abilityScoreModifier.toString())
                    append("<br>")
                }

                // Header for species traits
                append("<br>")
                append("Species traits: ")
                append("<br>")
                append("<br>")

                // Traits is only loaded if there were racial traits requiring picks
                if (!characterModel.traits.isNullOrEmpty())
                {
                    for (characterTrait in characterModel.traits!!)
                    {
                        append(characterTrait.toString())
                        append("<br>")
                    }
                }
                else    // If there were none, then print standard racial traits
                {
                    for (racialTrait in speciesModel.traitModels)
                    {
                        append(racialTrait.toString())
                        append("<br>")
                    }
                }
            }
        }
        catch (e: KotlinNullPointerException)
        {
            sb.clear()
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
        } else {
            HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }


    // Loads existing character if a character ID was provided
    fun track(parameter: Long)
    {
        uiScope.launch {
            characterId.value = parameter

            speciesList.value = getSpecies()
        }
    }

    fun track()
    {
        uiScope.launch {
            characterId.value = createNewCharacter()

            speciesList.value = getSpecies()
        }
    }

    fun onRacePicked(item: SpeciesModel?)
    {
        selectedRace.value = item

        traitsWithSelectionsCompleted.clear()
        traitsWithSelectionsCompletedLD.value = traitsWithSelectionsCompleted

        uiScope.launch {
//            updateCharacter(characterId.value!!, selectedRace.value!!.race.raceId)
            updateCharacter(characterId.value!!, item!!.race.raceId)

            // DONE Build algorithm to get racial abilities with options
            // Algorithm must:
            // - Get all racial traits
            // - Determine if there are any requiring that options are chosen
            // - Determine how many points must be distributed, and how

            val result = getRacialTraitsWithOptions(item.race.raceId)

            if (result.isNotEmpty())
            {
                traitsWithPendingSelections = result as ArrayList<Trait>
                traitsWithPendingSelectionsLD.value = traitsWithPendingSelections

                val first: Trait = result.first()

                traitOptionsLD.value = getTraitOptions(first.traitId)

                _activeTrait.value = first
            }
            else
            {
                val characterModel = CharacterModel(
                    Character(characterId.value!!, item.race.raceId, ""),
                    item
                )

                activeCharacter.postValue(
                    characterModel
                )
            }

//            _activeCharacter.value!!.speciesModel = item

            deleteTraitPicksFromOtherRaces(characterId.value!!).toString()

        }
    }

    private suspend fun getSpecies(): MutableList<SpeciesModel>
    {
        return withContext(Dispatchers.IO)
        {
            val racesAndAbilityModifiers = database.queryRacialAttributesView()

            val mListSpecies = mutableListOf<SpeciesModel>()

            val mapRacesWithMods = racesAndAbilityModifiers.groupBy(
                keySelector = { it.race },
                valueTransform = { it.abilityScoreModifier }
            )

            mapRacesWithMods.forEach {
                val species = SpeciesModel(it.key, it.value)

                species.traitModels = database.getRacialTraits(species.race.raceId)

                mListSpecies.add(species)
            }

            Timber.v("Races retrieved: %s", mListSpecies.size.toString())

            mListSpecies
        }
    }

    private suspend fun createNewCharacter() : Long
    {
        return withContext(Dispatchers.IO)
        {
            // TODO Fix insertion logic to avoid assigning a race to an empty character
            val newCharacterId = database.createNewCharacter(
                Character(
                    1
                )
            )

            val characterModel = CharacterModel(
                Character(newCharacterId, 1, "")
            )

            activeCharacter.postValue(
                characterModel
            )

            Timber.v("ID of new character: %s", newCharacterId.toString())

            newCharacterId
        }
    }

    private suspend fun updateCharacter(characterId: Long, raceId: Long) : Int
    {
        return withContext(Dispatchers.IO)
        {
            val result = database.updateCharacter(
                Character(
                    characterId,
                    raceId
                )
            )

            val character = database.getSingleCharacter(characterId)
            val race = database.getSingleRace(character?.raceId!!)

            Timber.v(character.characterId.toString())
            Timber.v(race!!.name)

            result
        }
    }

    fun getCharacterId(): Long
    {
        return characterId.value!!
    }

    // TRAIT GUBBINZ

    private suspend fun deleteTraitPicksFromOtherRaces(characterId: Long) : Int
    {
        return withContext(Dispatchers.IO)
        {
            val deleteAllRaceTraitsFromCharacter =
                database.deleteCharacterTraitKeysByCharacter(characterId)

            Timber.v("Deleted character-trait keys: %s", deleteAllRaceTraitsFromCharacter)

            Timber.v("Character traits: %s", database.getCharacterTraitKeys(activeCharacter.value!!.character.characterId).size.toString())

            deleteAllRaceTraitsFromCharacter
        }

    }

    private suspend fun getRacialTraitsWithOptions(raceId: Long): List<Trait>
    {
        return withContext(Dispatchers.IO)
        {
            database.getRacialTraitsWithOptions(raceId)
        }
    }

    private suspend fun getTraitOptions(traitId: Long): List<Trait>
    {
        return withContext(Dispatchers.IO)
        {
            database.getRacialTraitOptions(traitId)
        }
    }

    private suspend fun insertCharacterTrait(trait: Trait?) : Long
    {
        return withContext(Dispatchers.IO)
        {
            val id = database.getLastCharacterTraitId()

            // DONE This information cannot be read with the current data, create a character LiveData to access it
            val result = database.insertCharacterTrait(
                CharacterTrait(
                    id.toLong(), characterId.value!!, trait!!.traitId
                )
            )

            Timber.v("Character traits: %s", database.getCharacterTraitKeys(
                activeCharacter.value!!.character.characterId).size.toString()
            )

            result
        }
    }

    fun onTraitPicked(traitPicked: Trait?)
    {
        traitsWithSelectionsCompleted.add(traitPicked!!)
        traitsWithSelectionsCompletedLD.value = traitsWithSelectionsCompleted

        traitsWithPendingSelections.remove(activeTrait.value)
        traitsWithPendingSelectionsLD.value = traitsWithPendingSelections

        if (traitsWithPendingSelections.isNullOrEmpty())
        {
            val characterModel = CharacterModel(
                Character(characterId.value!!, selectedRace.value!!.race.raceId, ""),
                selectedRace.value!!
            )

            characterModel.traits = traitsWithSelectionsCompleted

            activeCharacter.postValue(characterModel)
        }
        else
        {
            _activeTrait.value = traitsWithPendingSelections.first()
        }

        uiScope.launch {
            insertCharacterTrait(traitPicked).toString()
        }
    }
}