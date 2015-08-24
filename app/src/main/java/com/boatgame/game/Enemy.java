package com.boatgame.game;

import android.content.Context;

/**
 * Created by rafalwesolowski on 04/08/15.
 */
public class Enemy {

    private float posX;
    private float posY;
    private float rate;
    private Context context;
    private boolean isMine;

    public Enemy(float x, float y, Context context)
    {
        posX = x;
        posY = y;
        rate = Helper.dp2px(2, context);
        this.context = context;
    }

    public void tick()
    {
        posY += rate;
    }

    public float getX()
    {
        return posX;
    }

    public float getY()
    {
        return posY;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setIsMine(boolean isMine) {
        this.isMine = isMine;
        if (!isMine) {
            rate += Helper.dp2px(1, context);
        }
    }
}
