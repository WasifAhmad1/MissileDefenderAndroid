package com.example.missiledefender;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    public static ImageView base1;
    public static ImageView base2;
    public static ImageView base3;
    public static ImageView gameOver;
    private static int funcCount = 0;
    public static ArrayList<ImageView> images;
    TextView scoreCounter;
    TextView levelCounter;
    public static int screenHeight;
    private ConstraintLayout layout;
    public static int screenWidth;
    private MissileMaker missileMaker;
    private int scoreValue;
    private int level = 1;
    public static ArrayList<Base> bases = new ArrayList<Base>();
    public static ArrayList<Interceptor> interceptors = new ArrayList<Interceptor>();


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SoundPlayer.getInstance().setupSound(this, "background", R.raw.background, true);
        SoundPlayer.getInstance().setupSound(this, "launch_missile", R.raw.launch_missile, false);
        SoundPlayer.getInstance().setupSound(this, "missile_miss", R.raw.missile_miss, false);
        SoundPlayer.getInstance().setupSound(this, "base_blast", R.raw.base_blast, false);
        SoundPlayer.getInstance().setupSound(this, "interceptor_blast", R.raw.interceptor_blast, false);
        SoundPlayer.getInstance().setupSound(this, "launch_interceptor", R.raw.launch_interceptor, false);
        SoundPlayer.getInstance().setupSound(this, "interceptor_hit_missile", R.raw.interceptor_hit_missile, false);
        layout=findViewById(R.id.layout);
        base1 = findViewById(R.id.base1);
        base2 = findViewById(R.id.base2);
        base3 = findViewById(R.id.base3);
        gameOver = findViewById(R.id.gameOver);
        gameOver.setVisibility(View.INVISIBLE);
        Base baseA = new Base(this, base1.getId());
        Base baseB = new Base(this, base2.getId());
        Base baseC = new Base(this, base3.getId());
        bases.add(baseA); bases.add(baseB); bases.add(baseC);

        images = new ArrayList<ImageView>();
        images.add(base1); images.add(base2); images.add(base3);
        //baseFactory(base1, base2, base3);

        scoreCounter = findViewById(R.id.scoreCounter); levelCounter = findViewById(R.id.level);
        scoreCounter.setVisibility(View.INVISIBLE); levelCounter.setVisibility(View.INVISIBLE);
        base1.setVisibility(View.INVISIBLE); base2.setVisibility(View.INVISIBLE); base3.setVisibility(View.INVISIBLE);


        setupFullScreen();
        getScreenDimensions();

        //In here we want to display the title that fades into view and then transition into the main activity
        imageView = findViewById(R.id.title);
        imageView.setAlpha(0.1f);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SoundPlayer.getInstance().start("background");
            }
        }, 4000);
        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(imageView, "alpha", 1.0f);
        alpha1.setDuration(3000);
        alpha1.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getLayout().removeView(imageView);
                layout.setOnTouchListener((view, motionEvent) -> {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        handleTouch(motionEvent.getX(), motionEvent.getY());
                    }
                    return false;
                });
                scoreCounter.setVisibility(View.VISIBLE); levelCounter.setVisibility(View.VISIBLE);
                base1.setVisibility(View.VISIBLE); base2.setVisibility(View.VISIBLE); base3.setVisibility(View.VISIBLE);
                new ParallaxBackground(MainActivity.this, layout, R.drawable.clouds, 10000);
                //These bases will represent the three bases in the layout.
                missileMaker = new MissileMaker(MainActivity.this, screenWidth, screenHeight);
                new Thread(missileMaker).start();
            }
        }, 3500);


    }

    public void removeMissile (Missile m, ImageView imageView) {
        missileMaker.remove(m);
        this.getLayout().removeView(imageView);
    }

    public void handleTouch(float x, float y) {
        double delta = screenHeight - (screenHeight * 0.2);
        System.out.println(y);
        double minDistance = Float.MAX_VALUE;
        Base baseToSend = new Base(this, 0);
        //if(bases.size()>0 && y<delta) {
        if(bases.size()>0) {
            for(Base base : bases){
                double distance = Math.hypot(base.getxBase()-x, base.getyBase()-y);
                if(distance<minDistance) {
                    baseToSend = base;
                    minDistance = distance;
                }
                //call method that takes selected x and y values and the base
            }
            launchInterceptor(baseToSend, x, y);

            }
        else{
            doNothing();
        }
    }


    public void launchInterceptor (Base base, float x, float y) {
        if(interceptors.size()<=3) {
            Interceptor interceptor = new Interceptor(this, base, x, y);
            interceptors.add(interceptor);
            SoundPlayer.getInstance().start("launch_interceptor");
            interceptor.launch();
        }
        else{
            doNothing();
        }

    }

    void removeInterceptor(Interceptor interceptor) {
        interceptors.remove(interceptor);
    }

    public void doNothing(){
        ArrayList<ImageView> toRemove = new ArrayList<ImageView>();
        ArrayList<Base> basesToRemove = new ArrayList<Base>();


    }

    public void applyInterceptorBaseBlast(float x, float y) {
        ArrayList<ImageView> toRemove = new ArrayList<ImageView>();
        ArrayList<Base> basesToRemove = new ArrayList<Base>();
        for(Base base : bases){
            double distance = Math.hypot(base.getxBase()-x, base.getyBase()-y);
            if(distance<120.0) {
                Base.removeBase(base.getId());
                basesToRemove.add(base);
                for (ImageView image : images){
                    if (base.getId()==image.getId()){
                        toRemove.add(image);
                    }
                }
                images.removeAll(toRemove);
            }
        }
        bases.removeAll(basesToRemove);

        if(bases.size()==0) {
            missileMaker.stopRunning(false);
            funcCount ++;
            missileMaker.removeMissiles();
            if(bases.size()==0) {
                gameOver.setVisibility(View.VISIBLE);
                // we only run this following code if the score is within the top ten scores
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //We go to the new activty to get the scores
                        // Single input value dialog
                        //first we query the score to make sure it is top ten
                        PlayerDataBaseHandler playerDataBaseHandler = new PlayerDataBaseHandler(MainActivity.this,
                                Integer.parseInt(scoreCounter.getText().toString()));


                        new Thread(playerDataBaseHandler).start();



                        //Intent intent = new Intent(MainActivity.this, ScoresActivity.class);
                        //startActivity(intent);

                    }
                }, 4000); }

        }


    }

    public void applyMissileBlast(float x, float y) {
        //we check if the distance is less than 250. If it is then we call a method in the
        //base class to explode the base. The explode method needs some sort of reference to the image id in
        //order to know what to remove exactly
        ArrayList<ImageView> toRemove = new ArrayList<ImageView>();
        ArrayList<Base> basesToRemove = new ArrayList<Base>();
        boolean hit = false;
        Base baseToSend = new Base (this, 0);
        for(Base base : bases) {
            double distance = Math.hypot(base.getxBase() - x, base.getyBase() - y);
            System.out.println("break");
            if ((float) distance < 250.0) {
                hit = true;
                baseToSend = base;
            }
        }
                if(hit == true) {
                    Base.removeBase(baseToSend.getId());
                    basesToRemove.add(baseToSend);
                    for (ImageView image : images) {
                        if (baseToSend.getId() == image.getId()) {
                            toRemove.add(image);
                        }
                    }
                }
                images.removeAll(toRemove);

                if(hit == false){
                    SoundPlayer.getInstance().start("missile_miss");
                }


        bases.removeAll(basesToRemove);

        if(bases.size()==0) {
            missileMaker.stopRunning(false);
            funcCount ++;
            if(bases.size()==0) {
                missileMaker.removeMissiles();
                gameOver.setVisibility(View.VISIBLE);
                // we only run this following code if the score is within the top ten scores
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //We go to the new activty to get the scores
                        // Single input value dialog
                        //first we query the score to make sure it is top ten
                        PlayerDataBaseHandler playerDataBaseHandler = new PlayerDataBaseHandler(MainActivity.this,
                                Integer.parseInt(scoreCounter.getText().toString()));


                        new Thread(playerDataBaseHandler).start();



                        //Intent intent = new Intent(MainActivity.this, ScoresActivity.class);
                        //startActivity(intent);

                    }
                }, 4000); }

        }

        //we pass in the x and y of the image view from the explosion here and then run a method from the base
        //class to determine if a base was in closeby proximity
    }
    public void jumpToActivity() {
        Intent intent = new Intent(MainActivity.this, ScoresActivity.class);
        startActivity(intent);
    }

    public void genDialogBox(int score) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        et.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3), new InputFilter.AllCaps()} );
        et.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        /*et.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT); */


        builder.setView(et);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(MainActivity.this, ScoresActivity.class);
                intent.putExtra("key", "abc");
                intent.putExtra("score", scoreCounter.getText().toString());
                intent.putExtra("initial", et.getText().toString());
                String [] level = levelCounter.getText().toString().split(" ");
                intent.putExtra("level", level[1].trim());
                startActivity(intent);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(MainActivity.this, ScoresActivity.class);
                //intent.putExtra("Score", String.valueOf(score));
                startActivity(intent);

            }
        });

        builder.setMessage("You are a Top-Player!");
        builder.setTitle("Please Enter Your Initials(up to three characters");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });



        //Intent intent = new Intent(MainActivity.this, ScoresActivity.class);
        //startActivity(intent);

    }

    public void applyInterceptorBlast (float x, float y) {
        missileMaker.applyInterceptorBlast(x, y);
    }

    public void incrementScore() {
        scoreValue++;
        scoreCounter.setText(String.format(Locale.getDefault(), "%d", scoreValue));
    }

    public void incrementLevel(final int value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                levelCounter.setText(String.format(Locale.getDefault(), "Level: %d", value));
            }
        });



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

    public ConstraintLayout getLayout() {
        return layout;
    }

    private void getScreenDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
    }

    public int getScreenHeight(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }





}