package com.example.ganapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequestGetHistory extends AsyncTask<Void, Void, String> {

    private static final String TAG = "HttpRequestGetHistory";
    private static final String SERVER_URL = "http://10.229.3.140:8080/getImage";
    private OnResponseReceivedListener listener;

    public HttpRequestGetHistory(OnResponseReceivedListener listener) {
        this.listener = listener;
    }
    @Override
    protected String doInBackground(Void... voids) {
        try {
            URL url = new URL(SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                return response.toString();
            } else {
                Log.e(TAG, "HTTP error code: " + responseCode);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error making HTTP request: " + e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(String response) {
        if (response != null) {
            if (listener != null) {
                Log.e(TAG,"Deu certo");
                listener.onResponseReceived(response);
            }
        } else {
            Log.e(TAG, "Failed to receive a response from the server");
        }
    }

    public interface OnResponseReceivedListener {
        void onResponseReceived(String response);
    }
}
