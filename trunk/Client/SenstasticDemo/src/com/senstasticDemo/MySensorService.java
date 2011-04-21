package com.senstasticDemo;

import com.senstastic.LocationReceiver;
import com.senstastic.LocationRequest;
import com.senstastic.NetworkLocationRequest;
import com.senstastic.SensorService;

public class MySensorService extends SensorService implements LocationReceiver 
{	
	// Overridden methods.
	
	protected String getSensorKind()
	{
		return "MySensor";
	}
	
	protected int getInterval()
	{
		return 60;
	}
	
	protected LocationRequest getLocationRequest()
	{
		return new NetworkLocationRequest(this, this, 100000);
	}
	
	protected void sense()
	{
		double data = 123.456;
		finishSensing(data);
	}
}
