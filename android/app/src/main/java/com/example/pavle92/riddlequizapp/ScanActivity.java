package com.example.pavle92.riddlequizapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

public class ScanActivity extends Activity {

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    public static final String PREFS_NAME = "LoginPrefs";
    private String userName;
    private double lat;
    private double log;
    private String ridle;
    private String solution;
    private String hint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        Bundle bnd=getIntent().getExtras();
        lat=bnd.getDouble("lat");
        log=bnd.getDouble("log");

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        userName=settings.getString("UserName", "");
    }

    public void scanBar(View v) {
        try {
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            showDialog(ScanActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    public void scanQR(View v) {
        try {
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            showDialog(ScanActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                Toast toast = Toast.makeText(this, "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG);
                toast.show();

                ridle=contents.split("&")[0];
                hint=contents.split("&")[1];
                solution=contents.split("&")[2];

                if(!isOnline())
                    Toast.makeText(this,"Enable internet connection before answering!",Toast.LENGTH_SHORT).show();
                else
                {
//                    String[] niz=marker.getSnippet().split(" ");
                    Intent in=new Intent(this,AnswerBox.class);

//                    Toast.makeText(this,marker.getTitle(),Toast.LENGTH_SHORT).show();

                    in.putExtra("lat",lat);
                    in.putExtra("log",log);
                    in.putExtra("riddleQuestionAnsw",contents);
                    in.putExtra("userName",userName);
                    in.putExtra("userNameQ","RiddleQuizTeam");
                    Log.e("QQQQ", "RiddleQuizTeam");
                    startActivityForResult(in, 9890);
                }
            }
        }
        else if(requestCode==9890) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle b = intent.getExtras();
                b.putString("ridle",ridle);
                b.putString("hint",hint);
                b.putString("solution",solution);

                Intent result=new Intent();
                result.putExtras(b);
                setResult(Activity.RESULT_OK, result);

                finish();

            }


        }
    }

    public boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
