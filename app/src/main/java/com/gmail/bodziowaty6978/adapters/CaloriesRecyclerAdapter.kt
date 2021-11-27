package com.gmail.bodziowaty6978.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gmail.bodziowaty6978.databinding.MealLayoutBinding
import com.gmail.bodziowaty6978.interfaces.OnAdapterItemClickListener
import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.model.Product

class CaloriesRecyclerAdapter(private var products: MutableList<Product>,private var journalEntries:MutableList<JournalEntry>, private var adapterItemClickListener: OnAdapterItemClickListener): RecyclerView.Adapter<CaloriesRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: MealLayoutBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener{

        fun bind(item: Product){
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(products[position])

    override fun getItemCount(): Int = products.size


}


