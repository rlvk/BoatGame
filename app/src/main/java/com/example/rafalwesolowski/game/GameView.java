package com.example.rafalwesolowski.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.Random;

/**
 * Created by rafalwesolowski on 23/07/15.
 */
class GameView extends View {

    private Paint paint;
    private int boatWidth = 180, boatHeight = 300;
    private int xPos, yPos;
    private int bombXPos, bombYPos;

    public GameView(Context context) {
        super(context);
        xPos = getRight()/2 - (boatWidth/2);
        yPos = getHeight() - (boatHeight) - 50;
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        xPos = getRight()/2 - (boatWidth/2);
        yPos = getHeight() - (boatHeight) - 50;
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        xPos = getRight()/2 - (boatWidth/2);
        yPos = getHeight() - (boatHeight) - 50;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap boat = BitmapFactory.decodeResource(getResources(), R.drawable.boat);
//        Bitmap bomb = BitmapFactory.decodeResource(getResources(), R.drawable.mine);
        Matrix matrix = new Matrix();
        matrix.setRotate(90);
        boatWidth = boat.getWidth();
        boatHeight = boat.getHeight();
        Bitmap boatRotated = Bitmap.createBitmap(boat, 0, 0, boatWidth, boatHeight, matrix, false);
        canvas.drawBitmap(boatRotated, xPos, yPos, new Paint());
//        bombYPos += 5;
//        if (bombYPos == getHeight()) {
//            bombXPos = getRandomInt();
//            bombYPos += 50;
//        }
//        canvas.drawBitmap(bomb, bombYPos, bombYPos, new Paint());
        super.onDraw(canvas);
    }

    private int getRandomInt() {
        Random rand = new Random();
        int randomNum = rand.nextInt((getWidth() - 50) + 1) + 50;
        return randomNum;
    }

    @Override
    protected void onMeasure(int widthMeasuredSpec, int heightMeasuredSpec) {
        super.onMeasure(widthMeasuredSpec, heightMeasuredSpec);
    }

    public void setPosition(int xPos) {
        this.xPos = xPos - boatWidth/2;
        yPos = getHeight() - (boatHeight) - 50;
    }
}
