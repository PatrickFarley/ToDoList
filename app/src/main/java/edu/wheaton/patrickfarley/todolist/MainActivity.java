package edu.wheaton.patrickfarley.todolist;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import edu.wheaton.patrickfarley.todolist.db.TaskContract;
import edu.wheaton.patrickfarley.todolist.db.TaskDBHelper;
import edu.wheaton.patrickfarley.todolist.evaluation.EvalSheet;


// since it extends ListActivity, it must implement a ListView in its layout.
public class MainActivity extends ListActivity {

    private TaskDBHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteDatabase sqlDB = new TaskDBHelper(this).getWritableDatabase();
        Cursor cursor = sqlDB.query(TaskContract.TABLE, new String[]{TaskContract.Columns.TASK},
                null,null,null,null,null);
        cursor.moveToFirst();
        while(cursor.moveToNext()) {
            Log.d("MainActivity cursor",cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.Columns.TASK)));
        }
        updateUI();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //??? need an alertDialog with 3 different fields.
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // if the add action icon is clicked:
            case R.id.action_add_task:
                Log.d("MainActivity", "Add a new task");

                // use an AlertDialog Builder object
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("add a task");
                builder.setMessage("What to do?");

                // construct a View with the new_task_entry layout
                final LayoutInflater inflater = getLayoutInflater();
                final View newView = inflater.inflate(R.layout.new_task_entry, null);

                // pass the same view into the alertDialog, for the user to ineract with
                builder.setView(newView);

                // set "add" button
                builder.setPositiveButton("add",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i){

                        // store entered data:
                        final EditText taskField = (EditText)newView.findViewById(R.id.taskEntry);
                        final EditText priorityField = (EditText)newView.findViewById(R.id.priorityEntry);
                        final EditText timeField = (EditText)newView.findViewById(R.id.timeEntry);

                        String task = "unnamed task";
                        int priority = -1;
                        int time = -1;

                        try {
                            task = taskField.getText().toString();
                            priority = Integer.parseInt(priorityField.getText().toString());
                            time = Integer.parseInt(timeField.getText().toString());
                        } catch (NumberFormatException e) {
                            Context context = getApplicationContext();
                            CharSequence toastMsg = "Enter a positive integer";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, toastMsg, duration);
                            toast.show();
                            return;
                        }

                        // construct a TaskDBHelper object inside this method (using this activity's
                        // innate database)
                        helper = new TaskDBHelper(MainActivity.this);
                        // instantiate the database: this automatically references MainActivity's
                        // innate database
                        SQLiteDatabase db = helper.getWritableDatabase();

                        // make a ContentValues object
                        ContentValues values = new ContentValues();
                        values.clear();
                        // put our task string into the TASK column of the ContentValues object
                        values.put(TaskContract.Columns.TASK,task);
                        values.put(TaskContract.Columns.PRIORITY,priority);
                        values.put(TaskContract.Columns.TIME,time);
                        // insert the colums from the ContentValues object into the referenced
                        // database table.
                        db.insertWithOnConflict(TaskContract.TABLE,null,values, SQLiteDatabase.CONFLICT_IGNORE);
                        updateUI();
                    }
                });

                // set "Cancel" button
                builder.setNegativeButton("Cancel",null);
                // create and display the Alert Dialog
                builder.create().show();
                return true;
            default:
                return false;
        }
    }

    private void updateUI() {

        // open the database and get a cursor over the desired fields
        helper = new TaskDBHelper(MainActivity.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        Cursor cursor = sqlDB.query(TaskContract.TABLE,
                new String[]{TaskContract.Columns._ID, TaskContract.Columns.TASK,
                TaskContract.Columns.PRIORITY, TaskContract.Columns.TIME},
                null,null,null,null,null);

        // this activity's ListAdapter. we must pass in the task_view layout, as well as the location
        // of the data to be presented.
        SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(
                this, // context
                R.layout.task_view, // the layout template to use to present each data
                cursor, // pass in cursor to bind to.
                new String[] {TaskContract.Columns.TASK,
                        TaskContract.Columns.PRIORITY,
                        TaskContract.Columns.TIME}, // array of items to bind
                new int[] {R.id.taskField,
                        R.id.priorityField,
                        R.id.timeField},// parallel array of layout object to bind data to
                0);

        // this activity displays a ListView and uses a ListAdapter to bind the data to the view
        this.setListAdapter(listAdapter);
    }

    public void onDoneButtonClick(View view) {
        // get a ref to the parent view of the calling button (get the listView)
        View v = (View) view.getParent();
        // get a ref to the TextView corresponding to this button
        TextView taskField = (TextView) v.findViewById(R.id.taskField);
        // get the text from that TextView
        String task = taskField.getText().toString();

        // write an SQL command to delete said row from the table by matching its task value
        String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
                TaskContract.TABLE,
                TaskContract.Columns.TASK,
                task);

        // construct a db helper with MainActivity's database, and use it to return a reference
        // to said database
        helper = new TaskDBHelper(MainActivity.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();

        // execute the command in the database
        sqlDB.execSQL(sql);

        // finally, update the UI to the database, which is now missing the previous entry.
        updateUI();
    }

    public void onEvaluate(View view) {
        Log.d("MainActivity","onEvaluate has been called");
        Intent intent = new Intent(this, EvalSheet.class);
        EditText editText = (EditText)findViewById(R.id.evalEntField);
        int minutes = Integer.parseInt(editText.getText().toString());
        intent.putExtra("MINUTES_AMOUNT",minutes);
        startActivity(intent);
    }
}
