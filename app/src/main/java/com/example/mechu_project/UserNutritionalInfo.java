package com.example.mechu_project;

public class UserNutritionalInfo {
    private String exerciseType;
    private double dailyCalorie;
    private double dailyCarbs;
    private double dailyProtein;
    private double dailyFat;
    private double currentCalorie;
    private double currentCarbs;
    private double currentProtein;
    private double currentFat;

    public UserNutritionalInfo(String exerciseType, double dailyCalorie, double dailyCarbs, double dailyProtein, double dailyFat, double currentCalorie, double currentCarbs, double currentProtein, double currentFat) {
        this.exerciseType = exerciseType;
        this.dailyCalorie = dailyCalorie;
        this.dailyCarbs = dailyCarbs;
        this.dailyProtein = dailyProtein;
        this.dailyFat = dailyFat;
        this.currentCalorie = currentCalorie;
        this.currentCarbs = currentCarbs;
        this.currentProtein = currentProtein;
        this.currentFat = currentFat;
    }

    // Getter methods
    public String getExerciseType() {
        return exerciseType;
    }

    public double getDailyCalorie() {
        return dailyCalorie;
    }

    public double getDailyCarbs() {
        return dailyCarbs;
    }

    public double getDailyProtein() {
        return dailyProtein;
    }

    public double getDailyFat() {
        return dailyFat;
    }

    public double getCurrentCalorie() {
        return currentCalorie;
    }

    public double getCurrentCarbs() {
        return currentCarbs;
    }

    public double getCurrentProtein() {
        return currentProtein;
    }

    public double getCurrentFat() {
        return currentFat;
    }
}