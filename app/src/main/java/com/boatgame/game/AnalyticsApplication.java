package com.boatgame.game;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseCrashReporting;

/**
 * Created by rafalwesolowski on 23/08/15.
 */
public class AnalyticsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Crash Reporting
        ParseCrashReporting.enable(this);

        // Setup Parse
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));
    }
}
