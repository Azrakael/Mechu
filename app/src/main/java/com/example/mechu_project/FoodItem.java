package com.example.mechu_project;

public class FoodItem {
    private String foodName;
    private double calorie;
    private double carbs;
    private double protein;
    private double fat;
    private String categoryName;

    public FoodItem(String foodName, double calorie, double carbs, double protein, double fat, String categoryName) {
        this.foodName = foodName;
        this.calorie = calorie;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.categoryName = categoryName;
    }

    public String getFoodName() {
        return foodName;
    }

    public double getCalorie() {
        return calorie;
    }

    public double getCarbs() {
        return carbs;
    }

    public double getProtein() {
        return protein;
    }

    public double getFat() {
        return fat;
    }

    public String getCategoryName() {
        return categoryName;
    }
}