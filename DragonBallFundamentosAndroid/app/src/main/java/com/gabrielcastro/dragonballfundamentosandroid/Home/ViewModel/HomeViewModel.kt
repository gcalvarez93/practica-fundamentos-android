package com.gabrielcastro.dragonballfundamentosandroid.Home.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabrielcastro.dragonballfundamentosandroid.Home.Model.Heroe
import com.gabrielcastro.dragonballfundamentosandroid.Home.Model.HeroeDTO
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.random.Random

class HomeViewModel : ViewModel() {

    lateinit var token: String
    private val baseUrl = "https://dragonball.keepcoding.education"
    var heroesList: List<Heroe> = listOf()
    lateinit var selectedHeroe: Heroe
    private val _uiStateCA = MutableStateFlow<UiStateCA>(UiStateCA.Started(true))
    val uiState: StateFlow<UiStateCA> = _uiStateCA

    fun retrieveHeroesList() {
        viewModelScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()
            val url = "$baseUrl/api/heros/all"
            val body = FormBody.Builder()
                .add("name", "")
                .build()
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .post(body)
                .build()
            val call = client.newCall(request)
            val response = call.execute()
            response.body?.let { responseBody ->
                val gson = Gson()
                try {
                    val heroesDTOList =
                        gson.fromJson(responseBody.string(), Array<HeroeDTO>::class.java)
                    heroesList = heroesDTOList.toList()
                        .map { Heroe(it.photo, it.id, it.favorite, it.description, it.name) }
                    _uiStateCA.value = UiStateCA.OnHeroesRetrieved(heroesList)
                    Log.w("TAG", "DECODING $heroesDTOList")
                } catch (e: Exception) {
                    _uiStateCA.value = UiStateCA.Error("Error while decoding the API response")
                }
            } ?: kotlin.run { Log.w("TAG", "Theres an error with tha heroes api call") }
        }
    }

    fun selectedHeroToFightClicked(heroe: Heroe) {
        heroe.timesSlected = heroe.timesSlected + 1
        selectedHeroe = heroe
        _uiStateCA.value = UiStateCA.OnHeroeSelectedToFight(heroe)
    }

    private fun healHeroe(): Int {
        var healValue = 20
        selectedHeroe.let {
            when (selectedHeroe.currentHitPoints) {
                in 80..99 -> healValue =
                    selectedHeroe.totalHitPoints - selectedHeroe.currentHitPoints

                0, 100 -> healValue = 0
                else -> healValue = 20
            }
        }
        return healValue
    }

    private fun damageHeroe(): Int {
        return Random.nextInt(-60, -10)
    }

    fun fightOnClickMethod(buttonTag: String) {
        var hpModifier = 0
        _uiStateCA.value = UiStateCA.Idle

        when (buttonTag) {
            "heal" -> hpModifier = healHeroe() //0 or positive value
            "attack" -> hpModifier = damageHeroe() //negative value between (-60, -10)
            else -> Log.e("TAG", "Error, non valid button clicked")
        }

        selectedHeroe.let {
            it.currentHitPoints = it.currentHitPoints + hpModifier
            if (it.currentHitPoints <= 0) {
                it.isDead = true
                val position = heroesList.indexOf(it)
                heroesList[position].currentHitPoints = it.currentHitPoints
                heroesList[position].isDead = it.isDead
                _uiStateCA.value = UiStateCA.OnHeroIsDead
            } else {
                _uiStateCA.value = UiStateCA.OnHeroeHPChange
            }
        }
    }

    fun healAllHeroes() {
        _uiStateCA.value = UiStateCA.Idle

        heroesList.forEach {
            it.currentHitPoints = 100
            it.isDead = false
        }

        _uiStateCA.value = UiStateCA.OnHeroeHPChange
    }

    fun transformHeroListToJson(): String {
        return Gson().toJson(heroesList)
    }

    fun heroesListJsonDecoderAndAssigment(jsonString: String) {
        val decodedHeroesJson = Gson().fromJson(jsonString, Array<Heroe>::class.java)
        heroesList = decodedHeroesJson.toList()
        _uiStateCA.value = UiStateCA.OnHeroesRetrieved(heroesList)
    }

    sealed class UiStateCA {
        data class Started(val started: Boolean) : UiStateCA()
        object Ended : UiStateCA()
        data class Error(val error: String) : UiStateCA()
        data class OnHeroesRetrieved(val heroesList: List<Heroe>) : UiStateCA()
        data class OnHeroeSelectedToFight(var heroe: Heroe) : UiStateCA()
        object OnHeroeHPChange : UiStateCA()
        object OnHeroIsDead : UiStateCA()
        object Idle : UiStateCA()
    }
}