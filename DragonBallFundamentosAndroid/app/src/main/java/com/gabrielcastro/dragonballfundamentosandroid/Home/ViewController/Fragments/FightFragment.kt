package com.gabrielcastro.dragonballfundamentosandroid.Home.ViewController.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.gabrielcastro.dragonballfundamentosandroid.Home.ViewModel.HomeViewModel
import com.gabrielcastro.dragonballfundamentosandroid.R
import com.gabrielcastro.dragonballfundamentosandroid.databinding.FragmentFightBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class FightFragment : Fragment() {

    private lateinit var binding: FragmentFightBinding
    val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFightBinding.inflate(inflater)
        setCardPropperties(container)

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.uiState.collect {
                //TODO Preguntar por que eso funciona
                //  binding.tvFigterName.text = (it as CoreViewModel.UiStateCA.OnHeroeSelectedToFight).heroe.name
                binding.tvFigterName.text = homeViewModel.selectedHeroe.name
                Picasso.get().load(homeViewModel.selectedHeroe.photo).into(binding.ivFighter)
                binding.pbFighterHitPoints.max = homeViewModel.selectedHeroe.totalHitPoints
                binding.pbFighterHitPoints.progress = homeViewModel.selectedHeroe.currentHitPoints
            }
        }
        setFightButtonsOnClickMethods()
        return binding.root
    }

    private fun setFightButtonsOnClickMethods() {
        binding.bnHeal.setOnClickListener {
            homeViewModel.fightOnClickMethod(binding.bnHeal.tag.toString())
        }
        binding.bnAtack.setOnClickListener {
            homeViewModel.fightOnClickMethod(binding.bnAtack.tag.toString())
        }
        binding.faTimesSelected.setOnClickListener {
            timesSelectedToast(it.context)
        }
    }

    private fun timesSelectedToast(context: Context) {
        val heroeName = homeViewModel.selectedHeroe.name
        val timesSelected = homeViewModel.selectedHeroe.timesSlected
        val toastString = getString(R.string.times_selected_string)

        Toast.makeText(context, "$heroeName, $toastString  $timesSelected", Toast.LENGTH_LONG)
            .show()

    }

    private fun setCardPropperties(aux: ViewGroup?) {

        aux?.let {
            val maxWidth = (it.measuredWidth * 3) / 4
            binding.ivFighter.maxWidth = maxWidth
            binding.ivFighter.minimumWidth = maxWidth

            binding.ivFighter.maxHeight = maxWidth
            binding.ivFighter.minimumHeight = maxWidth
        }
    }
}