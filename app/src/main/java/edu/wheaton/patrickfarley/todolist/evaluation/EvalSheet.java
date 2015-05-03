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
import java.util.List;

import edu.wheaton.patrickfarley.todolist.R;
import edu.wheaton.patrickfarley.todolist.db.TaskContract;
import edu.wheaton.patrickfarley.todolist.db.TaskDBHelper;

//??? this class should extend listView. Make an array of task objects,
// use an adapter, and send to the listView.
public class EvalSheet extends ListActivity {

    List<TaskItem> taskList = new ArrayList<TaskItem>();
    List<String> evalList = new ArrayList<String>();
    int[] assignedTimes = new int[taskList.size()];
    int minutes;
    private TaskDBHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eval_sheet);
        Intent intent = getIntent();
        minutes = intent.getIntExtra("MINUTES_AMOUNT", 0);

        /*
        helper = new TaskDBHelper(EvalSheet.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        Cursor cursor = sqlDB.query(TaskContract.TABLE,
                new String[]{TaskContract.Columns._ID, TaskContract.Columns.TASK,
                        TaskContract.Columns.PRIORITY, TaskContract.Columns.TIME},
                null,null,null,null,null);

        SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(
                this, // context
                R.layout.eval_task_view, // the layout template to use to present each data
                cursor, // pass in cursor to bind to.
                new String[] {TaskContract.Columns.TASK}, // array of items to bind
                new int[] {R.id.taskField},// parallel array of layout object to bind data to
                0);

        */
        // HERE make the listAdapter object for display.

        fillInternalList();
        // now tasklist is filled. Make a final list of strings for the answer:



        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.eval_task_view, R.id.taskField, makeEvalList(minutes));
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



    public void fillInternalList(){
        helper = new TaskDBHelper(EvalSheet.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        Cursor cursor = sqlDB.query(TaskContract.TABLE,
                new String[]{TaskContract.Columns._ID, TaskContract.Columns.TASK,
                        TaskContract.Columns.PRIORITY, TaskContract.Columns.TIME},
                null,null,null,null,null);

        /*
        (TaskContract.TABLE,
                new String[]{TaskContract.Columns.TASK,TaskContract.Columns.PRIORITY,
                TaskContract.Columns.TIME},
                null,null,null,TaskContract.Columns.PRIORITY,null)
         */


        if (cursor.moveToFirst() != false){
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
     * New method for making the list of evaluation strings
     * @param free_time
     * @return
     */
    private List<String> makeEvalList(int free_time) {
        // take the whole list, and set their calculated worths to zero.
        for (int i = 0; i < taskList.size(); i++) {
            taskList.get(i).updateNextWorth(0);
        }

        int[] assignedTimes = new int[taskList.size()];

        // set all the nextWorths first:

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
		 * assigned time and we should ignore these as best we can. We must print* out the tasks which
		 * do have assigned times.
		 */

        List<String> evalList = new ArrayList<String>();
        for (int i = 0; i < assignedTimes.length; i++) {
            if (assignedTimes[i] > 0)
                evalList.add("do " + taskList.get(i).label + " for " +
                        assignedTimes[i] + " minutes");
        }

        return evalList;
    }


    public void sortInternalList(int free_time){
        if (taskList.isEmpty())
                return;

        // fill out the assignedTimes array.
        while (free_time > 0){
            int bestIndex = -1;
            double bestWorth =  0;

            for (int i = 0; i<taskList.size(); i++){
                if (taskList.get(i).calcWorth(assignedTimes[i]+1) > bestWorth){
                    bestIndex = i;
                    bestWorth = taskList.get(i).calcWorth(assignedTimes[i]+1);
                    assignedTimes[i]++;
                    free_time--;
                }
            }
            if (bestIndex == -1){
                break;
            }

        }
        // at this point, the assignedTimes array has been filled out appropriately. now we
        // must iterate through it, posting (in order of priority) the taskList items and their
        // assigned minutes. Best way to post an array?

    }
}