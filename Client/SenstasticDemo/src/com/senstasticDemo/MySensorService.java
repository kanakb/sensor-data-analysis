package com.senstasticDemo;

import com.senstastic.Measurement;
import com.senstastic.SensorService;

public class MySensorService extends SensorService
{
	protected int getInterval()
	{
		return 300;
	}
	
	protected void sense()
	{
		Measurement.generate(getApplicationContext(), "MySensor", new Integer(123));
	}
}
