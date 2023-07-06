package com.example.ganapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class HttpRequest extends AsyncTask<Void, Void, String> {

    private static final String TAG = "HttpRequestGetHistory";
    private static final String SERVER_URL = "http://192.168.2.101:8080/";
    private static final String BOUNDARY = "/---------------------------/";
    private static final String LINE_FEED = "\r\n";
    private OnResponseReceivedListener listener;

    public HttpRequest(OnResponseReceivedListener listener) {
        this.listener = listener;
    }
    @Override
    protected String doInBackground(Void... voids) {
        try {
            URL url = new URL(SERVER_URL + "getImage");
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

    public static void sendMultipartData(Map<String, String> fields, Map<String, String> files) throws IOException {
        System.out.println("Começando a desgraçaaaa");
        URL url = new URL(SERVER_URL + "createImage");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        connection.setRequestProperty("Content-Type", "multipart/form-data");

        OutputStream outputStream = connection.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);

        // Write fields (key-value pairs)
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            writer.append("--").append(BOUNDARY).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"")
                    .append(entry.getKey()).append("\"").append(LINE_FEED);
            writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.append(entry.getValue()).append(LINE_FEED);
            writer.flush();
        }

        // Write files
        for (Map.Entry<String, String> entry : files.entrySet()) {
            File file = new File(entry.getValue());
            writer.append("--").append(BOUNDARY).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"")
                    .append(entry.getKey()).append("\"; filename=\"")
                    .append(file.getName()).append("\"").append(LINE_FEED);
            writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(file.getName()))
                    .append(LINE_FEED);
            writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();

            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            fileInputStream.close();

            writer.append(LINE_FEED);
            writer.flush();
        }

        writer.append("--").append(BOUNDARY).append("--").append(LINE_FEED);
        writer.close();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("Algo de errado não ta certo");
            // Request successful
            // Handle the response
            // ...
        } else {
            System.out.println("ai sim, ta td errado");
            // Request failed
            // Handle the error
            // ...
        }

        connection.disconnect();
    }

}
