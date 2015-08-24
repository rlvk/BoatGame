package com.boatgame.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
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
    public interface OnGameOverListener {
        void showDialog();
    }

    private OnGameOverListener onGameOverListener;

    public void setOnGameOverListener(OnGameOverListener listener) {
        onGameOverListener = listener;
    }

    private int enemyBoatFrequency = 3;
    private int initialBulletsAmount = 10;
    private static final int TEXT_SIZE = 33;
    private static final int TEXT_TOP_MARGIN = 50;

    Thread gameThread = null;
    Timer timer = null;

    SurfaceHolder ourHolder;

    volatile boolean playing;

    Canvas canvas;
    Paint paint;
    Paint textPaint;
    Paint bulletsCountPaint;
    Paint skyPaint;

    Bitmap boatBitmap;
    Bitmap background;
    Bitmap mine;
    Bitmap bulletBitmap;
    Bitmap enemyBoat;
    Bitmap skyBitmap;

    // Bob starts off not moving
    boolean isMoving = false;

    private Boat boat;

    float mineYPosition;

    private int boatWidth = 0, boatHeight = 0, skyWidth = Helper.dp2px(40, getContext());

    boolean isMovingRight = false;

    private ArrayList<Enemy> enemies;
    private ArrayList<Bullet> shoots;
    private ArrayList<Explosion> explosions;

    private int delayBetweenEnemies = Helper.dp2px(100, getContext());
    private long numberOfEnemies;

    private int counter;
    private int shootsNumberAvailable = initialBulletsAmount;
    private boolean shouldDisplayBulletsAmount = false;

    public GameSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mainActivity = (MainActivity)context;

        ourHolder = getHolder();
        paint = new Paint();
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(Helper.dp2px(TEXT_SIZE, getContext()));

        bulletsCountPaint = new Paint();
        bulletsCountPaint.setColor(Color.RED);
        bulletsCountPaint.setTextSize(Helper.dp2px(TEXT_SIZE, getContext()));

        skyPaint = new Paint();

        enemies = new ArrayList<Enemy>();
        shoots = new ArrayList<Bullet>();
        explosions = new ArrayList<Explosion>();

        boatBitmap = getBoatBitmap();

        boat = new Boat(getWidth() / 2, mainActivity.screenHeight - (boatBitmap.getHeight() + 50), getContext());

        Bitmap backgroundBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.background);
        background = Bitmap.createScaledBitmap(backgroundBitmap, mainActivity.screenWidth, mainActivity.screenHeight, true);

        mine = BitmapFactory.decodeResource(this.getResources(), R.drawable.mine);

        bulletBitmap = getBulletBitmap();

        enemyBoat = getEnemyBoatBitmap();

        Bitmap sky = BitmapFactory.decodeResource(this.getResources(), R.drawable.sky);
        skyBitmap = Bitmap.createScaledBitmap(sky, skyWidth, skyWidth, true);
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
                    Explosion explosion = new Explosion((int) enemies.get(index).getX(), (int) enemies.get(index).getY(), getContext());
                    explosions.add(explosion);

                    enemyShot(index, bulletIndex);
                    return;
                }
            }
        }

        if (explosions.size() > 0) {
            for (int index = 0; index < explosions.size(); index++) {
                explosions.get(index).decreseTransparency();
                explosions.get(index).increseYPos();
                if (explosions.get(index).getTransparency() == 0) {
                    explosions.remove(index);
                }
            }
        }

        // Update enemies position
        // Move
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

            canvas.drawText(String.valueOf(counter), getWidth() - Helper.dp2px(66, getContext()), Helper.dp2px(TEXT_TOP_MARGIN, getContext()), textPaint);

            canvas.drawText(String.valueOf(shootsNumberAvailable), Helper.dp2px(16, getContext()), Helper.dp2px(TEXT_TOP_MARGIN, getContext()), bulletsCountPaint);

            // Draw boat at boatXPosition, 200 pixels
            canvas.drawBitmap(boatBitmap, boat.getX(), boat.getY(), paint);

            if (explosions.size() > 0) {
                for (int index = 0; index < explosions.size(); index++) {
                    skyPaint.setAlpha(explosions.get(index).getTransparency());
                    canvas.drawBitmap(skyBitmap, explosions.get(index).getxPos(), explosions.get(index).getyPos(), skyPaint);
                }
            }

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

    private void enemyShot(int enemyIndex, int bulletIndex) {
        shootsNumberAvailable += enemies.get(enemyIndex).isMine() ? 2 : 4;

        shoots.remove(bulletIndex);
        enemies.remove(enemyIndex);

        counter += 2;
    }

    private void shootClickAction(float touchXPos) {
        if (shootsNumberAvailable > 0) {
            shouldDisplayBulletsAmount = false;
            isMoving = false;
            Bullet bullet = new Bullet(touchXPos, boat.getY(), getContext());
            shoots.add(bullet);

            boat.shoot();
            shootsNumberAvailable--;
        } else {
            shouldDisplayBulletsAmount = true;
        }
    }

    private void gameOver() {
        SharedPreferencesManager.saveScore(getContext(), counter);
        if (onGameOverListener != null) {
            onGameOverListener.showDialog();
        }
    }

    private void restart() {
        playing = false;
        counter = 0;
        shootsNumberAvailable = initialBulletsAmount;

        enemies.clear();
        shoots.clear();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (gameThread != null) {
            gameThread.interrupt();
            gameThread = null;
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
                Enemy enemy = new Enemy(randomXPos, mineYPosition, getContext());
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
        if (playing) {
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
                    } else if (touchYPos < getHeight() && touchYPos > boat.getY() - 10) {
                        shootClickAction(touchXPos);
                    } else {
                        isMoving = false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    isMoving = false;
                    break;
            }
        }
        return true;
    }
}
