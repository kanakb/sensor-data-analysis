package com.senstastic;

import java.util.Calendar;

import com.senstasticDemo.SensorService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Senstastic 
{
	public static void schedule(Context context, Class<?> sensorServiceClass, int interval)
	{
        // Create the Intent.
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("sensorServiceClassName", SensorService.class.getName());
        
        // Create the PendingIntent.
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the alarm.
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), interval*1000, pendingIntent);

	}
}
