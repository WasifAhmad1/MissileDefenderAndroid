package com.example.missiledefender;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class Missile {
    private MainActivity mainActivity;
    private final ImageView imageView;
    private final AnimatorSet aSet = new AnimatorSet();
    private final int screenHeight;
    private final int screenWidth;
    private final long screenTime;
    private static final String TAG = "OnUpdate";
    private final boolean hit = false;
    private static int count = 0;

    public Missile(int screenWidth, int screenHeight, long screenTime,  MainActivity mainActivity) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.screenTime = screenTime;
        this.mainActivity = mainActivity;
        //imageView.setX(-500);
        imageView = new ImageView(mainActivity);
        //mainActivity.runOnUiThread(() -> mainActivity.getLayout().addView(imageView));

    }

    AnimatorSet setData (final int drawId) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mainActivity.runOnUiThread(() -> mainActivity.getLayout().addView(imageView));
                //imageView.setImageResource(drawId);
            }
        });
        int startX = (int) (Math.random() * screenWidth );
        int endX = (int) (Math.random() * screenWidth );;
        /*if (Math.random() < 0.5)
        { endX = (startX + 150); }
        else { endX = (startX - 150 ); } */

        imageView.setImageResource(drawId);


        final int www = (int) (imageView.getDrawable().getIntrinsicWidth() * 0.5);

        startX = startX - www;
        int startY = -100 -www;
        int endY = screenHeight - 100;


        float a = calculateAngle(startX, startY, endX, endY);
        imageView.setX(startX);
        imageView.setY(startY);
        imageView.setZ(-10);

        imageView.setRotation(a);
        mainActivity.runOnUiThread(() -> mainActivity.getLayout().addView(imageView));
        ObjectAnimator xAnim = ObjectAnimator.ofFloat(imageView, "x", startX, endX);
        ObjectAnimator yAnim = ObjectAnimator.ofFloat(imageView, "y", startY, endY);
        double distance =  Math.sqrt((endY - startY) * (endY - startY) + (endX - startX) * (endX - startX));


        xAnim.setInterpolator(new LinearInterpolator());
        xAnim.setDuration((long) (distance * 10));

        yAnim.setInterpolator(new LinearInterpolator());
        yAnim.setDuration((long) (distance * 10));




        yAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                SoundPlayer.getInstance().start("launch_missile");
            }
        });

        xAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                    if(imageView.getY()>(screenHeight*0.85)) {
                        aSet.cancel();
                        makeGroundBlast(imageView.getX(), imageView.getY());
                        mainActivity.removeMissile(Missile.this, imageView);
                    //System.out.println("The count is " + count);
                    //after this we will call the make ground blast method passing the reference of the x and y coordinate of
                    //the image view

                }


            }
        });

        aSet.playTogether(xAnim, yAnim);
        return aSet;

    }

     public void makeGroundBlast(float x, float y){

         //SoundPlayer.getInstance().start("missile_miss");
         final ImageView explodeView = new ImageView(mainActivity);
         explodeView.setImageResource(R.drawable.explode);
         float w = (float) (0.5 * explodeView.getDrawable().getIntrinsicWidth());
         explodeView.setX(Missile.this.getX());

         explodeView.setY(Missile.this.getY());

         explodeView.setZ(-15);

         mainActivity.getLayout().addView(explodeView);
         explodeView.setAlpha(1.0f);

         final ObjectAnimator alpha = ObjectAnimator.ofFloat(explodeView, "alpha", 0.0f);
         alpha.setInterpolator(new LinearInterpolator());
         alpha.setDuration(3000);

         alpha.addListener(new AnimatorListenerAdapter() {

             @Override
             public void onAnimationEnd(Animator animation) {
                 mainActivity.getLayout().removeView(explodeView);
             }
         });
         alpha.start();
         mainActivity.applyMissileBlast(explodeView.getX(), explodeView.getY());


     }

     void interceptorBlast(float x, float y) {

         final ImageView iv = new ImageView(mainActivity);
         iv.setImageResource(R.drawable.explode);

         iv.setTransitionName("Missile Intercepted Blast");
         iv.setX(x); iv.setY(y);
         aSet.cancel();

         mainActivity.getLayout().removeView(imageView);
         mainActivity.getLayout().addView(iv);


         final ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 0.0f);
         alpha.setInterpolator(new LinearInterpolator());
         alpha.setDuration(3000);
         alpha.addListener(new AnimatorListenerAdapter() {
             @Override
             public void onAnimationEnd(Animator animation) {
                 mainActivity.getLayout().removeView(iv);
             }
         });
         alpha.start();




     }

    public ImageView getImageView() {
        return imageView;
    }

    void stop() {
        aSet.cancel();
    }

    float getX() {
        return imageView.getX();
    }

    float getY() {
        return imageView.getY();
    }

    float getWidth() {
        return imageView.getWidth();
    }

    float getHeight() {
        return imageView.getHeight();
    }

    private float calculateAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        // Keep angle between 0 and 360
        angle = angle + Math.ceil(-angle / 360) * 360;
        return (float) (180.0f - angle);

    }


}
