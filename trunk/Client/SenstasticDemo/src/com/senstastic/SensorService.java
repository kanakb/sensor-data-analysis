package com.senstastic;

import android.content.Intent;

public class SensorService extends WakefulIntentService 
{	
	public SensorService() 
	{
		super("SensorService");
	}
	
	protected void doWakefulWork(Intent intent)
	{
		sense();
	}
	
	// Override the following methods.
	
	protected int getInterval()
	{
		return 20;
	}
	
	protected void sense()
	{
		int i = 0;
		i++;
	}
}
