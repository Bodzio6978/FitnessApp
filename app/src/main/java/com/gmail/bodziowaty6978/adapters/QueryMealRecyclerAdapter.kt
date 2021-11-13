package com.gmail.bodziowaty6978.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gmail.bodziowaty6978.databinding.MealQueryLayoutBinding
import com.gmail.bodziowaty6978.interfaces.OnAdapterItemClickListener
import com.gmail.bodziowaty6978.model.Meal

class QueryMealRecyclerAdapter(private var meals: MutableList<Meal>,private val adapterItemClickListener:OnAdapterItemClickListener): RecyclerView.Adapter<QueryMealRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: MealQueryLayoutBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(item: Meal){
            binding.meal = item
            binding.executePendingBindings()
        }

        init {
            binding.cvItem.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            adapterItemClickListener.onAdapterItemClickListener(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MealQueryLayoutBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(meals[position])

    override fun getItemCount(): Int = meals.size



}