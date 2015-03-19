package edu.wheaton.patrickfarley.todolist;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

import edu.wheaton.patrickfarley.todolist.db.TaskContract;
import edu.wheaton.patrickfarley.todolist.db.TaskDBHelper;


public class EvalSheet extends ActionBarActivity {

    ArrayList<TaskEntry> internal = new ArrayList<TaskEntry>();

    private class TaskEntry {
        String task;
        int priority;
        int minutes;

        public TaskEntry(String task, int priority, int minutes){
            this.task=task; this.priority=priority; this.minutes=minutes;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eval_sheet);
        Intent intent = getIntent();
        int minutes = intent.getIntExtra("MINUTES_AMOUNT", 0);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_eval_sheet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void displayEvaluation(){
        fillInternalList();
    }

    public void fillInternalList(){
        TaskDBHelper helper = new TaskDBHelper(EvalSheet.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        Cursor cursor = sqlDB.query(TaskContract.TABLE,
                new String[]{TaskContract.Columns.TASK,TaskContract.Columns.PRIORITY,
                TaskContract.Columns.TIME},
                null,null,null,TaskContract.Columns.PRIORITY,null);


        if (cursor.moveToFirst() != false){
            for(int i=0; i<cursor.getCount(); i++){
                String task = cursor.getString(cursor.getColumnIndex(TaskContract.Columns.TASK));
                int priority = cursor.getInt(cursor.getColumnIndex(TaskContract.Columns.PRIORITY));
                int time = cursor.getInt(cursor.getColumnIndex(TaskContract.Columns.TIME));

                // add the retrieved information to the internal ArrayList instance.
                internal.add(i, new TaskEntry(task,priority,time));

                if(cursor.moveToNext() == false)
                    break;
            }
            cursor.close();
        }
    }

    public void sortInternalList(){
        if (internal.isEmpty())
                return;

        // ??? finish implementing this
    }
}
