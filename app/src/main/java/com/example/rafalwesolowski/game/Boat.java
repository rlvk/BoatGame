package com.example.rafalwesolowski.game;

/**
 * Created by rafalwesolowski on 04/08/15.
 */
public class Boat {

    float posX;
    float posY;
    public static float rate;

    public Boat(float x, float y)
    {
        posX = x;
        posY = y;
        rate = 5;
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
}
