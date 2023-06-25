package com.example.ganapp;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.VideoView;

public class AnimateWindow {
    private MediaPlayer mediaPlayer;
    public void animateWindow(ImageView ganappIcon, VideoView videoView, int animateDelay){
        //Rotation animation
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(ganappIcon, "rotation", 0f, 360f);
        rotationAnimator.setDuration(animateDelay);
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);

        //Pulse animation
        ObjectAnimator scaleAnimatorX = ObjectAnimator.ofFloat(ganappIcon, "scaleX", 1f, 1.2f, 1f);
        //scaleAnimatorX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleAnimatorX.setRepeatMode(ObjectAnimator.REVERSE);
        scaleAnimatorX.setDuration(animateDelay);
        ObjectAnimator scaleAnimatorY = ObjectAnimator.ofFloat(ganappIcon, "scaleY", 1f, 1.2f, 1f);
        //scaleAnimatorY.setRepeatCount(ObjectAnimator.INFINITE);
        scaleAnimatorY.setRepeatMode(ObjectAnimator.REVERSE);
        scaleAnimatorY.setDuration(animateDelay);

        ObjectAnimator translationY = ObjectAnimator.ofFloat(ganappIcon, "translationY", -550f);
        translationY.setDuration(animateDelay);


        //Start all the animations
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotationAnimator, scaleAnimatorX, scaleAnimatorY, translationY);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();

        //animate background
        String videoPath = "android.resource://com.example.ganapp/" + R.raw.space_test;
        videoView.setVideoURI(Uri.parse(videoPath));

        //Listener to start the video
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true); //Repeat the video
                mediaPlayer.start(); //Start the video
            }
        });

        //Listener to restart the video
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.seekTo(0); //Go to the beginning
                mediaPlayer.start(); //Starts again
            }
        });
    }
}
