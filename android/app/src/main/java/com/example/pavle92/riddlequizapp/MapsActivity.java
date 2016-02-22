package com.example.pavle92.riddlequizapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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

public class MapsActivity extends ActionBarActivity implements GoogleMap.OnCameraChangeListener,GoogleMap.OnMapLongClickListener,GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private ArrayList<Place> places;
    ArrayList<Marker> markers;
    ArrayList<Marker> markersPos;
    double lat=0;
    double log=0;
    Marker me;
    LocationManager locationManager;
    LocationListener listener;
    Circle circle;
    Location camLoc;
    private float currentZoom=15;
    boolean cam=false;
    private boolean first=false;
    private String userName;
    private int mod=3;
    private Handler guiThread;
    Timer timer;
    String userNamess="";
    private String name="";

    private void SavePreferance()
    {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(MapsActivity.this);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("lat", String.valueOf(camLoc.getLatitude()));
        editor.putString("log", String.valueOf(camLoc.getLongitude()));
        editor.apply();
        Log.e("Pref save",String.valueOf(lat+" "+log ));

    }
    private void LoadPreferance()
    {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(MapsActivity.this);
        camLoc.setLatitude(Double.valueOf(sharedPreferences.getString("lat","0")));
        camLoc.setLatitude(Double.valueOf(sharedPreferences.getString("log", "0")));
        Log.e("Pref load",String.valueOf(lat+" "+log ));
    }
    public boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==R.id.search && mod==0)
        {
            DBAdapterPlaces dbAdapterPlaces = new DBAdapterPlaces(this, userName);
            dbAdapterPlaces.OpenDB();
            ArrayList<String> imena=dbAdapterPlaces.getUserNames();
            dbAdapterPlaces.CloseDB();
            Set<String> hs = new HashSet<>();
            hs.addAll(imena);
            for(String ime:hs)
                  if(!ime.equals(userName))
                        userNamess=userNamess.concat(ime+" ");

            Intent in=new Intent(MapsActivity.this,Search.class);
            in.putExtra("imena",userNamess);
            startActivityForResult(in, 1234);
            userNamess="";

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        guiThread=new Handler();
        userName="";
        camLoc=new Location(LocationManager.NETWORK_PROVIDER);
        markersPos=new ArrayList<Marker>();

        locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);

        listener=new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {

                if(location!=null)
                {
                    lat=location.getLatitude();
                    log=location.getLongitude();
                    TrackMe();
                    if(!cam)
                    {

                        camLoc.setLatitude(lat);
                        camLoc.setLongitude(log);
                        setUpCamera();
                    }

                }
                cam=true;

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        markers=new ArrayList<Marker>();



        Bundle bnd=getIntent().getExtras();
        userName=bnd.getString("UserName");
        LoadPreferance();
        setUpMapIfNeeded();
        CheckBundle(bnd);
        mMap.setOnCameraChangeListener(this);
        mMap.setOnInfoWindowClickListener(this);
  //      getLocation();
  //      getUsersLocations(mod);
        Log.e("Mod", String.valueOf(mod));

    }

    private void getUsersLocations(int mod)
    {
        if(mod==0)
        {
            timer=new Timer();

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run()
                {

                    ExecutorService transThread = Executors.newSingleThreadExecutor();
                    transThread.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                if(lat!=0 && log!=0) {
                                    ArrayList<Player> players = MyPlacesHTTPHelper.getFriendsLocations(userName, String.valueOf(lat), String.valueOf(log));
                                    if (players != null)
                                    {

                                        guiNotifyUser(players);

                                    }


                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });


                }
            }, 5000, 15000);

        }

    }
    private void guiNotifyUser(final ArrayList<Player> players) {
        guiThread.post(new Runnable() {
            @Override
            public void run() {

                for (Marker m : markersPos)
                    m.remove();
                for (Player p : players) {

                    Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(p.getLatitude()), Double.parseDouble(p.getLongitude()))).title(p.getUser()).snippet("UserName: " + p.getUser()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person)));
                    markersPos.add(m);
                    Log.e("Timer1", "Timer1"+ p.getIme());
                }
            }
        });
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        setUpMapIfNeeded();
        getLocation();
        getUsersLocations(mod);

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        SavePreferance();
        locationManager.removeUpdates(listener);
        if(mod==0)
        {
            timer.cancel();
            timer.purge();

        }
        setResult(RESULT_OK);


    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        SavePreferance();
        locationManager.removeUpdates(listener);
        for(Marker m:markers)
            m.remove();
        if(mod==0)
        {
            timer.cancel();
            timer.purge();
            timer=null;
        }
        setResult(RESULT_OK);

    }



    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap(String imena)} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded()
    {
        //for(Marker m:markers)
        //    m.remove();
        getPlaces(mod);
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {

                setUpCamera();
                setUpMap(name);

            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
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

                    Marker m;
                    m = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(p.getLatitude()), Double.parseDouble(p.getLongitude()))).title(p.getName()).snippet("Mesto: " + p.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_myquestion)));
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

                        for (String in : imena)
                        {
                            Log.e("OPALALALA", in);
                            Marker m;
                            if (p.getUserName().equals(in))
                            {
                                if (p.isSolved())
                                {
                                    m = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(p.getLatitude()), Double.parseDouble(p.getLongitude()))).title(p.getName()).snippet("Hint: " + p.getHint()).icon(BitmapDescriptorFactory.fromResource(R.drawable.correct)));
                                } else
                                {

                                    m = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(p.getLatitude()), Double.parseDouble(p.getLongitude()))).title(p.getName()).snippet("by: " + p.getUserName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.image)));
                                }
                                m.setVisible(p.isVisible());
                                markers.add(m);
                            }

                        }
                    }

            }
            else
            {

                for (Place p : places)
                {
                    //     Log.e("Mesto",p.getName());


                        Marker m;

                            if (p.isSolved())
                            {
                                m = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(p.getLatitude()), Double.parseDouble(p.getLongitude()))).title(p.getName()).snippet("Hint: " + p.getHint()).icon(BitmapDescriptorFactory.fromResource(R.drawable.correct)));
                            } else
                            {

                                m = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(p.getLatitude()), Double.parseDouble(p.getLongitude()))).title(p.getName()).snippet("by: " + p.getUserName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.image)));
                            }
                            m.setVisible(p.isVisible());
                            markers.add(m);
                        }

                    }
                }
   }



    private  void TrackMe()
    {

        if (me != null && circle != null)
        {
            me.remove();
            circle.remove();
        }
        me = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, log)).title("You are here!"));
        circle = mMap.addCircle(new CircleOptions().center(new LatLng(lat, log)).strokeColor(Color.BLACK).strokeWidth(3).radius(2500));

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
    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     *
     */
    public void setUpCamera()
    {
        if(camLoc!=null)
        {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(camLoc.getLatitude(), camLoc.getLongitude()), currentZoom));

        }
        else
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), currentZoom));
    }


    public void MakeToast(String s)
    {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void getLocation()
    {
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
           locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,3000,10, listener);

            if(!first)
                Toast.makeText(this,"Getting you location...",Toast.LENGTH_LONG).show();

            first=true;
        }

        else

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000,0, listener);

            if(!first)
                Toast.makeText(this,"Getting you location...",Toast.LENGTH_LONG).show();

            first=true;

         }

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition)
    {

        if (cameraPosition.zoom != currentZoom)
        {
            currentZoom = cameraPosition.zoom;

        }
    }

    @Override
    public void onMapLongClick(LatLng latLng)
    {
        if(latLng!=null)
        {
            Intent in=new Intent(MapsActivity.this,NewPlace.class);
            in.putExtra("br",0);
            in.putExtra("latitude",latLng.latitude);
            in.putExtra("longitude",latLng.longitude);
            in.putExtra("userName",userName);
            startActivityForResult(in, 9000);
        }

    }
    public void CheckBundle(Bundle bnd)
    {

        if(bnd!=null)
        {

            mod=bnd.getInt("Mod");
            lat=bnd.getDouble("lat");
            log=bnd.getDouble("log");
            if(lat!=0 && log!=0 )
            {
                first=true;
                cam=true;
                currentZoom=18;
                camLoc=new Location(LocationManager.NETWORK_PROVIDER);
                camLoc.setLatitude(lat);
                camLoc.setLongitude(log);
                setUpCamera();
            }
            if(mod==1)
                mMap.setOnMapLongClickListener(this);

        }

    }
    @Override
    public void onInfoWindowClick(Marker marker)
    {

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
    public  void getPlaces(int mod)
    {
        DBAdapterPlaces dbp=new DBAdapterPlaces(this,userName);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==9000 && resultCode== Activity.RESULT_OK)
            MakeToast("Place added!");
        else
        if(requestCode==9890) {
            if (resultCode == Activity.RESULT_OK) {
                String lat = "";
                String log = "";
                Bundle b = data.getExtras();
                if (b != null) {
                    lat = b.getString("lat");
                    log = b.getString("log");
                }
                for (Marker m : markers)
                    if ((m.getPosition().latitude == Double.parseDouble(lat)) && (m.getPosition().longitude == Double.parseDouble(log))) {
                        DBAdapterPlaces dbAdapterPlaces = new DBAdapterPlaces(this, userName);
                        dbAdapterPlaces.OpenDB();
                        dbAdapterPlaces.UpdatePlaceSolved(lat, log);

                        dbAdapterPlaces.CloseDB();
                        updateScore(userName);
                        MakeToast("Place Solved, you earn 10 points");
                        m.remove();
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

    private void updateScore(final String userName) {
        ExecutorService transThread = Executors.newSingleThreadExecutor();
        transThread.submit(new Runnable() {
            @Override
            public void run() {
                try {

                String ret=MyPlacesHTTPHelper.UpdateScore(userName,1);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
