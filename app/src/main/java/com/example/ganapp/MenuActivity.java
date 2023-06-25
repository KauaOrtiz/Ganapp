package com.example.ganapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

public class MenuActivity extends AppCompatActivity {
    private ImageView ganappIcon;
    private TextView ganappName;
    private TextView ganappInfoPTBR;
    private TextView ganappInfoEN;
    private VideoView videoView;
    private MediaPlayer mediaPlayer;
    private int animateDelay = 5000;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CODE_GALLERY = 1;
    private boolean flag = false;
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
        VideoView videoView = findViewById(R.id.videoView2);

        //Animação de rotação
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(ganappIcon, "rotation", 0f, 360f);
        rotationAnimator.setDuration(animateDelay);
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        rotationAnimator.setRepeatMode(ObjectAnimator.RESTART);
        rotationAnimator.start();

        //Criação do background animado
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.space_test;

        videoView.setVideoURI(Uri.parse(videoPath));
        mediaPlayer = MediaPlayer.create(this, R.raw.background_sound);
        mediaPlayer.setLooping(true);

        // Iniciando a reprodução do áudio
        mediaPlayer.start();

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
                            dispatchTakePictureIntent();
                            //Intent intent = new Intent(MenuActivity.this, Broker.class);
                            //startActivity(intent);
                            //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            //finish();
                        }
                        return true;
                }
                return false;
            }

            private Runnable longClickRunnable = new Runnable() {
                @Override
                public void run() {
                    isLongClick = true;
                    openGallery();
                    //Intent intent = new Intent(MenuActivity.this, Broker.class);
                    //startActivity(intent);
                    //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    //finish();
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
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            flag = true;
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            flag = false;
            Uri selectedImageUri = data.getData();
            // Faça algo com a imagem selecionada
        }
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            flag = false;
            Uri selectedImageUri = data.getData();
            // Faça algo com a imagem selecionada
        }
    }

    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        flag = true;
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Liberando os recursos do MediaPlayer ao encerrar a atividade
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();

        // Liberando os recursos do MediaPlayer ao encerrar a atividade
        if (mediaPlayer != null && flag == false) {
            mediaPlayer.pause();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }
}