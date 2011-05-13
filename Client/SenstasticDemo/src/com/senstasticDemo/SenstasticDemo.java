package com.senstasticDemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SenstasticDemo extends Activity
{
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button next = (Button)findViewById(R.id.aggregateMapButton);
        next.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) 
            {
                Intent myIntent = new Intent(view.getContext(), MapActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });
    }
	
    /*
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);

        Senstastic.init("http://sensor-analysis.appspot.com/import");
        Senstastic.schedule(this, VolumeSensorService.class);
    }
    */
}