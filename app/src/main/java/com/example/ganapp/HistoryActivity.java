package com.example.ganapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
    ArrayAdapter<ListItem> adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_history);

        listView = findViewById(R.id.list_view);
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
                Intent intent = new Intent(HistoryActivity.this, ViewImageActivity.class);
                // Pass the imageBase64 as an extra in the Intent
                intent.putExtra("image", base64);
                Log.d(TAG, "base64 " + base64);


                // Start the ViewImageActivity
                startActivity(intent);
            }
        });
    }
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
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

                // Iterate over the keys and access the images
                while (keys.hasNext()) {
                    String key = keys.next();
                    String imageBase64 = json.getString(key);

                    byte[] bytes = Base64.decode(imageBase64, Base64.DEFAULT);

                    // Initialize bitmap
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    // Add the item to the list
                    items.add(new ListItem(decodedByte, key));
                }


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
