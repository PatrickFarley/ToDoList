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
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import edu.wheaton.patrickfarley.todolist.db.TaskContract;
import edu.wheaton.patrickfarley.todolist.db.TaskDBHelper;
import edu.wheaton.patrickfarley.todolist.evaluation.EvalSheet;


// the MainActivity is a ListActivity
public class MainActivity extends ListActivity {

    private TaskDBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // use helper to reference the database:
        helper = new TaskDBHelper(this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();

        // query a column in the database (this is only for logging purposes)
        Cursor cursor = sqlDB.query(TaskContract.TABLE, new String[]{TaskContract.Columns.TASK},
                null,null,null,null,null);
        cursor.moveToFirst();
        while(cursor.moveToNext()) {
            Log.d("MainActivity cursor",cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.Columns.TASK)));
        }
        // write from the database to the user interface:
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

            // if the 'add' icon is clicked:
            case R.id.action_add_task:
                Log.d("MainActivity", "Add a new task");

                // use an AlertDialog Builder object
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("add a task");

                // construct a View with the new_task_entry layout
                final LayoutInflater inflater = getLayoutInflater();
                final View newView = inflater.inflate(R.layout.new_task_entry, null);


                EditText taskEntry = (EditText)newView.findViewById(R.id.taskEntry);
                taskEntry.setHint("new task");

                taskEntry.requestFocus();

                taskEntry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(!hasFocus) {
                            InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }
                });



                NumberPicker priorityEntry = (NumberPicker)newView.findViewById(R.id.priorityEntry);
                numberPickerInit(priorityEntry,Const.MIN_PRIO,Const.MAX_PRIO,Const.PRIO_INCREMENT);
                priorityEntry.setValue(Const.PRIO_DEFAULT);
                NumberPicker timeEntry = (NumberPicker)newView.findViewById(R.id.timeEntry);
                numberPickerInit(timeEntry, Const.MIN_TIME, Const.MAX_TIME, Const.TIME_INCREMENT);
                timeEntry.setValue((Const.TIME_DEFAULT - Const.MIN_TIME)/Const.TIME_INCREMENT +1);


                // pass the same view into the alertDialog, for the user to ineract with
                builder.setView(newView);

                // set "add" button action
                builder.setPositiveButton("add",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i){

                        // reference the entered data and store it in variables:
                        final EditText taskField = (EditText) newView.findViewById(R.id.taskEntry);
                        final NumberPicker priorityField = (NumberPicker) newView.findViewById(R.id.priorityEntry);
                        final NumberPicker timeField = (NumberPicker) newView.findViewById(R.id.timeEntry);

                        String task;
                        int priority;
                        int time;

                        try {
                            task = taskField.getText().toString();
                            priority = Const.MIN_PRIO + ((priorityField.getValue())-1)*Const.PRIO_INCREMENT;
                            time = Const.MIN_TIME + ((timeField.getValue())-1)*Const.TIME_INCREMENT;
                        } catch (NumberFormatException e) {
                            Context context = getApplicationContext();
                            CharSequence toastMsg = "Enter a positive integer for priority and time";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, toastMsg, duration);
                            toast.show();
                            return;
                        }

                        // construct a TaskDBHelper object here
                        helper = new TaskDBHelper(MainActivity.this);

                        // instantiate the database: this automatically references MainActivity's
                        // innate database
                        SQLiteDatabase db = helper.getWritableDatabase();

                        // make a ContentValues object
                        ContentValues values = new ContentValues();
                        values.clear();

                        // put our info into the appropriate columns of the ContentValues object
                        values.put(TaskContract.Columns.TASK,task);
                        values.put(TaskContract.Columns.PRIORITY,priority);
                        values.put(TaskContract.Columns.TIME,time);

                        // insert the colums from the ContentValues object into the referenced
                        // database table.
                        db.insertWithOnConflict(TaskContract.TABLE,null,values, SQLiteDatabase.CONFLICT_IGNORE);

                        // update the UI from the database

                        updateUI();
                    }
                });

                // set "Cancel" button action
                builder.setNegativeButton("Cancel",null);

                //show keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

                // create and display the Alert Dialog
                builder.create().show();
                return true;
            default:
                return false;
        }
    }

    /**
     * Opens the apps database, reads certain data, constructs a listAdapter,
     * and sets it as this activity's listAdapter for the list view
     *
     */
    private void updateUI() {
        Log.i("updateUI", "updateUI called");
        // open the database and get a cursor over the desired fields
        helper = new TaskDBHelper(MainActivity.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        Cursor cursor = sqlDB.query(TaskContract.TABLE,
                new String[]{TaskContract.Columns._ID, TaskContract.Columns.TASK,
                        TaskContract.Columns.PRIORITY, TaskContract.Columns.TIME},
                null, null, null, null, null);


        // construct this activity's ListAdapter: we must pass in the task_view layout, as well as the location
        // of the data to be presented.
        SimpleCursorAdapter taskListAdapter = new SimpleCursorAdapter(
                this, // context
                R.layout.task_view, // the layout template to use to present each data
                cursor, // pass in the cursor to bind to.
                new String[]{TaskContract.Columns.TASK,
                        TaskContract.Columns.PRIORITY,
                        TaskContract.Columns.TIME}, // array of items to bind
                new int[]{R.id.taskField,
                        R.id.priorityField,
                        R.id.timeField},// parallel corresponding array of layout object to bind data to
                0) {

            // We override the getView method so that we can make changes to all task_views and their
            // child views as they are being created/filled by the SimpleCursorAdapter.
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);


                // store the original database row id
                long viewId = getItemId(position);

                // we tag the view with its row id so this data is never lost
                view.setTag(viewId);


                // myEditAlertListener is assigned to the task, time and priority views as their
                // OnClickListeners (passing in the db row id)
                View.OnClickListener myEditAlertListener = new EditAlertListener(viewId) ;
                view.findViewById(R.id.taskField).setOnClickListener(myEditAlertListener);
                view.findViewById(R.id.priorityField).setOnClickListener(myEditAlertListener);
                view.findViewById(R.id.timeField).setOnClickListener(myEditAlertListener);

                // getView method must return the original view (the task_view)
                return view;
            }
        };

        // MainActivity displays a ListView and uses a ListAdapter to bind the data to the view.
        // We pass in the taskListAdapter, defined above
        this.setListAdapter(taskListAdapter);

    }




    /**
     * when 'Done' button is clicked
     * removes the corresponding entry from the database, based on the name of the task
     * being removed.
     *
     * @param view
     */
    public void onDoneButtonClick(View view) {
        // get a ref to the parent view of the calling button (get the listView)
        View parent = (View) view.getParent();

        // get this task_view's original db row id (stored as tag)
        long viewId = (long)parent.getTag();

        // write an SQL command to delete said row from the table by matching its row id
        String sql = String.format("DELETE FROM %s WHERE %s = %d",
                TaskContract.TABLE,
                TaskContract.Columns._ID,
                viewId);

        // construct a db helper with MainActivity's database, and use it to return a reference
        // to said database
        helper = new TaskDBHelper(MainActivity.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();

        // execute the command in the database
        sqlDB.execSQL(sql);

        // finally, update the UI to the database, which is now missing the previous entry.
        updateUI();
    }

    /**
     * when 'Evaluate' button is clicked:
     * reads from the adjacent EditText field and constructs an intent to start the
     * EvalSheet activity.
     * @param view
     */
    public void onEvaluate(View view) {
        Log.d("MainActivity","onEvaluate has been called");
        Intent intent = new Intent(this, EvalSheet.class);
        EditText editText = (EditText)findViewById(R.id.evalEntField);
        try {
            int minutes = Integer.parseInt(editText.getText().toString());
            intent.putExtra("MINUTES_AMOUNT",minutes);
            startActivity(intent);
        } catch (NumberFormatException e) {
            Context context = getApplicationContext();
            CharSequence toastMsg = "Enter a positive integer here";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, toastMsg, duration);
            toast.show();
            return;
        }

    }

    /**
     * This non-static inner class is used as the onClickListener for the view objects that make up the
     * task info on the listView (the name, priority, and time fields). It presents an Alert Dialog
     * that allows the user to change any of these 3 fields. Upon hitting "enter", the changes will be
     * saved to the appropriate row of the "tasks" table in this application's database.
     */
    private class EditAlertListener implements View.OnClickListener {

        long viewId;

        // Constructor: the row id of the calling view is passed in.
        public EditAlertListener(long viewId){
            this.viewId = viewId;
        }


        public void onClick(View v) {

            Log.i("MainActivity","Edit Item");

            // reference the parent view (which is a task_view object)
            View parent =(View) v.getParent();

            // create an AlertDialog Builder object
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Edit Item");

            // inflate a View called startView with the new_task_entry layout
            // (this is the view the user will interact with)
            final LayoutInflater inflater = getLayoutInflater();
            final View startView = inflater.inflate(R.layout.new_task_entry, null);


            // Fill in the existing information for this task (from the 3 fields):

            // get references to the 3 fields on this view:
            TextView taskField = (TextView) parent.findViewById(R.id.taskField);
            TextView priorityField = (TextView) parent.findViewById(R.id.priorityField);
            TextView timeField = (TextView) parent.findViewById(R.id.timeField);

            // assign the text from the 3 fields to string variables
            String oldName = taskField.getText().toString();
            String oldPriority = priorityField.getText().toString();
            String oldTime = timeField.getText().toString();

            // get references to the corresponding 3 views on this AlertDialog's view:
            EditText startName = (EditText) startView.findViewById(R.id.taskEntry);


            NumberPicker startPriority = (NumberPicker) startView.findViewById(R.id.priorityEntry);
            numberPickerInit(startPriority,Const.MIN_PRIO,Const.MAX_PRIO,Const.PRIO_INCREMENT);
            NumberPicker startTime = (NumberPicker) startView.findViewById(R.id.timeEntry);
            numberPickerInit(startTime, Const.MIN_TIME, Const.MAX_TIME, Const.TIME_INCREMENT);

            // set the text in this AlertDialog's 3 views to the corresponding fields for this task:
            startName.setText(oldName);
            startPriority.setValue(1+(Integer.parseInt(oldPriority)- Const.MIN_PRIO)/ Const.PRIO_INCREMENT);
            startTime.setValue(1+(Integer.parseInt(oldTime)-Const.MIN_TIME)/Const.TIME_INCREMENT);

            // set the view to the alertDialog
            builder.setView(startView);

            // set "enter" button action
            builder.setPositiveButton("enter", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialogInterface, int i) {

                    // reference the entered data and store it in variables:

                    final EditText newName = (EditText) startView.findViewById(R.id.taskEntry);
                    final NumberPicker newPriority = (NumberPicker) startView.findViewById(R.id.priorityEntry);
                    final NumberPicker newTime = (NumberPicker) startView.findViewById(R.id.timeEntry);

                    String task;
                    int priority;
                    int time;

                    try {
                        task = newName.getText().toString();
                        priority = Const.MIN_PRIO + ((newPriority.getValue())-1)*Const.PRIO_INCREMENT;
                        time = Const.MIN_TIME + ((newTime.getValue())-1)*Const.TIME_INCREMENT;
                    } catch (NumberFormatException e) {
                        Context context = getApplicationContext();
                        CharSequence toastMsg = "Enter a positive integer for priority and time";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, toastMsg, duration);
                        toast.show();
                        return;
                    }

                    // construct a TaskDBHelper object here
                    helper = new TaskDBHelper(MainActivity.this);

                    // reference the database
                    SQLiteDatabase db = helper.getWritableDatabase();

                    // make a blank ContentValues object
                    ContentValues values = new ContentValues();
                    values.clear();

                    // put our entered data into the appropriate columns of the ContentValues object
                    values.put(TaskContract.Columns.TASK, task);
                    values.put(TaskContract.Columns.PRIORITY, priority);
                    values.put(TaskContract.Columns.TIME, time);

                    // update the specified row in the referenced database table with the
                    // data from the ContentValues object
                    db.update(TaskContract.TABLE, values, TaskContract.Columns._ID + " = " + viewId, null);

                    // update the UI from the database
                    updateUI();
                }
            });
            // set "Cancel" button action
            builder.setNegativeButton("Cancel",null);
            // create and display the Alert Dialog
            builder.create().show();
        }
    }

    /**
     * a helper method.. for NumberPicker objects
     */

    public void numberPickerInit(NumberPicker numberPicker, int min, int max, int increment) {


        int n = (max/increment - min/increment +1);
        String[] values = new String[n];
        for(int i = 0; i<n; i++){
            values[i] = Integer.toString(i*increment + min);
            System.out.println(values[i]);
        }

        numberPicker.setMaxValue(max / increment);
        numberPicker.setMinValue(min / increment);
        numberPicker.setDisplayedValues(values);
       // numberPicker.setMinValue(min);
       // numberPicker.setMaxValue(max);
        System.out.println(numberPicker.getMinValue()+"");
        System.out.println(numberPicker.getMaxValue()+"");

        numberPicker.setWrapSelectorWheel(false);
    }

    /**
     *
     */



    /**
     * custom class...
     */
    public class MyNumberPicker extends NumberPicker {
        private int min, max, increment;

        public MyNumberPicker(Context context,AttributeSet attrs){
            super(context,attrs);
        }

        public void initValues(int min, int max, int increment){
            this.min = min;
            this.max = max;
            this.increment = increment;

            int n = (max/increment - min/increment +1);
            String[] values = new String[n];
            for(int i = 0; i<n; i++){
                values[i] = Integer.toString(i*increment + min);
                System.out.println(values[i]);
            }

            setMaxValue(max / increment);
            setMinValue(min / increment);
            setDisplayedValues(values);
        }

        @Override
        public void setValue(int value){
            super.setValue(value * increment + min);
        }
    }

}
