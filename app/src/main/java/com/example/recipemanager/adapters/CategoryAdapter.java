package com.example.recipemanager.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.recipemanager.GlobalAppState;
import com.example.recipemanager.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Context context;
    private String[] categoryList;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private CategorySelectionListener categorySelectionListener;
    private boolean firstBind = true;

    public CategoryAdapter(Context context, String[] categoryList, CategorySelectionListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.categorySelectionListener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categoryList[position];
        holder.categoryName.setText(category);
        String imageName = position == selectedPosition ? category.toLowerCase() + "_white" : category.toLowerCase() + "_black";

        int imageResId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
        if (imageResId != 0)
            holder.categoryImage.setImageResource(imageResId);

        // Highlight the selected item
        if (position == selectedPosition) {
            holder.categoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.dark_green)); // Change CardView background color
            holder.categoryName.setTextColor(Color.WHITE);
        } else {
            holder.categoryCard.setCardBackgroundColor(Color.WHITE); // Set default background color
            holder.categoryName.setTextColor(Color.BLACK);
        }
        // Notify fragment about the category selection when adapter set for first time
        if (firstBind) {
            categorySelectionListener.onCategorySelected(null);
            firstBind = false;
        }

        // Category Click Listener
        holder.itemView.setOnClickListener(v -> {
            int previousSelectedPosition = selectedPosition;

            // Selection: if already selected, unselect it
            if (selectedPosition == holder.getAdapterPosition()) {
                selectedPosition = -1;
            } else {
                selectedPosition = holder.getAdapterPosition();
            }

            // Notify adapter of changes
            notifyItemChanged(previousSelectedPosition); // Unselect previous
            notifyItemChanged(selectedPosition);         // Select new (or unselect)

            // Notify fragment about the category selection
            if (selectedPosition != -1) {
                String selectedCategory = categoryList[selectedPosition];
                categorySelectionListener.onCategorySelected(selectedCategory);
            } else {
                categorySelectionListener.onCategorySelected(null);
            }

        });
    }

    @Override
    public int getItemCount() {
        return categoryList.length;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView categoryName;
        CardView categoryCard;


        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.category_image);
            categoryName = itemView.findViewById(R.id.category_name);
            categoryCard = itemView.findViewById(R.id.category_card);
        }
    }

    // Interface for communication with fragment or activity
    public interface CategorySelectionListener {
        void onCategorySelected(String category);
    }
}