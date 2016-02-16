package com.example.pavle92.riddlequizapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


public class Search extends ActionBarActivity {

    ListView listView;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Button btn=(Button)findViewById(R.id.btnSearch);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String retStr="";
                SparseBooleanArray checked = listView.getCheckedItemPositions();
                int size = checked.size(); // number of name-value pairs in the array
                for (int i = 0; i < size; i++)
                {
                    int key = checked.keyAt(i);
                    boolean value = checked.get(key);
                    if (value)
                        retStr=retStr.concat(listView.getItemAtPosition(key) + " ");
                }
                Intent in=new Intent();
                in.putExtra("retStr", retStr);
                setResult(RESULT_OK,in);

                finish();
            }
        });

        String ime="";
        Bundle bnd=getIntent().getExtras();
        if(bnd!=null)
            ime=bnd.getString("imena");
        String[] imena=ime.split(" ");
        listView=(ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice,imena);


        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        // ListView Item Click Listener


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
