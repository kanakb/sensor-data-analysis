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
			// Get the SensorService subclass.
			Bundle extras = intent.getExtras();
			String sensorServiceSubclassName = extras.getString("sensorServiceSubclassName");
			Class<?> sensorServiceSubclass = Class.forName(sensorServiceSubclassName);
			
			// Run the SensorService subclass once.
			WakefulIntentService.sendWakefulWork(context, sensorServiceSubclass);
		}
		catch(ClassNotFoundException e)
		{
			System.err.println("Sensor service class not found!");
		}
	}
}
