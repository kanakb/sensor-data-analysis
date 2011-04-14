package com.senstastic;

import android.content.Intent;

public class Sensor extends WakefulIntentService 
{
	public Sensor() 
	{
		super("SensorService");
	}

	protected void doWakefulWork(Intent intent)
	{
		int i = 0;
		i++;
	}
}
