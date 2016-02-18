package com.example.pavle92.riddlequizapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends Activity
{
    EditText etxUser,etxPass;
    Button btnLogin,btnSignUp;
    Player player=null;
    Handler guiThread;
    private Context context;
    ImageView im;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context=this;
        guiThread= new Handler();
        pd=new ProgressDialog(MainActivity.this);
        etxUser=(EditText)findViewById(R.id.etx_username);
        etxPass=(EditText)findViewById(R.id.etx_password);
        btnLogin=(Button)findViewById(R.id.btn_login);
        btnSignUp=(Button)findViewById(R.id.btn_signup);
        im=(ImageView)findViewById(R.id.image);
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String user=etxUser.getText().toString();
                String pass=etxPass.getText().toString();

                if(isOnline()) {
                    if(!user.equals("") && !pass.equals(""))
                        getPlayer();
                    else
                        MakeToast("Username and password fields must be entered");
                }
                else {
                    MakeToast("Enable internet connection!");
                }



            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(in);
                etxUser.setText("");
                etxPass.setText("");
            }
        });
    }

    public void getPlayer()
    {
        player=null;
        ExecutorService transThread= Executors.newSingleThreadExecutor();
        transThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    guiProgressStart();
                    player = MyPlacesHTTPHelper.SendUserAndPass(etxUser.getText().toString(), etxPass.getText().toString());
                    Log.e("OOO", player.getIme() + " " + player.getPrezime());
                    guiNotifyUser();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }
    public boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    private  void guiProgressStart()
    {
        guiThread.post(new Runnable() {
            @Override
            public void run() {
                pd.setMessage("Checking username and password ");
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.show();

            }
        });
    }

    private  void guiNotifyUser() {
        guiThread.post(new Runnable() {
            @Override
            public void run() {

                if (!player.getIme().equals("")) {
                    MakeToast(player.getIme() + " " + player.getPrezime());
                    pd.cancel();
                    Intent in = new Intent(MainActivity.this, MainScreenActivity.class);
                    in.putExtra("UserName",etxUser.getText().toString());
                    player=null;
                    startActivity(in);
                    etxUser.setText("");
                    etxPass.setText("");


                    //finish();
                } else
                {
                    pd.cancel();
                    MakeToast("Wrong user name or password!");
                }

            }
        });
    }
    public void MakeToast(String s)
    {
        Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
    }

}
