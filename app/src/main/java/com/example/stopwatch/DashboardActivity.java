package com.example.stopwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private TextView timeTxt;
    private Button startBtn, pauseBtn, resetBtn;
    private Handler handler;
    private long startTime = 0; // Start time in milliseconds
    private boolean running;
    private long pausedTime = 0; // Time elapsed when paused

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        timeTxt = findViewById(R.id.time_txt);
        startBtn = findViewById(R.id.start_btn);
        pauseBtn = findViewById(R.id.pause_btn);
        resetBtn = findViewById(R.id.reset_btn);

        handler = new Handler();

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!running) {
                    startTime = System.currentTimeMillis() - pausedTime;
                    handler.post(updateTime);
                    running = true;
                }
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (running) {
                    handler.removeCallbacks(updateTime);
                    pausedTime = System.currentTimeMillis() - startTime;
                    running = false;
                }
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(updateTime);
                running = false;
                startTime = 0;
                pausedTime = 0;
                updateUI(0);
            }
        });
    }

    private Runnable updateTime = new Runnable() {
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - startTime;
            updateUI(elapsedTime);
            handler.postDelayed(this, 10); // Update every 10 milliseconds
        }
    };

    private void updateUI(long elapsedTime) {
        int hours = (int) (elapsedTime / 3600000);
        int minutes = (int) ((elapsedTime % 3600000) / 60000);
        int seconds = (int) ((elapsedTime % 60000) / 1000);
        int milliseconds = (int) (elapsedTime % 1000);

        String time = String.format(Locale.getDefault(), "%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
        timeTxt.setText(time);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("startTime", startTime);
        outState.putBoolean("running", running);
        outState.putLong("pausedTime", pausedTime);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        startTime = savedInstanceState.getLong("startTime");
        running = savedInstanceState.getBoolean("running");
        pausedTime = savedInstanceState.getLong("pausedTime");

        if (running) {
            handler.post(updateTime);
        }

        updateUI(pausedTime);
    }
}
