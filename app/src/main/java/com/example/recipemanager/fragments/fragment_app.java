package com.example.recipemanager.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;

import com.example.recipemanager.GlobalAppState;
import com.example.recipemanager.R;
import com.example.recipemanager.activities.MainActivity;
import com.example.recipemanager.adapters.CategoryAdapter;
import com.example.recipemanager.adapters.RecipeAdapter;
import com.example.recipemanager.interfaces.RecipeFetchCallback;
import com.example.recipemanager.models.Recipe;
import com.example.recipemanager.models.User;
import com.example.recipemanager.utils.RecipeFetcher;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class fragment_app extends Fragment implements CategoryAdapter.CategorySelectionListener {
    RecipeFetcher recipeFetcher = new RecipeFetcher();
    private RecyclerView recyclerViewCategories, recyclerViewSearchResults,
            recyclerViewNeedToTry, recyclerViewSeason, recyclerViewBestSell;
    private CategoryAdapter categoryAdapter;
    private RecipeAdapter recipeAdapterSearch;
    private final LinearLayout[] titles = new LinearLayout[3];
    private TextView searchResultsText;
    private SearchView searchView;

    public fragment_app() {
        // Required empty public constructor
    }

    public static fragment_app newInstance(String param1, String param2) {
        fragment_app fragment = new fragment_app();
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
        View view =  inflater.inflate(R.layout.fragment_app, container, false);

        titles[0] = view.findViewById(R.id.linear_need_to_try);
        titles[1] = view.findViewById(R.id.linear_season);
        titles[2] = view.findViewById(R.id.linear_best_sell);
        searchResultsText = view.findViewById(R.id.text_search_results);
        recyclerViewNeedToTry = view.findViewById(R.id.recycler_view_need_to_try);
        recyclerViewSeason = view.findViewById(R.id.recycler_view_season_selection);
        recyclerViewBestSell = view.findViewById(R.id.recycler_view_best_sell);
        recyclerViewSearchResults = view.findViewById(R.id.recycler_view_search_results);
        searchView = view.findViewById(R.id.app_search_view);

        User user = GlobalAppState.getCurrentUser();
        if (user != null) {
            Log.d("UserData", "User Name: " + user.getFName());
        }

        recipeFetcher.fetchRecipesDB(null, new RecipeFetchCallback() {
            @Override
            public void onRecipesFetched(List<Recipe> recipeList) {
                setupRecyclerView(recyclerViewNeedToTry, recipeList, 1, 10, true);

                setupRecyclerView(recyclerViewSeason, recipeList, 11, 20, true);

                setupRecyclerView(recyclerViewBestSell, recipeList, 21, 30, true);

            }
        });

        // Initialize Categories RecyclerView
        String[] categoryNames = {
                "Beef", "Chicken", "Dessert", "Lamb", "Miscellaneous", "Pasta", "Pork", "Seafood", "Side", "Starter", "Vegan", "Vegetarian", "Breakfast", "Goat"
        };
        recyclerViewCategories = view.findViewById(R.id.recycler_view_categories);
        ViewGroup.LayoutParams params = recyclerViewCategories.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        recyclerViewCategories.setLayoutParams(params);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCategories.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCategories.setLayoutManager(layoutManager);

        categoryAdapter = new CategoryAdapter(getContext(), categoryNames, this);
        recyclerViewCategories.setAdapter(categoryAdapter);

        // Init Add Recipe Btn
        FloatingActionButton fab = view.findViewById(R.id.add_recipe_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_fragment_app_to_fragment_add_recipe);
            }
        });


        // Init Search View
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                recipeAdapterSearch.filterList(query);
                if(query.isEmpty())
                    showRecyclerViews();
                else
                    hideRecyclerViews();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                recipeAdapterSearch.filterList(newText);
                if(newText.isEmpty())
                    showRecyclerViews();
                else
                    hideRecyclerViews();
                return true;
            }
        });

        // Init Filter Modal
        ImageButton filterButton = view.findViewById(R.id.imageButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create the modal (Dialog) and inflate the layout
                View dialogView = getLayoutInflater().inflate(R.layout.modal_filter, null);
                CheckBox checkBoxFavorites = dialogView.findViewById(R.id.checkBox_favorites);
                Button buttonApplyFilter = dialogView.findViewById(R.id.button_apply_filter);
                checkBoxFavorites.setChecked(GlobalAppState.isFavouriteSearch());

                // Create a dialog and set the view
                android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(getContext())
                        .setView(dialogView)
                        .create();

                // Apply filter logic when the button is clicked
                buttonApplyFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isFavoriteSelected = checkBoxFavorites.isChecked();
                        GlobalAppState.setFavouriteSearch(isFavoriteSelected);
                        searchView.setQuery("", false);
                        recipeAdapterSearch.filterList("");
                        if(!isFavoriteSelected)
                            showRecyclerViews();
                        else
                            hideRecyclerViews();
                        dialog.dismiss(); // Close the modal
                    }
                });

                // Show the dialog
                dialog.show();
            }
        });
        return view;
    }

    // Handle category selection
    @Override
    public void onCategorySelected(String category) {
        recipeFetcher.fetchRecipesDB(category, new RecipeFetchCallback() {
            @Override
            public void onRecipesFetched(List<Recipe> recipeList) {
                recipeAdapterSearch = setupRecyclerView(recyclerViewSearchResults, recipeList, 0, recipeList.size(), true);
            }
        });

        if (category != null) {
            hideRecyclerViews();
        } else {
            showRecyclerViews();
        }
    }

    private void hideRecyclerViews() {
        recyclerViewNeedToTry.setVisibility(View.GONE);
        recyclerViewSeason.setVisibility(View.GONE);
        recyclerViewBestSell.setVisibility(View.GONE);
        for (LinearLayout linerView : titles) {
            linerView.setVisibility(View.GONE);
        }
        searchResultsText.setVisibility(View.VISIBLE);
        recyclerViewSearchResults.setVisibility(View.VISIBLE);
    }
    private void showRecyclerViews() {
        recyclerViewNeedToTry.setVisibility(View.VISIBLE);
        recyclerViewSeason.setVisibility(View.VISIBLE);
        recyclerViewBestSell.setVisibility(View.VISIBLE);
        for (LinearLayout linerView : titles) {
            linerView.setVisibility(View.VISIBLE);
        }
        searchResultsText.setVisibility(View.GONE);
        recyclerViewSearchResults.setVisibility(View.GONE);
    }
    private RecipeAdapter setupRecyclerView(RecyclerView recyclerView, List<Recipe> list, int startIndex, int endIndex, boolean isHorizontal) {
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        recyclerView.setLayoutParams(params);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), isHorizontal ? LinearLayoutManager.HORIZONTAL : LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Set Item Animator
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Set Adapter
        RecipeAdapter adapter = new RecipeAdapter(getContext(), list.subList(startIndex, endIndex));
        recyclerView.setAdapter(adapter);

        return adapter;
    }
}