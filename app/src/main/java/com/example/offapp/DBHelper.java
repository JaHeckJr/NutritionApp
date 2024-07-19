package com.example.offapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "FoodLogDB.db";
    private static final int DATABASE_VERSION = 2; // Incremented version number
    private final String databasePath;
    private final Context context;

    private static final String TAG = "DBHelper";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.databasePath = context.getDatabasePath(DATABASE_NAME).getPath();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    private void createTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS foodlogs (id INTEGER PRIMARY KEY AUTOINCREMENT, meal TEXT, foodName TEXT, calories INTEGER, userId TEXT, mealType TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE foodlogs ADD COLUMN mealType TEXT");
        }
    }

    public void createDatabase() throws IOException {
        if (!checkDatabase()) {
            this.getReadableDatabase(); // This call is crucial as it creates a path where the database will be copied
            try {
                copyDatabase();
                Log.d(TAG, "Database copied successfully");
            } catch (IOException e) {
                Log.e(TAG, "Error copying database", e);
                throw new Error("Error copying database", e);
            }
        }
    }

    private boolean checkDatabase() {
        File dbFile = new File(databasePath);
        boolean exists = dbFile.exists();
        Log.d(TAG, "Database exists: " + exists);
        return exists;
    }

    private void copyDatabase() throws IOException {
        InputStream myInput = context.getAssets().open(DATABASE_NAME);
        OutputStream myOutput = new FileOutputStream(databasePath);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public SQLiteDatabase openDatabase() {
        Log.d(TAG, "Opening database at: " + databasePath);
        return SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public long insertFoodLog(String meal, String foodName, int calories, String userId, String mealType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("meal", meal);
        values.put("foodName", foodName);
        values.put("calories", calories);
        values.put("userId", userId);
        values.put("mealType", mealType);
        return db.insert("foodlogs", null, values);
    }

    public Cursor getFoodLogsByMealType(String mealType) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("foodlogs", null, "mealType = ?", new String[]{mealType}, null, null, null);
    }

    public boolean updateFoodLog(int id, String meal, String foodName, int calories, String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("meal", meal);
        values.put("foodName", foodName);
        values.put("calories", calories);
        values.put("userId", userId);
        int rowsAffected = db.update("foodlogs", values, "id = ?", new String[]{String.valueOf(id)});
        return rowsAffected > 0;
    }
}

