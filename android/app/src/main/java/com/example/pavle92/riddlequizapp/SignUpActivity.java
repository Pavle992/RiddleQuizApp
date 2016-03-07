package com.example.pavle92.riddlequizapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SignUpActivity extends Activity implements View.OnClickListener
{

    EditText etxUser,etxPass,etxName,etxLastName,etxNumber;
    Button btnTakePicture,btnOk;
    ImageView imageView;
    Bitmap imageBitmap;
    Player player;
    List<String> userNames;
    ArrayList<Player> players;
    BluetoothAdapter bluetoothAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        Log.e("DEVICE", bluetoothAdapter.getAddress());
        getUserNames();

        etxName=(EditText)findViewById(R.id.reg_firstname);
        etxLastName=(EditText)findViewById(R.id.reg_lastname);
        etxUser=(EditText)findViewById(R.id.reg_username);
        etxPass=(EditText)findViewById(R.id.reg_password);
        etxNumber=(EditText)findViewById(R.id.reg_phonenumber);

        btnTakePicture=(Button)findViewById(R.id.btn_take_picture);
        btnTakePicture.setOnClickListener(this);

        btnOk=(Button)findViewById(R.id.finish);
        btnOk.setOnClickListener(this);

        imageView=(ImageView)findViewById(R.id.picture);
        imageView.setVisibility(View.INVISIBLE);

    }


    @Override
    public void onClick(View view)
    {
        if (view==btnOk)
        {
            ExecutorService transThread= Executors.newSingleThreadExecutor();
            String name=etxName.getText().toString();
            String lastName=etxLastName.getText().toString();
            String user=etxUser.getText().toString();
            String pass=etxPass.getText().toString();
            String number=etxNumber.getText().toString();

            //Ubaci za sliku i okvir za sliku

            if(name.compareTo("") == 0  || lastName.compareTo("") == 0   || user.compareTo("") == 0   || pass.compareTo("") == 0   || number.compareTo("") == 0 )
            {
                Toast.makeText(this,"Complete all fields!",Toast.LENGTH_SHORT).show();
            }
            else
            {
                if(isOnline())
                {
                    player = new Player(name, lastName, user, pass, Integer.parseInt(number), imageBitmap);
                    player.setBtDevice(bluetoothAdapter.getAddress());
                    if(!CheckPlayer(player))
                    {
                        transThread.submit(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    MyPlacesHTTPHelper.SendMyPlayer(player);
                                    //prebaci ovde odg od servera i na osnovu njega ispitai por da li je player upisan
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });
                        Toast.makeText(this, "Created new Player", Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, player.getIme() + " " + player.getPrezime() + " " + player.getPass() + " " + player.getUser() + " " + player.getBroj(), Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(SignUpActivity.this,MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else
                        Toast.makeText(this, "Chose different username", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(this, "Enable internet connection!", Toast.LENGTH_SHORT).show();




            }

        }

        else if(view==btnTakePicture)
        {
            open();
        }
    }
    public  byte[] getBitmapAsByteArray(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
    public void getUserNames()
    {
        ExecutorService transThread= Executors.newSingleThreadExecutor();
        transThread.submit(new Runnable() {

            @Override
            public void run() {

                try {

                    userNames = MyPlacesHTTPHelper.getPlayers();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
    }


    private static String inputStreamToString(InputStream is) {
        String line = "";
        StringBuilder total = new StringBuilder();
        BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        try {
            while ((line = bf.readLine()) != null) {
                total.append(line);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return total.toString();
    }

    public boolean CheckPlayer(Player player)
    {
        boolean t=false;
        for(String userN:userNames)
            if(userN.equals(player.getUser()))
                 t=true;

        return t;
    }
    public void open()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(imageBitmap);
        }
    }
    public boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
