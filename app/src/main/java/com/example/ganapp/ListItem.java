package com.example.ganapp;

import android.graphics.Bitmap;

public class ListItem {
    private Bitmap image;
    private String text;

    public ListItem(Bitmap image, String text) {
        this.image = image;
        this.text = text;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getText() {
        return text;
    }
}
