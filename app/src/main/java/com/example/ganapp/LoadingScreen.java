package com.example.ganapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

public class LoadingScreen extends AppCompatActivity {
    ImageView loadingIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading_screen);
        loadingIcon = findViewById(R.id.LoadingIcon);

        //Animação de rotação
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(loadingIcon, "rotation", 0f, 360f);
        rotationAnimator.setDuration(6000);
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);

        //Animação de pulso
        ObjectAnimator scaleAnimatorX = ObjectAnimator.ofFloat(loadingIcon, "scaleX", 1f, 1.2f, 1f);
        scaleAnimatorX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleAnimatorX.setRepeatMode(ObjectAnimator.REVERSE);
        scaleAnimatorX.setDuration(3000);
        ObjectAnimator scaleAnimatorY = ObjectAnimator.ofFloat(loadingIcon, "scaleY", 1f, 1.2f, 1f);
        scaleAnimatorY.setRepeatCount(ObjectAnimator.INFINITE);
        scaleAnimatorY.setRepeatMode(ObjectAnimator.REVERSE);
        scaleAnimatorY.setDuration(3000);

        //Une as animações
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotationAnimator, scaleAnimatorX, scaleAnimatorY);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }
}