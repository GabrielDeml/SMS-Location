package com.example.himom;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class test extends AppCompatActivity {
    Intent m_backgroundService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        m_backgroundService = new Intent(getApplicationContext(), BackgroundService.class);
        Button startButton = findViewById(R.id.testBackground);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                ComponentName service = startForegroundService(m_backgroundService);
            }
        });

        Button stopButton = findViewById(R.id.stopTestBackground);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopService(m_backgroundService);
            }
        });
    }
}
