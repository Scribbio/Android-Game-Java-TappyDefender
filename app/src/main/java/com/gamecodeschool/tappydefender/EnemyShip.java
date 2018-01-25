package com.gamecodeschool.tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by Joe on 30/10/2017.
 */
public class EnemyShip {

    private Bitmap bitmap;
    private int x, y;
    private int speed = 1;

    //Detect enemies leaving the screen
    private int maxX;
    private int minX;

    //Spawn enemies within screen bounds

    private int maxY;
    private int minY;

    //A hit box for collision detection
    //Rect holds four integer coordinates for a rectangle. The rectangle is represented by the coordinates of its 4 edges (left, top, right bottom).
    // These fields can be accessed directly. Use width() and height() to retrieve the rectangle's width and height.
    private Rect hitBox;


// Constructor

    public EnemyShip(Context context, int screenX, int screenY){
        //Choose an random enemy from agmong our images:
        Random generator = new Random();
        int whichBitmap = generator.nextInt(3);

        switch (whichBitmap){

            case 0:

                bitmap = BitmapFactory.decodeResource (context.getResources(), R.drawable.enemy3);
                break;

            case 1:

                bitmap = BitmapFactory.decodeResource (context.getResources(), R.drawable.enemy2);
                break;

            case 2:

                bitmap = BitmapFactory.decodeResource (context.getResources(), R.drawable.enemy);
                break;
        }

        //see it's method below, very cheap and quick code to balance the game on different resolutions
        scaleBitmap(screenX);

        //Although the enemies only move horizontally, we need the maxY and minY coordinates to make sure that we spawn them at a sensible height.
        //The maxX coordinate will enable us to spawn them just off-screen horizontally.
        maxX = screenX;
        maxY = screenY;

        minX = 0;
        minY = 0;


        speed = generator.nextInt(6)+10;

        //Set the coordinates for our enemey ship
        //bitmap is the image of the enemy ship itself, (remember an images is always draw from the top left)
        x = screenX;
        y = generator.nextInt(maxY) - bitmap.getHeight();

        // Initialize the hit box
        hitBox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());

    }

    //scales enemies down in size on lower resolution screens, cheap difficulty balance code for screens with smaller/larger sizes
    public void scaleBitmap(int x){

        if(x < 1000) {

            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 3, bitmap.getHeight() / 3, false);

        }else if(x < 1200){
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);

        }
    }



    public void update(int playerSpeed){

            //Move to the left
            //we first decreased the enemy's x coordinate by the player's speed then by the enemy's speed.
            //As the player boosts, the enemy will fly at the player faster.
            //However, if the player is not boosting then the enemy will attack at the speed that was previously and randomly generated -
            //This giver the impression that the player is going faster!
            x -= playerSpeed;
            x -= speed;

            //respawn when off screen
            if(x < minX-bitmap.getWidth()){
                //Then we respawn the very same object to come at the player again.
                // This appears to the player as if it is an entirely new enemy.
                Random generator = new Random();
                speed = generator.nextInt(10)+10;
                x = maxX;
                y = generator.nextInt(maxY) - bitmap.getHeight();
            }


            //Refresh hit box location and make sure it is kept up-to-date with the coordinates of the ship.
            //This code must go at the very end of the update() method so that the hitbox is updated with the coordinates after the update methods have done their adjustments.
            //bitmap is the ship's image, to get the left and bottom you take x and y (always the top left corner in Java) and add the dimensions of the image
            hitBox.left = x;
            hitBox.top = y;
            hitBox.right = x + bitmap.getWidth();
            hitBox.bottom = y + bitmap.getHeight();
        }

   //Getters and Setters

    public Bitmap getBitmap(){
        return bitmap;
    }

    public int getX() {

        return x;
    }

    public int getY() {

        return y;
    }

    public Rect getHitbox(){

        return hitBox;
    }

    //This is used by the TDView update() method to
    //Make an enemy out of bounds and force a re-spawn

    public void setX(int x) {
        this.x = x;
    }


}

