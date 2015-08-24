package com.boatgame.game;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by rafalwesolowski on 04/08/15.
 */
public class Boat {

    float posX;
    float posY;
    public static float rate;

    private final Context context;

    public Boat(float x, float y, Context context)
    {
        posX = x;
        posY = y;
        rate = Helper.dp2px(2, context);

        this.context = context;
    }

    public void moveToRight()
    {
        posX += rate;
    }

    public void moveToLeft()
    {
        posX -= rate;
    }

    public float getX()
    {
        return posX;
    }

    public float getY()
    {
        return posY;
    }

    public void shoot() {

        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.gunshot);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }

        });
    }
}
