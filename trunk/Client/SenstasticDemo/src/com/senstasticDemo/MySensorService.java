package com.senstasticDemo;

import com.senstastic.LocationRequest;
import com.senstastic.Measurement;
import com.senstastic.SensorService;

public class MySensorService extends SensorService
{
	LocationRequest locationRequest;
	
	protected int getInterval()
	{
		return 300;
	}
	
	protected void sense()
	{
		locationRequest = new LocationRequest(this, 100, 100000);
		
		Measurement.generate(this, "MySensor", new Integer(123));
		
		finish();
	}
}
