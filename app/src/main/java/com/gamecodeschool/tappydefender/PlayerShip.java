package com.gamecodeschool.tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * This is our main sprite.
 *
 * It needs to
 * 1. Know where it is on the screen
 * 2. What it looks like
 * 3. How fast it is flying
 */

public class PlayerShip {

    //Bitmap will hold the graphic which represents the ship - it's a container for an image
    private Bitmap bitmap;
    //the ships location
    private int x, y;
    //the ship's speed
    private int speed;
    //the spaceship needs to know if it is boosting or not boosting
    private boolean boosting;

    private final int GRAVITY = -12;

    //Stop ship leaving the screen
    private int maxY;
    private int minY;

    //Limit the bounds of the ship's speed
    private final int MIN_SPEED = 1;
    private final int MAX_SPEED = 20;

    //A hit box for collision detection
    private Rect hitBox;

    private int shieldStrength;

    // Constructor
    public PlayerShip(Context context, int screenX, int screenY) {
        x = 50;
        y = 50;
        speed = 1;
        //To load bitmaps, we require an Android Context object.
        //The BitmapFactory class creates bitmap (image) objects from a selected source.
        // It uses its static method decodeResource()
        // to attempt to load our graphic of the player ship.
        // It requires two parameters. The first is the getResources method
        // supplied by the Context object that was passed from the view.
        // The second parameter R.drawable.ship is requesting a graphic
        // called ship from the (R)esource folder named drawable.
        //(context, as you imagine, tells the method that there are resources and where those are.
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship);
        boosting = false;
        maxY = screenY - bitmap.getHeight();
        minY = 0;

        // Initialize the hit box
        hitBox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());

        shieldStrength = 10;
    }

    public void update() {
       //Are we boosting?
       if (boosting) {
            //Speed up
            speed += 2;
        } else {
            //Slow down
            speed -= 5;
        }

        //Constrain top speed
        if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        }

        //Never stop completely
        if (speed < MIN_SPEED) {
            speed = MIN_SPEED;
        }

        // the ship up or down
        y -= speed + GRAVITY;

       // But don't let ship stray off screen

        if (y < minY) {
        y = minY;
        }

        if (y > maxY) {
        y = maxY;
        }

        //Refresh hit box location and make sure it is kept up-to-date with the coordinates of the ship.
        //This code must go at the very end of the update() method so that the hitbox is updated with the coordinates after the update methods have done their adjustments.
        //bitmap is the ship's image, to get the left and bottom you take x and y (always the top left corner in Java) and add the dimensions of the image
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();

}


    public void reduceShieldStrength(){
        shieldStrength--;
    }


//////////Getters////////////////////////////////////////
    public Bitmap getBitmap() {

        return bitmap;
    }

    public int getSpeed() {

        return speed;
    }

    public int getX() {

        return x;
    }

    public int getY() {

        return y;
    }

    public void setBoosting() {

        boosting = true;
    }

    public void stopBoosting() {

        boosting = false;
    }

    public Rect getHitbox(){

        return hitBox;
    }

    public int getShieldStrength() {

        return shieldStrength;
    }

}
