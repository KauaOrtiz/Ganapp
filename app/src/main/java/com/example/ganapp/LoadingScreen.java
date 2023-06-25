package com.example.ganapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.VideoView;

public class LoadingScreen extends AppCompatActivity {
    private ImageView loadingIcon;
    private VideoView videoView;
    private int animateDelay = 4000;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions(new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading_screen);
        loadingIcon = findViewById(R.id.LoadingIcon);
        videoView = findViewById(R.id.videoView);
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

        //Criação do background animado
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.space_test;

        videoView.setVideoURI(Uri.parse(videoPath));

        // Iniciando a reprodução assim que o vídeo estiver pronto
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true); // Repetir o vídeo
                mediaPlayer.start(); // Iniciar a reprodução
            }
        });

        // Reiniciando o vídeo quando ele terminar
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.seekTo(0); // Voltar para o início do vídeo
                mediaPlayer.start(); // Iniciar a reprodução novamente
            }
        });

    }
}