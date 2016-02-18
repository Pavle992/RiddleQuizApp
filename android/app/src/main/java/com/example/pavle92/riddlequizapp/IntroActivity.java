package com.example.pavle92.riddlequizapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;


public class IntroActivity extends Activity {

    private int progressStatus = 0;
    private Handler handler = new Handler();
    ProgressBar progressBar;
    Intent in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        progressBar= (ProgressBar) findViewById(R.id.progressBar);
        //in=new Intent(this,MainActivity.class);
        in=new Intent(this,GuestActivity.class);
        new Thread(new Runnable()
        {
            public void run()
            {
               while (progressStatus<100)
                {

                        progressStatus += 3;

                        handler.post(new Runnable() {
                            public void run() {
                                progressBar.setProgress(progressStatus);
                            }
                        });
                        try
                        {
                            
                            Thread.sleep(200);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    startActivity(in);
                    finish();

                }

          }).start();


    }


}
