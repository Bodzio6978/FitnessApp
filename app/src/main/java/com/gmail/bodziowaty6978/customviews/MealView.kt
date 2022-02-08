package com.gmail.bodziowaty6978.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.adapters.CaloriesRecyclerAdapter
import com.gmail.bodziowaty6978.databinding.MealViewBinding
import com.gmail.bodziowaty6978.interfaces.OnAdapterItemClickListener
import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.singleton.Strings
import com.gmail.bodziowaty6978.view.AddActivity

class MealView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs), OnAdapterItemClickListener {

    var binding: MealViewBinding = MealViewBinding.inflate(LayoutInflater.from(context))

    private var entriesList: MutableList<JournalEntry> = mutableListOf()

    private val mClickedEntry = MutableLiveData<Pair<Int, String>>()

    init {
        addView(binding.root)

        binding.rvMeal.layoutManager = LinearLayoutManager(context)
        binding.rvMeal.adapter = CaloriesRecyclerAdapter(entriesList, this)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MealView)
        binding.tvNameMealView.text = attributes.getString(R.styleable.MealView_mealName)
        (attributes.getInteger(R.styleable.MealView_kcalValue, 0).toString() + " " + Strings.get(R.string.kcal)).also { binding.tvKcalValueMeal.text = it }
        (attributes.getInteger(R.styleable.MealView_carbValue, 0).toString() + Strings.get(R.string.g)).also { binding.tvCarbsValueMeal.text = it }
        (attributes.getInteger(R.styleable.MealView_protValue, 0).toString() + Strings.get(R.string.g)).also { binding.tvProteinValueMeal.text = it }
        (attributes.getInteger(R.styleable.MealView_fatValue, 0).toString() + Strings.get(R.string.g)).also { binding.tvFatValueMeal.text = it }

        attributes.recycle()

        binding.fabMeal.setOnClickListener {
            val intent = Intent(context, AddActivity::class.java).putExtra("mealName", binding.tvNameMealView.text.toString())
            context.startActivity(intent)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setProducts(map: Map<String, JournalEntry>?) {
        if(map!=null){
            entriesList.clear()
            entriesList.addAll(map.values)
            binding.rvMeal.adapter?.notifyDataSetChanged()
            updateValues(map.values.toList())
        }
    }

    private fun updateValues(entries: List<JournalEntry>) {
        val values = mapOf(
                "calories" to entries.sumOf(JournalEntry::calories).toDouble(),
                "carbohydrates" to entries.sumOf(JournalEntry::carbs),
                "protein" to  entries.sumOf(JournalEntry::protein),
                "fat" to entries.sumOf(JournalEntry::fat)
        )

        if (entries.isEmpty()){
            binding.tvCarbsValueMeal.visibility = View.GONE
            binding.tvKcalValueMeal.visibility = View.GONE
            binding.tvFatValueMeal.visibility = View.GONE
            binding.tvProteinValueMeal.visibility = View.GONE
        }else{
            binding.tvCarbsValueMeal.visibility = View.VISIBLE
            binding.tvKcalValueMeal.visibility = View.VISIBLE
            binding.tvFatValueMeal.visibility = View.VISIBLE
            binding.tvProteinValueMeal.visibility = View.VISIBLE
        }

        (values["calories"]?.toInt().toString() + " " + Strings.get(R.string.kcal)).also { binding.tvKcalValueMeal.text = it }
        (values["carbohydrates"].toString() + Strings.get(R.string.g)).also { binding.tvCarbsValueMeal.text = it }
        (values["protein"].toString() + Strings.get(R.string.g)).also { binding.tvProteinValueMeal.text = it }
        (values["fat"].toString() + Strings.get(R.string.g)).also { binding.tvFatValueMeal.text = it }
    }


    fun getEntry(position: Int): JournalEntry = entriesList[position]

    fun getClickedEntry(): MutableLiveData<Pair<Int, String>> = mClickedEntry

    override fun onAdapterItemClickListener(position: Int) {
        mClickedEntry.value = Pair(position, binding.tvNameMealView.text.toString())
    }
}