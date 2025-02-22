package com.example.recipemanager.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.recipemanager.R;
import com.example.recipemanager.models.Recipe;
import com.example.recipemanager.utils.RecipeFetcher;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class fragment_add_recipe extends Fragment {
    private ImageView recipeImage;
    private EditText recipeName, recipeArea, recipeInstructions, recipeYoutube,
            recipeTime, recipeCalories;
    private AutoCompleteTextView categoryDropdown, difficultyDropdown;
    private LinearLayout ingredientsContainer;
    private Uri selectedImageUri;
    private RecipeFetcher recipeFetcher;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    recipeImage.setImageURI(selectedImageUri);
                    recipeImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    requireView().findViewById(R.id.upload_text).setVisibility(View.GONE);
                }
            }
    );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recipeFetcher = new RecipeFetcher();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_recipe, container, false);
        initializeViews(view);
        setupDropdowns();
        setupImagePicker(view);
        setupIngredientAdding(view);
        setupSubmitButton(view);

        // Handle Go Back
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        Navigation.findNavController(requireView()).popBackStack();
                    }
                }
        );

        return view;
    }

    private void initializeViews(View view) {
        recipeImage = view.findViewById(R.id.recipe_image);
        recipeName = view.findViewById(R.id.recipe_name);
        categoryDropdown = view.findViewById(R.id.category_dropdown);
        recipeArea = view.findViewById(R.id.recipe_area);
        recipeInstructions = view.findViewById(R.id.recipe_instructions);
        recipeYoutube = view.findViewById(R.id.recipe_youtube);
        recipeTime = view.findViewById(R.id.recipe_time);
        recipeCalories = view.findViewById(R.id.recipe_calories);
        difficultyDropdown = view.findViewById(R.id.difficulty_dropdown);
        ingredientsContainer = view.findViewById(R.id.ingredients_container);
    }

    private void setupDropdowns() {
        String[] categories = {"Beef", "Chicken", "Dessert", "Lamb", "Miscellaneous",
                "Pasta", "Pork", "Seafood", "Side", "Starter", "Vegan", "Vegetarian"};
        String[] difficulties = {"Easy", "Medium", "Hard", "Expert", "Master"};

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, categories);
        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, difficulties);

        categoryDropdown.setAdapter(categoryAdapter);
        difficultyDropdown.setAdapter(difficultyAdapter);
    }

    private void setupImagePicker(View view) {
        view.findViewById(R.id.image_card).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });
    }

    private void setupIngredientAdding(View view) {
        ingredientsContainer = view.findViewById(R.id.ingredients_container);
        Button addIngredientButton = view.findViewById(R.id.add_ingredient);
        addIngredientButton.setOnClickListener(v -> {
            View ingredientView = createIngredientView();
            ingredientsContainer.addView(ingredientView);
        });
    }

    private View createIngredientView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.ingredient_item, ingredientsContainer, false);

        ImageButton removeButton = view.findViewById(R.id.remove_ingredient);
        removeButton.setOnClickListener(v -> ingredientsContainer.removeView(view));

        return view;
    }

    private void setupSubmitButton(View view) {
        view.findViewById(R.id.submit_recipe).setOnClickListener(v -> {
            if (validateInputs()) {
                uploadRecipe();
            }
        });
    }

    private boolean validateInputs() {
        if (recipeName.getText().toString().isEmpty() ||
                categoryDropdown.getText().toString().isEmpty() ||
                recipeInstructions.getText().toString().isEmpty() ||
                recipeTime.getText().toString().isEmpty() ||
                difficultyDropdown.getText().toString().isEmpty() ||
                ingredientsContainer.getChildCount() == 0) {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadRecipe() {
        try {
            // Generate a unique ID for the recipe
            String recipeId = UUID.randomUUID().toString();

            // Get image URL (you may need to implement actual image upload to storage)
            String imageUrl = selectedImageUri != null ? selectedImageUri.toString() : "";

            // Parse time and calories with validation
            int cookingTime = 0;
            int calorieCount = 0;
            try {
                cookingTime = Integer.parseInt(recipeTime.getText().toString().trim());
                calorieCount = !recipeCalories.getText().toString().trim().isEmpty() ?
                        Integer.parseInt(recipeCalories.getText().toString().trim()) : 0;
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Please enter valid numbers for time and calories", Toast.LENGTH_SHORT).show();
                return;
            }

            // Collect ingredients
            List<Recipe.IngredientWithMeasure> ingredientsList = new ArrayList<>();
            for (int i = 0; i < ingredientsContainer.getChildCount(); i++) {
                View ingredientView = ingredientsContainer.getChildAt(i);
                EditText ingredientName = ingredientView.findViewById(R.id.ingredient_name);
                EditText ingredientMeasure = ingredientView.findViewById(R.id.ingredient_measure);

                String ingredient = ingredientName.getText().toString().trim();
                String measure = ingredientMeasure.getText().toString().trim();

                if (!ingredient.isEmpty() && !measure.isEmpty()) {
                    ingredientsList.add(new Recipe.IngredientWithMeasure(ingredient, measure));
                }
            }

            // Create recipe object
            Recipe recipe = new Recipe(
                    recipeId,
                    recipeName.getText().toString().trim(),
                    categoryDropdown.getText().toString().trim(),
                    recipeArea.getText().toString().trim(),
                    recipeInstructions.getText().toString().trim(),
                    imageUrl,
                    recipeYoutube.getText().toString().trim(),
                    cookingTime,
                    difficultyDropdown.getText().toString().trim(),
                    calorieCount,
                    ingredientsList
            );

            // Save to database
            recipeFetcher.addRecipeDB(recipe);

            // Show success message
            Toast.makeText(requireContext(), "Recipe saved successfully!", Toast.LENGTH_SHORT).show();

            // Clear The inputs for next add
            clearForm();

            // Navigate back
            Navigation.findNavController(requireView()).popBackStack();

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error saving recipe: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        recipeName.setText("");
        categoryDropdown.setText("");
        recipeArea.setText("");
        recipeInstructions.setText("");
        recipeYoutube.setText("");
        recipeTime.setText("");
        recipeCalories.setText("");
        difficultyDropdown.setText("");
        ingredientsContainer.removeAllViews();
        recipeImage.setImageResource(0);
        requireView().findViewById(R.id.upload_text).setVisibility(View.VISIBLE);
        selectedImageUri = null;
    }
}