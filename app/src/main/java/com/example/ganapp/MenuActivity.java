package com.example.ganapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
// Import the necessary classes

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
        Uri selectedImageUri = null;
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {
            System.out.println("TO DOIDOOOOOOOOOO");
            flag = false;
            selectedImageUri = data.getData();
            //Make something with the picture
            System.out.println("oxiiiiiiiiiiiiiiiiiiiii");
            System.out.println(getRealPathFromGallery(selectedImageUri));
            System.out.println("oxiiiiiiiiiiiiiiiiiiiii");
            FileUploader.uploadFile(getRealPathFromGallery(selectedImageUri));
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null && data.getData() == null) {
            System.out.println("PUTSSSS GRILAAAAAAAAAAAA");
            System.out.println(requestCode);
            flag = false;
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            String filePath = getImagePathFromCamera(imageBitmap);
            //Make something with the picture
            FileUploader.uploadFile(filePath);

        }


    }
    private String getRealPathFromGallery(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public String getImagePathFromCamera(Bitmap bitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "IMG_" + timeStamp + ".jpg";
        File file = new File(myDir, fileName);

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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
    private void sendImageToApi(Uri imageUri) throws JSONException {
        String imagePath = imageUri.getPath();
        File imageFile = new File(imagePath);

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("userName", "Rafael");
        String jsonPayload = jsonBody.toString();

        Map<String, String> fields = new HashMap<>();
// Add any additional fields to the map if required
        fields.put("jsonPayload", jsonPayload); // Add the JSON payload to the map

        Map<String, String> files = new HashMap<>();
        files.put("image", imageFile.getAbsolutePath()); // Add the image file to the map
    }
    private String convertImageToBase64(Uri imageUri) {
        try {
            // Open an input stream from the image URI
            InputStream inputStream = getContentResolver().openInputStream(imageUri);

            // Read the input stream into a byte array
            byte[] imageBytes = getBytes(inputStream);

            // Convert the byte array to a base64 string
            String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            // Close the input stream
            inputStream.close();

            return base64Image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}