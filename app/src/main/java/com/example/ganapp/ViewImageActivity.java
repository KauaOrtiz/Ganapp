package com.example.ganapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewImageActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("começou");

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Receive the imageBase64 as a parameter from the Intent
        System.out.println("oxi e agr?");

        String imageBase64 = getIntent().getStringExtra("image");
        System.out.println(imageBase64);


        // Convert the imageBase64 to a Bitmap
        Bitmap bitmap = base64ToBitmap(imageBase64);

        // Display the image in fullscreen
        imageView.setImageBitmap(bitmap);


    }

    private Bitmap base64ToBitmap(String base64) {
        byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
