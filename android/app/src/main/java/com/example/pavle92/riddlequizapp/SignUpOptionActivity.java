package com.example.pavle92.riddlequizapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignUpOptionActivity extends Activity {

    private Button classicSignUp;
    private Button fbSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_option);

        classicSignUp= (Button) findViewById(R.id.btn_signUpCr);
        classicSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent si=new Intent(SignUpOptionActivity.this,SignUpActivity.class);
                startActivity(si);
            }
        });

    }
}
