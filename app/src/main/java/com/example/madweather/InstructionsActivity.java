package com.example.madweather;

import android.app.Activity;
import android.os.Bundle;

/**
 * The activity for the instructions page.
 */
public class InstructionsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle("Instructions");
        setContentView(R.layout.activity_instructions);
    }
}
