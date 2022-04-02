package com.example.missiledefender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.Locale;

public class ScoresActivity extends AppCompatActivity {
    static TextView scoreList;
    public static int screenHeight;
    private ConstraintLayout layout;
    public static int screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        setupFullScreen();
        getScreenDimensions();
        scoreList = findViewById(R.id.scoreList);

        //we have two threads = one to query and one to insert into the table
        if(getIntent().hasExtra("key")){
            int score = Integer.parseInt(getIntent().getStringExtra("score"));
            String initial = getIntent().getStringExtra("initial");
            int level = Integer.parseInt(getIntent().getStringExtra("level"));
            PlayerDBInserter playerDBInserter = new PlayerDBInserter(ScoresActivity.this, score, initial, level);
            new Thread(playerDBInserter).start();

        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                queryThread();
            }
        }, 500);




    }

    public void queryThread () {
        PlayerDataBaseHandler playerDataBaseHandler = new PlayerDataBaseHandler(this, 0);
        new Thread(playerDataBaseHandler).start();

    }

    public static void displayScores(String text){
        String id = "#";
        String init = "Init";
        String score = "Score";
        String level = "Level";
        String dateTime = "Date/Time";
        String padding = "";
        String test = (String.format(Locale.getDefault(), "%-3s %-10s %-10s %-10s %-22s %12s",
                padding, id, init, score, level, dateTime ));
        scoreList.setText(test + "\n" + text);


    }

    private void setupFullScreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void getScreenDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
    }

    public void exitApp(View v) {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

}