package com.example.recipemanager.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.recipemanager.GlobalAppState;
import com.example.recipemanager.R;
import com.example.recipemanager.activities.MainActivity;
import com.example.recipemanager.models.Recipe;
import com.example.recipemanager.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private Context context;
    private List<Recipe> recipeList, fullRecipeList;

    public RecipeAdapter(Context context, List<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
        this.fullRecipeList = new ArrayList<>(recipeList);
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe_card, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        User user = GlobalAppState.getCurrentUser();

        // Set recipe title
        String title = recipe.getStrMeal();
        title = title.length() > 30
                ? title.substring(0, 30) + "..."
                : title;
        holder.recipeTitle.setText(title);

        // Set recipe description
        String instructions = recipe.getStrInstructions();
        if (instructions != null && !instructions.isEmpty()) {
            String preview = instructions.length() > 100
                    ? instructions.substring(0, 100) + "..."
                    : instructions;
            holder.recipeDescription.setText(preview);
        }

        // Load image using Glide
        Glide.with(context)
                .load(recipe.getStrMealThumb())
                .centerCrop()
                .placeholder(R.drawable.loading_card)  // GIF file in drawable
                .into(holder.recipeImage);

        holder.recipeTime.setText(recipe.getTime() + "m");
        holder.recipeDifficulty.setText(recipe.getStrDifficulty());
        holder.recipeCalories.setText(recipe.getCalories() + " kcal");

        if(user != null)
            holder.bookmarkButton.setImageResource(user.isRecipeInFavourites(recipe.getIdMeal()) ? R.drawable.ic_bookmark_yellow : R.drawable.ic_bookmark);


        // Card Click Listener
        holder.itemView.setOnClickListener(v -> {
            GlobalAppState.setSelectedRecipe(recipe);
            Navigation.findNavController(v).navigate(R.id.action_fragment_app_to_fragment_recipe);
        });

        // Bookmark button click listener
        holder.bookmarkButton.setOnClickListener(v -> {
            GlobalAppState.getCurrentUser().toggleFavourites(recipe.getIdMeal());
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImage;
        TextView recipeTitle;
        TextView recipeDescription;
        TextView recipeTime;
        TextView recipeDifficulty;
        TextView recipeCalories;
        ImageButton bookmarkButton;
        CardView recipeCard;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipe_card_image);
            recipeTitle = itemView.findViewById(R.id.recipe_card_title);
            recipeDescription = itemView.findViewById(R.id.recipe_card_description);
            recipeTime = itemView.findViewById(R.id.recipe_card_time);
            recipeDifficulty = itemView.findViewById(R.id.recipe_card_difficulty);
            recipeCalories = itemView.findViewById(R.id.recipe_card_calories);
            bookmarkButton = itemView.findViewById(R.id.btn_bookmark);
            recipeCard = (CardView) itemView;
        }
    }

    public void filterList(String query) {
        recipeList.clear();
        User currentUser = GlobalAppState.getCurrentUser();
        boolean isFavouriteSearch = GlobalAppState.isFavouriteSearch();

        if (query.isEmpty() && !isFavouriteSearch) {
            recipeList.addAll(fullRecipeList);
        } else {
            String regex = "(?i).*" + query + ".*"; // Case-insensitive regex
            for (Recipe recipe : fullRecipeList) {
                if (recipe.getStrMeal().matches(regex) &&
                        (!isFavouriteSearch || currentUser.isRecipeInFavourites(recipe.getIdMeal()))) { // Check if title matches
                    recipeList.add(recipe);
                }
            }
        }
        notifyDataSetChanged();
    }
}