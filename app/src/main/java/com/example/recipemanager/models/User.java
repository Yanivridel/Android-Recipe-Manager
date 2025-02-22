package com.example.recipemanager.models;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User {
    private String id;
    private String fName;
    private String lName;
    private String email;
    private String phoneNumber;
    private List<String> favourites;
    private DatabaseReference userRef;

    // Constructors
    public User() {
    }
    public User(String id, String fName, String lName, String email, String phoneNumber) {
        this(id, fName, lName, email, phoneNumber, new ArrayList<>());
    }

    public User(String id, String fName, String lName, String email, String phoneNumber, List<String> favourites) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.favourites = favourites != null ? favourites : new ArrayList<>();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFName() {
        return fName;
    }

    public void setFName(String fName) {
        this.fName = fName;
    }

    public String getLName() {
        return lName;
    }

    public void setLName(String lName) {
        this.lName = lName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDatabaseReference(DatabaseReference userRef) {
        this.userRef = userRef;
    }

    public void toggleFavourites(String recipeId) {
        if (favourites == null) {
            favourites = new ArrayList<>();
        }

        if (!isRecipeInFavourites(recipeId))
            favourites.add(recipeId);
        else
            favourites.remove(recipeId);

        // Update database
        if (userRef != null) {
            userRef.child("favourites").setValue(favourites)
                    .addOnSuccessListener(aVoid -> Log.d("User", "Favourites updated successfully"))
                    .addOnFailureListener(e -> Log.e("User", "Error updating favourites: " + e.getMessage()));
        }
    }

    public boolean isRecipeInFavourites(String recipeId) {
        if (favourites == null) {
            return false;
        }
        return favourites.contains(recipeId);
    }
    public List<String> getFavourites() {
        return favourites;
    }

    public void setFavourites(List<String> favourites) {
        this.favourites = favourites;
    }
}
