# Recipe Manager Android App

This Android app is a Recipe Manager that allows users to browse, search, and filter recipes. The app includes functionality for managing favorite recipes, adding new recipes, and filtering recipes based on categories and favorites. The app is built with Java in Android Studio.

## Features

### 1. **Browse Recipes by Category**
   - The app allows users to browse recipes based on different categories like:
     - Beef
     - Chicken
     - Dessert
     - Lamb
     - Miscellaneous
     - Pasta
     - Pork
     - Seafood
     - Side
     - Starter
     - Vegan
     - Vegetarian
     - Breakfast
     - Goat

### 2. **Search Recipes**
   - A `SearchView` is provided to search recipes by their names.
   - Recipes will be filtered in real-time as the user types in the search box.
   - Search results can be shown or hidden based on whether a valid search query is entered.

### 3. **Filter Recipes by Favorites**
   - A button (ImageButton) in the app opens a modal with a checkbox option to filter recipes based on favorites.
   - Users can select the checkbox to search only for favorite recipes.
   - The app filters the recipes to show only those marked as favorites if the checkbox is selected.

### 4. **Add New Recipe**
   - Users can add new recipes using a floating action button (`FAB`).
   - The recipe adding process allows users to input:
     - Recipe name
     - Ingredients
     - Instructions
     - Category
     - A favorite toggle to mark the recipe as a favorite
   - The recipe is stored and displayed in the app once added.

### 5. **Favorites**
   - Recipes can be marked as favorites and can later be filtered based on the favorite status.
   - The app remembers which recipes are marked as favorites across sessions.

### 6. **Category-wise Recipe Display**
   - Recipes are displayed in horizontal sections for categories such as:
     - "Need to Try"
     - "Seasonal"
     - "Best Sellers"
   - Users can select a category, and the app will show a list of recipes specific to that category.

### 7. **Responsive UI**
   - The app's UI adapts to different screen sizes and orientations.
   - Recipes are displayed in a clean and structured way using `RecyclerView` and `LinearLayoutManager`.
   - Dynamic updating of RecyclerViews when search results change or filters are applied.

## Installation

To get started with the app, clone the repository and open it in Android Studio:

```bash
git clone https://github.com/yourusername/recipe-manager.git
```

# Image Gallery

Explore the various features and UI of the Recipe Manager app through the images below:

| ![Image 1](assets/login.jpeg) | ![Image 2](assets/register.jpeg) | ![Image 3](assets/main_app.jpeg) |
|-----------------------------|----------------------------|------------------------------|
| **Login Screen** - This is the user login page where users can sign in with their credentials. | **Register Screen** - This screen allows users to create a new account with necessary information like email and password. | **Main App Screen** - The home screen of the app where users can browse different recipe categories. |

| ![Image 4](assets/map_app_carousels.jpeg) | ![Image 5](assets/category_selection.jpeg) | ![Image 6](assets/recipe_details.gif) |
|---------------------------------|----------------------------------|---------------------------------|
| **Carousel Feature** - A carousel feature for browsing different recipe categories or featured recipes in a visually appealing way. | **Category Selection** - This screen allows users to select a specific category of recipes, such as Vegan, Dessert, or Chicken. | **Recipe Details** - Displays the recipe with ingredients, instructions, and the option to mark it as a favorite or share. |

| ![Image 7](assets/search_by_favourites.jpeg) | ![Image 8](assets/add_recipe_1.jpeg) | ![Image 9](assets/app_recipe_2.jpeg) |
|-------------------------------------|---------------------------------|--------------------------------|
| **Search by Favourites** - This feature enables users to filter recipes based on their favorites, making it easy to find saved recipes. | **Add Recipe Screen** - Users can add a new recipe by inputting ingredients, steps, and other relevant details. | **Add Recipe Screen** - Users can add a new recipe by inputting ingredients, steps, and other relevant details. |
