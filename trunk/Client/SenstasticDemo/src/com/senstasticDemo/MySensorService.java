package com.senstasticDemo;

import com.senstastic.SensorService;

public class MySensorService extends SensorService
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
	
	protected void sense()
	{
		double data = 123.456;
		finishSensing(data);
	}
}
