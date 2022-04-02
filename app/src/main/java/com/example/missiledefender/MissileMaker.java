package com.example.missiledefender;

import static com.example.missiledefender.Interceptor.INTERCEPTOR_BLAST;

import android.animation.AnimatorSet;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;

//This class will be what sends down the missiles. The missiles will file from the top of the screen all the way to the bottom of the
//screen. Once it hits the ground it will emit an explosion and play a sound.

public class MissileMaker implements Runnable {

    private static final String TAG = "MissileMaker";
    private final MainActivity mainActivity;
    private boolean isRunning;
    private final ArrayList<Missile> activeMissiles = new ArrayList<>();
    private final int screenWidth;
    private final int screenHeight;
    private static long delayBetweenMissles = 3000;
    private static int misslesPerLevel = 10;
    private static int level = 1;
    private static long sleepTime= (long) (delayBetweenMissles * 0.5);

    public MissileMaker(MainActivity mainActivity, int screenWidth, int screenHeight) {
        this.mainActivity = mainActivity;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    void setRunning(boolean running) {
        isRunning = running;
        ArrayList<Missile> temp = new ArrayList<>(activeMissiles);
        for (Missile m : temp) {
            m.stop();
        }
    }

    @Override
    public void run() {
        setRunning(true);
        //long delay = 10000;
        int count = 0;

        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (isRunning) {
            int redID = R.drawable.missile;
            long missileTime = (long) (5000 + (Math.random() * delayBetweenMissles));
            final Missile missile = new Missile(screenWidth, screenHeight, missileTime, mainActivity);
            activeMissiles.add(missile);
            Log.d(TAG, "The amount of missles is " + activeMissiles.size());
            count ++;
            final AnimatorSet as = missile.setData(redID);
            if(count>misslesPerLevel) {
                count = 0;
                level++;
                delayBetweenMissles = delayBetweenMissles - 500;
                delayBetweenMissles = checkMillis(delayBetweenMissles);
                mainActivity.incrementLevel(level);
                Log.d(TAG, "The " + delayBetweenMissles + " is");
            }

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    as.start();
                }
            });

            try {
                long sleep = getSleepTime();
                if (delayBetweenMissles <= 0)
                    delayBetweenMissles = 1;
                Thread.sleep(sleep);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

    }

    void removeMissiles() {
        ImageView imageView = new ImageView(mainActivity);
        for(Missile missile : activeMissiles) {
            imageView = missile.getImageView();
            mainActivity.getLayout().removeView(imageView);
            missile.stop();
        }
    }

    void stopRunning(boolean running) {
        isRunning = running;
    }

    void remove(Missile m) {
        activeMissiles.remove(m);
    }

    long getSleepTime (){
        double d = Math.random();
        if(d<0.1) {
            return (1);
        }
        else if (d<0.2) {
            return (long) (0.5* delayBetweenMissles);
        }
        else{
            return delayBetweenMissles;
        }
    }

    long checkMillis (long time) {
        if(time <= 0){
            return 1;
        }
        else{
            return time;
        }
    }

    void applyInterceptorBlast(float x1, float y1) {

         ArrayList<Missile> nowGone = new ArrayList<>();
         ArrayList<Missile> temp = new ArrayList<>(activeMissiles);
         for(Missile m : temp) {
             //float x2 = (int) (m.getX() + (0.5 * m.getWidth()));
             float x2 = (int) (m.getX());
             float y2 = (int) (m.getY());

             float f = (float) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));

             if (f < INTERCEPTOR_BLAST) {

                 SoundPlayer.getInstance().start("interceptor_hit_missile");
                 mainActivity.incrementScore();

                 m.interceptorBlast(x2, y2);
                 //m.interceptorBlast(x2, y2);
                 nowGone.add(m);
             }
         }

         for (Missile m : nowGone) {
             activeMissiles.remove(m);
         }

    }
}
