package com.example.ganapp;

import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import okhttp3.*;

public class FileUploader {
    private static final String SERVER_URL = "http://10.229.3.140:8080";

    public static void uploadFile(String filePath, Toast toast) {
        OkHttpClient client = new OkHttpClient();
        toast.setText("Processando a imagem");
        toast.show();

        // Create the request body
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userName", "Rafael")
                .addFormDataPart("photo", "photo.jpg",
                        RequestBody.create(MediaType.parse("image/jpeg"), new File(filePath)))
                .build();

        // Create the request
        System.out.println("\n\n\n--------------\n\n\n------------\nChegou aqui \n\n\n--------------\n\n\n------------");

        Request request = new Request.Builder()
                .url(SERVER_URL + "/createImage")
                .post(requestBody)
                .build();


        // Execute the request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Não deu");
                System.out.println(e);
                toast.setText("Algo deu errado");
                toast.show();

                // Handle failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("\n\n\n--------------\n\n\n------------\nMandou para a API \n\n\n--------------\n\n\n------------");
                System.out.println(response);
                toast.setText("Imagem salva com sucesso");
                toast.show();

                // Handle success
            }
        });
    }
}
