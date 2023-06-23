package com.example.ganapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {
    ImageView ganappIcon;
    TextView ganappName;
    TextView ganappInfoPTBR;
    TextView ganappInfoEN;
    private int animateDelay = 5000;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);
        ImageView ganappIcon = findViewById(R.id.GanappIcon);
        TextView ganappName = findViewById(R.id.GanappName);
        TextView ganappInfoPTBR = findViewById(R.id.GanappInfoPTBR);
        TextView ganappInfoEN = findViewById(R.id.GanappInfoEN);

        //Animação de rotação
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(ganappIcon, "rotation", 0f, 360f);
        rotationAnimator.setDuration(animateDelay);
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        rotationAnimator.setRepeatMode(ObjectAnimator.RESTART);
        rotationAnimator.start();

        writeTxt(ganappName,"Ganapp.");
        writeTxt(ganappInfoPTBR, "Toque para câmera.\nPressione para galeria.");
        writeTxt(ganappInfoEN, "Touch to camera.\nPress to gallery.");

        ganappIcon.setOnTouchListener(new View.OnTouchListener() {
            private Handler longClickHandler = new Handler();
            private boolean isLongClick = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isLongClick = false;
                        longClickHandler.postDelayed(longClickRunnable, 500); // Tempo em milissegundos para considerar como um long click
                        return true;
                    case MotionEvent.ACTION_UP:
                        longClickHandler.removeCallbacks(longClickRunnable);
                        if (!isLongClick) {
                            Intent intent = new Intent(MenuActivity.this, CameraActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        }
                        return true;
                }
                return false;
            }

            private Runnable longClickRunnable = new Runnable() {
                @Override
                public void run() {
                    isLongClick = true;
                    Intent intent = new Intent(MenuActivity.this, GalleryActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
            };
        });
    }
    public void writeTxt(TextView id, String name){
        final int nameLength = name.length();
        final long delayBetweenChar = animateDelay/nameLength;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            private int counter = 0;

            @Override
            public void run() {
                if (counter < nameLength) {
                    id.setText(name.substring(0, counter + 1));
                    counter++;
                    handler.postDelayed(this, delayBetweenChar);
                }
            }
        }, delayBetweenChar);
    }
}