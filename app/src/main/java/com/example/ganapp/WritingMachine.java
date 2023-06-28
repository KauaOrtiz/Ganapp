package com.example.ganapp;

import android.os.Handler;
import android.widget.TextView;

public class WritingMachine {
    public void writeMachine(TextView id, String name, int animateDelay){
        final int nameLength = name.length();
        final long delayBetweenChar = animateDelay/nameLength;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            private int counter = 0;

            @Override
            public void run() {
                if (counter < nameLength) {
                    id.setText(name.substring(0, counter + 1));
                    counter++;
                    handler.postDelayed(this, delayBetweenChar);
                }
            }
        }, animateDelay);
    }
}
