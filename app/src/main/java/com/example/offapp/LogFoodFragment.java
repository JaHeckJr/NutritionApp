package com.example.offapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class LogFoodFragment extends Fragment {

    private DBHelper dbHelper;
    private RecyclerView breakfastRecyclerView;
    private RecyclerView lunchRecyclerView;
    private RecyclerView dinnerRecyclerView;
    private FoodLogAdapter breakfastAdapter;
    private FoodLogAdapter lunchAdapter;
    private FoodLogAdapter dinnerAdapter;
    private List<FoodLog> breakfastList;
    private List<FoodLog> lunchList;
    private List<FoodLog> dinnerList;

    private static final String TAG = "LogFoodFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_food, container, false);

        dbHelper = new DBHelper(getContext());

        breakfastRecyclerView = view.findViewById(R.id.breakfastRecyclerView);
        lunchRecyclerView = view.findViewById(R.id.lunchRecyclerView);
        dinnerRecyclerView = view.findViewById(R.id.dinnerRecyclerView);

        breakfastList = new ArrayList<>();
        lunchList = new ArrayList<>();
        dinnerList = new ArrayList<>();

        breakfastAdapter = new FoodLogAdapter(breakfastList);
        lunchAdapter = new FoodLogAdapter(lunchList);
        dinnerAdapter = new FoodLogAdapter(dinnerList);

        breakfastRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        breakfastRecyclerView.setAdapter(breakfastAdapter);

        lunchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        lunchRecyclerView.setAdapter(lunchAdapter);

        dinnerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dinnerRecyclerView.setAdapter(dinnerAdapter);

        Button addBreakfastButton = view.findViewById(R.id.addBreakfastButton);
        Button addLunchButton = view.findViewById(R.id.addLunchButton);
        Button addDinnerButton = view.findViewById(R.id.addDinnerButton);

        addBreakfastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFoodDialog("Breakfast");
            }
        });

        addLunchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFoodDialog("Lunch");
            }
        });

        addDinnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFoodDialog("Dinner");
            }
        });

        loadFoodLogs("Breakfast");
        loadFoodLogs("Lunch");
        loadFoodLogs("Dinner");

        return view;
    }

    private void showAddFoodDialog(final String mealType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add " + mealType + " Item");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_food, null);
        final EditText mealEditText = view.findViewById(R.id.editMealEditText);
        final EditText foodNameEditText = view.findViewById(R.id.editFoodNameEditText);
        final EditText caloriesEditText = view.findViewById(R.id.editCaloriesEditText);

        builder.setView(view);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String meal = mealEditText.getText().toString();
                String foodName = foodNameEditText.getText().toString();
                String caloriesStr = caloriesEditText.getText().toString();

                if (TextUtils.isEmpty(meal) || TextUtils.isEmpty(foodName) || TextUtils.isEmpty(caloriesStr)) {
                    Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                int calories = Integer.parseInt(caloriesStr);
                String userId = "exampleUserId"; // Replace with actual userId logic

                long newRowId = dbHelper.insertFoodLog(meal, foodName, calories, userId, mealType);
                if (newRowId == -1) {
                    Toast.makeText(getContext(), "Error adding food", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error inserting new food log");
                } else {
                    Toast.makeText(getContext(), "Food added successfully", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Food added successfully with id: " + newRowId);
                    loadFoodLogs(mealType);
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void loadFoodLogs(String mealType) {
        List<FoodLog> foodLogs = new ArrayList<>();
        Cursor cursor = dbHelper.getFoodLogsByMealType(mealType);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String meal = cursor.getString(cursor.getColumnIndexOrThrow("meal"));
            String foodName = cursor.getString(cursor.getColumnIndexOrThrow("foodName"));
            int calories = cursor.getInt(cursor.getColumnIndexOrThrow("calories"));

            FoodLog foodLog = new FoodLog(id, meal, foodName, calories);
            foodLogs.add(foodLog);

            Log.d(TAG, "Loaded food log: ID=" + id + ", Meal=" + meal + ", FoodName=" + foodName + ", Calories=" + calories);
        }
        cursor.close();

        switch (mealType) {
            case "Breakfast":
                breakfastList.clear();
                breakfastList.addAll(foodLogs);
                breakfastAdapter.notifyDataSetChanged();
                break;
            case "Lunch":
                lunchList.clear();
                lunchList.addAll(foodLogs);
                lunchAdapter.notifyDataSetChanged();
                break;
            case "Dinner":
                dinnerList.clear();
                dinnerList.addAll(foodLogs);
                dinnerAdapter.notifyDataSetChanged();
                break;
        }
    }
}
