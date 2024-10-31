// HomeFragment.kt
package com.example.nutritionapp.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.nutritionapp.CalorieViewModel
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.FragmentHomeBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var calorieViewModel: CalorieViewModel

    private val totalStepsGoal = 10000
    private val stepsWalked = 6789
    private val totalExerciseGoal = 1000
    private val caloriesBurned = 625

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.bind(
            inflater.inflate(R.layout.fragment_home, container, false)
        )

        // Access the shared ViewModel
        calorieViewModel = ViewModelProvider(requireActivity()).get(CalorieViewModel::class.java)

        setupPieChart()
        setupTrackingData()

        return binding.root
    }

    private fun setupTrackingData() {
        // Set the steps progress and text
        binding.progressBarSteps.max = totalStepsGoal
        binding.progressBarSteps.progress = stepsWalked
        binding.stepsText.text = "You have walked $stepsWalked steps today"

        // Set the exercise progress and text
        binding.progressBarExercise.max = totalExerciseGoal
        binding.progressBarExercise.progress = caloriesBurned
        binding.exerciseText.text = "You have burned $caloriesBurned calories today"
    }

    private fun setupPieChart() {
        // Observe calorie data
        calorieViewModel.caloriesConsumed.observe(viewLifecycleOwner) { updatePieChart() }
        calorieViewModel.remainingCalories.observe(viewLifecycleOwner) { updatePieChart() }
        calorieViewModel.totalCalorieGoal.observe(viewLifecycleOwner) { updatePieChart() }
    }

    private fun updatePieChart() {
        val consumed = calorieViewModel.caloriesConsumed.value?.toFloat() ?: 0f
        val remaining = calorieViewModel.remainingCalories.value?.toFloat() ?: 0f

        val entries = listOf(
            PieEntry(consumed, "Consumed"),
            PieEntry(remaining, "Remaining")
        )

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                Color.parseColor("#4CAF50"), // Green color for consumed
                Color.parseColor("#E0E0E0")  // Light gray color for remaining
            )
            valueTextSize = 16f
            valueTextColor = Color.BLACK
        }

        val data = PieData(dataSet)

        binding.pieChartCalories.apply {
            this.data = data
            centerText = "${remaining.toInt()}\nRemaining"
            invalidate()
        }

        // Update the remaining calories TextView
        binding.remainingCaloriesText.text = "Remaining Calories: ${remaining.toInt()}"
    }
}
