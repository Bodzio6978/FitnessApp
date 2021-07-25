package com.gmail.bodziowaty6978.mainFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmail.bodziowaty6978.Meal
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.adapters.CaloriesRecyclerAdapter


class CaloriesFragment : Fragment() {

    lateinit var notification: TextView

    lateinit var recycler: RecyclerView
    private var meals = mutableListOf<Meal>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val v = inflater.inflate(R.layout.fragment_calories, container, false)

        notification = v.findViewById(R.id.calories_notification)
        recycler = v.findViewById(R.id.calories_recycler)

        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = CaloriesRecyclerAdapter(meals)

        val meal1 = Meal("Boczek","Morliny","184","2","12","19")
        meals.add(meal1)
        recycler.adapter?.notifyDataSetChanged()

        return v
    }

}