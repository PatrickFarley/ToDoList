package edu.wheaton.patrickfarley.todolist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

/**
 * Created by patrickfarley on 5/5/15.
 */
public class MyEditText extends EditText {
    public MyEditText(Context context) {
        super(context);
        //init(context);
    }
    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        //init(context);
    }
    public MyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //init(context);
    }
/*
    private void init(Context context) {
        final Context ref = context;
        setOnKeyListener( new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    MainActivity.onTaskEdit;
                    return true;
                }
                return false;
            }

        });
    }
    */


}
