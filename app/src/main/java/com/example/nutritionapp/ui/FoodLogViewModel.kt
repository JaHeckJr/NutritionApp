package com.example.nutritionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nutritionapp.data.FoodItem
import com.example.nutritionapp.data.MealType

class FoodLogViewModel : ViewModel() {
    private val _foodItems = MutableLiveData<List<FoodItem>>(emptyList())
    val foodItems: LiveData<List<FoodItem>> = _foodItems

    private val _totalCalories = MutableLiveData(0)
    val totalCalories: LiveData<Int> = _totalCalories

    fun addFoodItem(foodItem: FoodItem) {
        val currentList = _foodItems.value.orEmpty().toMutableList()
        currentList.add(foodItem)
        _foodItems.value = currentList
        updateTotalCalories()
    }

    fun removeFoodItem(foodItem: FoodItem) {
        val currentList = _foodItems.value.orEmpty().toMutableList()
        currentList.remove(foodItem)
        _foodItems.value = currentList
        updateTotalCalories()
    }

    private fun updateTotalCalories() {
        val total = _foodItems.value.orEmpty().sumOf { it.calories }
        _totalCalories.value = total
    }

    fun getFoodItemsByMealType(mealType: MealType): List<FoodItem> {
        return _foodItems.value.orEmpty().filter { it.mealType == mealType }
    }

    fun getMealTypeCalories(mealType: MealType): Int {
        return getFoodItemsByMealType(mealType).sumOf { it.calories }
    }
}