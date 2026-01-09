package com.example.expenseetracker;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_TIME = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Wait for SPLASH_TIME then open LoginActivity
        new Handler().postDelayed(() -> {
            // Open LoginActivity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish(); // close splash screen so user cannot return to it
        }, SPLASH_TIME);
    }
}
