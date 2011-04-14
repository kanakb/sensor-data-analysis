package com.senstastic;



import com.senstastic.app.RandomSensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

public class AlarmReceiver extends BroadcastReceiver
{
	Context getContext(Context context) 
	{
		try
		{
		    return context.createPackageContext("com.senstasticDemo", Context.CONTEXT_INCLUDE_CODE + Context.CONTEXT_IGNORE_SECURITY);
		}
	    catch (NameNotFoundException ignore)
	    {
			return null;
	    }
	}
	
	public void onReceive(Context context, Intent intent) 
	{
		try
		{
			Bundle extras = intent.getExtras();
			String sensorClassName = extras.getString("sensorClassName");
			Class<?> sensorClass = Class.forName(sensorClassName);
			
			//WakefulIntentService.sendWakefulWork(context, RandomSensor.class);//SensorService.class);
			//WakefulIntentService.sendWakefulWork(getContext(context), RandomSensor.class);
			WakefulIntentService.sendWakefulWork(context, new Intent("com.senstasticDemo.RandomSensor"));
		}
		catch(ClassNotFoundException e)
		{
			System.err.println("Class not found!");
		}
	}
}
