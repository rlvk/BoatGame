package com.example.rafalwesolowski.game;

/**
 * Created by rafalwesolowski on 04/08/15.
 */
public class Bullet {

    float posX;
    float posY;
    float rate;

    public Bullet(float x, float y)
    {
        posX = x;
        posY = y;
        rate = 16;
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
