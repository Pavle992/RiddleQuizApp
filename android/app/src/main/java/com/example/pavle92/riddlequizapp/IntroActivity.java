package com.example.pavle92.riddlequizapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Scene;
import android.widget.ProgressBar;


public class IntroActivity extends Activity {


    //LOGIN
    public static final String PREFS_NAME = "LoginPrefs";

    private int progressStatus = 0;
    private Handler handler = new Handler();
    ProgressBar progressBar;
    Intent in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);


        /* Check if we successfully logged in before.
                * If we did, redirect to home page
                */
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings.getString("logged", "").toString().equals("logged")) {
            Intent intent1 = new Intent(IntroActivity.this, MainScreenActivity.class);
            startActivity(intent1);
        }
        else {
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            //in=new Intent(this,MainActivity.class);
            in = new Intent(this, GuestActivity.class);
            new Thread(new Runnable() {
                public void run() {
                    while (progressStatus < 100) {

                        progressStatus += 3;

                        handler.post(new Runnable() {
                            public void run() {
                                progressBar.setProgress(progressStatus);
                            }
                        });
                        try {

                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    startActivity(in);


                    finish();

                }

            }).start();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
