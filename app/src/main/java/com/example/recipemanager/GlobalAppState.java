package com.example.recipemanager;

import com.example.recipemanager.models.Recipe;
import com.example.recipemanager.models.User;

import java.util.List;

public class GlobalAppState {
    private static User currentUser;
    private static Recipe selectedRecipe;
    private static boolean favouriteSearch = false;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    public static User getCurrentUser() {
        return currentUser;
    }

    public static Recipe getSelectedRecipe() {
        return selectedRecipe;
    }

    public static void setSelectedRecipe(Recipe selectedRecipe) {
        GlobalAppState.selectedRecipe = selectedRecipe;
    }

    public static boolean isFavouriteSearch() {
        return favouriteSearch;
    }

    public static void setFavouriteSearch(boolean favouriteSearch) {
        GlobalAppState.favouriteSearch = favouriteSearch;
    }
}
