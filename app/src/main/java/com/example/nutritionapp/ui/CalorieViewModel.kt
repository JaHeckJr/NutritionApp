package com.example.nutritionapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nutritionapp.data.FoodItem
import com.example.nutritionapp.data.MealType

class CalorieViewModel : ViewModel() {
    // Total calorie goal
    private val _totalCalorieGoal = MutableLiveData(2000)
    val totalCalorieGoal: LiveData<Int> = _totalCalorieGoal

    // Food items storage
    private val _foodItems = MutableLiveData<List<FoodItem>>(emptyList())
    val foodItems: LiveData<List<FoodItem>> = _foodItems

    // Calories from food
    private val _caloriesConsumed = MutableLiveData(0)
    val caloriesConsumed: LiveData<Int> = _caloriesConsumed

    // Exercise tracking
    private val _exerciseCalories = MutableLiveData(0)
    val exerciseCalories: LiveData<Int> = _exerciseCalories

    private val _exerciseGoal = MutableLiveData(500)
    val exerciseGoal: LiveData<Int> = _exerciseGoal

    // Steps tracking
    private val _stepsWalked = MutableLiveData(0)
    val stepsWalked: LiveData<Int> = _stepsWalked

    private val _stepsGoal = MutableLiveData(10000)
    val stepsGoal: LiveData<Int> = _stepsGoal

    // Remaining calories
    private val _remainingCalories = MutableLiveData(calculateRemainingCalories())
    val remainingCalories: LiveData<Int> = _remainingCalories

    // Food tracking functions
    fun addFoodItem(foodItem: FoodItem) {
        val currentList = _foodItems.value.orEmpty().toMutableList()
        currentList.add(foodItem)
        _foodItems.value = currentList
        updateCaloriesConsumed()
    }

    fun removeFoodItem(foodItem: FoodItem) {
        val currentList = _foodItems.value.orEmpty().toMutableList()
        currentList.remove(foodItem)
        _foodItems.value = currentList
        updateCaloriesConsumed()
    }

    private fun updateCaloriesConsumed() {
        val total = _foodItems.value.orEmpty().sumOf { it.calories }
        _caloriesConsumed.value = total
        updateRemainingCalories()
    }

    // Exercise tracking functions
    fun setExerciseCalories(calories: Int) {
        _exerciseCalories.value = calories
        updateRemainingCalories()
    }

    fun setExerciseGoal(goal: Int) {
        _exerciseGoal.value = goal
    }

    fun getExerciseProgress(): Float {
        val current = _exerciseCalories.value ?: 0
        val goal = _exerciseGoal.value ?: 1
        return (current.toFloat() / goal.toFloat()) * 100
    }

    // Steps tracking functions
    fun setStepsWalked(steps: Int) {
        _stepsWalked.value = steps
    }

    fun setStepsGoal(goal: Int) {
        _stepsGoal.value = goal
    }

    fun getStepsProgress(): Float {
        val current = _stepsWalked.value ?: 0
        val goal = _stepsGoal.value ?: 1
        return (current.toFloat() / goal.toFloat()) * 100
    }

    // Calorie goal functions
    fun setTotalCalorieGoal(goal: Int) {
        _totalCalorieGoal.value = goal
        updateRemainingCalories()
    }

    private fun updateRemainingCalories() {
        _remainingCalories.value = calculateRemainingCalories()
    }

    private fun calculateRemainingCalories(): Int {
        val goal = _totalCalorieGoal.value ?: 0
        val consumed = _caloriesConsumed.value ?: 0
        val exercise = _exerciseCalories.value ?: 0
        return goal - consumed + exercise
    }

    // Meal type functions
    fun getMealTypeCalories(mealType: MealType): Int {
        return _foodItems.value.orEmpty()
            .filter { it.mealType == mealType }
            .sumOf { it.calories }
    }

    fun getFoodItemsByMealType(mealType: MealType): List<FoodItem> {
        return _foodItems.value.orEmpty()
            .filter { it.mealType == mealType }
    }

    // Progress calculation helpers
    fun getRemainingSteps(): Int {
        val current = _stepsWalked.value ?: 0
        val goal = _stepsGoal.value ?: 0
        return (goal - current).coerceAtLeast(0)
    }

    fun getRemainingExerciseCalories(): Int {
        val current = _exerciseCalories.value ?: 0
        val goal = _exerciseGoal.value ?: 0
        return (goal - current).coerceAtLeast(0)
    }
}