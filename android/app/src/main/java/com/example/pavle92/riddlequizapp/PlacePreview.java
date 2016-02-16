package com.example.pavle92.riddlequizapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;


public class PlacePreview extends ActionBarActivity {

    TextView txtTitle,txtDesc;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_preview);

        txtTitle=(TextView)findViewById(R.id.txtTitle);
        txtDesc=(TextView)findViewById(R.id.txtDesc);
        img=(ImageView)findViewById(R.id.imgPreview);

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null)
        {
            txtTitle.setText(bundle.getString("title"));
            img.setImageResource(R.drawable.elfak);
            txtDesc.setText(bundle.getString("ridle"));

        }
    }



}
