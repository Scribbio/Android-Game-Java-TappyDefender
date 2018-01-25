package com.gamecodeschool.tappydefender;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This is the screen, containing the game's thread.
 *
 *
 * The android.view.SurfaceView class not only provides a view that is designed for drawing pixels,
 * text, lines, and sprites onto, but also enables quick handling of player input as well.
 */
public class TDView extends SurfaceView implements Runnable {

 /////////////////////////////////////////VARIABLES//////////////////////////////////////////////////////////////////////

    private SoundPool soundPool;
    int start = -1;
    int bump = -1;
    int destroyed = -1;
    int win = -1;

    //For saving the highscore to file
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;



    //volatile keyword as it will be accessed from outside the thread and from within. Build upon!
    volatile boolean playing;
    Thread gameThread = null;

    // Game objects
    private PlayerShip player;
    public EnemyShip enemy1;
    public EnemyShip enemy
    public EnemyShip enemy3;
    public EnemyShip enemy4;
    public EnemyShip enemy5;

    // For drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;

    // Make some random space dust
    public ArrayList<SpaceDust> dustList = new ArrayList<SpaceDust>();


    //member variables for our HUD (head-up display)
    private float distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private long fastestTime;
    private int screenX;
    private int screenY;

    private Context context;

    private boolean gameEnded;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public TDView(Context context, int x, int y) {

        //super calls the parent's (SurfaceView) constructor, the param "context" is complicated
        //A View type class relies on the context to get information about the app’s environment which is necessary to be able to construct the View object.
        //Consequently all of its constructors accept a context parameter.
        //The Context class is an “Interface to global information about an application environment".
        //In other words, the Context provides the answer to the components question of
        // where am I in relation to this app generally and how do I access/communicate with the rest of the app?”
        // a quick look at the methods exposed by the Context class provides some further clues about its true nature.
        // Here’s a random sampling of those methods:
        //getAssets()
        //getResources()
        //getPackageManager()
        //getString()
        //getSharedPrefsFile()

        super(context);
        this.context	= context;


        ///////////////////////INITIATING SOUNDS////////////////////////////
        // This SoundPool is deprecated (outdated API), but don't worry
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        try{
        //Create objects of the 2 required classes
        AssetManager assetManager = context.getAssets();
        AssetFileDescriptor descriptor;

        // Create our three fx in memory ready for use
        // Note that the call to load() starts a process of converting our .ogg files to raw sound data.
        // If the process is not finished when a call to playSound() is made, the sound won't play.
        descriptor = assetManager.openFd("assets/start.ogg");
        start = soundPool.load(descriptor, 0);
        descriptor = assetManager.openFd("assets/win.ogg");
        win = soundPool.load(descriptor, 0);
        descriptor = assetManager.openFd("assets/bump.ogg");
        bump = soundPool.load(descriptor, 0);
        descriptor = assetManager.openFd("assets/destroyed.ogg");
        destroyed = soundPool.load(descriptor, 0);
        }catch(IOException e){
            //Print an error message to the console
            Log.e("error", "failed to load sound files");
        }
        /////////////////////////////////////////////////////////////////////
        // Initialize our drawing objects
        ourHolder = getHolder();
        //The Paint class holds the style and color information about how to draw geometries, text and bitmaps
        paint = new Paint();

        //Screen dimensions
        screenX = x;
        screenY = y;
        ///////////////////////////////////////////////////////////////////////
        //Load previous high score
        // Get a reference to a file called HiScores.
        // If id doesn't exist one is created
        prefs = context.getSharedPreferences("HiScores", context.MODE_PRIVATE);
        //Initialize the editor ready
        editor = prefs.edit();
        //Load fastest time from a entry in the file labeled "fastestTime"
        //if not available highscore = 1000000
        fastestTime = prefs.getLong("fastestTime", 100);


        startGame();
        //Play intro sound
        soundPool.play(start, 1, 1, 0, 0, 1);


    }


    /**
     * This is the bare-bones of our game.
     *
     * The run method will execute in a thread,
     * but it will only execute the game loop while the Boolean playing instance is true.
     *
     * Then, it will update all the game data, draw the screen based on that game data,
     * and control how long it is until the run method is called again.
     *
     * These three "update", "draw" and "control" methods are found immediately below.
     */

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    private void startGame() {

        // Initialise our player ship
        player = new PlayerShip(context, screenX, screenY);

        //Initialise our enemy ships
        enemy1 = new EnemyShip(context, screenX, screenY);
        enemy2 = new EnemyShip(context, screenX, screenY);
        enemy3 = new EnemyShip(context, screenX, screenY);

        //These two "if" statements create 2 extra enemies depending on the screen size
        //It's a cheap and quick fix to address difficulty balancing on larger screens
        if(screenX > 1000){

            enemy4 = new EnemyShip(context, screenX, screenY);
        }

        if(screenX > 1200){
            enemy5 = new EnemyShip(context, screenX, screenY);
        }


        //Create the space dust
        int numSpecs = 200;
        for (int i = 0; i < numSpecs; i++) {
            //Where will the dust spawn?
            SpaceDust spec = new SpaceDust(screenX, screenY);
            dustList.add(spec);
        }

        //Reset time and distance
        distanceRemaining = 10000;// 10 km
        timeTaken = 0;
        //Get start time
        timeStarted = System.currentTimeMillis();

        gameEnded = false;
    }


    private void update(){

        /////////////////////////////////////////////////////////////////////////////////////
        //COLLISION DETECTION///////////////////////////////////////////////////////////////
        // We update this bef ore moving the player & enemies  because we are testing  the previous frames'
        // position which has just been drawn
        boolean hitDetected = false;
        if(Rect.intersects (player.getHitbox(), enemy1.getHitbox())){
            hitDetected = true;
            enemy1.setX(-200);
        }

        if(Rect.intersects(player.getHitbox(), enemy2.getHitbox())){
            hitDetected = true;
            enemy2.setX(-200);
        }

        if(Rect.intersects (player.getHitbox(), enemy3.getHitbox())){
            hitDetected = true;
            enemy3.setX(-200);
        }

        //Two extra collision detections if two extra enemies are used to balance difficult depending
        //on screen size, see constructor (and constructor of EnemyShip class )
        if(screenX > 1000){
            if(Rect.intersects(player.getHitbox(), enemy4.getHitbox())){
                hitDetected = true;
                enemy4.setX(-100);
            }
        }
        if(screenX > 1200){
            if(Rect.intersects(player.getHitbox(), enemy5.getHitbox())){
                hitDetected = true;
                enemy5.setX(-100);
            }
        }


        if(hitDetected) {
            player.reduceShieldStrength();
            soundPool.play(bump, 1, 1, 0, 0, 1);
            if (player.getShieldStrength() < 0) {
                soundPool.play(destroyed, 1, 1, 0, 0, 1);
                //game over
            }
        }
        /////////////////////////////////////////////////////////////////////////////////////

        // Update the player
        player.update();
        //Update the enemies
        enemy1.update(player.getSpeed());
        enemy2.update(player.getSpeed());
        enemy3.update(player.getSpeed());
        if(screenX > 1000) {
            enemy4.update(player.getSpeed());
        }
        if(screenX > 1200) {
            enemy5.update(player.getSpeed());
        }
        //update space dust
        for (SpaceDust sd : dustList) {
            sd.update(player.getSpeed());
        }


        if(!gameEnded) {
        //subtract distance to home planet based on current speed
            distanceRemaining -= player.getSpeed();
        //How long has the player been flying
            timeTaken = System.currentTimeMillis() - timeStarted;
        }

        ////////////Ending the game/////////////////////////////////////////////////////////////////
        //Player loses
        if(hitDetected) {

            player.reduceShieldStrength();
            if (player.getShieldStrength() < 0) {
                gameEnded = true;

            }

        }

        //Completed the game!
        if(distanceRemaining < 0){
            soundPool.play(win, 1, 1, 0, 0, 1);
            //check for new fastest time
            if(timeTaken < fastestTime) {
                fastestTime = timeTaken;
                //save high score
                editor.putLong("fastestTime", timeTaken);
                editor.commit();
                fastestTime = timeTaken;
            }
            // avoid ugly negative numbers in the HUD
            distanceRemaining = 0;
            //Now end the game
            gameEnded = true;
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
    }




    /**
     *The aptly named Canvas class provides just what you will expect—a virtual canvas to draw our graphics upon.

     *We can make a virtual canvas using the Canvas class and project it onto our SurfaceView object which is the view of your GameActivity class.
     *We can actually add Bitmap objects and even manipulate individual pixels on our Canvas object using methods from our Paint object. In addition, we also need an object of the SurfaceHolder class. This allows us to lock your Canvas object while we are manipulating it and unlock it when we are ready to draw the frame.
     *
     * lockCanvas() creates a surface area that you will write to. The reason it's called lockCanvas() is because until you call unlockCanvasAndPost()
     * no other code can call lockCanvas() and write to the surface until your code is finished.
     * In general, locks are important to understand, specifically when it relates to multithreaded programming.
     * lock is a synchronization primitive that is used to guard against simultaneous accessing of resources/code by multiple threads.
     * It gets it's name because it behaves much like a physical lock. Generally one thread can obtain a lock,
     * and until it releases the lock, no other thread can obtain the lock.
     * One potential gotcha to using a lock is that misuse of it can lead to a "dead lock" situation, where threads are left waiting for the lock, and it's never released.
     */
    private void draw(){

        if (ourHolder.getSurface().isValid()) {

            //First we lock the area of memory we will be drawing to
            //see explanation above too.
            canvas = ourHolder.lockCanvas();

            // Rub out the last frame ie. clear the screen
            //the "a" argb stands for alpha (Opacity) the other values are set to 0. So essentially, it creates a fully opaque blank screen,
            //0,0,0 is black
            canvas.drawColor(Color.argb(255, 0, 0, 0));


            //Draw the "space dust" (these needs to be drawn before other player and enemies so that it apepars behind.
            //color is white
            paint.setColor(Color.argb(255, 255, 255, 255));
            //Draw the dust from our arrayList
            for (SpaceDust sd : dustList) {
                canvas.drawPoint(sd.getX(), sd.getY(), paint);
            }


            // Draws the player and enemies
            canvas.drawBitmap(player.getBitmap(), player.getX(), player.getY(), paint);
            canvas.drawBitmap(enemy1.getBitmap(), enemy1.getX(), enemy1.getY(), paint);
            canvas.drawBitmap(enemy2.getBitmap(), enemy2.getX(), enemy2.getY(), paint);
            canvas.drawBitmap(enemy3.getBitmap(), enemy3.getX(), enemy3.getY(), paint);
            if(screenX > 1000) {
                canvas.drawBitmap(enemy4.getBitmap(),
                        enemy4.getX(), enemy4.getY(), paint);
            }
            if(screenX > 1200) {
                canvas.drawBitmap(enemy5.getBitmap(),
                        enemy5.getX(), enemy5.getY(), paint);
            }


            //Draw the HUD (scores on the screen) and keep it updated
            if(!gameEnded) {
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(25);
                canvas.drawText("Fastest:" + formatTime(fastestTime) + "s", 10, 20, paint);
                canvas.drawText("Time:" + formatTime(timeTaken) + "s", screenX / 2, 20, paint); //has a helper method (found below) to format the time.
                canvas.drawText("Distance:" + distanceRemaining / 1000 + "KM", screenX / 3, screenY - 20, paint);
                canvas.drawText("Shield:" + player.getShieldStrength(), 10, screenY - 20, paint);
                canvas.drawText("Speed:" + player.getSpeed() * 60 + "MPS", (screenX / 3) * 2, screenY - 20, paint);
            }else{
                //Show pause screen
                paint.setTextSize(80);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Game Over", screenX / 2, 100, paint); //note, we have adjusted text size and aligned to centre for the "GAME OVER".
                paint.setTextSize(25); canvas.drawText("Fastest:" + fastestTime + "s", screenX / 2, 160, paint);
                canvas.drawText("Time:" + formatTime(timeTaken) + "s", screenX / 2, 200, paint);
                canvas.drawText("Fastest:"+ formatTime(fastestTime) + "s", screenX/2, 160, paint);
                canvas.drawText("Distance remaining:" + distanceRemaining/1000 + " KM",screenX/2, 240, paint);
                paint.setTextSize(80);
                canvas.drawText("Tap to replay!", screenX/2, 350, paint);
            }


            // Unlock and draw the scene
            ourHolder.unlockCanvasAndPost(canvas);
        }

    }


    /**
     * Sixty frames per second (FPS) is a reasonable goal. This goal implies the need for timing.
     * The Android system measures time in milliseconds (thousandths of a second).
     * Therefore, we can add the following code to the control method:
     *
     * [ 1000ms/60 = 16.8 ] this code puts the thread to sleep for approx. 60 frames
     *
     * note: this is often a derided way of creating a loop
     */
    private void control(){
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
        }
    }

    //This method handles screen touches
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
       //motionEvent reacts to any contact at all of the screen
        //ACTION_MASK seperates the actual action and the pointer identifier.
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
        // Has the player lifted their finger up?
        case MotionEvent.ACTION_UP:
            player.stopBoosting();
            break;
        // Has the player touched the screen?
        case MotionEvent.ACTION_DOWN:
            player.setBoosting();
            //If we are currently on the pause screen, start a new game
            if(gameEnded){
            startGame();
        }
            break;
        }
        return true;
    }


    /**This is a helper method that makes the Time look a whole lot nicer.
     * Java's default for measuring time is unformated milliseconds (1000th of a second)
     *
     */
    public String formatTime(long time){

        long seconds = (time) / 1000;
        long thousandths = (time) - (seconds * 1000);
        String strThousandths = "" + thousandths;

        if (thousandths < 100){strThousandths = "0" + thousandths;}
        if (thousandths < 10){strThousandths = "0" + strThousandths;}

        String stringTime = "" + seconds + "." + strThousandths;
        return stringTime;
    }






    /**
     * Clean up our thread if the game is interrupted or the player quits
     * The join() method is used to hold the execution of the currently running thread until the specified thread is dead(finished execution).
     */
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {

        }
    }

    // Make a new thread and start it
    public void resume() {
        playing = true;
        gameThread = new Thread(this); //these run and draw methods
        gameThread.start();
    }


}