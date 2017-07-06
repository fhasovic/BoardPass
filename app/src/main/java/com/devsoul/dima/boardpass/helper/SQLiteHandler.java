package com.devsoul.dima.boardpass.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.devsoul.dima.boardpass.model.Client;

import java.util.HashMap;

/**
 *  This class takes care of storing the user data in SQLite database.
 *  Whenever we needs to get the logged in user information,
 *  we fetch from SQLite instead of making request to server.
 */
public class SQLiteHandler extends SQLiteOpenHelper
{
    // Logcat tag
    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "login_db";

    // Login table name
    public static final String TABLE_USER = "user";

    // Login Table Columns names
    public static final String KEY_UID = "uid";
    public static final String KEY_FULL_NAME = "full_name";
    public static final String KEY_BIRTH_DATE = "birth_date";
    public static final String KEY_ID_CARD = "id_card";
    public static final String KEY_VIP_PREF = "vip_pref";
    private static final String KEY_EMAIL = "email";
    public static final String KEY_CREATED_AT = "created_at";

    // Table Create Statements
    // Users table create statement
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USER + "("
            + KEY_FULL_NAME + " TEXT," + KEY_BIRTH_DATE + " TEXT,"
            + KEY_ID_CARD + " TEXT," + KEY_VIP_PREF + " TEXT,"
            + KEY_EMAIL + " TEXT UNIQUE," + KEY_CREATED_AT + " DATETIME" + ")";

    // Constructor
    public SQLiteHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //SQLiteDatabase db = this.getWritableDatabase();
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // creating required tables
        db.execSQL(CREATE_TABLE_USERS);
        Log.d(TAG, "Database Tables Created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        // Create new tables
        onCreate(db);
    }

    /**
     * Storing user details in database
     */
    public void addUser(Client user)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FULL_NAME, user.GetFullName());
        values.put(KEY_BIRTH_DATE, user.GetBirthDate());
        values.put(KEY_ID_CARD, user.GetID());
        values.put(KEY_VIP_PREF, user.GetVIP());
        values.put(KEY_EMAIL, user.GetEmail());
        values.put(KEY_CREATED_AT, user.GetCreated_At());

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user was added to SQLite: " + id);
    }

    /**
     * Getting user data from database
     */
    public HashMap<String, String> getUserDetails()
    {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0)
        {
            user.put(KEY_FULL_NAME, cursor.getString(0));
            user.put(KEY_BIRTH_DATE, cursor.getString(1));
            user.put(KEY_ID_CARD, cursor.getString(2));
            user.put(KEY_VIP_PREF, cursor.getString(3));
            user.put(KEY_EMAIL, cursor.getString(4));
            user.put(KEY_CREATED_AT, cursor.getString(5));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user info from SQLite" + user.toString());

        return user;
    }

    /**
     * Will return total number of users in SQLite database table.
     * @param table_name - Name of the table to get the number of rows.
     * @return number of rows
     */
    public int getRowCount(String table_name)
    {
        String countQuery = "SELECT * FROM " + table_name;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(countQuery, null);
        int row_count = c.getCount();
        db.close();
        c.close();
        return row_count;
    }

    /**
     * Delete table from SQLite that get as parameter
     * @param table_name - Name of the table
     */
    public void deleteTable(String table_name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(table_name, null, null);
        db.close();

        Log.d(TAG, table_name + " has been deleted from SQLite");
    }
}
