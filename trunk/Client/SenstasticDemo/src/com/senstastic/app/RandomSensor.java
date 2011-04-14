package com.senstastic.app;


import com.senstastic.WakefulIntentService;

import android.content.Intent;


public class RandomSensor extends WakefulIntentService 
{
	public RandomSensor() 
	{
		super("RandomSensor");
	}

	protected void doWakefulWork(Intent intent)
	{
		int i = 0;
		i++;
	}
}
