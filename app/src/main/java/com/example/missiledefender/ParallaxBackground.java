package com.example.missiledefender;

import static com.example.missiledefender.MainActivity.screenHeight;
import static com.example.missiledefender.MainActivity.screenWidth;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ParallaxBackground implements Runnable{
    private final Context context;
    private final ViewGroup layout;
    private ImageView backImageA;
    private ImageView backImageB;
    private final long duration;
    private final int resId;
    private static final String TAG = "ParallaxBackground";
    private static boolean running = true;
    public static boolean checkAlpha = false;
    public static float f;


    ParallaxBackground(Context context, ViewGroup layout, int resId, long duration) {
        this.context = context;
        this.layout = layout;
        this.resId = resId;
        this.duration = duration;

        setupBackground();
    }

    public static void stop() {
        running = false;
    }

    private void setupBackground() {
        backImageA = new ImageView(context);
        backImageB = new ImageView(context);

        LinearLayout.LayoutParams params = new LinearLayout
                .LayoutParams(screenWidth + getBarHeight(), screenHeight);
        backImageA.setLayoutParams(params);
        backImageB.setLayoutParams(params);

        layout.addView(backImageA);
        layout.addView(backImageB);

        Bitmap backBitmapA = BitmapFactory.decodeResource(context.getResources(), resId);
        Bitmap backBitmapB = BitmapFactory.decodeResource(context.getResources(), resId);
        //f = changeAlpha();
        backImageA.setAlpha(0.25f);
        backImageB.setAlpha(0.25f);

        backImageA.setImageBitmap(backBitmapA);
        backImageB.setImageBitmap(backBitmapB);

        backImageA.setScaleType(ImageView.ScaleType.FIT_XY);
        backImageB.setScaleType(ImageView.ScaleType.FIT_XY);

        backImageA.setZ(40);
        backImageB.setZ(40);
        animateBack();
        new Thread(this).start();
    }

    @Override
    public void run() {

        backImageA.setX(0);
        backImageB.setX(-(screenWidth + getBarHeight()));
        double cycleTime = 25.0;

        double cycles = duration / cycleTime;
        double distance = (screenWidth + getBarHeight()) / cycles;
        //f = changeAlpha();
        f = 0.25f;
        //f = (float) 0.95;
        float low = 0.25F;
        float high = 0.95F;
        float setLowAlpha = 0.25F;
        float setHighAlpha = 0.95F;


        while (running) {

            if(f == (float)0.25){
                while(low<=0.95F){
                    try {
                        Thread.sleep(700);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    backImageA.setAlpha(setLowAlpha);
                    backImageB.setAlpha(setLowAlpha);
                    low+=0.05;
                    setLowAlpha += 0.05;
                    if(low>=0.95){
                        f= (float) 0.95;
                        low = 0.25F;
                        setLowAlpha = 0.25F;
                        break;
                    }
                }
            }

            if(f == (float)0.95){
                while(high>=0.25){
                    try {
                        Thread.sleep(700);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    backImageA.setAlpha(setHighAlpha);
                    backImageB.setAlpha(setHighAlpha);
                    high-=0.05;
                    setHighAlpha -= 0.05;
                    if(high<=0.25){
                        f=(float) 0.25;
                        high = 0.95F;
                        setHighAlpha = 0.95F;
                        break;
                    }

                }
            }




        }
    }

    private float changeAlpha() {
        float alpha;
        double d = Math.random();
        if(d>.50) {
            alpha = (float) 0.95;
            return alpha;
        }
        else{
            alpha=(float)0.25;
            return alpha;
        }
    }

    private void animateBack() {

        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(duration);

        animator.addUpdateListener(animation -> {
            if (!running) {
                animator.cancel();
                return;
            }
            final float progress = (float) animation.getAnimatedValue();
            float width = screenWidth + getBarHeight();

            float a_translationX = width * progress;
            float b_translationX = width * progress - width;

            backImageA.setTranslationX(a_translationX);
            backImageB.setTranslationX(b_translationX);

        });
        animator.start();
    }


    private int getBarHeight() {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
