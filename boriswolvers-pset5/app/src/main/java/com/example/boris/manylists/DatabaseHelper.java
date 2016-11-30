package com.example.boris.manylists;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {
    // instance (singleton)
    private static DatabaseHelper sInstance;

    // Name of the tables
    public static final String NAME_LIST = "NAME_LIST";
    public static final String NAME_TODO = "NAME_TODO";

    // Table columns of the to-do items
    public static final String _ID = "_id";
    public static final String IMAGE_CHECKED_OR_UNCHECKED = "image_file";
    public static final String DESC_OF_TODO_ITEM = "description_of_todo_item";
    public static final String FROM_WHICH_LIST = "from_which_list";

    // Table column of the category
    public static final String DESC_OF_LIST = "description_of_list";

    // Database Information
    static final String DB_NAME = "TODOLISTLAST.DB";

    // Database version
    static final int DB_VERSION = 2;

    // Creating 2 tables
    private static final String CREATE_TABLE_TODO = "create table " + NAME_TODO + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + IMAGE_CHECKED_OR_UNCHECKED + " integer not null, " + DESC_OF_TODO_ITEM + " text not null, " + FROM_WHICH_LIST + " integer default 0);";

    private static final String CREATE_TABLE_LIST = "create table " + NAME_LIST + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DESC_OF_LIST + " text not null);";


    // Singleton, database can obtained from any activity
    public static synchronized DatabaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    // The constructor
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TODO);
        db.execSQL(CREATE_TABLE_LIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NAME_TODO);
        db.execSQL("DROP TABLE IF EXISTS " + NAME_LIST);
        onCreate(db);
    }

    // Closing the database
    public void close() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    // Inserting new todolist
    public TodoListClass insertList(String desc_of_list) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValue = new ContentValues();
        contentValue.put(DESC_OF_LIST, desc_of_list);

        db.insert(NAME_LIST, null, contentValue);

        TodoListClass list = new TodoListClass(desc_of_list);
        return list;
    }

    // Inserting new Todoitem
    public TodoItemClass insertTodo(int imageID, String desc, long fromwhich) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValue = new ContentValues();
        contentValue.put(DESC_OF_TODO_ITEM, desc);
        contentValue.put(IMAGE_CHECKED_OR_UNCHECKED, imageID);
        contentValue.put(FROM_WHICH_LIST, fromwhich);
        db.insert(NAME_TODO, null, contentValue);

        TodoItemClass todo = new TodoItemClass(desc, imageID, fromwhich);
        return todo;
    }

    // Obtaining the to-do list categories
    public Cursor fetchList() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<TodoListClass> allCategories = new ArrayList<TodoListClass>();

        // Using list objects to save data to a list
        String selectQuery = "SELECT  * FROM " + NAME_LIST;
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                // Setting data to list object
                TodoListClass list = new TodoListClass();
                list.setId(c.getInt((c.getColumnIndex(_ID))));
                list.setCategory(c.getString((c.getColumnIndex(DESC_OF_LIST))));
                allCategories.add(list);
            } while (c.moveToNext());
        }

        String[] columns = new String[] { _ID, DESC_OF_LIST,};
        Cursor cursor = db.query(NAME_LIST, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    // Obtaining the to-do items
    public Cursor fetchItems(long category_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<TodoItemClass> alltodos = new ArrayList<TodoItemClass>();

        // Using to-do objects to save data to a list
        String selectQuery = "SELECT  * FROM " + NAME_TODO;
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                // Setting data to to-do object
                TodoItemClass todo = new TodoItemClass();
                todo.setId(c.getInt((c.getColumnIndex(_ID))));
                todo.setImage_checked_or_unchecked(c.getInt((c.getColumnIndex(IMAGE_CHECKED_OR_UNCHECKED))));
                todo.setDescription(c.getString((c.getColumnIndex(DESC_OF_TODO_ITEM))));
                todo.setFromwich(c.getLong((c.getColumnIndex(FROM_WHICH_LIST))));
                alltodos.add(todo);
            } while (c.moveToNext());
        }

        String[] columns = new String[] { _ID, IMAGE_CHECKED_OR_UNCHECKED, DESC_OF_TODO_ITEM};
        String where = FROM_WHICH_LIST + "=" + category_id;
        Cursor cursor = db.query(true, NAME_TODO, columns, where, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        // Now the alltodos list can be returned but I prefer cursor
        return cursor;
    }

    // Get a specific row (by rowId) from todos
    public Cursor getRow(long _id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = new String[] { _ID, IMAGE_CHECKED_OR_UNCHECKED,
                DESC_OF_TODO_ITEM};
        String where = _ID + "=" + _id;
        Cursor c = 	db.query(true, NAME_TODO, columns,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Get a specific row (by rowId) from lists
    public Cursor getRowList(long _id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = new String[] { _ID, DESC_OF_LIST };
        String where = _ID + "=" + _id;
        Cursor c = 	db.query(true, NAME_LIST, columns,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Updates a row for checking/unchecking purposes
    public boolean updateRow(long _id, int imageID, String desc) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = _ID + "=" + _id;

        // Create row's data:
        ContentValues newValues = new ContentValues();
        newValues.put(IMAGE_CHECKED_OR_UNCHECKED, imageID);
        newValues.put(DESC_OF_TODO_ITEM, desc);

        // Insert it into the database.
        return db.update(NAME_TODO, newValues, where, null) != 0;
    }

    // Delete a to-do item with the specific _id
    public void deleteTodo(long _id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NAME_TODO, _ID + "=" + _id, null);
    }

    // Delete a category with the specific _id
    public void deleteList(long _id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NAME_LIST, _ID + "=" + _id, null);
    }

    // Deletes all items under a category when category is deleted
    public void deleteListTodo(long _id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NAME_TODO, FROM_WHICH_LIST + "=" + _id, null);
    }
}
