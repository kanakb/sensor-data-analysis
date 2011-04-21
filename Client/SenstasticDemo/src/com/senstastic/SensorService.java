package com.senstastic;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SensorService extends Service
{
	public SensorService() 
	{
		super();
	}
	
	public int onStartCommand(Intent intent, int flags, int startId) 
	{	
		Wakefulness.acquire(this);
	    sense();
	    return START_NOT_STICKY;
	}
	
	protected void finish()
	{
		Wakefulness.release(this);
		stopSelf();
	}
	
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	// Override the following methods.
	
	protected int getInterval()
	{
		return 300;
	}
	
	protected void sense()
	{
		int i = 0;
		i++;
	}
}
