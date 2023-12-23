package com.gabrielcastro.dragonballfundamentosandroid.Home.ViewController

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gabrielcastro.dragonballfundamentosandroid.Home.ViewModel.HomeViewModel
import com.gabrielcastro.dragonballfundamentosandroid.Home.ViewController.Fragments.FightFragment
import com.gabrielcastro.dragonballfundamentosandroid.Home.ViewController.Fragments.HeroesListFragment
import com.gabrielcastro.dragonballfundamentosandroid.R
import com.gabrielcastro.dragonballfundamentosandroid.databinding.ActivityHomeBinding
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    companion object {
        const val TAG_TOKEN = "TOKEN_KEY"
        fun launch(context: Context, token: String) {
            val intent = Intent(context, HomeActivity::class.java)
            intent.putExtra(TAG_TOKEN, token)
            context.startActivity(intent)
        }
    }


    private lateinit var binding: ActivityHomeBinding
    private val viewModelCA: HomeViewModel by viewModels()
    private val TAG_HEROE_LIST = "MyHeroeList"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrieveToken()

        lifecycleScope.launch {
            viewModelCA.uiState.collect {
                when (it) {
                    is HomeViewModel.UiStateCA.Started -> retrieveStoredHeroesData()
                    is HomeViewModel.UiStateCA.Ended -> Log.w("TAG", "Ended")
                    is HomeViewModel.UiStateCA.OnHeroesRetrieved -> {
                        addHeroesListFragment()
                        binding.tvTitle.text = getString(R.string.fight_title)
                    }//TODO se obtiene el listado de heroes bien
                    is HomeViewModel.UiStateCA.Error -> Log.w("TAG", "Error en UiState")
                    is HomeViewModel.UiStateCA.OnHeroeSelectedToFight -> {
                        addFragmentTwo()
                        binding.tvTitle.text = getString(R.string.fight_title)
                    }

                    is HomeViewModel.UiStateCA.OnHeroIsDead -> {
                        addHeroesListFragment()
                        binding.tvTitle.text = getString(R.string.heroes_list_title)
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun retrieveToken() {
        viewModelCA.token = intent.getStringExtra(TAG_TOKEN).toString()
        binding.tvTitle.text = viewModelCA.token
    }

    private fun addHeroesListFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(
                binding.fFragment.id,
                HeroesListFragment()
            ) //TODO pass context in here if needed
            .commitNow()
    }

    private fun addFragmentTwo() {
        supportFragmentManager
            .beginTransaction()
            .replace(binding.fFragment.id, FightFragment()) //TODO pass context in here if needed
            .commitNow()

    }

    override fun onStop() {
        super.onStop()
        storeHeroesData()
    }

    override fun onDestroy() {
        super.onDestroy()
        storeHeroesData()
    }

    private fun storeHeroesData() {
        val myHeroesJson: String = viewModelCA.transformHeroListToJson()
        getPreferences(Context.MODE_PRIVATE).edit().apply {
            putString(TAG_HEROE_LIST, myHeroesJson).apply()
        }
    }

    private fun retrieveStoredHeroesData() {
        getPreferences(Context.MODE_PRIVATE).apply {
            val heroesJson = getString(TAG_HEROE_LIST, "")
            heroesJson?.let {
                if (heroesJson == "") {
                    viewModelCA.retrieveHeroesList()
                } else {
                    viewModelCA.heroesListJsonDecoderAndAssigment(heroesJson)
                }
            }
        }
    }
}