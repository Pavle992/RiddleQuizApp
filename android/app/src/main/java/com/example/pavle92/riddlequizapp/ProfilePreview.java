package com.example.pavle92.riddlequizapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Pavle92 on 5/25/2015.
 */
public class ProfilePreview extends Activity{
    TextView txtIme,txtPrezime,txtLevel,txtPass,txtBroj;
    ImageView imgFavorite;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_preview);
        txtIme = (TextView) findViewById(R.id.txtIme2);
        txtPrezime = (TextView) findViewById(R.id.txtPrezime2);
        txtBroj = (TextView) findViewById(R.id.txtPhone2);
        txtLevel=(TextView)findViewById(R.id.txtLevel);
        imgFavorite = (ImageView) findViewById(R.id.imageView2);
        txtLevel.setText("1");

        Bundle bnd=getIntent().getExtras();
        if(bnd!=null)
        {
            txtIme.setText(bnd.getString("ime"));
            txtPrezime.setText(bnd.getString("prezime"));
            txtBroj.setText(bnd.getString("broj"));
            Bitmap bitmap = (Bitmap) getIntent().getParcelableExtra("Image");
            imgFavorite.setImageBitmap(bitmap);
            int score=bnd.getInt("score");
            if(score<30)
                txtLevel.setText("1");
            else
            {
                Log.e("SCORE",String.valueOf(score/30));
                if(score%30==0)
                    txtLevel.setText(String.valueOf(score / 30));
                else
                    txtLevel.setText(String.valueOf(score / 30 + 1));
            }

        }

    }

}
