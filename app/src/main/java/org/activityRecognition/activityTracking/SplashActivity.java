package org.activityRecognition.activityTracking;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

public class SplashActivity extends AppCompatActivity {

    private  static  int SPLASH_SCREEN_TIMEOUT= 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //set splash screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splashIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(splashIntent);
                finish();
            }
        },SPLASH_SCREEN_TIMEOUT);

        TextView textView = findViewById(R.id.current_time);;
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        // textView is the TextView view that should display it
        textView.setText(currentDateTimeString);

    }
}
