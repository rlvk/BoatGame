package com.boatgame.game;

import android.content.Intent;
import android.graphics.Point;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MainActivity extends AppCompatActivity{

    public static int screenWidth;
    public static int screenHeight;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        insertIntroductionFragment(false);
    }

    private void insertGameFragment() {
        GameFragment gameFragment = new GameFragment();
        gameFragment.setOnGameFinishedListener(onGameFinishedListener);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.frame_container, gameFragment, gameFragment.getTag()).commit();
    }

    private void insertIntroductionFragment(boolean isGameOver) {
        IntroductionFragment introductionFragment = IntroductionFragment.newInstance(isGameOver);
        introductionFragment.setOnDismissIntroductionListener(onDismissIntroductionListener);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (isGameOver) {
            fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        fragmentTransaction.replace(R.id.frame_container, introductionFragment, introductionFragment.getTag()).commit();
    }

    private GameFragment.OnGameFinishedListener onGameFinishedListener = new GameFragment.OnGameFinishedListener() {
        @Override
        public void gameFinished() {
            insertIntroductionFragment(true);
        }
    };

    private IntroductionFragment.OnDismissIntroductionListener onDismissIntroductionListener = new IntroductionFragment.OnDismissIntroductionListener() {

        @Override
        public void startGame() {
            insertGameFragment();
        }

        @Override
        public void exit() {
            finish();
        }
    };

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
