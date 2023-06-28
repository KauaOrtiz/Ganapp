package com.example.ganapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.util.List;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity implements HttpRequestGetHistory.OnResponseReceivedListener {
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
        HttpRequestGetHistory httpRequest = new HttpRequestGetHistory(this);

        // Execute the HTTP GET request
        httpRequest.execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Intent intent = new Intent(HistoryActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResponseReceived(String response) {
        // Handle the received response here
        if (response != null) {
            Log.d(TAG, "Received response: " + response);
            final String pureBase64Encoded = response.substring(response.indexOf(",")  + 1);
            byte[] bytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);

            // Initialize bitmap
            Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            // Add the item to the list
            items.add(new ListItem(decodedByte, "Example Text"));

            // Notify the adapter that the data has changed
            adapter.notifyDataSetChanged();
        } else {
            Log.e(TAG, "Received null response");
            // Handle the case when the response is null
        }
    }
}
