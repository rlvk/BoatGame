package com.boatgame.game;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by rafalwesolowski on 23/08/15.
 */
public class GameFragment extends Fragment {

    private GameSurfaceView gameSurfaceView;

    public interface OnGameFinishedListener {
        void gameFinished();
    }
    private OnGameFinishedListener onGameFinishedListener;

    public void setOnGameFinishedListener(OnGameFinishedListener listener) {
        onGameFinishedListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View gameView = inflater.inflate(R.layout.game_fragment_layout, container, false);
        gameSurfaceView = (GameSurfaceView)gameView.findViewById(R.id.game_surface_view);
        gameSurfaceView.setOnGameOverListener(mGameOverListener);
        return gameView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        gameSurfaceView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        gameSurfaceView.pause();
    }

    private GameSurfaceView.OnGameOverListener mGameOverListener = new GameSurfaceView.OnGameOverListener() {
        @Override
        public void showDialog() {
            gameSurfaceView.pause();
            if (onGameFinishedListener != null) {
                onGameFinishedListener.gameFinished();
            }
        }
    };
}
