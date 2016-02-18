package com.example.pavle92.riddlequizapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GuestActivity extends Activity {

    private Button loginButton;
    private Button signUpButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);

        loginButton= (Button) findViewById(R.id.btn_loginGuest);
        signUpButton= (Button) findViewById(R.id.btn_signupGuest);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent li=new Intent(GuestActivity.this,MainActivity.class);
                startActivity(li);
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent si=new Intent(GuestActivity.this,SignUpOptionActivity.class);
                startActivity(si);
            }
        });
    }
}
