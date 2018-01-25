package com.gamecodeschool.tappydefender;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;

/**
 * Wwhen the player clicks on the Play button,
 * main activity will close and game activity will begin. Therefore,
 * we need this activity called GameActivitythat will be were your game actually executes.
 *
 * Remembering that Android Studio is structured around activities.
 */


public class GameActivity extends Activity {

    private TDView gameView;

    //This is where the "Play" button from HomeActivity sends us
    // @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();
        //Load the resolution into a Point object
        //The Point object can hold two coordinates and we then pass it as an argument into the getSize method of our new Display object.
        //We then  have the resolution of the Android device our game is running on, neatly stored in size.
        Point size = new Point();
        display.getSize(size);


        //Create an instance of our Tappy Defender View (TDView)
        //Also passing in "this" which is the Context of our app
        //size is the object we have created above
        gameView = new TDView(this, size.x, size.y);;

        //Make our gameView the view for the Activity

        setContentView(gameView);
    }

    //If the Activity is paused make sure to pause our thread
    // @Override
    protected void onPause() {
        super.onPause(); gameView.pause();
    }

    //If the Activity is resumed make sure to resume our thread
    // @Override
    protected void onResume() {
        super.onResume(); gameView.resume();
    }

    //If the player hits the back button, quit the app public boolean
    /
    onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }


}
