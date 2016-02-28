package com.example.pavle92.riddlequizapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AnswerBox extends Activity {

    double lat=-1;
    double log=-1;
    String lat1;
    String log1;
    ArrayList<Place> places;
    String resenje="";
    String hint="";
    String ridle="";
    private String userName="";
    private String userNameQ="";
    private String riddleQuestAnsw="";
    private boolean conr=true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_box);
        places=new ArrayList<Place>();
        userName="";
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null)
        {
            lat=bundle.getDouble("lat");
            log=bundle.getDouble("log");
            userName=bundle.getString("userName");
            userNameQ=bundle.getString("userNameQ");
            riddleQuestAnsw=bundle.getString("riddleQuestionAnsw");
            if(riddleQuestAnsw!=null){
                conr=false;
            }
            //adedd for scan
            if(conr) {
                DBAdapterPlaces dbp = new DBAdapterPlaces(this, userName);
                dbp.OpenDB();
                places = dbp.getPlaceses();
                dbp.CloseDB();
            }
        }
        //checking if it is scaned
        if(conr) {
            for (Place p : places) {
                if ((lat == Double.parseDouble(p.getLatitude())) && (log == Double.parseDouble(p.getLongitude()))) {
                    lat1 = p.getLatitude();
                    log1 = p.getLongitude();
                    resenje = p.getSolution();
                    hint = p.getHint();
                    ridle = p.getRidle();
                }
            }
        }
        else{
            String [] qest_sol=riddleQuestAnsw.split("&");
            lat1 = String.valueOf(lat);
            log1 = String.valueOf(log);
            resenje = qest_sol[2];
            hint = qest_sol[1];
            ridle = qest_sol[0];
        }
        final EditText etx=(EditText)findViewById(R.id.etxABAnswer);
        final TextView txtRidle=(TextView)findViewById(R.id.txtRidle);
        txtRidle.setText(ridle);
        Button btnOk=(Button)findViewById(R.id.btnABok);
        Button btnCancel=(Button)findViewById(R.id.btnABCancel);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(etx.getText().toString().equals(resenje)) {
                    MakeToast("Correct");

                    Intent result=new Intent();
                    result.putExtra("lat",lat1);
                    result.putExtra("log",log1);
                    result.putExtra("hint",hint);
                    setResult(Activity.RESULT_OK,result);

                    finish();
                }
                else
                {
                    if(userNameQ!="RiddleQuizTeam") {
                        MakeToast("Sorry, " + userNameQ + " gets 5 points!");
                        updateScore(userNameQ);
                    }
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    public void MakeToast(String s)
    {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }


    private void updateScore(final String userName) {
        ExecutorService transThread = Executors.newSingleThreadExecutor();
        transThread.submit(new Runnable() {
            @Override
            public void run() {
                try {

                    String ret = MyPlacesHTTPHelper.UpdateScore(userName,2);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }



}
