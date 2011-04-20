package com.senstastic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class SensorService extends Service implements LocationListener
{		
	LocationManager locationManager;
	
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
	
	protected void finishSensing()
	{
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0.0f, this);
	}
	
	private void finish()
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
		return 20;
	}
	
	protected void sense()
	{
		int i = 0;
		i++;
	}

	@Override
	public void onLocationChanged(Location location) 
	{
		Logger.d(Double.toString(location.getLatitude()));
		
		finish();
	}

	@Override
	public void onProviderDisabled(String provider) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) 
	{
		// TODO Auto-generated method stub
		
	}
}
