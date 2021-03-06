package com.example.pavle92.riddlequizapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainScreenActivity extends ActionBarActivity implements OnMapReadyCallback ,
        GoogleMap.OnMapLongClickListener,GoogleMap.OnInfoWindowClickListener,GoogleMap.OnCameraChangeListener {

    public static final String PREFS_NAME = "LoginPrefs";
    public static final int MAX_SEEK_BAR_VALUE=3000;

    private String userName;
    private Handler guiThread;
    DBAdapterPlaces dbAdapter;
    private ProgressDialog pd;
    private Bitmap profPic;

    //MENU MENI
    private DrawerLayout mDrawerLayout;
    private ImageView prfpc;
    private TextView usrnm;
    //MAP
    private GoogleMap map;
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener;
    private Marker mMarker;
    private Circle circle;
    private ArrayList<Place> places;
    private ArrayList<Marker> markers;
    private ArrayList<Marker> markersPos;
    private int mod=3;
    private Location curLoc;
    private double lat=0.0;
    private double lon=0.0;
    private  Location camLoc;
    private boolean first;
    private float currentZoom=15;
    boolean cam=false;
    private String name="";
    private boolean m3=false;
    private boolean m2=false;
    private DBAdapterPlaces db;
    private Marker me;
    private String userNamess;
    private Timer timer;
    private boolean m1;
    private SeekBar seekBar;
    private Double radious_circle=700.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        //Shared pref
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        userName=settings.getString("UserName", "");
        profPic=Ba64StringToBitmap(settings.getString("UserPicture", ""));

        db=new DBAdapterPlaces(this,userName);
        markersPos=new ArrayList<Marker>();
        markers=new ArrayList<Marker>();

        //MAP
        setUpMapIfNeeded();
        myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                if (mMarker != null) {
                    mMarker.remove();
                    circle.remove();
                }
                curLoc=location;
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                mMarker=map.addMarker(new MarkerOptions().position(loc).title("Your Location"));
                circle = map.addCircle(new CircleOptions().center(loc).strokeColor(Color.BLACK).strokeWidth(3).radius(radious_circle));
                if(map != null){
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 14.0f));
                }
                boolean up=false;


                db.OpenDB();

                for (Place p : places)
                {
                    float[] distance = new float[2];

                    Location.distanceBetween(Double.parseDouble(p.getLatitude()),Double.parseDouble(p.getLongitude()), circle.getCenter().latitude, circle.getCenter().longitude, distance);

                    if (distance[0] < circle.getRadius()) {
                        p.setVisible(true);
                        db.UpdatePlaceVisible(String.valueOf(p.getLatitude()),String.valueOf(p.getLongitude()));
                        up=true;
                    }


                }
                db.CloseDB();

                if (up && m3){
                    getPlaces(mod);

                    setUpMap(name);
                }
                else if(up & m2)
                {
                    getPlaces(mod);

                    setUpMap(name);
                }
                else if(up & m1){
                    getUsersLocations(mod);
                    Log.d("FriendsModGoogle", "" + mod);
                }
            }

        };
        map.setOnMyLocationChangeListener(myLocationChangeListener);

        CheckMod();

        map.setOnCameraChangeListener(this);
        map.setOnInfoWindowClickListener(this);
        //      getLocation();
        //      getUsersLocations(mod);
        Log.e("Mod", String.valueOf(mod));

        //MENU MENI
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        prfpc= (ImageView) findViewById(R.id.userPctr);
        usrnm= (TextView) findViewById(R.id.userNm);

        //SEEKBAR
        seekBar= (SeekBar) findViewById(R.id.radiusBar);
        seekBar.setMax(MAX_SEEK_BAR_VALUE);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar inUse, int progress, boolean b) {
                final int prog=progress;
//             Thread t1 = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        radious_circle=(double)prog;
//                        setUpMap(name);
//                    }
//                });
//                t1.start();
//
//                try {
//                    t1.join();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                radious_circle=(double)progress;
                //setUpMap(name);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                MakeToast("You are changing radius.");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setVisibility(View.GONE);
            }
        });

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

                switch(id) {
                    case R.id.logout:
                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.remove("logged");
                        editor.commit();


                        Intent in = new Intent(MainScreenActivity.this, MainActivity.class);
                        startActivity(in);
                        finish();
                        break;
                    case R.id.my_places:

                        LocationManager lm=(LocationManager)getSystemService(LOCATION_SERVICE);
                        if(m2){
                            for (Marker m:markers)
                                m.remove();

                            m2=false;
                        }
                        else if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
                        {
//                            Intent in12=new Intent(MainScreenActivity.this,MapsActivity.class);
//                            in12.putExtra("UserName",userName);
//                            in12.putExtra("Mod",0);
//                            startActivity(in12);
                            //CHANGE MAPS
                            mod=1;
                            setUpMap("");

                            m2=true;
                            m3=false;
                            m1=false;
                            CheckMod();
                        }
                        else
                        {
                            showSettingsAlert();
                        }

                        break;
                    case  R.id.high_score:
                        Intent in11 = new Intent(MainScreenActivity.this, HighScore.class);
                        in11.putExtra("username",userName);
                        startActivity(in11);
                        break;
                    case R.id.my_friends:
                        Intent in22 = new Intent(MainScreenActivity.this, Friends.class);
                        in22.putExtra("UserName", userName);
                        startActivity(in22);
                        break;
                    case R.id.my_location:
//                        Intent in123 = new Intent(MainScreenActivity.this, MyPlacesList.class);
//                        in123.putExtra("UserName",userName);
//                        startActivity(in123);

                        LocationManager lm1=(LocationManager)getSystemService(LOCATION_SERVICE);
                        if(m3)
                        {//if already clicked
                            for (Marker m:markers)
                                m.remove();

                            m3=false;
                        }
                        else  if (lm1.isProviderEnabled(LocationManager.GPS_PROVIDER))
                        {
                            //CHANGE MAPS
                            mod=2;
                            setUpMap("");

                            m3=true;
                            m2=false;
                            m1=false;
                        }
                        else
                        {
                            showSettingsAlert();
                        }
                        break;
                    case R.id.scanNN:
                        Intent in33 = new Intent(MainScreenActivity.this, ScanActivity.class);
//            in.putExtra("UserName",userName);
                       // startActivity(in33);
                        in33.putExtra("lat", curLoc.getLatitude());
                        in33.putExtra("log", curLoc.getLongitude());

                        startActivityForResult(in33,9890);
                    break;
                    case R.id.model3dItem:
                        Intent in66 = new Intent(MainScreenActivity.this, ARSimple.class);
//            in.putExtra("UserName",userName);
                        // startActivity(in33);
//                        in66.putExtra("lat", curLoc.getLatitude());
//                        in66.putExtra("log", curLoc.getLongitude());
                        in66.putExtra("lat", curLoc.getLatitude());
                        in66.putExtra("log", curLoc.getLongitude());

                        startActivityForResult(in66,9899);
                        //startActivity(in66);
                        break;
                    case R.id.about_app:
                        Intent in44=new Intent(MainScreenActivity.this,About.class);
                        startActivity(in44);
                        break;
                    default:
                }
                return true;
            }
        });
        //NAVIGATION VIEW

//        ImageView img=(ImageView)findViewById(R.id.imageView2);
//        img.setImageResource(R.drawable.ic_ic_question_mark_hd_wallpaper1);

//        userName="";
//        profPic=null;
        guiThread=new Handler();
        pd=new ProgressDialog(MainScreenActivity.this);

        //Bundle bnd=getIntent().getExtras();
//        //Shared pref
//        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//        userName=settings.getString("UserName","");
//        profPic=Ba64StringToBitmap(settings.getString("UserPicture",""));
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

            if(m1)
            {//if already clicked
                for (Marker m : markersPos)
                    m.remove();

                m1=false;
            }

            if(m3)
            {//if already clicked
                for (Marker m:markers)
                    m.remove();

                m3=false;
            }
            else  if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
//                Intent in=new Intent(this,MapsActivity.class);
//                in.putExtra("UserName",userName);
//                in.putExtra("Mod",0);
//                startActivity(in);
                //CHANGE MAPS
                mod=2;
                setUpMap("");

                m3=true;
                m2=false;
                m1=false;
            }
            else
            {
                showSettingsAlert();
            }

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
            getMyPlacesRefresh();

            //MAYBE
            getPlaces(mod);
            setUpMap(name);
           // TrackMe();
        }
        else if(id==R.id.myplaces)
        {
            Intent in = new Intent(this, MyPlacesList.class);
            in.putExtra("UserName",userName);
            startActivity(in);
        }
        else if(id==R.id.search1)
        {

            if(m1)
            {//if already clicked
                for (Marker m : markersPos)
                    m.remove();

                m1=false;
                mod=3;
            }
            else {
                mod = 0;
                getUsersLocations(mod);
                m3=false;
                m2=false;
                m1=true;
            }
        }
        else if (id == R.id.radious){

            seekBar.setProgress(radious_circle.intValue());
            seekBar.setVisibility(View.VISIBLE);


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
        guiThread.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainScreenActivity.this, msg, Toast.LENGTH_SHORT).show();
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
    public boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        map.addMarker(new MarkerOptions()
//                .position(new LatLng(0, 0))
//                .title("Marker"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded()
    {
        //for(Marker m:markers)
        //    m.remove();
       // getPlaces(mod);
        // Do a null check to confirm that we have not already instantiated the map.
        getPlaces(mod);
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            map.setMyLocationEnabled(true);
            // Check if we were successful in obtaining the map.
            if (map != null) {

                setUpCamera();
                setUpMap(name);
                //setUpMap();

            }
        }
    }

    private void setUpMap(){
        map.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Marker"));
        map.setMyLocationEnabled(true);
    }
    public  void getPlaces(int mod)
    {
        DBAdapterPlaces dbp=new DBAdapterPlaces(this,userName);
        Log.d("UserZaBAzu",userName);
        dbp.OpenDB();
        places=dbp.getPlaceses();
        dbp.CloseDB();

        if(mod==1)
        {
            for (Iterator<Place> it = places.iterator(); it.hasNext(); ) {
                Place p = it.next();

                if (!p.getUserName().equals(userName)) {
                    it.remove();
                }
            }
        }
        else if(mod==0)
        {
            for (Iterator<Place> it = places.iterator(); it.hasNext(); )
            {
                Place p = it.next();

                if (p.getUserName().equals(userName))
                {
                    it.remove();
                }
            }
        }

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        if(marker.getSnippet().contains("by"))
        {
            if(!isOnline())
                Toast.makeText(this,"Enable internet connection before answering!",Toast.LENGTH_SHORT).show();
            else
            {
                String[] niz=marker.getSnippet().split(" ");
                Intent in=new Intent(this,AnswerBox.class);

                Toast.makeText(this,marker.getTitle(),Toast.LENGTH_SHORT).show();

                in.putExtra("lat", marker.getPosition().latitude);
                in.putExtra("log", marker.getPosition().longitude);
                in.putExtra("userName",userName);
                in.putExtra("userNameQ",niz[1]);
                Log.e("QQQQ",niz[1]);
                startActivityForResult(in, 9890);
            }

        }



    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    public void CheckMod()
    {

            if(curLoc!=null) {
                lat = curLoc.getLatitude();
                lon = curLoc.getLongitude();
            }
            if(lat!=0 && lon!=0 )
            {
                first=true;
                cam=true;
                currentZoom=18;
                camLoc = new Location(LocationManager.NETWORK_PROVIDER);
                camLoc.setLatitude(lat);
                camLoc.setLongitude(lon);
                //setUpCamera();
            }
            if(mod==1)
                map.setOnMapLongClickListener(this);

        }

    @Override
    public void onMapLongClick(LatLng latLng) {

        if(latLng!=null)
        {
            Intent in=new Intent(MainScreenActivity.this,NewPlace.class);
            in.putExtra("br",0);
            in.putExtra("latitude",latLng.latitude);
            in.putExtra("longitude",latLng.longitude);
            in.putExtra("userName",userName);
            startActivityForResult(in, 9000);
        }

    }

    public void setUpCamera()
    {
        if(camLoc!=null)
        {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(camLoc.getLatitude(), camLoc.getLongitude()), currentZoom));

        }
        else
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), currentZoom));
    }
    private void setUpMap(String ime)
    {


        for (Marker m:markers)
            m.remove();

        markers=new ArrayList<Marker>();

        if(places!=null)
        {
            if (mod==1)
            {
                for(Place p:places)
                {
                    if(!p.getUserName().equals(userName))
                        continue;
                    Marker m;
                    m = map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(p.getLatitude()), Double.parseDouble(p.getLongitude()))).title(p.getName()).snippet("Mesto: " + p.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_myquestion)));
                    m.setVisible(true);
                    markers.add(m);

                }
            }
            else  if (mod == 0 && !ime.equals(""))
            {
                String[] imena = ime.split(" ");
                for (Place p : places)
                {
                    Log.e("Mesto",p.getName());
                    if(p.getUserName().equals(userName))
                        continue;

                    for (String in : imena)
                    {
                        Log.e("OPALALALA", in);
                        Marker m;
                        if (p.getUserName().equals(in))
                        {
                            if (p.isSolved())
                            {
                                m = map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(p.getLatitude()), Double.parseDouble(p.getLongitude()))).title(p.getName()).snippet("Hint: " + p.getHint()).icon(BitmapDescriptorFactory.fromResource(R.drawable.correct)));
                            } else
                            {

                                m = map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(p.getLatitude()), Double.parseDouble(p.getLongitude()))).title(p.getName()).snippet("by: " + p.getUserName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.image)));
                            }
                            m.setVisible(p.isVisible());
                            markers.add(m);
                        }

                    }
                }

            }
            else if (mod ==2)
            {


                for (Place p : places)
                {
                    //     Log.e("Mesto",p.getName());

                    if(p.getUserName().equals(userName))
                        continue;

                    Marker m;

                    if (p.isSolved())
                    {
                        m = map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(p.getLatitude()), Double.parseDouble(p.getLongitude()))).title(p.getName()).snippet("Hint: " + p.getHint()).icon(BitmapDescriptorFactory.fromResource(R.drawable.correct)));
                    } else
                    {

                        m = map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(p.getLatitude()), Double.parseDouble(p.getLongitude()))).title(p.getName()).snippet("by: " + p.getUserName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.image)));
                    }
                    if(insideCircle(p,circle) || p.isSolved())
                       // m.setVisible(p.isVisible());
                        m.setVisible(true);
                    else
                        m.setVisible(false);
                    markers.add(m);
                }

            }
        }
    }

    private boolean insideCircle(Place place, Circle circle){
        float[] distance = new float[2];
        boolean inRadius=false;
        Location.distanceBetween(Double.parseDouble(place.getLatitude()),Double.parseDouble(place.getLongitude()), circle.getCenter().latitude, circle.getCenter().longitude, distance);

        Log.d("Razlika", String.valueOf(distance[0]));
        Log.d("Radius", String.valueOf(circle.getRadius()));
        if (distance[0] < circle.getRadius()) {
            inRadius=true;
            Log.d("Proso", String.valueOf(circle.getRadius()));
        }
        return inRadius;
    }
    private  void TrackMe()
    {

        if (me != null && circle != null)
        {
            me.remove();
            circle.remove();
        }
        me = map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("You are here!"));
        circle = map.addCircle(new CircleOptions().center(new LatLng(lat, lon)).strokeColor(Color.BLACK).strokeWidth(3).radius(radious_circle));

        boolean up=false;

        DBAdapterPlaces db=new DBAdapterPlaces(this,userName);
        db.OpenDB();

        for (Place p : places)
        {
            float[] distance = new float[2];

            Location.distanceBetween(Double.parseDouble(p.getLatitude()),Double.parseDouble(p.getLongitude()), circle.getCenter().latitude, circle.getCenter().longitude, distance);

            if (distance[0] < circle.getRadius()) {
                p.setVisible(true);
                db.UpdatePlaceVisible(String.valueOf(p.getLatitude()),String.valueOf(p.getLongitude()));
                up=true;
            }


        }
        db.CloseDB();

        if (up)
            getPlaces(mod);

        setUpMap(name);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==9000 && resultCode== Activity.RESULT_OK)
            MakeToast("Place added!");
        else
        if(requestCode==9890) {
            //qrcode or barcode
            if (resultCode == Activity.RESULT_OK) {
                String lat = "";
                String log = "";
                String rd="";
                String ht="";
                String sl="";
                Bundle b = data.getExtras();
                if (b != null) {
                    lat = b.getString("lat");
                    log = b.getString("log");
                    rd=b.getString("ridle");
                    ht=b.getString("hint");
                    sl=b.getString("solution");
                }
                boolean qft=true;//question from team
                for (Marker m : markers) {
                    if ((m.getPosition().latitude == Double.parseDouble(lat)) && (m.getPosition().longitude == Double.parseDouble(log))) {
                        DBAdapterPlaces dbAdapterPlaces = new DBAdapterPlaces(this, userName);
                        dbAdapterPlaces.OpenDB();
                        dbAdapterPlaces.UpdatePlaceSolved(lat, log);

                        dbAdapterPlaces.CloseDB();
                        updateScore(userName);
                        MakeToast("Place Solved, you earn 10 points");
                        m.remove();
                        qft = false;
                    }
                }
                if(qft){
                    //question from team QrScaned
                    DBAdapterPlaces dbAdapterPlaces = new DBAdapterPlaces(this, userName);
                    dbAdapterPlaces.OpenDB();
                    dbAdapterPlaces.SavePlace(new Place(lat,log,name,rd,sl,ht));
                    dbAdapterPlaces.UpdatePlaceSolved(lat, log);

                    dbAdapterPlaces.CloseDB();
                    updateScore(userName);

                }

            }


        }
        else
        if(requestCode==9899) {
            //3dmodel
            if (resultCode == Activity.RESULT_OK) {
                String lat = "";
                String log = "";
                String rd="";
                String ht="";
                String sl="";
                Bundle b = data.getExtras();
                if (b != null) {
                    //meanjaj!!!
                    lat = b.getString("lat");
                    log = b.getString("log");
                    rd=b.getString("ridle");
                    ht=b.getString("hint");
                    sl=b.getString("solution");
                }
                boolean qft=true;//question from team
                for (Marker m : markers) {
                    if ((m.getPosition().latitude == Double.parseDouble(lat)) && (m.getPosition().longitude == Double.parseDouble(log))) {
                        DBAdapterPlaces dbAdapterPlaces = new DBAdapterPlaces(this, userName);
                        dbAdapterPlaces.OpenDB();
                        dbAdapterPlaces.UpdatePlaceSolved(lat, log);

                        dbAdapterPlaces.CloseDB();
                        updateScore(userName);
                        MakeToast("Place Solved, you earn 10 points");
                        m.remove();
                        qft = false;
                    }
                }
                if(qft){
                    //question from team QrScaned
                    DBAdapterPlaces dbAdapterPlaces = new DBAdapterPlaces(this, userName);
                    dbAdapterPlaces.OpenDB();
                    dbAdapterPlaces.SavePlace(new Place(lat,log,name,rd,sl,ht));
                    dbAdapterPlaces.UpdatePlaceSolved(lat, log);

                    dbAdapterPlaces.CloseDB();
                    updateScore(userName);

                }

            }


        }
        else if(requestCode==1234)
        {
            if (resultCode==RESULT_OK)
            {
                name = data.getStringExtra("retStr");

            }
        }

        getPlaces(mod);
        setUpMap(name);
        TrackMe();
    }

    public void MakeToast(String s)
    {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
    private void updateScore(final String userName) {
        ExecutorService transThread = Executors.newSingleThreadExecutor();
        transThread.submit(new Runnable() {
            @Override
            public void run() {
                try {

                    String ret = MyPlacesHTTPHelper.UpdateScore(userName, 1);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
    private void getMyPlacesRefresh()
    {
        ExecutorService transThread = Executors.newSingleThreadExecutor();
        transThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    guiProgressStart("Refreshing places");
                    ArrayList<Place> placesServer = MyPlacesHTTPHelper.getMyPlaces(userName);
                    Log.e("User", userName);
                    db.OpenDB();
                    ArrayList<Place> myPlaces = db.getMyPlaceses(userName);
                    for (Place place : placesServer) {
                        Log.e("MestoServer  ", place.getName() + " " + place.getLatitude() + " " + place.getLongitude());
                        int ima = 0;
                        for (Place p : myPlaces) {
                            Log.e("MestoBaza  ", p.getName() + " " + p.getLatitude().substring(0, 7) + " " + p.getLongitude().substring(0, 7));
                            if (place.getLongitude().substring(0, 7).equals(p.getLongitude().substring(0, 7)) && place.getLatitude().substring(0, 7).equals(p.getLatitude().substring(0, 7)))
                                ima = 1;
                        }
                        if (ima == 0) {
                            Log.e("Dodato u bazu  ", place.getName());
                            db.SavePlace(place);
                        } else
                            Log.e("NIJE!Dodato u bazu  ", place.getName());
                    }


                    db.CloseDB();
                    guiProgressFinish(1);
                    //     guiNotifyUser("Mesta su osvezena!");

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }
    private  void guiProgressFinish(final int a)
    {
        guiThread.post(new Runnable() {
            @Override
            public void run() {
                pd.cancel();
                if (a == 1)
                    MakeToast("Downloaded");
                else
                    MakeToast("Uploaded");
            }
        });
    }
    private void guiNotifyUser(final ArrayList<Player> players) {
        guiThread.post(new Runnable() {
            @Override
            public void run() {

                for (Marker m : markersPos)
                    m.remove();
                for (Player p : players) {

                    Marker m = map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(p.getLatitude()), Double.parseDouble(p.getLongitude()))).title(p.getUser()).snippet("UserName: " + p.getUser()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person)));
                    markersPos.add(m);
                    Log.e("Timer1", "Timer1"+ p.getIme());
                }
            }
        });
    }
    private void getUsersLocations(final int mod)
    {
        if(mod==0)
        {
            timer=new Timer();

            lat=curLoc.getLatitude();
            lon=curLoc.getLongitude();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run()
                {

                    ExecutorService transThread = Executors.newSingleThreadExecutor();
                    transThread.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {


                                if(lat!=0 && lon!=0 && mod==0) {
                                    ArrayList<Player> players = MyPlacesHTTPHelper.getFriendsLocations(userName, String.valueOf(lat), String.valueOf(lon));
                                    if (players != null)
                                    {
                                        Log.d("FriendsModSearch",""+mod);
                                        guiNotifyUser(players);

                                    }


                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });


                }
            }, 1000, 2000);

        }

    }
}
