package com.example.ganapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

public class LoadingScreen extends AppCompatActivity {
    ImageView loadingIcon;
    private int animateDelay = 5000;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading_screen);
        loadingIcon = findViewById(R.id.LoadingIcon);
        //Animação de rotação
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(loadingIcon, "rotation", 0f, 360f);
        rotationAnimator.setDuration(animateDelay);
        //rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);

        //Animação de pulso
        ObjectAnimator scaleAnimatorX = ObjectAnimator.ofFloat(loadingIcon, "scaleX", 1f, 1.2f, 1f);
        //scaleAnimatorX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleAnimatorX.setRepeatMode(ObjectAnimator.REVERSE);
        scaleAnimatorX.setDuration(animateDelay);
        ObjectAnimator scaleAnimatorY = ObjectAnimator.ofFloat(loadingIcon, "scaleY", 1f, 1.2f, 1f);
        //scaleAnimatorY.setRepeatCount(ObjectAnimator.INFINITE);
        scaleAnimatorY.setRepeatMode(ObjectAnimator.REVERSE);
        scaleAnimatorY.setDuration(animateDelay);

        ObjectAnimator translationY = ObjectAnimator.ofFloat(loadingIcon, "translationY", -550f);
        translationY.setDuration(animateDelay);

        //Une as animações
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotationAnimator, scaleAnimatorX, scaleAnimatorY, translationY);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadingScreen.this, MenuActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }, animateDelay);


    }
}