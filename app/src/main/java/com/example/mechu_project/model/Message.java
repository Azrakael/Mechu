package com.example.mechu_project.model;

public class Message {
    public static final int SENT_BY_USER = 0;
    public static final int SENT_BY_SYSTEM = 1;

    private String message;
    private int sentBy;
    private String menuName;
    private String foodImgPath; // 이미지 경로 필드
    private double calorie;
    private boolean isMenuItem;

    public Message(String message, int sentBy) {
        this.message = message;
        this.sentBy = sentBy;
        this.isMenuItem = false;
    }

    public Message(String message, int sentBy, String menuName, String foodImgPath, double calorie) {
        this.message = message;
        this.sentBy = sentBy;
        this.menuName = menuName;
        this.foodImgPath = foodImgPath; // 이미지 경로 사용
        this.calorie = calorie;
        this.isMenuItem = true;
    }

    public String getMessage() {
        return message;
    }

    public int getSentBy() {
        return sentBy;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setFoodImgPath(String foodImgPath) {
        this.foodImgPath = foodImgPath;
    }

    public String getFoodImgPath() {
        return foodImgPath;
    }

    public double getCalorie() {
        return calorie;
    }

    public boolean isMenuItem() {
        return isMenuItem;
    }
}
