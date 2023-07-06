package com.example.ganapp;

import java.io.File;
import java.io.IOException;

import okhttp3.*;

public class FileUploader {

    public static void uploadFile(String filePath) {
        OkHttpClient client = new OkHttpClient();

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
                .url("http://192.168.2.101:8080/createImage")
                .post(requestBody)
                .build();
        System.out.println("\n\n\n--------------\n\n\n------------\nVishhhh aqui \n\n\n--------------\n\n\n------------" + new File(filePath).getName());


        // Execute the request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Não deu");
                System.out.println(e);
                // Handle failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("\n\n\n--------------\n\n\n------------\nMandou para a API \n\n\n--------------\n\n\n------------");
                System.out.println(response);
                // Handle success
            }
        });
    }
}
