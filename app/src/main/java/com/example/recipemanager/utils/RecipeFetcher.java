package com.example.recipemanager.utils;

import android.util.Log;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.example.recipemanager.GlobalAppState;
import com.example.recipemanager.R;
import com.example.recipemanager.interfaces.RecipeFetchCallback;
import com.example.recipemanager.models.Recipe;
import com.example.recipemanager.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.json.JSONObject;
import org.json.JSONArray;

public class RecipeFetcher {
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public void fetchRecipesApi(String searchText) {
        String apiUrl = "https://www.themealdb.com/api/json/v1/1/search.php?s=" + searchText;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(apiUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setReadTimeout(5000);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    reader.close();

                    // Parse the JSON response
                    JSONObject response = new JSONObject(stringBuilder.toString());
                    JSONArray recipesArray = response.getJSONArray("meals");
                    String[] difficulties = {"Easy", "Medium", "Hard", "Expert", "Master"};

                    // Loop through the array and insert into Firebase
                    for (int i = 0; i < recipesArray.length(); i++) {
                        JSONObject recipeData = recipesArray.getJSONObject(i);

                        int randomTime = ThreadLocalRandom.current().nextInt(5, 60 + 1);
                        int randomIndex = ThreadLocalRandom.current().nextInt(0, difficulties.length);
                        int randomCalories = ThreadLocalRandom.current().nextInt(100, 1000 + 1);

                        Recipe recipe = new Recipe(
                                recipeData.getString("idMeal"),
                                recipeData.getString("strMeal"),
                                recipeData.getString("strCategory"),
                                recipeData.getString("strArea"),
                                recipeData.getString("strInstructions"),
                                recipeData.getString("strMealThumb"),
                                recipeData.getString("strYoutube"),
                                randomTime,
                                difficulties[randomIndex],
                                randomCalories,
                                extractIngredients(recipeData)
                        );
                        Log.d("RecipeFetcher", "Recipe: " + recipe.getTime());
                        Log.d("RecipeFetcher", "Recipe: " + recipe.getStrDifficulty());
                        Log.d("RecipeFetcher", "Recipe: " + recipe.getCalories());
                        addRecipeDB(recipe);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void fetchRecipesDB(@Nullable String category, RecipeFetchCallback callback) {
        DatabaseReference recipesRef = database.getReference("recipes");

        List<Recipe> recipesList = new ArrayList<>();
        boolean isFavourite = GlobalAppState.isFavouriteSearch();
        User currentUser = GlobalAppState.getCurrentUser();

        recipesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = recipeSnapshot.getValue(Recipe.class);

                    if (recipe != null) {
                        // Check if a category is provided, and if so, filter by it
                        if (category == null || category.equalsIgnoreCase(recipe.getStrCategory())
                            && (!isFavourite || currentUser != null && currentUser.isRecipeInFavourites(recipe.getIdMeal()))
                            ) {
                            recipesList.add(recipe);
                        }
                    }
                }
                callback.onRecipesFetched(recipesList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("RecipeFetcher", "loadRecipes:onCancelled", databaseError.toException());
                callback.onRecipesFetched(new ArrayList<>());
            }
        });
    }
    // Helper function to extract ingredients from the response
    private List<Recipe.IngredientWithMeasure> extractIngredients(JSONObject recipeData) {
        List<Recipe.IngredientWithMeasure> ingredients = new ArrayList<>();

        for (int i = 1; i <= 20; i++) {
            try {
                String ingredient = recipeData.getString("strIngredient" + i);
                String measure = recipeData.getString("strMeasure" + i);

                // Only add non-null ingredients
                if (!ingredient.isEmpty() && !measure.isEmpty()) {
                    ingredients.add(new Recipe.IngredientWithMeasure(ingredient, measure));
                }
            } catch (Exception e) {
                // Handle missing data gracefully
                break;
            }
        }
        return ingredients;
    }

    public void addRecipeDB(Recipe recipe) {
        // route inside the database
        DatabaseReference usersRef = database.getReference("recipes").child(recipe.getIdMeal());
        usersRef.setValue(recipe);
    }
}
