package com.senstastic;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
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
	
	public static void scheduleSensorServiceSubclass(Context context, Class<?> sensorServiceSubclass, int interval)
	{
        // Create the Intent.
        Intent intent = new Intent(context, AlarmReceiver.class);
        String sensorServiceSubclassName = sensorServiceSubclass.getName();
        intent.putExtra("sensorServiceSubclassName", sensorServiceSubclassName);
        
        // Create the PendingIntent.
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the alarm.
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), interval*1000, pendingIntent);
	}
}
