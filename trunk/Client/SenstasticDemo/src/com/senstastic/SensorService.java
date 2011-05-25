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
	
	/*
	 * Creates a measurement with the provided data and location, 
	 * pushes the measurement out to the server when convenient,
	 * stops the service. 
	 * This is good for SensorServices that fetch their own location in their own way, 
	 * perhaps to collect additional data like speed and altitude to put in the data field of the measurement.
	 */
	protected void finishSensing(Object data, double latitude, double longitude)
	{
		// Create the new measurement with the passed in data and location.
		new Measurement(this, getSensorKind(), data, latitude, longitude);
		
		finish();
	}

	/*
	 * Issues a location request based on the LocationRequest type return in getLocationRequest(),
	 * creates a measurement with the provided data and the result of the location request,
	 * pushes the measurement out to the server when convenient,
	 * stops the service. 
	 * If the location request fails, drops the measurements.
	 */	
	protected void finishSensing(Object data)
	{	
		// Save the data until the location request finishes.
		this.data = data;
		
		// Start the location request.
		locationRequest = getLocationRequest();
	}
	
	/*
	 * Call this function when you would like your sensor service to finish sensing without pushing anything out to the server.
	 * For example, if your sensor service encounters and error and should not create and send a measurement, then call this function to end the service. 
	 */
	protected void finishSensing()
	{
		finish();
	}

	public void onLocationReceived(Location location)
	{
		// If location acquisition failed.
		if (location == null)
		{
			finish();
			return;
		}
		
		// If location acquisition succeeded, create the new measurement.
		Measurement measurement = new Measurement(this, getSensorKind(), data, location.getLatitude(), location.getLongitude());
		measurement.sendWithSaveFallback();
		
		finish();
	}
	
	private void finish()
	{
	    // Before we stop this service, use it send out any saved measurements from any sensors if we happen to have wifi.
	    Measurement.sendSavedMeasurements(this);
	    
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
	
	protected int getIntervalInSeconds()
	{
		return 300;
	}
	
	protected LocationRequest getLocationRequest()
	{
		return new NetworkLocationRequest(this, this, 100000);
	}
	
	protected void sense()
	{
		finishSensing(null);
	}
}
