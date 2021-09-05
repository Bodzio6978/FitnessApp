package com.gmail.bodziowaty6978.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gmail.bodziowaty6978.databinding.MealLayoutBinding
import com.gmail.bodziowaty6978.model.Meal

class CaloriesRecyclerAdapter(private var meals: MutableList<Meal>): RecyclerView.Adapter<CaloriesRecyclerAdapter.ViewHolder>() {

    fun updateMeals(newMeals:MutableList<Meal>){
        meals.clear()
        meals.addAll(newMeals)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: MealLayoutBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item:Meal){
            binding.item = item
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MealLayoutBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(meals[position])

    override fun getItemCount(): Int = meals.size

}