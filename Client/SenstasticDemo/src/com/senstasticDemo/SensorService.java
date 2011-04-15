package com.senstasticDemo;



import com.senstastic.WakefulIntentService;

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
	
	protected void sense()
	{
		int i = 0;
		i++;
	}
	
	public int interval()
	{
		return 20;
	}
}
