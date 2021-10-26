package com.gmail.bodziowaty6978.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gmail.bodziowaty6978.databinding.MealLayoutBinding
import com.gmail.bodziowaty6978.model.Meal

class CaloriesRecyclerAdapter(private var meals: MutableList<Meal>, private var adapterItemClickListener: OnAdapterItemClickListener): RecyclerView.Adapter<CaloriesRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: MealLayoutBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener{

        fun bind(item: Meal){
            binding.item = item
            binding.executePendingBindings()
        }

        init{
            binding.mealDelete.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            adapterItemClickListener.onAdapterItemClickListener(adapterPosition)
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

interface OnAdapterItemClickListener {
    fun onAdapterItemClickListener(position: Int)
}
