package com.senstasticDemo;

import com.senstastic.DeviceInfo;
import com.senstastic.Senstastic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SenstasticDemo extends Activity implements OnClickListener
{
	private Button aggregateMapButton;
	private Button personalMapButton;
	private Button logButton;
	private Button aboutButton;
	
	public static void initSenstastic(Context context)
	{
        String senstasticEndpointUrl = context.getString(R.string.senstastic_endpoint_url);
        Senstastic.init(senstasticEndpointUrl);
        Senstastic.schedule(context, VolumeSensorService.class);
	}
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_view);

        // Add event listeners for the menu buttons.
        aggregateMapButton = (Button)findViewById(R.id.menuViewAggregateMapButton);
        aggregateMapButton.setOnClickListener(this);
        
        personalMapButton = (Button)findViewById(R.id.menuViewPersonalMapButton);
        personalMapButton.setOnClickListener(this);
        
        logButton = (Button)findViewById(R.id.menuViewLogButton);
        logButton.setOnClickListener(this);
        
        aboutButton = (Button)findViewById(R.id.menuViewAboutButton);
        aboutButton.setOnClickListener(this);  
        
        // If the user has just downloaded the app and started it for the first time, start Senstastic here. Otherwise, Senstastic will be launched when the device boots up, and shouldn't be relaunched here when the user opens the app again.
        if (Senstastic.isFirstLaunch(this))
        	initSenstastic(this);
    }
    
    public void onClick(View view)
    {
    	Intent intent = new Intent();
    	
    	if (view == aggregateMapButton)
    	{
    		intent.setClass(this, MapActivity.class);
    		intent.putExtra("mapUrl", "http://sensor-analysis.appspot.com/s/heatgrid.html?dataUrl=heatgrid.xml&zoom=13&colors=00FF00,FFCC00,FF0000");
    	}
    	else if (view == personalMapButton)
    	{
    		intent.setClass(this, MapActivity.class);
    		intent.putExtra("refreshDataUrl", "http://sensor-analysis.appspot.com/refreshHeatgrid?id=" + DeviceInfo.getDeviceId(this));
    		intent.putExtra("mapUrl", "http://sensor-analysis.appspot.com/s/heatgrid.html?dataUrl=heatgrid.xml?id=" + DeviceInfo.getDeviceId(this) + "&zoom=13&colors=00FF00,FFCC00,FF0000");
    	}
    	else if (view == logButton)
    	{
    		intent.setClass(this, LogActivity.class);
    	}
    	else if (view == aboutButton)
    	{
    		intent.setClass(this, AboutActivity.class);
    	}

        startActivity(intent);
    }
}