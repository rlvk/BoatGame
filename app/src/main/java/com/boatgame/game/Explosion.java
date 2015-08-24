package com.boatgame.game;

import android.content.Context;

/**
 * Created by rafalwesolowski on 23/08/15.
 */
public class Explosion {

    private int transparency;
    private int xPos;
    private int yPos;
    private final Context context;

    public Explosion(int xPos, int yPos, Context context) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.context = context;
        transparency = 100;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void increseYPos() {
        yPos = yPos + Helper.dp2px(2, context);
    }

    public void decreseTransparency() {
        transparency = transparency - 1;
    }

    public int getTransparency() {
        return transparency;
    }
}
