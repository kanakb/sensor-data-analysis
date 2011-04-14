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
		int j = 1;
		for (int i = 0; i < 100000000; i++)
		{
			j += i;
		}
	}
}
