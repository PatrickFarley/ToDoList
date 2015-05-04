package edu.wheaton.patrickfarley.todolist.evaluation;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.wheaton.patrickfarley.todolist.R;
import edu.wheaton.patrickfarley.todolist.db.TaskContract;
import edu.wheaton.patrickfarley.todolist.db.TaskDBHelper;

// this class is a ListActivity.
public class EvalSheet extends ListActivity {

    private List<TaskItem> taskList = new ArrayList<TaskItem>();  // list of task items read from db
    private List<String> evalList = new ArrayList<String>();    // list of task suggestion strings
    private int[] assignedTimes = new int[taskList.size()];// array for storing the time allocated to each task
    private int minutes;                                   // amount of time to allocate (passed in via Intent)
    private TaskDBHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eval_sheet);
        Intent intent = getIntent();
        // assign the 'minutes' variable
        minutes = intent.getIntExtra("MINUTES_AMOUNT", 0);

        // fill the 'taskList' object from db.
        fillInternalList();

        // 'taskList' is filled. Now sort it by priority.
        prioritySort();

        // 'taskList' is sorted. Make a list of suggestion strings for the answer,
        // and make an ArrayAdapter out of this list
        // note the call to makeEvalList():
        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.eval_task_view, R.id.taskField, makeEvalList(minutes));

        // set this ArrayAdapter as the ListAdapter for this activity (show on the UI). This should
        // only be done once per EvalSheet activity.
        this.setListAdapter(adapter);
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


    /**
     * This method fills an internal arrayList of taskItem objects, from a table in the app's
     * database
     */
    public void fillInternalList(){
        helper = new TaskDBHelper(EvalSheet.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        Cursor cursor = sqlDB.query(TaskContract.TABLE,
                new String[]{TaskContract.Columns._ID, TaskContract.Columns.TASK,
                        TaskContract.Columns.PRIORITY, TaskContract.Columns.TIME},
                null,null,null,null,null);


        if (cursor.moveToFirst() != false){
            // for each row returned by the sql cursor:
            for(int i=0; i<cursor.getCount(); i++){
                String task = cursor.getString(cursor.getColumnIndex(TaskContract.Columns.TASK));
                int priority = cursor.getInt(cursor.getColumnIndex(TaskContract.Columns.PRIORITY));
                int time = cursor.getInt(cursor.getColumnIndex(TaskContract.Columns.TIME));

                // add the retrieved information to the internal ArrayList instance.
                taskList.add(i, new TaskItem(task,priority,time));

                if(cursor.moveToNext() == false)
                    break;
            }
            cursor.close();
        }
    }

    /**
     * This method fills and returns a list of evaluation strings
     * @param free_time
     * @return
     */
    private List<String> makeEvalList(int free_time) {

        // take the whole list, and set their calculated worths to zero.
        for (int i = 0; i < taskList.size(); i++) {
            taskList.get(i).updateNextWorth(0);
        }

        // initialize the assignedTimes array
        int[] assignedTimes = new int[taskList.size()];

        while (free_time > 0) {
            int bestIndex = -1;
            double bestWorth = 0;

            for (int i = 0; i < taskList.size(); i++) {
                if (taskList.get(i).calcWorth(assignedTimes[i] + 1) > bestWorth) {
                    bestIndex = i;
                    bestWorth = taskList.get(i).calcWorth(assignedTimes[i] + 1);
                    assignedTimes[i]++;
                    free_time--;
                }
            }
            if (bestIndex == -1) {
                break;
            }

        }
		/* at this point, the assignedTimes array is filled out. some tasks still have zero
		 * assigned time and we should ignore these. We must add all the tasks which
		 * do have assigned times to evalList.
		 */

        List<String> evalList = new ArrayList<String>();
        for (int i = 0; i < assignedTimes.length; i++) {
            if (assignedTimes[i] > 0)
                evalList.add("do " + taskList.get(i).label + " for " +
                        assignedTimes[i] + " minutes");
        }

        return evalList;
    }

    /**
     * sort taskList by priority
     */
    public void prioritySort() {


        List<TaskItem> list = this.taskList;

        Collections.sort(taskList, new Comparator<TaskItem>() {
            @Override
            public int compare(TaskItem t1, TaskItem t2) {

                if (t1.priority == t2.priority){
                    // look at the minutes instead:
                    return t1.minutes - t2.minutes;
                } else {
                    return t1.priority - t2.priority; // Ascending
                }
            }

        });

    }

}
