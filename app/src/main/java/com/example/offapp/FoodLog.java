package com.example.offapp;

public class FoodLog {
    private int id;
    private String meal;
    private String foodName;
    private int calories;

    public FoodLog(int id, String meal, String foodName, int calories) {
        this.id = id;
        this.meal = meal;
        this.foodName = foodName;
        this.calories = calories;
    }

    public int getId() {
        return id;
    }

    public String getMeal() {
        return meal;
    }

    public String getFoodName() {
        return foodName;
    }

    public int getCalories() {
        return calories;
    }
}
