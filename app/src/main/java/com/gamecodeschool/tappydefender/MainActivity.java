package com.gamecodeschool.tappydefender;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity
        implements View.OnClickListener{

    // This is the entry point to our game
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView line that loads our UI layout from activity_main.xml to the players screen.
        setContentView(R.layout.activity_main);

        //	Prepare to load fastest time
        SharedPreferences prefs;
        SharedPreferences.Editor editor;
        prefs = getSharedPreferences("HiScores", MODE_PRIVATE);

        // Get a reference to the button in our layout
        final Button buttonPlay =
                (Button) findViewById(R.id.buttonPlay);

        //Get a reference to the TextView in our layout
        final TextView textFastestTime = (TextView)findViewById(R.id.textHighScore);

        // Listen for clicks
        buttonPlay.setOnClickListener(this);

        //load fastest time
        //if not available our high score = 1000000
        long fastestTime = prefs.getLong("fastestTime", 1000000);
        //Put the high score in our TextView
        textFastestTime.setText("Fastest Time:" + fastestTime);

    }


    /**
     * When we implement the onClickListener interface, we must also implement the onClick method.
     * This is where we will handle what happens when a button is clicked.
     */
    @Override
    public void onClick(View v) {
        // As we only have one Button object and one listener, we can safely assume that any clicks on our home screen are the player pressing our Play button.

        // Create a new Intent object
        // Android uses the Intent class to switch between activities.
        // As we need to go to a new activity when the Play button is clicked, this takes us to GameActivity class' constructor
        Intent i = new Intent(this, GameActivity.class);
        // Start our GameActivity class via the Intent
        startActivity(i);
        finish();
    }

    //If the player hits the back button, quit the app public boolean

    onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }



}
