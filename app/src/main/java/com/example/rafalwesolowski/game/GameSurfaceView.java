package com.example.rafalwesolowski.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.andengine.entity.sprite.Sprite;
import org.andengine.util.adt.list.DoubleArrayList;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by rafalwesolowski on 24/07/15.
 */
public class GameSurfaceView extends SurfaceView implements Runnable {

    private MainActivity mainActivity;

    private int enemyBoatFrequency = 3;

    Thread gameThread = null;
    Timer timer = null;

    SurfaceHolder ourHolder;

    volatile boolean playing;

    Canvas canvas;
    Paint paint;
    Paint textPaint;

    Bitmap boatBitmap;
    Bitmap background;
    Bitmap mine;
    Bitmap bulletBitmap;
    Bitmap enemyBoat;

    // Bob starts off not moving
    boolean isMoving = false;

    private Boat boat;

    float mineYPosition;

    private int boatWidth = 0, boatHeight = 0;

    boolean isMovingRight = false;

    private ArrayList<Enemy> enemies;
    private ArrayList<Bullet> shoots;

    private int delayBetweenEnemies = 400;
    private long numberOfEnemies;

    private int counter;

    // When the we initialize (call new()) on gameView
    // This special constructor method runs
    public GameSurfaceView(Context context) {
        super(context);
        mainActivity = (MainActivity)context;

        ourHolder = getHolder();
        paint = new Paint();
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(100);

        enemies = new ArrayList<Enemy>();
        shoots = new ArrayList<Bullet>();

        boatBitmap = getBoatBitmap();

        boat = new Boat(getWidth() / 2, mainActivity.screenHeight - (boatBitmap.getHeight() + 50), getContext());

        Bitmap backgroundBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.background);
        background = Bitmap.createScaledBitmap(backgroundBitmap, mainActivity.screenWidth, mainActivity.screenHeight, true);

        mine = BitmapFactory.decodeResource(this.getResources(), R.drawable.mine);

        bulletBitmap = getBulletBitmap();

        enemyBoat = getEnemyBoatBitmap();
    }

    private Bitmap getEnemyBoatBitmap() {
        Bitmap enemyBoat = BitmapFactory.decodeResource(this.getResources(), R.drawable.enemy_boat);
        Matrix matrix = new Matrix();
        matrix.setRotate(-90);
        return Bitmap.createBitmap(enemyBoat, 0, 0, enemyBoat.getWidth(), enemyBoat.getHeight(), matrix, false);
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
            if (Helper.isCollisionDetected(boatBitmap, (int) boat.getX(), (int) boat.getY(), enemies.get(index).isMine() ? mine : enemyBoat, (int) enemies.get(index).getX(), (int) enemies.get(index).getY())) {
                gameOver();
                return;
            }

            for (int bulletIndex = 0; bulletIndex < shoots.size(); bulletIndex++) {
                if (Helper.isCollisionDetected(bulletBitmap, (int) shoots.get(bulletIndex).getX(), (int) shoots.get(bulletIndex).getY(),
                        enemies.get(index).isMine() ? mine : enemyBoat, (int) enemies.get(index).getX(), (int) enemies.get(index).getY()))
                {
                    shoots.remove(bulletIndex);
                    // Remove object only if it's other ship
                    if (!enemies.get(index).isMine()) {
                        enemies.remove(index);
                        counter += 2;
                    }

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
                counter++;
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
                if (getWidth() > boat.getX() + boatHeight) {
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

            canvas.drawText(String.valueOf(counter), getWidth() - 150, 150, textPaint);
            // Draw boat at boatXPosition, 200 pixels
            canvas.drawBitmap(boatBitmap, boat.getX(), boat.getY(), paint);

            //Draw enemies
            for (int index = 0; index < enemies.size(); index++)
            {
                canvas.drawBitmap(enemies.get(index).isMine() ? mine : enemyBoat, enemies.get(index).getX(), enemies.get(index).getY(), paint);
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

    private void gameOver() {
        Helper.showGameOverDialog(mainActivity);
        SharedPreferencesManager.saveScore(getContext(), counter);
        restart();
    }

    private void restart() {
        playing = false;
        counter = 0;

        enemies.clear();
        shoots.clear();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        try {
            if (gameThread != null) {
                gameThread.join();
                gameThread = null;
            }
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }

    public void pause() {
        restart();
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();

        mineYPosition = 0;

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int randomXPos = Helper.randomXpos(mainActivity, mine.getWidth(), enemies);
                Enemy enemy = new Enemy(randomXPos, mineYPosition);
                if (numberOfEnemies % enemyBoatFrequency == 0 && numberOfEnemies > 0) {
                    enemy.setIsMine(false);
                } else {
                    enemy.setIsMine(true);
                }
                enemies.add(enemy);
                for (Enemy enemy2 : enemies) {
                    if (enemy2.getX() < 0) {
                        enemies.remove(enemy2);
                    }
                }
                numberOfEnemies++;
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
                    Bullet bullet = new Bullet(touchXPos, boat.getY());
                    shoots.add(bullet);

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
