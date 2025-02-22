package com.example.recipemanager.models;

import java.util.List;

public class Recipe {
    private String idMeal; // Unique ID for the recipe
    private String strMeal; // Name of the recipe
    private String strCategory; // Category (e.g., Chicken, Beef, etc.)
    private String strArea; // Area/Cuisine (e.g., Indian, Italian, etc.)
    private String strInstructions; // Cooking instructions
    private String strMealThumb; // Image URL for the recipe
    private String strYoutube; // Video URL for the recipe
    private int time; // Time taken to cook the recipe
    private String strDifficulty; // Difficulty level of the recipe
    private int calories; // Calories in the recipe
    private List<IngredientWithMeasure> ingredients; // Combined list of ingredients and their measures

    // Constructors
    public Recipe() {
    }
    public Recipe(String idMeal, String strMeal, String strCategory, String strArea,
                  String strInstructions, String strMealThumb, String strYoutube, int time, String strDifficulty, int calories,
                  List<IngredientWithMeasure> ingredients) {
        this.idMeal = idMeal;
        this.strMeal = strMeal;
        this.strCategory = strCategory;
        this.strArea = strArea;
        this.strInstructions = strInstructions;
        this.strMealThumb = strMealThumb;
        this.strYoutube = strYoutube;
        this.time = time;
        this.strDifficulty = strDifficulty;
        this.calories = calories;
        this.ingredients = ingredients;
    }

    // Getters and Setters
    public String getIdMeal() {
        return idMeal;
    }

    public void setIdMeal(String idMeal) {
        this.idMeal = idMeal;
    }

    public String getStrMeal() {
        return strMeal;
    }

    public void setStrMeal(String strMeal) {
        this.strMeal = strMeal;
    }

    public String getStrCategory() {
        return strCategory;
    }

    public void setStrCategory(String strCategory) {
        this.strCategory = strCategory;
    }

    public String getStrArea() {
        return strArea;
    }

    public void setStrArea(String strArea) {
        this.strArea = strArea;
    }

    public String getStrInstructions() {
        return strInstructions;
    }

    public void setStrInstructions(String strInstructions) {
        this.strInstructions = strInstructions;
    }

    public String getStrMealThumb() {
        return strMealThumb;
    }

    public void setStrMealThumb(String strMealThumb) {
        this.strMealThumb = strMealThumb;
    }

    public String getStrYoutube() {
        return strYoutube;
    }

    public void setStrYoutube(String strYoutube) {
        this.strYoutube = strYoutube;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getStrDifficulty() {
        return strDifficulty;
    }

    public void setStrDifficulty(String strDifficulty) {
        this.strDifficulty = strDifficulty;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public List<IngredientWithMeasure> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientWithMeasure> ingredients) {
        this.ingredients = ingredients;
    }

    // Inner class to represent ingredient and its measurement
    public static class IngredientWithMeasure {
        private String ingredient; // Name of the ingredient
        private String measure;    // Measurement of the ingredient

        // No-argument constructor for Firebase
        public IngredientWithMeasure() {
        }

        // Constructor with parameters
        public IngredientWithMeasure(String ingredient, String measure) {
            this.ingredient = ingredient;
            this.measure = measure;
        }

        // Getters and Setters
        public String getIngredient() {
            return ingredient;
        }

        public void setIngredient(String ingredient) {
            this.ingredient = ingredient;
        }

        public String getMeasure() {
            return measure;
        }

        public void setMeasure(String measure) {
            this.measure = measure;
        }
    }
}
