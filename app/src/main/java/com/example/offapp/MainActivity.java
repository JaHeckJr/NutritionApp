package com.example.offapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize DBHelper and create the database if it does not exist
        dbHelper = new DBHelper(this);
        try {
            dbHelper.createDatabase(); // Ensure database is copied and ready
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Setup bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int id = item.getItemId();
                if (id == R.id.navigation_dashboard) {
                    selectedFragment = new DashboardFragment();
                } else if (id == R.id.navigation_learn) {
                    selectedFragment = new LearnFragment();
                } else if (id == R.id.navigation_recipes) {
                    selectedFragment = new RecipesFragment();
                } else if (id == R.id.navigation_more) {
                    selectedFragment = new MoreFragment();
                } else if (id == R.id.navigation_log_food) {
                    selectedFragment = new LogFoodFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }
                return true;
            }
        });

        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.navigation_dashboard);
    }
}
