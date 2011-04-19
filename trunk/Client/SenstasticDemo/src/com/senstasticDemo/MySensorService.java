package com.senstasticDemo;

import com.senstastic.Measurement;
import com.senstastic.SensorService;

public class MySensorService extends SensorService
{
	protected int getInterval()
	{
		return 10;
	}
	
	protected void sense()
	{
		Measurement.generate(this, "MySensor", new Integer(123));
		
		finish();
	}
}
