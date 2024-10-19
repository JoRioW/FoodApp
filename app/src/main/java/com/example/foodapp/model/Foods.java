package com.example.foodapp.model;

import java.io.Serializable;

public class Foods implements Serializable{
    private int CategoryId;
    private int id;
    private String ImagePath;
    private boolean PopularFood;
    private String foodName;
    private int price;
    private int numInCart;

    public Foods() {
    }

    @Override
    public String toString() {
        return foodName;
    }

    public int getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(int categoryId) {
        CategoryId = categoryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public boolean isPopularFood() {
        return PopularFood;
    }

    public void setPopularFood(boolean popularFood) {
        PopularFood = popularFood;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getNumInCart() {
        return numInCart;
    }

    public void setNumInCart(int numInCart) {
        this.numInCart = numInCart;
    }
}
