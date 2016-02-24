package com.example.pavle92.riddlequizapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainScreenActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "LoginPrefs";

    private String userName;
    private Handler guiThread;
    DBAdapterPlaces dbAdapter;
    private ProgressDialog pd;
    private Bitmap profPic;

    //MENU MENI
    private DrawerLayout mDrawerLayout;
    private ImageView prfpc;
    private TextView usrnm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);


        //MENU MENI
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        prfpc= (ImageView) findViewById(R.id.userPctr);
        usrnm= (TextView) findViewById(R.id.userNm);
        //TOOLBAR

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_home_black_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //TOOLBAR END


        //NAVIGATION VIEW
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
//                Toast.makeText(MainScreenActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();

                int id=menuItem.getItemId();


                if (id == R.id.logout)
                {
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.remove("logged");
                    editor.commit();


                    Intent in =new Intent(MainScreenActivity.this,MainActivity.class);
                    startActivity(in);
                    finish();
                }


                return true;
            }
        });
        //NAVIGATION VIEW

        ImageView img=(ImageView)findViewById(R.id.imageView2);
        img.setImageResource(R.drawable.ic_ic_question_mark_hd_wallpaper1);

        userName="";
        profPic=null;
        guiThread=new Handler();
        pd=new ProgressDialog(MainScreenActivity.this);

        //Bundle bnd=getIntent().getExtras();
        //Shared pref
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        userName=settings.getString("UserName","");
        profPic=Ba64StringToBitmap(settings.getString("UserPicture",""));
//        if(bnd!=null) {
//            userName = bnd.getString("UserName");
//            profPic=(Bitmap)bnd.getParcelable("UserPicture");
//
//        }
        Log.e("User1", userName);

        dbAdapter=new DBAdapterPlaces(MainScreenActivity.this,userName);

        //MENU MENI
        if(profPic!= null){
            prfpc.setImageBitmap(profPic);
        }
        if(userName!= null){
            usrnm.setText(userName);
        }




 //       dbAdapter.OpenDB();
 //       dbAdapter.ClearAll();
 //       dbAdapter.CloseDB();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //MENU MENI
        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                return true;
        }


        if(id==R.id.show_map)
        {
            LocationManager lm=(LocationManager)getSystemService(LOCATION_SERVICE);
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                Intent in=new Intent(this,MapsActivity.class);
                in.putExtra("UserName",userName);
                in.putExtra("Mod",0);
                startActivity(in);
            }
            else
            {
                showSettingsAlert();
            }

        }
        else if (id == R.id.highscore)
        {

            Intent in = new Intent(this, HighScore.class);
            in.putExtra("username",userName);
            startActivity(in);
        }
        else if(id==R.id.refresh)
        {
            Toast.makeText(this, "Getting places!", Toast.LENGTH_SHORT).show();
            ExecutorService transThread = Executors.newSingleThreadExecutor();
            transThread.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        guiProgressStart("Getting places from server");
                        ArrayList<Place> places = MyPlacesHTTPHelper.getPlaces(userName);
                        Log.e("userName", userName);
                        dbAdapter.OpenDB();
                        ArrayList<Place> placesAll = dbAdapter.getPlaceses();
                        for (Place place : places) {
                            Log.e("MestoServer  ", place.getName());
                            int ima = 0;
                            for (Place p : placesAll) {
                                Log.e("MestoBaza  ", p.getName());
                                if (place.getLongitude().equals(p.getLongitude()) && place.getLatitude().equals(p.getLatitude()))
                                    ima = 1;
                            }
                            if (ima == 0) {
                                dbAdapter.SavePlace(place);
                                Log.e("Dodato  ", place.getName() + " " + place.getLongitude() + " " + place.getLatitude());
                            } else
                                Log.e("NIJE!Dodato  ", place.getName());
                        }

                        dbAdapter.CloseDB();

                        guiNotifyUser("Places downloaded!");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        else if(id==R.id.myfriends)
        {
            Intent in = new Intent(this, Friends.class);
            in.putExtra("UserName",userName);
            startActivity(in);
        }
        else if(id==R.id.myplaces)
        {
            Intent in = new Intent(this, MyPlacesList.class);
            in.putExtra("UserName",userName);
            startActivity(in);
        }
        else if(id==R.id.scan)
        {
            Intent in = new Intent(this, ScanActivity.class);
//            in.putExtra("UserName",userName);
            startActivity(in);
        }
        return super.onOptionsItemSelected(item);
    }
    private  void guiProgressStart(final String msg)
    {
        guiThread.post(new Runnable() {
            @Override
            public void run() {
                pd.setMessage(msg);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.show();

            }
        });
    }

    private  void guiNotifyUser(final String msg)
    {
        guiThread.post(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(MainScreenActivity.this,msg,Toast.LENGTH_SHORT).show();
                pd.cancel();
            }
        });
    }
    public void showSettingsAlert()
    {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("GPS SETTINGS");

        alertDialog.setMessage("GPS is not enabled! Want to go to settings menu?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }
    private Bitmap Ba64StringToBitmap(String encoded){
        byte[] imageAsBytes = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
        Bitmap retb= BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        return retb;
    }
}
