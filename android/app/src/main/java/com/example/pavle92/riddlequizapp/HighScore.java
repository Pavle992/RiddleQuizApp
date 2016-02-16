package com.example.pavle92.riddlequizapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HighScore extends Activity {

    ListView listView;
    String userName;

    ArrayList<Player> players=new ArrayList<Player>();
    private Handler guiThread;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        guiThread=new Handler();
        pd=new ProgressDialog(HighScore.this);

        Bundle bnd=getIntent().getExtras();
        if(bnd!=null)
            userName=bnd.getString("username");
        listView=(ListView)findViewById(R.id.listVewHigh);
        View header=(View)getLayoutInflater().inflate(R.layout.header,null);
        listView.addHeaderView(header);
        ExecutorService transThread = Executors.newSingleThreadExecutor();
        transThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    guiProgressStart("Getting data from server");
                    players = MyPlacesHTTPHelper.getFriendsData(userName);
                    Collections.sort(players, new MyComparator());

                    guiNotifyUser();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }
    private void guiNotifyUser()
    {
        guiThread.post(new Runnable() {
            @Override
            public void run() {

                listView.setAdapter(new MyAdapter(HighScore.this));

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i > 0) {
                            Intent in = new Intent(HighScore.this, ProfilePreview.class);
                            in.putExtra("ime", players.get(i - 1).getIme());
                            in.putExtra("prezime", players.get(i - 1).getPrezime());
                            in.putExtra("broj", String.valueOf(players.get(i - 1).getBroj()));
                            in.putExtra("Image", players.get(i - 1).getImg());
                            in.putExtra("score", players.get(i - 1).getScore());
                            startActivity(in);

                        }

                    }
                });
                pd.cancel();
            }
        });

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
    private void MakeToast(String s)
    {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    private class MyAdapter extends BaseAdapter
    {
        Context context;
        MyAdapter(Context context)
        {
            this.context=context;


        }

        @Override
        public int getCount() {
            return players.size();
        }

        @Override
        public Object getItem(int i) {
            return players.get(i);
        }

        @Override
        public long getItemId(int i)
        {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View row=inflater.inflate(R.layout.single_row,viewGroup,false);
            TextView txtIme=(TextView)row.findViewById(R.id.txtIme);
            TextView txtScore=(TextView)row.findViewById(R.id.txtScore);
            ImageView img=(ImageView)row.findViewById(R.id.imageViewRow);


            txtIme.setText(players.get(i).getUser());
            txtScore.setText(String.valueOf(players.get(i).getScore()));
            if(players.get(i).getImg()!=null)
                img.setImageBitmap(players.get(i).getImg());
                else
                img.setImageResource(R.drawable.elfak);


            return row;
        }

    }
    class MyComparator implements Comparator<Player> {
        @Override
        public int compare(Player o1, Player o2) {
            if (o1.getScore() > o2.getScore()) {
                return -1;
            } else if (o1.getScore() < o2.getScore()) {
                return 1;
            }
            return 0;
        }}
}
