package com.senstasticDemo;

import android.content.Intent;

public class SensorService extends WakefulIntentService 
{
	public SensorService() 
	{
		super("SensorService");
	}

	protected void doWakefulWork(Intent intent)
	{
		int i = 0;
		i++;
	}
}
