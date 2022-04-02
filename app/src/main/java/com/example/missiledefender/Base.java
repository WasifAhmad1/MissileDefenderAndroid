package com.example.missiledefender;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class Base {
    private static MainActivity mainActivity;
    private static ImageView imageView;
    private final AnimatorSet aSet = new AnimatorSet();
    private final int id;



    public Base(final MainActivity mainActivity, int id) {
        this.mainActivity = mainActivity;
        this.id = id;
        imageView = new ImageView(mainActivity);
    }

    public static void removeBase(int idPass) {
        ImageView temp = (ImageView) mainActivity.findViewById(idPass);
        SoundPlayer.getInstance().start("base_blast");
        mainActivity.getLayout().removeView(temp);
        ImageView explodeView = new ImageView(mainActivity);
        explodeView.setImageResource(R.drawable.blast);
        explodeView.setX(temp.getX());
        explodeView.setY(temp.getY());
        float x = temp.getY();
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


    }

    public float getxBase() {
        imageView = (ImageView)mainActivity.findViewById(id);
        return imageView.getX();
    }

    public float getyBase() {
        imageView = (ImageView)mainActivity.findViewById(id);
        return imageView.getY();
    }

    public int getId() {
        return id;
    }



    //in this base class we need methods that are called upon being hit with a missile. If the missile hits
    //the base we are to call a method (a listneer of sorts) that will respond appropriately.


}
