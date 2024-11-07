package com.example.nutritionapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nutritionapp.data.FoodItem
import com.example.nutritionapp.data.MealType

class CalorieViewModel : ViewModel() {
    // Total calorie goal with no default value
    private val _totalCalorieGoal = MutableLiveData<Int>()
    val totalCalorieGoal: LiveData<Int> = _totalCalorieGoal

    // Food items storage
    private val _foodItems = MutableLiveData<List<FoodItem>>(emptyList())
    val foodItems: LiveData<List<FoodItem>> = _foodItems

    // Calories from food
    private val _caloriesConsumed = MutableLiveData(0)
    val caloriesConsumed: LiveData<Int> = _caloriesConsumed

    // Exercise tracking with exercise-specific goals
    private val _exerciseCalories = MutableLiveData(0)
    val exerciseCalories: LiveData<Int> = _exerciseCalories

    private val _exerciseGoal = MutableLiveData(322)
    val exerciseGoal: LiveData<Int> = _exerciseGoal

    // Steps tracking
    private val _stepsWalked = MutableLiveData(0)
    val stepsWalked: LiveData<Int> = _stepsWalked

    private val _stepsGoal = MutableLiveData(3444)
    val stepsGoal: LiveData<Int> = _stepsGoal

    // Remaining calories
    private val _remainingCalories = MutableLiveData<Int>()
    val remainingCalories: LiveData<Int> = _remainingCalories

    init {
        updateRemainingCalories()
    }

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
        _exerciseCalories.value = calories.coerceAtLeast(0)
        updateRemainingCalories() // Update remaining calories when exercise changes
    }

    fun setExerciseGoal(goal: Int) {
        _exerciseGoal.value = goal.coerceAtLeast(0)
    }

    fun getExerciseProgress(): Float {
        val current = _exerciseCalories.value ?: 0
        val goal = _exerciseGoal.value ?: 1
        return (current.toFloat() / goal.toFloat()) * 100
    }

    // Steps tracking functions
    fun setStepsWalked(steps: Int) {
        _stepsWalked.value = steps.coerceAtLeast(0)
    }

    fun setStepsGoal(goal: Int) {
        _stepsGoal.value = goal.coerceAtLeast(0)
    }

    fun getStepsProgress(): Float {
        val current = _stepsWalked.value ?: 0
        val goal = _stepsGoal.value ?: 1
        return (current.toFloat() / goal.toFloat()) * 100
    }

    // Calorie goal functions
    fun setTotalCalorieGoal(goal: Int) {
        _totalCalorieGoal.value = goal.coerceAtLeast(0)
        updateRemainingCalories()
    }

    private fun updateRemainingCalories() {
        val goal = _totalCalorieGoal.value ?: 0
        val consumed = _caloriesConsumed.value ?: 0
        val exercise = _exerciseCalories.value ?: 0
        // Subtract both consumed calories and exercise calories from goal
        _remainingCalories.value = (goal - consumed - exercise).coerceAtLeast(0)
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

    // Helper function to check if calorie goal is set
    fun isCalorieGoalSet(): Boolean {
        return _totalCalorieGoal.value != null
    }

    // Reset functions
    fun resetDailyProgress() {
        _caloriesConsumed.value = 0
        _exerciseCalories.value = 0
        _stepsWalked.value = 0
        _foodItems.value = emptyList()
        updateRemainingCalories()
    }

    fun resetAll() {
        resetDailyProgress()
        _totalCalorieGoal.value = null
        _exerciseGoal.value = 322
        _stepsGoal.value = 3444
        updateRemainingCalories()
    }
}