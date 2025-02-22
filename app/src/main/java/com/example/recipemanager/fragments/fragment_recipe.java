package com.example.recipemanager.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.recipemanager.GlobalAppState;
import com.example.recipemanager.R;
import com.example.recipemanager.models.Recipe;
import com.example.recipemanager.models.User;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class fragment_recipe extends Fragment {

    public fragment_recipe() {
        // Required empty public constructor
    }

    public static fragment_recipe newInstance(String param1, String param2) {
        fragment_recipe fragment = new fragment_recipe();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

        Recipe recipe = GlobalAppState.getSelectedRecipe();
        User user = GlobalAppState.getCurrentUser();

        ImageView imageView = view.findViewById(R.id.recipe_detail_image);
        ImageButton bookmarkButton = view.findViewById(R.id.recipe_detail_bookmark_button);
        TextView title = view.findViewById(R.id.recipe_detail_title);
        TextView time = view.findViewById(R.id.recipe_detail_time);
        TextView difficulty = view.findViewById(R.id.recipe_detail_difficulty);
        TextView calories = view.findViewById(R.id.recipe_detail_calories);
        TextView description = view.findViewById(R.id.recipe_detail_description);
        TextView ingredients = view.findViewById(R.id.recipe_detail_ingredients);
        TextView area = view.findViewById(R.id.recipe_detail_area);
        TextView youtube = view.findViewById(R.id.recipe_detail_youtube);


        title.setText(recipe.getStrMeal());
        description.setText(recipe.getStrInstructions());
        area.setText(recipe.getStrArea());
        youtube.setText(recipe.getStrYoutube());

        time.setText(recipe.getTime() + " m");
        difficulty.setText(recipe.getStrDifficulty());
        calories.setText(recipe.getCalories() + " kcal");

        String ingredientsText = "";
        List<Recipe.IngredientWithMeasure> recipeIngredients = recipe.getIngredients();
        for (Recipe.IngredientWithMeasure ingredient : recipeIngredients) {
            if (!ingredient.getIngredient().equals("null") && !ingredient.getMeasure().equals("null"))
                ingredientsText += ingredient.getIngredient() + " - " + ingredient.getMeasure() + "\n";
        }
        ingredients.setText(ingredientsText);


        // Load image using Glide
        Glide.with(this)
                .load(recipe.getStrMealThumb())
                .centerCrop()
                .placeholder(R.drawable.loading_card)  // GIF file in drawable
                .into(imageView);

        if(user != null)
            bookmarkButton.setImageResource(user.isRecipeInFavourites(recipe.getIdMeal()) ? R.drawable.ic_bookmark_yellow : R.drawable.ic_bookmark);

        // Go Back
        ImageButton goBack = view.findViewById(R.id.recipe_detail_back_button);
        goBack.setOnClickListener(v ->
                Navigation.findNavController(requireView()).popBackStack()
        );
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        Navigation.findNavController(requireView()).popBackStack();
                    }
                }
        );

        bookmarkButton.setOnClickListener(v -> {
            User currentUser = GlobalAppState.getCurrentUser();
            currentUser.toggleFavourites(recipe.getIdMeal());

            // Refresh the bookmark button image
            boolean isFavorite = currentUser.isRecipeInFavourites(recipe.getIdMeal());
            bookmarkButton.setImageResource(isFavorite ? R.drawable.ic_bookmark_yellow : R.drawable.ic_bookmark);
        });

        return view;
    }
}