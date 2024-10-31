package com.example.nutritionapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.nutritionapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var calorieViewModel: CalorieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        calorieViewModel = ViewModelProvider(this).get(CalorieViewModel::class.java)

        // Setup NavController with BottomNavigationView
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        // Setup calorie tracking UI
        setupCalorieTracking()
    }

    private fun setupCalorieTracking() {
        // Observe total calorie goal for progress bar
        calorieViewModel.totalCalorieGoal.observe(this) { goal ->
            binding.calorieProgressBar.max = goal
        }

        // Observe calories consumed
        calorieViewModel.caloriesConsumed.observe(this) { calories ->
            binding.calorieProgressBar.progress = calories
            updateCalorieText()
        }

        // Observe exercise calories
        calorieViewModel.exerciseCalories.observe(this) {
            updateCalorieText()
        }

        // Observe remaining calories
        calorieViewModel.remainingCalories.observe(this) {
            updateCalorieText()
        }
    }

    private fun updateCalorieText() {
        val consumed = calorieViewModel.caloriesConsumed.value ?: 0
        val exercise = calorieViewModel.exerciseCalories.value ?: 0
        val remaining = calorieViewModel.remainingCalories.value ?: 0

        binding.calorieTrackingLabel.text = "Calories Logged"
        binding.calorieCountText.text =
            "Goal: ${calorieViewModel.totalCalorieGoal.value} | " +
                    "Food: $consumed | " +
                    "Exercise: +$exercise | " +
                    "Remaining: $remaining"
    }
}