package com.example.ganapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    List<String> opcoes;
    ListView list_view;
    ArrayAdapter<String> opcoes_arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_history);

        list_view = findViewById(R.id.list_view);

        opcoes = new ArrayList<String>();

        opcoes.add("Navegar na Internet");
        opcoes.add("Fazer uma ligação");
        opcoes.add("Sobre");
        opcoes.add("Sair");
        opcoes_arr = new ArrayAdapter<String>(this, R.layout.activity_history_item, R.id.text1, opcoes);
        list_view.setAdapter(opcoes_arr);
        list_view.setClickable(true);

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Intent intent = new Intent(HistoryActivity.this, LoadingScreen.class);
                startActivity(intent);
            }
        });
    }
}
