package com.example.boris.manylists;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String stringList;
    private String description_list;
    private SimpleCursorAdapter adapter;
    private ListView listViewList;
    private DatabaseHelper db;
    private Cursor cursor;

    // Containing the image for checked/unchecked image and description for list item
    final String[] from = new String[] { DatabaseHelper.DESC_OF_LIST };

    // Pass the elements to list item xml
    final int[] to = new int[] { R.id.textviewCategoryItem };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Open the database (singleton)
        db = DatabaseHelper.getInstance(this);

        // Set the listview
        setList();
    }

    private void setList() {

        // Obtain all the data from the database
        cursor = db.fetchList();

        // Obtain the listview
        listViewList = (ListView) findViewById(R.id.listViewMain);
        adapter = new SimpleCursorAdapter(this, R.layout.category_item, cursor, from, to, 0);

        // Set the adapter for the list view
        listViewList.setAdapter(adapter);

        // when a list item is LongClicked -> remove from listview and sql database
        listViewList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                // Delete selected item
                db.deleteListTodo(l);
                db.deleteList(l);

                // Update cursor object
                cursor.requery();

                // Change the content in the listview
                adapter.notifyDataSetChanged();

                Toast toast = Toast.makeText(MainActivity.this, "Category deleted!", Toast.LENGTH_SHORT);
                toast.show();
                return true;
            }
        });

        // when a listview item is (short) onclicked -> go to another activity
        listViewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Cursor cursorRow = db.getRowList(l);

                if (cursorRow.moveToFirst()) {

                    description_list = cursorRow.getString(1);

                }
                cursorRow.close();

                Intent gotoTodo= new Intent(MainActivity.this, todoItem.class);
                gotoTodo.putExtra("todoItem", l);
                gotoTodo.putExtra("title", description_list);
                startActivity(gotoTodo);
            }
        });
    }

    public void addItemtoView(View view) {
        EditText editToDoItem = (EditText)findViewById(R.id.editTextCategoy);
        stringList = editToDoItem.getText().toString();
        editToDoItem.setText("");

        // make sure user enters an item
        if (!(stringList.length() == 0)) {

            // Add new item to the database with the right unchecked image and description
            TodoListClass list = db.insertList(stringList);

            // Update cursor object
            cursor.requery();

            // Change the content in the listview
            adapter.notifyDataSetChanged();

            // Get string from list object to toast
            Toast toast = Toast.makeText(this, list.gettodoCategory() + " has been added!", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            Toast toast = Toast.makeText(this, "You did not enter a category", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}