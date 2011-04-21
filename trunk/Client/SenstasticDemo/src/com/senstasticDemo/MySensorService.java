package com.senstasticDemo;

import android.location.Location;

import com.senstastic.LocationReceiver;
import com.senstastic.NetworkLocationRequest;
import com.senstastic.SensorService;

public class MySensorService extends SensorService implements LocationReceiver 
{
	NetworkLocationRequest locationRequest;
	
	protected int getInterval()
	{
		return 300;
	}
	
	protected void sense()
	{
		locationRequest = new NetworkLocationRequest(this, this, 10000);
		
		finish();
	}

	public void onLocationReceived(Location location) 
	{
		
	}
	
	@Override
	protected void finish()
	{
		
		
		super.finish();
	}
}
