package edu.wheaton.patrickfarley.todolist;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import edu.wheaton.patrickfarley.todolist.db.TaskContract;
import edu.wheaton.patrickfarley.todolist.db.TaskDBHelper;


public class MainActivity extends ActionBarActivity {

    private TaskDBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                Log.d("MainActivity", "Add a new task");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("add a task");
                builder.setMessage("What to do?");
                // create a blank EditText object
                final EditText inputField = new EditText(this);
                builder.setView(inputField);
                builder.setPositiveButton("add",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i){
                        // store entered text in a string
                        String task = inputField.getText().toString();

                        Log.d("MainActivity",task);

                        // construct a TaskDBHelper object inside this method
                        helper = new TaskDBHelper(MainActivity.this);
                        // instantiate the database: this automatically references
                        SQLiteDatabase db = helper.getWritableDatabase();

                        // make a ContentValues object
                        ContentValues values = new ContentValues();
                        values.clear();
                        // put our task string into the TASK column of the ContentValues object
                        values.put(TaskContract.Columns.TASK,task);
                        // insert the colums from the ContentValues object into the referenced
                        // database table.
                        db.insertWithOnConflict(TaskContract.TABLE,null,values, SQLiteDatabase.CONFLICT_IGNORE);
                    }
                });

                builder.setNegativeButton("Cancel",null);
                // show the created Alert Dialog
                builder.create().show();
                return true;
            default:
                return false;
        }
    }
}
