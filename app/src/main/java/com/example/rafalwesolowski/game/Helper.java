package com.example.rafalwesolowski.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by rafalwesolowski on 04/08/15.
 */
public class Helper {

    private static final int SHARE_ON_FACEBOOK = 0;
    private static final int RESTART = 1;
    private static final int EXIT = 2;

    public static boolean isCollisionDetected(Bitmap bitmap1, int x1, int y1,
                                              Bitmap bitmap2, int x2, int y2) {

        Rect bounds1 = new Rect(x1, y1, x1+bitmap1.getWidth(), y1+bitmap1.getHeight());
        Rect bounds2 = new Rect(x2, y2, x2+bitmap2.getWidth(), y2+bitmap2.getHeight());

        if (Rect.intersects(bounds1, bounds2)) {
            Rect collisionBounds = getCollisionBounds(bounds1, bounds2);
            for (int i = collisionBounds.left; i < collisionBounds.right; i++) {
                for (int j = collisionBounds.top; j < collisionBounds.bottom; j++) {
                    int bitmap1Pixel = bitmap1.getPixel(i-x1, j-y1);
                    int bitmap2Pixel = bitmap2.getPixel(i-x2, j-y2);
                    if (isFilled(bitmap1Pixel) && isFilled(bitmap2Pixel)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static Rect getCollisionBounds(Rect rect1, Rect rect2) {
        int left = (int) Math.max(rect1.left, rect2.left);
        int top = (int) Math.max(rect1.top, rect2.top);
        int right = (int) Math.min(rect1.right, rect2.right);
        int bottom = (int) Math.min(rect1.bottom, rect2.bottom);
        return new Rect(left, top, right, bottom);
    }

    private static boolean isFilled(int pixel) {
        return pixel != Color.TRANSPARENT;
    }

    public static int randomXpos(MainActivity mainActivity, int mineWidth, List<Enemy> list) {
        Random random = new Random();
        int randomPosition = random.nextInt(mainActivity.screenWidth - mineWidth) + 0;
        for (int i = 0; i < list.size(); i++) {
            if ((randomPosition > list.get(i).getX() - mineWidth && randomPosition < list.get(i).getX() + mineWidth)) {
                return -200;
            }
        }
        return randomPosition;
    }

    public static void showGameOverDialog(final MainActivity mainActivity) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setTitle("GAME OVER")
                        .setCancelable(false)
                        .setItems(R.array.game_over_options,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case SHARE_ON_FACEBOOK:
                                                shareOnFB(mainActivity);
                                                break;
                                            case RESTART:
                                                mainActivity.gameSurfaceView.resume();
                                                break;
                                            case EXIT:
                                                mainActivity.finish();
                                                break;
                                        }
                                    }
                                });
                builder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mainActivity.gameSurfaceView.resume();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        mainActivity.gameSurfaceView.resume();
                    }
                });
//                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//                        mainActivity.gameSurfaceView.resume();
//                    }
//                });
                builder.create().show();
            }
        });
    }

    private static void shareOnFB(Activity activity) {
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentTitle("My score was " + String.valueOf(SharedPreferencesManager.readScore(activity.getApplicationContext())))
                .setContentDescription("Download the app and try to beat me")
                .setContentUrl(Uri.parse("http://facebook.com"))
                .build();
        ShareDialog.show(activity, content);
    }
}
