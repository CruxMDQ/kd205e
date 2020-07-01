package com.callisto.kd205e.fragments.race

import android.app.Application
import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.callisto.kd205e.database.RaceDao
import com.callisto.kd205e.database.model.*
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.StringBuilder

class RaceViewModel
(
    val database: RaceDao,
    application: Application
) : AndroidViewModel(application)
{
    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var races = MutableLiveData<List<RaceModifierPair>>()

    private var selectedRace = MutableLiveData<Species>()

    val pickClassButtonEnabled = Transformations.map(selectedRace) {
        it != null
    }

//    val nightsString = Transformations.map(nights) {
//            nights ->
//        formatNights(nights, application.resources)
//    }
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

    override fun onCleared()
    {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun onStartTracking()
    {
        uiScope.launch {
            races.value = getRacesWithModsFromDB()
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

    fun onRacePicked(item: Species?)
    {
        selectedRace.value = item
    }
}