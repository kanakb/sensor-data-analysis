package com.senstastic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlarmReceiver extends BroadcastReceiver
{
	public void onReceive(Context context, Intent intent) 
	{
		try
		{
			Bundle extras = intent.getExtras();
			String sensorClassName = extras.getString("sensorClassName");
			Class<?> sensorClass = Class.forName(sensorClassName);
			
			WakefulIntentService.sendWakefulWork(context, SensorService.class);
		}
		catch(ClassNotFoundException e)
		{
			System.err.println("Class not found!");
		}
	}
}
