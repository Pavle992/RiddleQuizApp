package com.example.pavle92.riddlequizapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;


public class NewPlace extends ActionBarActivity implements View.OnClickListener {

    public ArrayList<Place> placeses;
    EditText etSolution, etHint, etName, etRidle;
    Button btnOk, btnCancle;
    DBAdapterPlaces dbP;
    String type;
    int br = 0;
    double lat = 0, lng = 0;
    String lat1 = "", lng1 = "";
    private String userName;
    private Handler guiThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_place);
        userName = "";

        guiThread = new Handler();

        btnOk = (Button) findViewById(R.id.btnNewPlaceOk);
        btnOk.setOnClickListener(this);
        btnCancle = (Button) findViewById(R.id.btnNewPlaceCancle);
        btnCancle.setOnClickListener(this);
        placeses = new ArrayList<Place>();


        etName = (EditText) findViewById(R.id.etxName);
        etRidle = (EditText) findViewById(R.id.etxRidle);
        etHint = (EditText) findViewById(R.id.etxHint);
        etSolution = (EditText) findViewById(R.id.etxSolution);

        Bundle bnd = getIntent().getExtras();
        CheckBundle(bnd);

        dbP = new DBAdapterPlaces(this, userName);

        btnOk = (Button) findViewById(R.id.btnNewPlaceOk);
        btnOk.setOnClickListener(this);

        btnCancle = (Button) findViewById(R.id.btnNewPlaceCancle);
        btnCancle.setOnClickListener(this);
    }


    public void CheckBundle(Bundle bnd) {
        if (bnd != null) {
            br = bnd.getInt("br");
            lat = bnd.getDouble("latitude");
            lng = bnd.getDouble("longitude");
            lat1 = bnd.getString("latitudeU");
            lng1 = bnd.getString("longitudeU");
            userName = bnd.getString("userName");
            //        Log.e("User3", userName);
            if (br == 0) {
                btnOk.setText("OK");
            } else if (br == 13) {
                btnOk.setText("Update");
                etName.setText(String.valueOf(bnd.getString("name")));
                etRidle.setText(String.valueOf(bnd.getString("ridle")));
                etHint.setText(String.valueOf(bnd.getString("hint")));
                etSolution.setText(String.valueOf(bnd.getString("sol")));
            }

        }


    }

    @Override
    public void onClick(View view) {
        if (view == btnOk) {
            final Place place = new Place();
            if (br == 0) {
                //         Toast.makeText(this, lat + " " + lng, Toast.LENGTH_SHORT).show();
                place.setLatitude(String.valueOf(lat));
                place.setLongitude(String.valueOf(lng));
                place.setUserName(userName);
                place.setName(etName.getText().toString());
                place.setRidle(etRidle.getText().toString());
                place.setHint(etHint.getText().toString());
                place.setSolution(etSolution.getText().toString());
                place.setSolved(false);
                place.setVisible(false);
                place.setUpload(false);

                dbP.OpenDB();
                dbP.SavePlace(place);
                dbP.CloseDB();

                setResult(Activity.RESULT_OK);
                finish();

            } else if (br == 13)

            {
                dbP.OpenDB();
                dbP.UpdatePlace(lat1, lng1, etName.getText().toString(), etRidle.getText().toString(), etSolution.getText().toString(), etHint.getText().toString());
                Log.e("DDD",lat1+" "+lng1);
                Toast.makeText(this, "Place updated", Toast.LENGTH_SHORT).show();
                dbP.CloseDB();
                setResult(Activity.RESULT_OK);
                finish();
            }


        } else if (view == btnCancle) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }

    }

}