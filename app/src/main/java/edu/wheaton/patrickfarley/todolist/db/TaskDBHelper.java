package edu.wheaton.patrickfarley.todolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
 * Created by patrickfarley on 2/20/15.
 * Pass in an sqlite database object to onCreate and this class will open the database with its
 * superclass' getWritableDatabase command
 */
public class TaskDBHelper extends SQLiteOpenHelper {

    // takes a context upon construction, and relises on SQLiteOpenHelper constructor
    public TaskDBHelper(Context context) {
        super(context, TaskContract.DB_NAME, null, TaskContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqlDB) {
        // creates table in this given database
        String sqlQuery =
                "CREATE TABLE " + TaskContract.TABLE + " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + TaskContract.Columns.TASK + " TEXT NOT NULL, "
                        + TaskContract.Columns.PRIORITY + " INTEGER NOT NULL, "
                        + TaskContract.Columns.TIME + " INTEGER NOT NULL);";
        Log.d("TaskDBHelper", "Query to form table:" + sqlQuery);

        // send the SQL command
        sqlDB.execSQL(sqlQuery);
    }

    // ??? may need to change this in the future. don't want table being deleted if out-of-date.
    @Override
    public void onUpgrade(SQLiteDatabase sqlDB, int i, int i2){
        sqlDB.execSQL("DROP TABLE IF EXISTS "+TaskContract.TABLE);
        onCreate(sqlDB);
    }
}
