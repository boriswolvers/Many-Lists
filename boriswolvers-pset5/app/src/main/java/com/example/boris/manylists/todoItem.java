package com.example.boris.manylists;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class todoItem extends AppCompatActivity {
    // Declaring list with the sources for the checked/unchecked images
    int[] imageIDs = {
            R.drawable.ic_unchecked,
            R.drawable.ic_checked,
    };

    private Bundle extras;
    private String stringItem;
    private SimpleCursorAdapter adapterTodo;
    private ListView listViewTodo;
    private DatabaseHelper db;
    private Cursor cursorTodo;
    private Long category_id;

    // Containing the image for checked/unchecked image and description for list item
    final String[] from = new String[] { DatabaseHelper.IMAGE_CHECKED_OR_UNCHECKED, DatabaseHelper.DESC_OF_TODO_ITEM };

    // Pass the elements to list item xml
    final int[] to = new int[] { R.id.imageCheck, R.id.textviewListItem};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_item);

        //  Open the database
        db = DatabaseHelper.getInstance(this);

        // obtain movie title user wants to add to watch list
        extras = getIntent().getExtras();
        category_id = extras.getLong("todoItem");

        // Setting title of activity
        TextView title = (TextView)findViewById(R.id.textViewTodoItem);
        String titleCategory = extras.getString("title");
        title.setText(titleCategory);

        setList(category_id);

    }

    private void setList(Long category_id) {

        // Obtain all the data from the database
        cursorTodo = db.fetchItems(category_id);

        // Obtain the listview
        listViewTodo = (ListView) findViewById(R.id.listViewTodoitems);
        adapterTodo = new SimpleCursorAdapter(this, R.layout.list_item, cursorTodo, from, to, 0);

        // Set the adapter for the list view
        listViewTodo.setAdapter(adapterTodo);

        // when a list item is LongClicked -> remove from listview and sql database
        listViewTodo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                // Delete selected item
                db.deleteTodo(l);

                // Update cursor object
                cursorTodo.requery();

                // Change the content in the listview
                adapterTodo.notifyDataSetChanged();

                Toast toast = Toast.makeText(todoItem.this, "Item deleted!", Toast.LENGTH_SHORT);
                toast.show();
                return true;
            }
        });

        // when a listview item is (short) onclicked -> check the item as task done
        listViewTodo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // Get intire row of the list item clicked
                Cursor cursorRow = db.getRow(l);
                if (cursorRow.moveToFirst()) {

                    // Obtaining the right data out of the row
                    int idOfImage = cursorRow.getInt(1);
                    String description = cursorRow.getString(2);

                    // Changing the image to checked or unchecked
                    if (idOfImage == imageIDs[0]) {
                        idOfImage = imageIDs[1];
                    }
                    else {
                        idOfImage = imageIDs[0];
                    }

                    // Update the row with the new image
                    db.updateRow(l, idOfImage, description);
                }
                cursorRow.close();

                // Update cursor object
                cursorTodo.requery();

                // Change the content in the listview
                adapterTodo.notifyDataSetChanged();
            }
        });
    }

    public void addItemtoView(View view) {
        EditText editToDoItem = (EditText)findViewById(R.id.editTextToDoItem);
        stringItem = editToDoItem.getText().toString();
        editToDoItem.setText("");

        // make sure user enters an item
        if (!(stringItem.length() == 0)) {

            // Add new item to the database with the right unchecked image and description
            TodoItemClass todo = db.insertTodo(imageIDs[0], stringItem, category_id);

            // Update cursor object
            cursorTodo.requery();

            // Change the content in the listview
            adapterTodo.notifyDataSetChanged();

            // Get string from to-do object to toast
            Toast toast = Toast.makeText(this, todo.getDescription() + " has been added!", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            Toast toast = Toast.makeText(this, "You did not enter an item", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}

