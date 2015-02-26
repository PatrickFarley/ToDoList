package edu.wheaton.patrickfarley.todolist.db;

import android.provider.BaseColumns;

/*
 * Created by patrickfarley on 2/20/15. A simple class for storing public constants related to the
 * database
 */
public class TaskContract {
    public static final String DB_NAME = "edu.wheaton.patrickfarley.todolist.db.tasks";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "tasks";

    public class Columns {
        public static final String TASK = "task";
        public static final String _ID = BaseColumns._ID;
    }
}
