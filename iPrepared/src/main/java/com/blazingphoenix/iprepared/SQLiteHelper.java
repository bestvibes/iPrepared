package com.blazingphoenix.iprepared;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Vaibhav on 11/20/13.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static String DATABASE_NAME = "checklists";

    //table names
    private static final String TABLE_TASKS = "tasks";
    private static final String TABLE_HOME = "home";
    private static final String TABLE_CAR = "car";
    private static final String TABLE_WORK = "work";

    // common table keys
    private static final String KEY_NAME = "itemName";
    private static final String KEY_DATE = "expiryDate";
    private static final String KEY_CHECKED = "checked";
    private static final String KEY_LIST = "list";
    private static final String KEY_NOTIFICATION_ID = "notification_id";

    //init tables
    String CREATE_TABLE_TASKS = "CREATE TABLE IF NOT EXISTS " + TABLE_TASKS + " ( " + KEY_NAME + " TEXT PRIMARY KEY, " + KEY_DATE + " TEXT, " + KEY_CHECKED + " INTEGER, " + KEY_LIST + " TEXT, " + KEY_NOTIFICATION_ID + " INTEGER)";
    String CREATE_TABLE_HOME = "CREATE TABLE IF NOT EXISTS " + TABLE_HOME + " ( " + KEY_NAME + " TEXT PRIMARY KEY, " + KEY_DATE + " TEXT, " + KEY_CHECKED + " INTEGER, " + KEY_LIST + " TEXT, " + KEY_NOTIFICATION_ID + " INTEGER)";
    String CREATE_TABLE_CAR = "CREATE TABLE IF NOT EXISTS " + TABLE_CAR + " ( " + KEY_NAME + " TEXT PRIMARY KEY, " + KEY_DATE + " TEXT, " + KEY_CHECKED + " INTEGER, " + KEY_LIST + " TEXT, " + KEY_NOTIFICATION_ID + " INTEGER)";
    String CREATE_TABLE_WORK = "CREATE TABLE IF NOT EXISTS " + TABLE_WORK + " ( " + KEY_NAME + " TEXT PRIMARY KEY, " + KEY_DATE + " TEXT, " + KEY_CHECKED + " INTEGER, " + KEY_LIST + " TEXT, " + KEY_NOTIFICATION_ID + " INTEGER)";


    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_CAR);
        db.execSQL(CREATE_TABLE_HOME);
        db.execSQL(CREATE_TABLE_TASKS);
        db.execSQL(CREATE_TABLE_WORK);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOME);

        // create new tables
        onCreate(db);
    }

    // Adding new task
    public void addItem(ListItemClass item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, item.getName());
        values.put(KEY_DATE, item.getExpiry());
        // status of task- can be 0 for not done and 1 for done
        values.put(KEY_CHECKED, item.isChecked());
        values.put(KEY_LIST, item.getList());
        values.put(KEY_NOTIFICATION_ID, item.getNotificationID());

        // Inserting Row
        db.insert(item.getList(), null, values);
        db.close();
        //db.close(); // Closing database connection
    }

    public List<ListItemClass> getAllItems(String list) {
        List<ListItemClass> outList = new ArrayList<ListItemClass>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + list;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ListItemClass item = new ListItemClass();
                //item.setId(cursor.getInt((cursor.getColumnIndex(KEY_ID))));
                item.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                item.setExpiry(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
                item.setChecked(cursor.getInt(cursor.getColumnIndex(KEY_CHECKED)));
                item.setList(cursor.getString(cursor.getColumnIndex(KEY_LIST)));
                item.setNotificationID(cursor.getInt(cursor.getColumnIndex(KEY_NOTIFICATION_ID)));
                // Adding item to list
                outList.add(item);
            } while (cursor.moveToNext());
        }

        //db.close();
        // return task list
        cursor.close();
        db.close();
        return outList;
    }

    public List<ListItemClass> getExpiringItems() {
        List<ListItemClass> outList = new ArrayList<ListItemClass>();
        outList.addAll(getAllItems(TABLE_CAR));
        outList.addAll(getAllItems(TABLE_HOME));
        outList.addAll(getAllItems(TABLE_TASKS));
        outList.addAll(getAllItems(TABLE_WORK));
        List<ListItemClass> result = new ArrayList<ListItemClass>(outList);

        for (ListItemClass item : outList) {
            if (item.getExpiry() == null || item.getExpiry().equals("")) {
                result.remove(item);
            }
        }
        outList.clear();

        Collections.sort(result, new Comparator<ListItemClass>() {
            @Override
            public int compare(ListItemClass lhs, ListItemClass rhs) {
                return lhs.getExpiry().compareTo(rhs.getExpiry());
            }
        });
        return result;
    }

    public void updateItem(ListItemClass task, String list) {
        // updating row
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, task.getName());
        values.put(KEY_DATE, task.getExpiry());
        values.put(KEY_LIST, task.getList());
        values.put(KEY_CHECKED, task.isChecked());
        values.put(KEY_NOTIFICATION_ID, task.getNotificationID());
        db.update(list, values, KEY_NAME + " = ?", new String[]{task.getName()});
        db.close();
    }

    public String getPercentage() {
        List<ListItemClass> everything = new ArrayList<ListItemClass>();
        everything.addAll(getAllItems(TABLE_CAR));
        everything.addAll(getAllItems(TABLE_HOME));
        everything.addAll(getAllItems(TABLE_TASKS));
        everything.addAll(getAllItems(TABLE_WORK));

        int total = everything.size();
        int checked = 0;

        for (ListItemClass item : everything) {
            if (item.isChecked() == 1) {
                checked++;
            }
        }

        try {
            Double percentage = ((double) checked / total) * 100;
            //Log.d("SQLiteHelper", "CHECKED: " + checked + "        TOTAL: " + total + "         PERCENTAGE: " + String.valueOf(percentage.intValue()));
            return String.valueOf(percentage.intValue());
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    public ListItemClass getItemByName(String name, String list) {
        ListItemClass item = new ListItemClass();
        // Select All Query
        String selectQuery = "SELECT * FROM " + list + " WHERE " + KEY_NAME + " = \"" + name + "\"";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //item.setId(cursor.getInt((cursor.getColumnIndex(KEY_ID))));
                item.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                item.setExpiry(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
                item.setChecked(cursor.getInt(cursor.getColumnIndex(KEY_CHECKED)));
                item.setList(cursor.getString(cursor.getColumnIndex(KEY_LIST)));
                item.setNotificationID(cursor.getInt(cursor.getColumnIndex(KEY_NOTIFICATION_ID)));
                // Adding item to list
            } while (cursor.moveToNext());
        }

        //db.close();
        // return task list
        cursor.close();
        db.close();
        return item;
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}