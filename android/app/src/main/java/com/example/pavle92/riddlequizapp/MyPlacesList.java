package com.example.pavle92.riddlequizapp;

import android.app.Activity;
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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MyPlacesList extends ActionBarActivity implements  ListView.OnItemClickListener {

    ArrayList<Place> places;
    ArrayList<String> lista;
    DBAdapterPlaces dbp;
    ListView myPlaceList;
    String userName;
    private Handler guiThread;
    ProgressDialog pd;

    public void showSettingsAlert()
    {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("GPS SETTINGS");

        alertDialog.setMessage("GPS is not enabled! Want to go to settings menu?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
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
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places_list);

        guiThread=new Handler();
        userName="";
        pd=new ProgressDialog(this);
        places=null;
        lista=null;

        Bundle bnd=getIntent().getExtras();
        userName=bnd.getString("UserName");
        dbp=new DBAdapterPlaces(this,userName);

        myPlaceList=(ListView) findViewById(R.id.my_places_list);
        getPlaces();
        myPlaceList.setOnItemClickListener(this);
        myPlaceList.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) contextMenuInfo;

                Place place = places.get(info.position);
                contextMenu.setHeaderTitle(place.getName());

                contextMenu.add(0, 1, 1, "Upload");
                contextMenu.add(0, 2, 2, "Edit");
                contextMenu.add(0, 3, 3, "Delete");
                contextMenu.add(0, 4, 4, "Show on map");


            }
        });


    }

    public void getPlaces()
    {
        lista=new ArrayList<String>();
        dbp.OpenDB();
        places=dbp.getMyPlaceses(userName);
        dbp.CloseDB();

        if (places!=null)
        {
            for(Place p:places)
            {
                lista.add(p.getName());
                Log.e("TTTTT",p.getLatitude()+" "+p.getLongitude());
            }
            final ArrayAdapter<String> listAd= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lista);
            myPlaceList.setAdapter(listAd);

        }
        else
            Toast.makeText(this,"No places",Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        Intent in;
        final AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId()==1)
        {
            if(places.get(info.position).isUpload())
            {
                Toast.makeText(MyPlacesList.this,"Vec je uploadovano",Toast.LENGTH_SHORT).show();
            }
            else
            {
                guiProgressStart("Uploading place");
                //Provera da li je vec uploadovano, jos jedna boolean promenljiva kod mesta.
                ExecutorService transThread = Executors.newSingleThreadExecutor();
                transThread.submit(new Runnable() {
                    @Override
                    public void run() {
                        try
                        {
                            String dodato = MyPlacesHTTPHelper.SendMyPlace(places.get(info.position), userName);
                          //  places.get(info.position).setUpload(true);
                            DBAdapterPlaces dbp=new DBAdapterPlaces(MyPlacesList.this,userName);
                            dbp.OpenDB();
                            dbp.UpdatePlaceUploaded(places.get(info.position).getLatitude(),places.get(info.position).getLongitude());
                            places=dbp.getMyPlaceses(userName);
                            dbp.CloseDB();
                            Log.e("OOO", dodato);
                            guiProgressFinish(2);
                           // getPlaces();

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                });
            }


        }
        else if(item.getItemId()==2)
        {

            in=new Intent(this,NewPlace.class);

            in.putExtra("userName",userName);
            in.putExtra("latitudeU", places.get(info.position).getLatitude());
            in.putExtra("longitudeU",places.get(info.position).getLongitude());
            in.putExtra("name",places.get(info.position).getName());
            in.putExtra("ridle",places.get(info.position).getRidle());
            in.putExtra("sol",places.get(info.position).getSolution());
            in.putExtra("hint",places.get(info.position).getHint());
            in.putExtra("br",Integer.valueOf(13));

            Toast.makeText(this,places.get(info.position).getLatitude()+"  "+places.get(info.position).getLongitude(),Toast.LENGTH_SHORT).show();
            startActivityForResult(in, 1234);
        }
        else if(item.getItemId()==3)
        {
            dbp.OpenDB();
            dbp.DeletePlace(String.valueOf(places.get(info.position).getLatitude()),String.valueOf(places.get(info.position).getLongitude()));
            dbp.CloseDB();
            getPlaces();

        }
        else if(item.getItemId()==4)
        {

            in=new Intent(this,MapsActivity.class);
            in.putExtra("lat",Double.parseDouble(places.get(info.position).getLatitude()));
            in.putExtra("log",Double.parseDouble(places.get(info.position).getLongitude()));
            in.putExtra("UserName",userName);
            in.putExtra("Mod",1);
            Toast.makeText(this,places.get(info.position).getLatitude()+"  "+places.get(info.position).getLongitude(),Toast.LENGTH_SHORT).show();
            startActivity(in);
        }


        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_places_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if(id==R.id.show_map_item)
        {
            LocationManager lm=(LocationManager)getSystemService(LOCATION_SERVICE);
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                Intent in = new Intent(this, MapsActivity.class);
                in.putExtra("Mod", 1);
                in.putExtra("UserName", userName);
                startActivityForResult(in, 12345);
            }
            else
                Toast.makeText(this,"Enable GPS first!",Toast.LENGTH_SHORT).show();
        }
        else if(id==R.id.new_place_item)
        {
                getMyPlaces();
        }



        return super.onOptionsItemSelected(item);
    }

    private void getMyPlaces()
    {
        ExecutorService transThread = Executors.newSingleThreadExecutor();
        transThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    guiProgressStart("Refreshing places");
                    ArrayList<Place> placesServer = MyPlacesHTTPHelper.getMyPlaces(userName);
                    Log.e("User", userName);
                    dbp.OpenDB();
                    ArrayList<Place> myPlaces = dbp.getMyPlaceses(userName);
                    for (Place place : placesServer)
                    {
                        Log.e("MestoServer  ", place.getName()+" "+place.getLatitude()+" "+place.getLongitude());
                        int ima = 0;
                        for (Place p : myPlaces)
                        {
                            Log.e("MestoBaza  ", p.getName()+" "+p.getLatitude().substring(0, 7)+" "+p.getLongitude().substring(0, 7));
                            if (place.getLongitude().substring(0, 7).equals(p.getLongitude().substring(0,7)) && place.getLatitude().substring(0,7).equals(p.getLatitude().substring(0,7)))
                                ima = 1;
                        }
                        if (ima == 0)
                        {
                            Log.e("Dodato u bazu  ", place.getName());
                            dbp.SavePlace(place);
                        } else
                            Log.e("NIJE!Dodato u bazu  ", place.getName());
                    }


                    dbp.CloseDB();
                    guiProgressFinish(1);
               //     guiNotifyUser("Mesta su osvezena!");

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }
    private  void guiNotifyUser(final String msg)
    {
        guiThread.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyPlacesList.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private  void guiProgressStart(final String msg)
    {
        guiThread.post(new Runnable() {
            @Override
            public void run()
            {
                    pd.setMessage(msg);
                    pd.setProgressStyle(AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                    pd.show();

            }
        });
    }
    private  void guiProgressFinish(final int a)
    {
        guiThread.post(new Runnable() {
            @Override
            public void run()
            {
                pd.cancel();
                if(a==1)
                    Toast.makeText(MyPlacesList.this,"Downloaded",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MyPlacesList.this,"Uploaded",Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1234 || requestCode==12345)
            if(resultCode== Activity.RESULT_OK)
               getPlaces();
    }



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        Intent in =new Intent(this,PlacePreview.class);
        in.putExtra("title",places.get(i).getName());
        in.putExtra("ridle",places.get(i).getRidle());
        startActivity(in);
    }
}
