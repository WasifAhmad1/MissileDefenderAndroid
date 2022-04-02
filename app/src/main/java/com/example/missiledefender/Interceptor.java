package com.example.missiledefender;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class Interceptor {
    private final MainActivity mainActivity;
    private static int count = 0;
    private final int id;
    private ImageView imageview;
    private ObjectAnimator moveX, moveY;
    private float endX;
    private float endY;
    private Base base;
    private static int idVal = -1;
    static final int INTERCEPTOR_BLAST = 120;

    public Interceptor(MainActivity mainActivity, Base base, float endX, float endY) {
        this.mainActivity = mainActivity;
        this.base = base;
        this.endX = endX;
        this.endY = endY;
        this.id = count++;
        initialize();
    }
    public void initialize() {
        imageview = new ImageView(mainActivity);
        imageview.setId(idVal--);
        imageview.setImageResource(R.drawable.interceptor);
        imageview.setTransitionName("Interceptor " + id);
        final int www = (int) (imageview.getDrawable().getIntrinsicWidth() * 0.5);
        float x = base.getxBase();
        imageview.setX(x+www);
        int y = mainActivity.getScreenHeight() - 70;
        imageview.setY(y);

        endX -= www;
        endY -= www;

        float a = calculateAngle(x, y, endX, endY);

        imageview.setZ(-10);
        imageview.setRotation(a);

        mainActivity.getLayout().addView(imageview);
        double distance =  Math.sqrt((endY - imageview.getY()) * (endY - imageview.getY()) + (endX - imageview.getX()) * (endX - imageview.getX()));


        moveX = ObjectAnimator.ofFloat(imageview, "x", endX);
        moveX.setInterpolator(new AccelerateInterpolator());
        moveX.setDuration((long) (distance * 2));

        moveY = ObjectAnimator.ofFloat(imageview, "y", endY);
        moveY.setInterpolator(new AccelerateInterpolator());
        moveY.setDuration((long) (distance * 2));

        moveX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(imageview);
                makeBlast();
            }
        });


    }

    private void makeBlast(){
        final ImageView explodeView = new ImageView(mainActivity);
        explodeView.setImageResource(R.drawable.i_explode);
        SoundPlayer.getInstance().start("interceptor_blast");
        explodeView.setTransitionName("Interceptor blast");
        float w = explodeView.getDrawable().getIntrinsicWidth();
        explodeView.setX(endX - (w/2));

        explodeView.setY(endY- (w/2));

        explodeView.setZ(-15);

        mainActivity.getLayout().addView(explodeView);

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
        mainActivity.applyInterceptorBlast((endX - w/2), (endY - w/2));
        mainActivity.applyInterceptorBaseBlast((endX - w/2), (endY - w/2));
        mainActivity.removeInterceptor(this);




    }

    void launch() {
        moveX.start();
        moveY.start();
    }

    float getX(){
        int xVar = imageview.getWidth() /2;
        return imageview.getX() + xVar;
    }

    float getY() {
        int yVar = imageview.getHeight()/2;
        return imageview.getY() + yVar;
    }

    private float calculateAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        // Keep angle between 0 and 360
        angle = angle + Math.ceil(-angle / 360) * 360;
        return (float) (180.0f - angle);

    }

}
