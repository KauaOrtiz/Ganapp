package com.example.ganapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {
    ImageView ganappIcon;
    TextView ganappName;
    TextView ganappInfoPTBR;
    TextView ganappInfoEN;
    private int animateDelay = 5000;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CODE_GALLERY = 1;
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
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            // Faça algo com a imagem selecionada
        }
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            // Faça algo com a imagem selecionada
        }
    }

    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

}