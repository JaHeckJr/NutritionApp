// CalorieViewModel.kt
package com.example.nutritionapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalorieViewModel : ViewModel() {

    // Total calorie goal
    private val _totalCalorieGoal = MutableLiveData(1500)
    val totalCalorieGoal: LiveData<Int> get() = _totalCalorieGoal

    // Calories consumed
    private val _caloriesConsumed = MutableLiveData(1356) // Initial value as per your example
    val caloriesConsumed: LiveData<Int> get() = _caloriesConsumed

    // Remaining calories
    private val _remainingCalories = MutableLiveData(calculateRemainingCalories())
    val remainingCalories: LiveData<Int> get() = _remainingCalories

    // Update functions
    fun setTotalCalorieGoal(goal: Int) {
        _totalCalorieGoal.value = goal
        updateRemainingCalories()
    }

    fun setCaloriesConsumed(consumed: Int) {
        _caloriesConsumed.value = consumed
        updateRemainingCalories()
    }

    private fun updateRemainingCalories() {
        _remainingCalories.value = calculateRemainingCalories()
    }

    private fun calculateRemainingCalories(): Int {
        return (_totalCalorieGoal.value ?: 0) - (_caloriesConsumed.value ?: 0)
    }
}
