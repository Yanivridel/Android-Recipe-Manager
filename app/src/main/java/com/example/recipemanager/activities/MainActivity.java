package com.example.recipemanager.activities;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsetsController;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.recipemanager.GlobalAppState;
import com.example.recipemanager.R;
import com.example.recipemanager.interfaces.RecipeFetchCallback;
import com.example.recipemanager.models.Recipe;
import com.example.recipemanager.models.User;
import com.example.recipemanager.utils.RecipeFetcher;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Set the status bar and his icons colors
        getWindow().setStatusBarColor(getResources().getColor(R.color.dark_green));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // Android 11+
            getWindow().getInsetsController().setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Android 6+
            getWindow().getDecorView().setSystemUiVisibility(0);
        }

        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup NavController to manage navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_login);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
        }

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Populate Recipe DB
        RecipeFetcher recipeFetcher = new RecipeFetcher();
//        for (String keyword : searchKeywords) {
//            recipeFetcher.fetchRecipesApi(keyword);
//        }
    }


    // Register
    public void register() {
        String email = ((EditText)findViewById(R.id.email_input_register)).getText().toString();
        String password = ((EditText)findViewById(R.id.pass_input_register)).getText().toString();
        String confirmPassword = ((EditText)findViewById(R.id.confirm_pass_input_register)).getText().toString();

        if (!validateEmail(email)) {
            Toast.makeText(MainActivity.this, "Invalid email format", Toast.LENGTH_LONG).show();
            return;
        } else if (!validatePassword(password) || !validatePassword(confirmPassword)) {
            Toast.makeText(MainActivity.this, "Password cannot be empty", Toast.LENGTH_LONG).show();
            return;
        } else if (!validateSamePasswords(password,confirmPassword)) {
            Toast.makeText(MainActivity.this, "Passwords are not equal", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (firebaseUser != null) {
                                String userId = firebaseUser.getUid();
                                addUserDB(userId, email);
                            }

                            Toast.makeText(MainActivity.this , "Register Success", Toast.LENGTH_LONG).show();
                            Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment).navigate(R.id.action_fragment_register_to_fragment_login);
                        } else {
                            // If sign up fails, display a message to the user.
                            Toast.makeText(MainActivity.this , "Register Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    // Login
    public void login() {
        String email = ((EditText)findViewById(R.id.email_input_login)).getText().toString();
        String password = ((EditText)findViewById(R.id.pass_input_login)).getText().toString();

        if (!validateEmail(email)) {
            Toast.makeText(MainActivity.this, "Invalid email format", Toast.LENGTH_LONG).show();
            return;
        } else if (!validatePassword(password)) {
            Toast.makeText(MainActivity.this, "Password cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (firebaseUser != null) {
                                String userId = firebaseUser.getUid();
                                fetchUserData(userId);  // Fetch user data using UID
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this , "Login Failed", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    // Firebase Functions
    public void addUserDB(String userId, String email) {
        String phoneNumber = ((EditText)findViewById(R.id.phone_input_register)).getText().toString();
        String fName = ((EditText)findViewById(R.id.fName_input_register)).getText().toString();
        String lName = ((EditText)findViewById(R.id.lName_input_register)).getText().toString();

        // route inside the database
        DatabaseReference usersRef = database.getReference("users").child(userId);
        User user = new User(userId, fName, lName, email, phoneNumber);
        Log.d("USER" + user.getEmail(),user.getId());

        usersRef.setValue(user);
    }
    public void fetchUserData(String userId) {
        DatabaseReference userRef = database.getReference("users").child(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                // Fetch user object
                User user = task.getResult().getValue(User.class);
                user.setDatabaseReference(userRef);
                // Store user in global state
                GlobalAppState.setCurrentUser(user);

                Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment)
                        .navigate(R.id.action_fragment_login_to_fragment_app);
            } else {
                Toast.makeText(MainActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Help Functions
    public boolean validateEmail(String email) {
        return !email.isEmpty() && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    public boolean validatePassword(String password) {
        return !password.isEmpty();
    }
    public boolean validateSamePasswords(String password1, String password2) {
        return Objects.equals(password1, password2);
    }

}

//String[] searchKeywords = {
//        // Meat-based dishes
//        "Steak", "BBQ", "Ribs", "Meatballs", "Beef Stew", "Roast Beef", "Grilled Chicken", "Fried Chicken",
//        "Chicken Wings", "Turkey", "Duck", "Pork Chops", "Lamb Curry", "Venison", "Sausage", "Bacon",
//
//        // Seafood
//        "Salmon", "Tuna", "Shrimp", "Crab", "Lobster", "Clams", "Oysters", "Mussels", "Fish Tacos",
//        "Grilled Fish", "Ceviche", "Paella", "Seafood Chowder", "Sushi", "Poke Bowl", "Scallops",
//
//        // Vegetarian & Vegan
//        "Vegetarian", "Vegan", "Tofu", "Tempeh", "Lentils", "Chickpeas", "Mushrooms", "Eggplant",
//        "Cauliflower", "Jackfruit", "Falafel", "Hummus", "Buddha Bowl", "Quinoa", "Avocado", "Zucchini",
//
//        // Pasta & Rice
//        "Spaghetti", "Lasagna", "Fettuccine Alfredo", "Penne Arrabbiata", "Carbonara", "Mac and Cheese",
//        "Risotto", "Fried Rice", "Jambalaya", "Biryani", "Paella", "Gnocchi", "Ravioli", "Soba Noodles",
//        "Ramen", "Pad Thai", "Pho", "Lo Mein",
//
//        // Bread & Baked Goods
//        "Bread", "Sourdough", "Garlic Bread", "Baguette", "Croissant", "Pizza Dough", "Muffins",
//        "Pancakes", "Waffles", "Crepes", "Bagels", "Pita", "Pretzel", "Cinnamon Rolls", "Cornbread",
//        "Brioche", "Focaccia", "Biscuits",
//
//        // Soups & Stews
//        "Chicken Soup", "Beef Stew", "Tomato Soup", "Minestrone", "French Onion Soup", "Chili",
//        "Clam Chowder", "Pumpkin Soup", "Lentil Soup", "Miso Soup", "Gumbo", "Pho", "Pozole",
//        "Bouillabaisse", "Avgolemono", "Gazpacho",
//
//        // Appetizers & Snacks
//        "Nachos", "Bruschetta", "Spring Rolls", "Samosas", "Mozzarella Sticks", "Deviled Eggs",
//        "Stuffed Mushrooms", "Onion Rings", "Sliders", "Empanadas", "Tzatziki", "Guacamole",
//        "Tapenade", "Caprese Salad", "Caviar",
//
//        // Desserts
//        "Chocolate Cake", "Cheesecake", "Brownies", "Tiramisu", "Apple Pie", "Donuts", "Ice Cream",
//        "Pudding", "Macarons", "Baklava", "Fruit Tart", "Churros", "Pavlova", "Gulab Jamun",
//        "Mochi", "Flan", "Truffles", "Soufflé",
//
//        // Drinks & Smoothies
//        "Mojito", "Margarita", "Piña Colada", "Sangria", "Hot Chocolate", "Espresso", "Latte",
//        "Milkshake", "Smoothie", "Lemonade", "Iced Tea", "Matcha", "Bubble Tea", "Chai Tea",
//        "Turmeric Latte", "Detox Juice",
//
//        // Breakfast & Brunch
//        "Omelette", "Scrambled Eggs", "French Toast", "Granola", "Yogurt Parfait", "Smoothie Bowl",
//        "Eggs Benedict", "Avocado Toast", "Breakfast Burrito", "Shakshuka", "Hash Browns",
//        "Chia Pudding", "Breakfast Quesadilla",
//
//        // International Cuisines
//        "Mexican", "Italian", "Indian", "Chinese", "Thai", "Japanese", "Greek", "French",
//        "Spanish", "Korean", "Mediterranean", "Turkish", "Lebanese", "Persian", "Ethiopian",
//        "Brazilian", "Caribbean", "Argentinian",
//
//        // Holiday & Special Occasion Meals
//        "Thanksgiving Turkey", "Christmas Ham", "Easter Brunch", "Halloween Treats", "Hanukkah Latkes",
//        "New Year's Eve Appetizers", "Birthday Cake", "BBQ Ribs", "Romantic Dinner", "Bridal Shower Brunch",
//
//        // Cooking Methods
//        "Grilled", "Roasted", "Steamed", "Sautéed", "Baked", "Boiled", "Fried", "Braised",
//        "Poached", "Smoked", "Stir-Fried", "Pressure Cooker", "Slow Cooker", "Air Fryer", "Sous Vide"
//};