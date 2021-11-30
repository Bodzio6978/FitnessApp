package com.gmail.bodziowaty6978.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gmail.bodziowaty6978.databinding.MealLayoutBinding
import com.gmail.bodziowaty6978.interfaces.OnAdapterItemClickListener
import com.gmail.bodziowaty6978.model.JournalEntry

class CaloriesRecyclerAdapter(private var journalEntries: MutableList<JournalEntry>, private var adapterItemClickListener: OnAdapterItemClickListener): RecyclerView.Adapter<CaloriesRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: MealLayoutBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener{

        fun bind(item: JournalEntry){
            binding.item = item
            binding.executePendingBindings()
        }

        init{
            binding.rlMeal.setOnClickListener(this)

            if (binding.mealProducer.text.isNullOrEmpty()){
                binding.mealProducer.visibility = View.GONE
            }

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(journalEntries[position])

    override fun getItemCount(): Int = journalEntries.size


}


