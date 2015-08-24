package com.boatgame.game;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by rafalwesolowski on 23/08/15.
 */
public class IntroductionFragment extends Fragment implements View.OnClickListener{

    private static String IsGameOver = "isGameOver";
    private View introView;
    private TextView gameOverTextView;
    private boolean isGameOver;

    public static IntroductionFragment newInstance(boolean isGameOver) {
        IntroductionFragment introductionFragment = new IntroductionFragment();
        Bundle args = new Bundle();
        args.putBoolean(IsGameOver, isGameOver);
        introductionFragment.setArguments(args);
        return introductionFragment;
    }

    public interface OnDismissIntroductionListener {
        void startGame();
        void exit();
    }
    private OnDismissIntroductionListener onDismissIntroductionListener;

    public void setOnDismissIntroductionListener(OnDismissIntroductionListener listener) {
        onDismissIntroductionListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isGameOver = getArguments().getBoolean(IsGameOver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View introductionView = inflater.inflate(R.layout.introduction_layout, container, false);

        introView = introductionView.findViewById(R.id.introduction_view);

        Button startGameButton = (Button)introductionView.findViewById(R.id.start_game_button);
        Button postOnFB = (Button)introductionView.findViewById(R.id.share_on_facebook);
        Button exitButton = (Button)introductionView.findViewById(R.id.exit_button);
        ImageButton shareButton = (ImageButton)introductionView.findViewById(R.id.share);

        gameOverTextView = (TextView)introductionView.findViewById(R.id.game_over_text_view);
        if (isGameOver) {
            gameOverTextView.setVisibility(View.VISIBLE);
        } else {
            gameOverTextView.setVisibility(View.GONE);
        }
        startGameButton.setOnClickListener(this);
        postOnFB.setOnClickListener(this);
        exitButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);

        return introductionView;
    }

    private void share() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Get Boat game from Play Store");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_game_button:
                if (onDismissIntroductionListener != null) {
                    onDismissIntroductionListener.startGame();
                }
                break;
            case R.id.share_on_facebook:
                Helper.shareOnFB(getActivity());
                break;
            case R.id.exit_button:
                if (onDismissIntroductionListener != null) {
                    onDismissIntroductionListener.exit();
                }
                break;
            case R.id.share:
                share();
                break;
        }
    }
}
