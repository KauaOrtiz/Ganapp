package com.example.ganapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
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

    //defining the attributes from this layout
    private ImageView ganappIcon;
    private TextView ganappName;
    private TextView ganappInfoPTBR;
    private TextView ganappInfoEN;
    private VideoView videoView;

    //defining code variables
    private MediaPlayer mediaPlayer;
    private int animateDelay = 5000;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CODE_GALLERY = 1;
    private boolean flag = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //request device permissions
        requestPermissions(new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        //make this activity fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //binding layout and yours widgets
        setContentView(R.layout.activity_menu);
        ImageView ganappIcon = findViewById(R.id.GanappIcon);
        TextView ganappName = findViewById(R.id.GanappName);
        TextView ganappInfoPTBR = findViewById(R.id.GanappInfoPTBR);
        TextView ganappInfoEN = findViewById(R.id.GanappInfoEN);
        VideoView videoView = findViewById(R.id.videoView2);

        //Calling the animate class
        AnimateWindow anim = new AnimateWindow();
        anim.animateWindow(ganappIcon, videoView, animateDelay);

        //Calling the animate writing class
        WritingMachine write = new WritingMachine();
        write.writeMachine(ganappName,"Ganapp.", animateDelay);
        write.writeMachine(ganappInfoPTBR,"Toque para câmera.\nPressione para galeria.", animateDelay);
        write.writeMachine(ganappInfoEN,"Touch to camera.\nPress to gallery.", animateDelay);

        //Starts background soundtrack
        mediaPlayer = MediaPlayer.create(this, R.raw.background_sound);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        //Method to quick click and long click
        ganappIcon.setOnTouchListener(new View.OnTouchListener() {
            private Handler longClickHandler = new Handler();
            private boolean isLongClick = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isLongClick = false;
                        longClickHandler.postDelayed(longClickRunnable, 500); // time for a long click
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

    //Function to open camera
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            flag = true;
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    //Function to open gallery
    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        flag = true;
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    //Function to return the picture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            flag = false;
            Uri selectedImageUri = data.getData();
            //Make something with the picture
        }
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            flag = false;
            Uri selectedImageUri = data.getData();
            //Make something with the picture
        }
    }
    //default app functions
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Finish the soundtrack
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();

        //Pause soundtrack
        if (mediaPlayer != null && flag == false) {
            mediaPlayer.pause();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        //Play soundtrack
        mediaPlayer.start();
    }
}