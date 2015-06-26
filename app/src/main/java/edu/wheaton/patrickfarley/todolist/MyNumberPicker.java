package edu.wheaton.patrickfarley.todolist;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/**
 * Created by patrickfarley on 6/25/15.
 */
public class MyNumberPicker extends android.widget.NumberPicker {

    private EditText eText;

    public MyNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void addView(View child) {
        super.addView(child);
        initEditText(child);
    }

    @Override
    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        initEditText(child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        initEditText(child);
    }

    public void initEditText(View view){
        if(view instanceof EditText){
            EditText eText = (EditText) view;
            eText.addTextChangedListener(new TextWatcher(){

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1,
                                              int arg2, int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try{
                        int num = Integer.parseInt(s.toString());
                        setValue(num); //this line changes the value of your numberpicker
                    }catch(NumberFormatException E){
                        //do your catching here
                    }
                }});
        }
    }
}
