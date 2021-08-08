package com.frontend.billify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.frontend.billify.R;

public class BillifySplashScreen extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.billify_splash_screen);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(BillifySplashScreen.this, AuthenticationActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 1000);
        }
}
