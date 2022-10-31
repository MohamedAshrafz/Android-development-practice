package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.databinding.AsteroidItemViewBinding

class AsteroidRecyclerViewAdaptor(private val clickListener: (asteroid: Asteroid) -> Unit) :
    ListAdapter<Asteroid, AsteroidRecyclerViewAdaptor.AsteroidItemViewHolder>(DiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidItemViewHolder {
        return AsteroidItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AsteroidItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    class AsteroidItemViewHolder private constructor(private val binding: AsteroidItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Asteroid, clickListener: (asteroid: Asteroid) -> Unit) {
//            binding.clickListener = clickListener
            binding.asteroidItem = item

            binding.itemConstraintView.setOnClickListener { clickListener(item) }

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): AsteroidItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                // use this way of inflating
                val binding = AsteroidItemViewBinding.inflate(layoutInflater, parent, false)
                // to not use
                //val binding = AsteroidItemViewBinding.inflate(layoutInflater)

                return AsteroidItemViewHolder(binding)
            }
        }

    }

    object DiffCallback : DiffUtil.ItemCallback<Asteroid>() {

        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem == newItem
        }
    }

//    class AsteroidItemClickListener(val clickListener: (asteroid: Asteroid) -> Unit) {
//        fun onClick(asteroid: Asteroid) = clickListener(asteroid)
//    }
}