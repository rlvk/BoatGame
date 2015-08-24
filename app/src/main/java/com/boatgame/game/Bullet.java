package com.boatgame.game;

import android.content.Context;

/**
 * Created by rafalwesolowski on 04/08/15.
 */
public class Bullet {

    float posX;
    float posY;
    float rate;

    public Bullet(float x, float y, Context context)
    {
        posX = x;
        posY = y;
        rate = Helper.dp2px(5, context);
    }

    public void tick()
    {
        posY -= rate;
    }

    public float getX()
    {
        return posX;
    }

    public float getY()
    {
        return posY;
    }
}
