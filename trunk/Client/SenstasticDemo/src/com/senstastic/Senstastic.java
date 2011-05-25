package com.senstastic;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Senstastic 
{
	public static String endpointURL;

	public static void init(String endpointURL)
	{
		Senstastic.endpointURL = endpointURL;
	}
	
	public static void schedule(Context context, Class<? extends SensorService> sensorServiceClass)
	{
		// Get the interval.
		int interval = 0;
		try 
		{
			SensorService sensorServiceInstance = sensorServiceClass.newInstance();
			interval = sensorServiceInstance.getInterval();
		}
		catch (Exception e)
		{
			Logger.e("Error getting interval!");
			return;
		}
		
        // Create the Intent.
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("sensorServiceClassName", sensorServiceClass.getName());
        
        // Create the PendingIntent.
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the alarm.
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), interval*1000, pendingIntent);
	}
}
