package com.example.nutritionapp.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.nutritionapp.CalorieViewModel
import com.example.nutritionapp.R
import com.example.nutritionapp.data.FoodItem
import com.example.nutritionapp.data.MealType
import com.example.nutritionapp.databinding.FragmentFoodLogBinding

class FoodLogFragment : Fragment() {
    private var _binding: FragmentFoodLogBinding? = null
    private val binding get() = _binding!!

    // Use activityViewModels() to share ViewModel with activity
    private val viewModel: CalorieViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMealSections()
        observeViewModel()
    }

    private fun setupMealSections() {
        binding.addBreakfastButton.setOnClickListener { showAddFoodDialog(MealType.BREAKFAST) }
        binding.addLunchButton.setOnClickListener { showAddFoodDialog(MealType.LUNCH) }
        binding.addDinnerButton.setOnClickListener { showAddFoodDialog(MealType.DINNER) }
        binding.addSnackButton.setOnClickListener { showAddFoodDialog(MealType.SNACKS) }
    }

    private fun showAddFoodDialog(mealType: MealType) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_food, null)
        val foodNameInput = dialogView.findViewById<EditText>(R.id.foodNameInput)
        val caloriesInput = dialogView.findViewById<EditText>(R.id.caloriesInput)

        AlertDialog.Builder(requireContext())
            .setTitle("Add Food")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val foodName = foodNameInput.text.toString()
                val calories = caloriesInput.text.toString().toIntOrNull() ?: 0
                if (foodName.isNotBlank() && calories > 0) {
                    val foodItem = FoodItem(
                        name = foodName,
                        calories = calories,
                        mealType = mealType
                    )
                    viewModel.addFoodItem(foodItem)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observeViewModel() {
        // Observe total calories
        viewModel.caloriesConsumed.observe(viewLifecycleOwner) { totalCalories ->
            binding.totalCaloriesText.text = "Total Calories: $totalCalories"
        }

        // Observe food items
        viewModel.foodItems.observe(viewLifecycleOwner) { foodItems ->
            updateMealSections()
        }
    }

    private fun updateMealSections() {
        // Update Breakfast section
        val breakfastCalories = viewModel.getMealTypeCalories(MealType.BREAKFAST)
        val breakfastItems = viewModel.getFoodItemsByMealType(MealType.BREAKFAST)
        binding.breakfastCalories.text = "$breakfastCalories cal"
        binding.breakfastItems.text = breakfastItems.joinToString("\n") {
            "${it.name} (${it.calories} cal)"
        }

        // Update Lunch section
        val lunchCalories = viewModel.getMealTypeCalories(MealType.LUNCH)
        val lunchItems = viewModel.getFoodItemsByMealType(MealType.LUNCH)
        binding.lunchCalories.text = "$lunchCalories cal"
        binding.lunchItems.text = lunchItems.joinToString("\n") {
            "${it.name} (${it.calories} cal)"
        }

        // Update Dinner section
        val dinnerCalories = viewModel.getMealTypeCalories(MealType.DINNER)
        val dinnerItems = viewModel.getFoodItemsByMealType(MealType.DINNER)
        binding.dinnerCalories.text = "$dinnerCalories cal"
        binding.dinnerItems.text = dinnerItems.joinToString("\n") {
            "${it.name} (${it.calories} cal)"
        }

        // Update Snacks section
        val snackCalories = viewModel.getMealTypeCalories(MealType.SNACKS)
        val snackItems = viewModel.getFoodItemsByMealType(MealType.SNACKS)
        binding.snackCalories.text = "$snackCalories cal"
        binding.snackItems.text = snackItems.joinToString("\n") {
            "${it.name} (${it.calories} cal)"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}