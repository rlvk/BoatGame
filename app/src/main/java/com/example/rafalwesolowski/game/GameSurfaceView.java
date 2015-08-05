package com.example.rafalwesolowski.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by rafalwesolowski on 24/07/15.
 */
public class GameSurfaceView extends SurfaceView implements Runnable {

    private MainActivity mainActivity;

    Thread gameThread = null;
    Timer timer = null;

    SurfaceHolder ourHolder;

    volatile boolean playing;

    Canvas canvas;
    Paint paint;

    Bitmap boatBitmap;
    Bitmap background;
    Bitmap mine;
    Bitmap bulletBitmap;

    // Bob starts off not moving
    boolean isMoving = false;

    private Boat boat;

    float mineYPosition = 0.0f;

    private int boatWidth = 0, boatHeight = 0;

    boolean isMovingRight = false;

    private ArrayList<Enemy> enemies;
    private ArrayList<Bullet> shoots;

    private int delayBetweenEnemies = 1000;

    // When the we initialize (call new()) on gameView
    // This special constructor method runs
    public GameSurfaceView(Context context) {
        super(context);
        mainActivity = (MainActivity)context;

        ourHolder = getHolder();
        paint = new Paint();

        enemies = new ArrayList<Enemy>();
        shoots = new ArrayList<Bullet>();

        boatBitmap = getBoatBitmap();

        boat = new Boat(getWidth() / 2, mainActivity.screenHeight - (boatBitmap.getHeight() + 50), getContext());

        Bitmap backgroundBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.background);
        background = Bitmap.createScaledBitmap(backgroundBitmap, mainActivity.screenWidth, mainActivity.screenHeight, true);

        mine = BitmapFactory.decodeResource(this.getResources(), R.drawable.mine);

        bulletBitmap = getBulletBitmap();

        playing = true;
    }

    private Bitmap getBulletBitmap() {
        Bitmap bullet = BitmapFactory.decodeResource(this.getResources(), R.drawable.bullet);
        Matrix matrix = new Matrix();
        matrix.setRotate(-90);
        return Bitmap.createBitmap(bullet, 0, 0, bullet.getWidth(), bullet.getHeight(), matrix, false);
    }

    private Bitmap getBoatBitmap() {
        Bitmap boat = BitmapFactory.decodeResource(this.getResources(), R.drawable.boat);

        boatWidth = boat.getWidth();
        boatHeight = boat.getHeight();

        // Need to rotate bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(-90);
        return Bitmap.createBitmap(boat, 0, 0, boatWidth, boatHeight, matrix, false);
    }

    @Override
    public void run() {
        while (playing) {
            // Update the frame
            update();
            // Draw the frame
            draw();
        }
    }

    public void update() {

        // Check for collision
        for (int index = 0; index < enemies.size(); index++) {
            if (Helper.isCollisionDetected(boatBitmap, (int) boat.getX(), (int) boat.getY(), mine, (int) enemies.get(index).getX(), (int) enemies.get(index).getY())) {
                enemies.clear();
                shoots.clear();
                playing = false;
                timer.cancel();
                Helper.showGameOverDialog(mainActivity);
                return;
            }

            for (int bulletIndex = 0; bulletIndex < shoots.size(); bulletIndex++) {
                if (Helper.isCollisionDetected(bulletBitmap, (int) shoots.get(bulletIndex).getX(), (int) shoots.get(bulletIndex).getY(),
                        mine, (int) enemies.get(index).getX(), (int) enemies.get(index).getY()))
                {
                    shoots.remove(bulletIndex);
                    enemies.remove(index);
                    return;
                }
            }
        }

        // Update enemies position
        //Move
        for (int index = 0; index < enemies.size(); index++)
        {
            enemies.get(index).tick();
        }

        //Remove
        for (int index = 0; index < enemies.size(); index++)
        {
            if (enemies.get(index).getY() > getHeight()) {
                enemies.remove(index);
            }
        }

        // Bullets
        // Draw
        for (int index = 0; index < shoots.size(); index ++) {
            shoots.get(index).tick();
        }

        //Remove
        for (int index = 0; index < shoots.size(); index ++) {
            if (shoots.get(index).getY() < 0) {
                shoots.remove(index);
            }
        }

        // If boat is moving (the player is touching the screen)
        // then move him to the right based on his target speed and the current fps.
        if(isMoving){
            if (isMovingRight) {
                if (getWidth() > boat.getX() + boat.rate) {
                    boat.moveToRight();
                }
            } else {
                if (boat.getX() - boat.rate > 0) {
                    boat.moveToLeft();
                }
            }
        }
    }

    // Draw the newly updated scene
    public void draw() {
        // Make sure our drawing surface is valid
        if (ourHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = ourHolder.lockCanvas();

            // Draw the background
            canvas.drawBitmap(background, 0, 0, null);

            // Draw boat at boatXPosition, 200 pixels
            canvas.drawBitmap(boatBitmap, boat.getX(), boat.getY(), paint);

            //Draw enemies
            for (int index = 0; index < enemies.size(); index++)
            {
                canvas.drawBitmap(mine, enemies.get(index).getX(), enemies.get(index).getY(), paint);
                enemies.get(index).tick();
            }

            // Draw bullets
            for (int index = 0; index < shoots.size(); index ++) {
                canvas.drawBitmap(bulletBitmap, shoots.get(index).getX(), shoots.get(index).getY(), paint);
            }

            // Draw everything to the screen
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
            timer.cancel();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();

        mineYPosition = 20;

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Enemy enemy = new Enemy(Helper.randomXpos(mainActivity, mine.getWidth()), mineYPosition);
                enemies.add(enemy);
            }
        }, 0, delayBetweenEnemies);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        float touchXPos = motionEvent.getX();
        float touchYPos = motionEvent.getY();

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (touchXPos > (boat.getX() + boatHeight)) {
                    isMoving = true;
                    isMovingRight = true;
                } else if (touchXPos < boat.getX()) {
                    isMoving = true;
                    isMovingRight = false;
                } else if (touchYPos < getHeight() && touchYPos > boat.getY()-10) {
                    isMoving = false;
                    Bullet enemy = new Bullet(touchXPos, boat.getY());
                    shoots.add(enemy);

                    boat.shoot();
                } else {
                    isMoving = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                isMoving = false;
                break;
        }
        return true;
    }
}
