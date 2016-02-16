package com.example.pavle92.riddlequizapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainScreenActivity extends ActionBarActivity {

    private String userName;
    private Handler guiThread;
    DBAdapterPlaces dbAdapter;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        ImageView img=(ImageView)findViewById(R.id.imageView2);
        img.setImageResource(R.drawable.ic_ic_question_mark_hd_wallpaper1);

        userName="";
        guiThread=new Handler();
        pd=new ProgressDialog(MainScreenActivity.this);

        Bundle bnd=getIntent().getExtras();
        if(bnd!=null)
            userName=bnd.getString("UserName");
        Log.e("User1", userName);

        dbAdapter=new DBAdapterPlaces(MainScreenActivity.this,userName);

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
                    try
                    {
                        guiProgressStart("Getting places from server");
                        ArrayList<Place> places=MyPlacesHTTPHelper.getPlaces(userName);
                        Log.e("userName",userName);
                        dbAdapter.OpenDB();
                        ArrayList<Place> placesAll=dbAdapter.getPlaceses();
                        for (Place place:places)
                        {
                            Log.e("MestoServer  ",place.getName());
                            int ima=0;
                            for (Place p : placesAll)
                            {
                                Log.e("MestoBaza  ",p.getName());
                                if (place.getLongitude().equals(p.getLongitude()) && place.getLatitude().equals(p.getLatitude()))
                                    ima=1;
                            }
                            if(ima==0)
                            {
                                dbAdapter.SavePlace(place);
                                Log.e("Dodato  ", place.getName()+" "+place.getLongitude()+" "+place.getLatitude());
                            }
                            else
                                Log.e("NIJE!Dodato  ",place.getName());
                        }

                        dbAdapter.CloseDB();

                        guiNotifyUser("Places downloaded!");

                    }
                    catch (Exception e)
                    {
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
}
