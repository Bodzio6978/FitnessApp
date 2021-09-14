package com.gmail.bodziowaty6978.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityMainBinding
import kotlinx.coroutines.DelicateCoroutinesApi


@DelicateCoroutinesApi
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

//    lateinit var database: FirebaseDatabase
//    lateinit var instance: FirebaseAuth
//    lateinit var userId: String
//
//    lateinit var bottomNav: BottomNavigationView
//    lateinit var calories: CaloriesFragment
//    lateinit var training: TrainingFragment
//    lateinit var recipes: RecipesFragment
//    lateinit var shopping: ShoppingFragment
//    lateinit var settings: SettingFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        calories = CaloriesFragment()
//        training = TrainingFragment()
//        recipes = RecipesFragment()
//        shopping = ShoppingFragment()
//        settings = SettingFragment()
//
//        setFragment(calories)
//
//        bottomNav = findViewById(R.id.main_bottom_nav)
//
//        bottomNav.setOnItemSelectedListener {
//            when (it.itemId) {
//                R.id.menu_calories -> {
//
//                    setFragment(calories)
//                }
//                R.id.menu_training -> {
//                    setFragment(training)
//                }
//                R.id.menu_recipes -> {
//                    setFragment(recipes)
//                }
//                R.id.menu_shopping -> {
//                    setFragment(shopping)
//
//                }
//                R.id.menu_settings -> {
//                    setFragment(settings)
//                }
//            }
//            true
//        }

    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.apply {
            beginTransaction().replace(R.id.main_fl, fragment).commit()
        }
    }
}