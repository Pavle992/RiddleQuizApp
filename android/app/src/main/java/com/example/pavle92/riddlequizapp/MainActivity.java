package com.example.pavle92.riddlequizapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends Activity
{

    //LOGIN
    public static final String PREFS_NAME = "LoginPrefs";
    //LOGIN
    EditText etxUser,etxPass;
    Button btnLogin,btnSignUp;
    Player player=null;
    Handler guiThread;
    private Context context;
    ImageView im;
    private ProgressDialog pd;

    //SignUP varables
    List<String> userNames;
    BluetoothAdapter bluetoothAdapter;
    Profile profile;
    //SignUp variables end

    ///FACEBOOK login components
    private AccessToken accessToken;

    LoginButton loginButton;
    ImageView iv;

    private CallbackManager mCallbackManager;
    FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            accessToken=loginResult.getAccessToken();
            profile=Profile.getCurrentProfile();
            if(profile!=null){

                Toast.makeText(MainActivity.this, "Cao "+profile.getName(), Toast.LENGTH_LONG).show();
//                String username=profile.getName();
//                String password=""+profile.hashCode();
                getPlayer(profile);
                guiNotifyUserFB();
            }
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

            if(!isOnline()){
                Toast.makeText(MainActivity.this, "No internet access", Toast.LENGTH_LONG).show();
            }
        }
    };


    /// FACEBOOK login part ended


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager=CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);


/*
         * Check if we successfully logged in before.
         * If we did, redirect to home page
         */
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings.getString("logged", "").toString().equals("logged")) {
            Intent intent1 = new Intent(MainActivity.this, MainScreenActivity.class);
            startActivity(intent1);
        }

        //after view created
        LoginManager.getInstance().logOut();
        //FACEBOOK login part

        iv= (ImageView) findViewById(R.id.image1);

        loginButton= (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
//        loginButton.setFragment(this);
        loginButton.registerCallback(mCallbackManager,mCallback);

        //FACEBOOK login part ended

        //SIGNUP PART

        bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        Log.e("DEVICE", bluetoothAdapter.getAddress());
        getUserNames();

        //SIGNUP PART END

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
    public void getPlayer(final Profile pr)
    {
        final String username=profile.getFirstName()+profile.getLastName();//!!! pr.getName();
        final String password= Integer.toString(pr.hashCode()).substring(0, 1);
        player=null;
        Thread trd=new Thread(
        /*ExecutorService transThread= Executors.newSingleThreadExecutor();
        transThread.submit(*/new Runnable() {
            @Override
            public void run() {
                try {
//                    guiProgressStart();
                    player = MyPlacesHTTPHelper.SendUserAndPass(username, password);
                    Log.e("OOO", player.getIme() + " " + player.getPrezime());
//                    guiNotifyUserFB();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        trd.start();
        try {
            trd.join();
        }
        catch (InterruptedException ir)
        {
            ir.printStackTrace();
        }
//        guiNotifyUserFB();

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

                    //make SharedPreferences object
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("logged", "logged");
                    editor.putString("UserName",etxUser.getText().toString());
                    editor.putString("UserPicture",BitmapToByteArrayConvert(player.getImg()));
                    editor.commit();


                    Intent in = new Intent(MainActivity.this, MainScreenActivity.class);
                   // in.putExtra("UserName",etxUser.getText().toString());
                    //in.putExtra("UserPicture",player.getImg());
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

    //perform, login or creates new profile if user don't exist
    private  void guiNotifyUserFB() {
//        guiThread.post(new Runnable() {
//            @Override
//            public void run() {

                if (!player.getIme().equals("")) {
                    //login

                    MakeToast(player.getIme() + " " + player.getPrezime());
                    pd.cancel();

                    //make SharedPreferences object
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("logged", "logged");
                    editor.putString("UserName",player.getUser());
                    editor.putString("UserPicture", BitmapToByteArrayConvert(player.getImg()));
                    editor.commit();



                    Intent in = new Intent(MainActivity.this, MainScreenActivity.class);
                   // in.putExtra("UserName",player.getUser());
                    //in.putExtra("UserPicture",player.getImg());
                    player = null;
                    startActivity(in);
                    etxUser.setText("");
                    etxPass.setText("");


                    //finish();
                } else {
                    //signup

                    pd.cancel();
                    //PERFORM SING UP PROCEDURE
                    final String idUser=profile.getId().toString();

                    getUserProfilePciture(idUser);

                    ExecutorService transThread = Executors.newSingleThreadExecutor();
                    String name = profile.getFirstName();
                    String lastName = profile.getLastName();
                    String user = profile.getFirstName()+profile.getLastName();//profile.getName();//!!! mora spojeno
                    String pass = Integer.toString(profile.hashCode()).substring(0,1);
                    String number = "000";
                    try {
                        Bitmap imageBitmap = ((BitmapDrawable) iv.getDrawable()).getBitmap();

                    if (isOnline()) {
                        player = new Player(name, lastName, user, pass, Integer.parseInt(number), imageBitmap);
                        player.setBtDevice(bluetoothAdapter.getAddress());
                        if (!CheckPlayer(player)) {
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


                            //make SharedPreferences object
                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("logged", "logged");
                            editor.putString("UserName",etxUser.getText().toString());
                            editor.putString("UserPicture",BitmapToByteArrayConvert(player.getImg()));
                            editor.commit();


                            Intent in = new Intent(MainActivity.this, MainScreenActivity.class);
                           // in.putExtra("UserName", player.getUser());
                            //in.putExtra("UserPicture",player.getImg());
                            player = null;
                            startActivity(in);

                            Toast.makeText(getApplicationContext(), "Created new Player", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), player.getIme() + " " + player.getPrezime() + " " + player.getPass() + " " + player.getUser() + " " + player.getBroj(), Toast.LENGTH_SHORT).show();
                            finish();
                        } else
                            Toast.makeText(getApplicationContext(), "Chose different username", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getApplicationContext(), "Enable internet connection!", Toast.LENGTH_SHORT).show();

                    //SIGN UP PROCEDURE END
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                }

            }
//        });
//    }
    public void MakeToast(String s)
    {
        Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();


        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        LoginManager.getInstance().logOut();
    }

    private void getUserProfilePciture(final String id){

        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {

                //stuff that updates ui
                String id=accessToken.getUserId();
                try {

                    URL imageURL = new URL("https://graph.facebook.com/" +
                            id+ "/picture?type=small");
                    Log.e("URL", imageURL.toString());

                    final Bitmap bitmap= BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv.setImageBitmap(bitmap);
                        }
                    });


                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                catch (IOException e) {

                    e.printStackTrace();
                }
            }
        });
        ///
        t.start();

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
    public boolean CheckPlayer(Player player)
    {
        boolean t=false;
        for(String userN:userNames)
            if(userN.equals(player.getUser()))
                t=true;

        return t;
    }

    private String BitmapToByteArrayConvert(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        String encoded = Base64.encodeToString(b, Base64.DEFAULT);
        return encoded;
    }


}
