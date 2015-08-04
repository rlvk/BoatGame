package com.example.rafalwesolowski.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener{

    GameSurfaceView gameSurfaceView;
    public static int screenWidth;
    public static int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

//        gameSurfaceView = (GameSurfaceView)findViewById(R.id.game_surface_view);
        gameSurfaceView = new GameSurfaceView(this);
        setContentView(gameSurfaceView);
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        gameSurfaceView.pause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        gameSurfaceView.resume();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                gameSurfaceView.setX(event.getX());

                break;
            case MotionEvent.ACTION_UP:
                gameSurfaceView.setX(event.getX());

                break;
            case MotionEvent.ACTION_MOVE:
                gameSurfaceView.setX(event.getX());

                break;
        }

        return true;
    }
}
