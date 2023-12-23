package com.gabrielcastro.dragonballfundamentosandroid.Home.ViewController.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gabrielcastro.dragonballfundamentosandroid.Home.Model.Heroe
import com.gabrielcastro.dragonballfundamentosandroid.databinding.HeroeCellBinding
import com.gabrielcastro.dragonballfundamentosandroid.R
import com.squareup.picasso.Picasso

interface onClickGridItem {
    fun onClick(heroe: Heroe)
}

class FragmentListAdapter(

    private val heroesList: List<Heroe>,
    private val callback: onClickGridItem
) : RecyclerView.Adapter<FragmentListAdapter.HeroesListFragmentViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HeroesListFragmentViewHolder {
        val binding = HeroeCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val maxWidth = parent.measuredWidth / 2
        binding.ivHeroePicture.maxWidth = maxWidth
        binding.ivHeroePicture.minimumWidth = maxWidth
        binding.ivHeroePicture.maxHeight = maxWidth
        binding.ivHeroePicture.minimumHeight = maxWidth

        return HeroesListFragmentViewHolder(binding, callback)
    }

    override fun onBindViewHolder(
        holder: HeroesListFragmentViewHolder,
        position: Int
    ) {
        holder.showHeroes(heroesList[position])
    }

    override fun getItemCount(): Int {
        return heroesList.size
    }


    class HeroesListFragmentViewHolder(
        private var item: HeroeCellBinding,
        private val callback: onClickGridItem
    ) : RecyclerView.ViewHolder(item.root) {
        fun showHeroes(heroe: Heroe) {
            item.tvName.text = heroe.name
            Picasso.get().load(heroe.photo).into(item.ivHeroePicture)
            // Creamos un método para calcular la salud y añadirla al ProgressBar
            item.pbHitPoint.max = heroe.totalHitPoints
            item.pbHitPoint.progress = heroe.currentHitPoints
            item.heroeCell.setOnClickListener {
                callback.onClick(heroe)
            }

            if (heroe.isDead) {
                item.ivHeroePicture.foreground = ContextCompat.getDrawable(
                    item.ivHeroePicture.context,
                    R.drawable.dead_character_filter
                )
                item.ivHeroePicture.alpha = 0.6F
                item.heroeCell.isClickable = false
                item.tvName.setTextColor(
                    ContextCompat.getColor(
                        item.tvName.context,
                        R.color.red_dragon_ball
                    )
                )

            } else {
                item.ivHeroePicture.foreground =
                    ContextCompat.getDrawable(item.ivHeroePicture.context, R.drawable.gradient)
                item.ivHeroePicture.alpha = 1.0F
                item.heroeCell.isClickable = true
                item.tvName.setTextColor(ContextCompat.getColor(item.tvName.context, R.color.white))
            }
        }
    }
}