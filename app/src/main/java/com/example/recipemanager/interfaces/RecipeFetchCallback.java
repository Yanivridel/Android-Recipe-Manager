package com.example.recipemanager.interfaces;

import com.example.recipemanager.models.Recipe;

import java.util.List;

public interface RecipeFetchCallback {
    void onRecipesFetched(List<Recipe> recipes);
}