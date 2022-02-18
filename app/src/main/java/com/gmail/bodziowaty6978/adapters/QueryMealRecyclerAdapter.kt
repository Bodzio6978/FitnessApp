package com.gmail.bodziowaty6978.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gmail.bodziowaty6978.databinding.MealQueryLayoutBinding
import com.gmail.bodziowaty6978.interfaces.OnAdapterItemClickListener
import com.gmail.bodziowaty6978.model.Product

class QueryMealRecyclerAdapter(private var meals: MutableList<Product>,private val adapterItemClickListener:OnAdapterItemClickListener): RecyclerView.Adapter<QueryMealRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: MealQueryLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Product){
            binding.product = item
            binding.executePendingBindings()
        }

        init {
            binding.cvItem.setOnLongClickListener{
                adapterItemClickListener.onAdapterItemClickListener(adapterPosition,true)
                true
            }
            binding.cvItem.setOnClickListener {
                adapterItemClickListener.onAdapterItemClickListener(adapterPosition,false)
            }
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