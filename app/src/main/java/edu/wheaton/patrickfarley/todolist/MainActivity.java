package edu.wheaton.patrickfarley.todolist;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import edu.wheaton.patrickfarley.todolist.db.TaskContract;
import edu.wheaton.patrickfarley.todolist.db.TaskDBHelper;




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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                Log.d("MainActivity", "Add a new task");
                // an AlertDialog Builder object
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
                        // insert the colums from the ContentValues object into the referenced
                        // database table.
                        db.insertWithOnConflict(TaskContract.TABLE,null,values, SQLiteDatabase.CONFLICT_IGNORE);
                        updateUI();
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

    private void updateUI() {

        helper = new TaskDBHelper(MainActivity.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        Cursor cursor = sqlDB.query(TaskContract.TABLE,
                new String[]{TaskContract.Columns._ID, TaskContract.Columns.TASK},
                null,null,null,null,null);

        // this activity's ListAdapter. must pass in the task_view layout, as well as the location
        // of the data to be presented.
        SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(
                this, // context
                R.layout.task_view, // the layout template to use to present each data
                cursor, // pass in cursor to bind to.
                new String[] {TaskContract.Columns.TASK}, // array of items to bind
                new int[] {R.id.taskTextView},// parallel array of layout object to bind data to
                0);

        // this activity displays a ListView and uses a ListAdapter to bind the data to the view
        this.setListAdapter(listAdapter);
    }

    public void onDoneButtonClick(View view) {
        // get a ref to the parent view of the calling button (get the listView)
        View v = (View) view.getParent();
        // get a ref to the TextView corresponding to this button
        TextView taskTextView = (TextView) v.findViewById(R.id.taskTextView);
        // get the text from that TextView
        String task = taskTextView.getText().toString();

        // write an SQL command to delete said task from the table
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
}
