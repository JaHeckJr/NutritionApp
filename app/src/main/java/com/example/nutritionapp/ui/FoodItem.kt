package com.example.nutritionapp.data

data class FoodItem(
    val id: Long = System.currentTimeMillis(), // Unique identifier
    val name: String,
    val calories: Int,
    val mealType: MealType
)

enum class MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACKS
}