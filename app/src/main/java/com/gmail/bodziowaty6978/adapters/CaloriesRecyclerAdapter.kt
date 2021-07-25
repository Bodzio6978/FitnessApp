package com.gmail.bodziowaty6978.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gmail.bodziowaty6978.Meal
import com.gmail.bodziowaty6978.R

class CaloriesRecyclerAdapter(private var meals:MutableList<Meal>
):RecyclerView.Adapter<CaloriesRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){


        val name :TextView = itemView.findViewById(R.id.meal_name)
        val producer:TextView = itemView.findViewById(R.id.meal_producer)
        val calorie : TextView = itemView.findViewById(R.id.meal_kcal_value)
        val carb : TextView = itemView.findViewById(R.id.meal_carb_value)
        val protein : TextView = itemView.findViewById(R.id.meal_prot_value)
        val fat : TextView = itemView.findViewById(R.id.meal_fat_value)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.meal_delete)

        init {
            deleteButton.setOnClickListener {
                meals.removeAt(adapterPosition)
                notifyDataSetChanged()
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.meal_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = meals[position].name
        holder.producer.text = meals[position].producer
        holder.calorie.text = meals[position].calories
        holder.carb.text = meals[position].carbs
        holder.protein.text = meals[position].protein
        holder.fat.text = meals[position].fats

    }

    override fun getItemCount(): Int {
        return meals.size
    }
}