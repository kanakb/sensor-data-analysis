package com.senstastic;

import com.senstastic.location.LocationReceiver;
import com.senstastic.location.LocationRequest;
import com.senstastic.location.NetworkLocationRequest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class SensorService extends Service implements LocationReceiver
{
	private static final String WAKE_LOCK_TAG = "SensorService";
	
	private WakeLock wakeLock;
	private Object data;
	private LocationRequest locationRequest;
	
	public SensorService() 
	{
		super();
	}
	
	public int onStartCommand(Intent intent, int flags, int startId) 
	{	
		acquireWakeLock();
	    sense();
	    
	    return START_NOT_STICKY;
	}
	
	private void acquireWakeLock()
	{
		if (wakeLock == null)
		{
			PowerManager powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
			wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG);
			wakeLock.acquire();
		}
	}
	
	private void releaseWakeLock()
	{
		if (wakeLock != null)
		{
			wakeLock.release();
			wakeLock = null;
		}
	}
	
	protected void finishSensing(Object data)
	{	
		// Save the data until the location request finishes.
		this.data = data;
		
		// Start the location request.
		locationRequest = getLocationRequest();
	}

	public void onLocationReceived(Location location)
	{
		// If location acquisition failed.
		if (location == null)
		{
			finish();
			return;
		}
		
		// If location acquisition succeeded.
		Measurement measurement = new Measurement(this, getSensorKind(), data, location.getLatitude(), location.getLongitude());
		
		// TODO: Change.
		measurement.save();
		
		finish();
	}
	
	private void finish()
	{
		// Release the wake lock.
		releaseWakeLock();
		
		// Stop this service.
		stopSelf();
	}
	
	public void onDestroy()
	{
		if (locationRequest != null)
			locationRequest.cancel();
		
		releaseWakeLock();
	}
	
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	// Override the following methods.
	
	protected String getSensorKind()
	{
		return "Sensor";
	}
	
	protected int getInterval()
	{
		return 300;
	}
	
	protected LocationRequest getLocationRequest()
	{
		return new NetworkLocationRequest(this, this, 100000);
	}
	
	protected void sense()
	{

	}
}
