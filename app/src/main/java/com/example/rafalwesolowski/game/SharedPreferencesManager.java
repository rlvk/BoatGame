package com.example.rafalwesolowski.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by rafalwesolowski on 07/08/15.
 */
public class SharedPreferencesManager {

    private static String SCORE_VALUE = "ScoreValue";

    public static void saveScore(Context context, int score) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(SCORE_VALUE, score);
        editor.commit();
    }

    public static int readScore(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getInt(SCORE_VALUE, 0);
    }
}
