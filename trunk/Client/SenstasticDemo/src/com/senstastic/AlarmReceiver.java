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
        	String sensorServiceClassName = extras.getString("sensorServiceClassName");
        	Class<?> sensorServiceClass = Class.forName(sensorServiceClassName);
	            
        	WakefulIntentService.sendWakefulWork(context, sensorServiceClass);
        }
        catch(ClassNotFoundException e)
        {
            Logger.e("Class not found!");
        }
	}
}
