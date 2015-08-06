package com.example.rafalwesolowski.game;

/**
 * Created by rafalwesolowski on 04/08/15.
 */
public class Enemy {

    private float posX;
    private float posY;
    private float rate;

    private boolean isMine;

    public Enemy(float x, float y)
    {
        posX = x;
        posY = y;
        rate = 6;
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
    }
}
