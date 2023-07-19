package com.example.ganapp;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;
public class HistoryActivity extends AppCompatActivity implements HttpRequest.OnResponseReceivedListener {
    private static final String TAG = "MainActivity";
    List<ListItem> items;
    ListView listView;
    ImageView ShowImage;
    boolean isShowing = false;
    ArrayAdapter<ListItem> adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_history);
        listView = findViewById(R.id.list_view);
        ShowImage = findViewById(R.id.imageView3);
        items = new ArrayList<>();

        adapter = new ArrayAdapter<ListItem>(this, R.layout.activity_history_item, R.id.textView, items) {
            @NonNull
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_history_item, parent, false);
                }

                ImageView imageView = convertView.findViewById(R.id.imageView);
                TextView textView = convertView.findViewById(R.id.textView);

                ListItem listItem = getItem(position);

                if (listItem != null) {
                    imageView.setImageBitmap(listItem.getImage());
                    textView.setText(listItem.getText());
                }

                return convertView;
            }
        };

        listView.setAdapter(adapter);
        listView.setClickable(true);

        // Create an instance of HttpRequestGetHistory and pass 'this' as the listener
        HttpRequest httpRequest = new HttpRequest(this);

        // Execute the HTTP GET request
        httpRequest.execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                if (isShowing) return;
                Log.d(TAG, "position " + position);
                Bitmap imageBitMap = null;
                int count = 0;
                for (ListItem item : items) {
                    if (count == position){
                        imageBitMap = item.getImage();
                        break;
                    }
                    count += 1;
                }
                String base64 = bitmapToBase64(imageBitMap);
                Log.d(TAG, "base64 " + base64);
                isShowing = true;
                listView.setClickable(false);
                ShowImage.setImageBitmap(imageBitMap);
                //Zoom animation
                ObjectAnimator scaleAnimatorX = ObjectAnimator.ofFloat(ShowImage, "scaleX", .1f, 1.2f, 1f);
                ObjectAnimator scaleAnimatorY = ObjectAnimator.ofFloat(ShowImage, "scaleY", .1f, 1.2f, 1f);
                //Start all the animations
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(scaleAnimatorX, scaleAnimatorY);
                animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
                animatorSet.start();
            }
        });
    }
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    @Override
    public void onBackPressed(){
        if (isShowing) {

            //Zoom animation
            ObjectAnimator scaleAnimatorX = ObjectAnimator.ofFloat(ShowImage, "scaleX", 1f, 1.2f, 0.01f);
            ObjectAnimator scaleAnimatorY = ObjectAnimator.ofFloat(ShowImage, "scaleY", 1f, 1.2f, 0.1f);
            //Start all the animations
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(scaleAnimatorX, scaleAnimatorY);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.start();
            final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        ShowImage.setImageBitmap(null);
                        listView.setClickable(true);
                        isShowing = false;
                    }
                }, 300);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public void onResponseReceived(String response) {
        // Handle the received response here
        if (response != null) {
            Log.d(TAG, "Received response: " + response);
            // Extract the base64-encoded image string from the JSON response
            try {
                // Create a JSONObject from the response string
                JSONObject json = new JSONObject(response);
                // Access the value of the 'image' key
                // Get the keys from the JSON object
                Iterator<String> keys = json.keys();

                // Create a temporary list to store the items in reverse order
                List<ListItem> reverseItems = new ArrayList<>();

                // Iterate over the keys and access the images
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject image_classification = json.getJSONObject(key);

                    String imageBase64 = image_classification.getString("Image");
                    String classification = image_classification.getString("Classification");

                    byte[] bytes = Base64.decode(imageBase64, Base64.DEFAULT);

                    // Initialize bitmap
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    // Add the item to the temporary list in reverse order
                    reverseItems.add(0, new ListItem(decodedByte, classification));
                }

                // Add the items from the temporary list to the original list
                items.addAll(reverseItems);


                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged();

            } catch (JSONException e) {
                // Handle JSON exception
                e.printStackTrace();
                // or throw a custom exception or handle the error in a different way
            }
        } else {
            Log.e(TAG, "Received null response");
            // Handle the case when the response is null
        }
    }
}
