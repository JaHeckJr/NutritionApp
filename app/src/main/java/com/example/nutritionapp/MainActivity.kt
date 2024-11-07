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
        // Observe all relevant data for the progress bar and text
        calorieViewModel.totalCalorieGoal.observe(this) { updateCalorieText() }
        calorieViewModel.caloriesConsumed.observe(this) { updateCalorieText() }
        calorieViewModel.exerciseCalories.observe(this) { updateCalorieText() }
        calorieViewModel.remainingCalories.observe(this) { updateCalorieText() }
    }

    private fun updateCalorieText() {
        val goal = calorieViewModel.totalCalorieGoal.value ?: 2000
        val consumed = calorieViewModel.caloriesConsumed.value ?: 0
        val exercise = calorieViewModel.exerciseCalories.value ?: 0
        val remaining = calorieViewModel.remainingCalories.value ?: 0

        // Update the progress bar
        binding.calorieProgressBar.max = goal
        binding.calorieProgressBar.progress = consumed

        // Update the labels
        binding.calorieTrackingLabel.text = getString(R.string.calories_logged)
        binding.calorieCountText.text = getString(
            R.string.calorie_summary,
            goal,
            consumed,
            exercise,
            remaining
        )
    }
}