package com.senstastic;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class LocationRequest extends Handler implements LocationListener 
{
	private static final int TIMEOUT_MESSAGE_CODE = 0;
	
	LocationManager locationManager;
	
	public LocationRequest(Context context, float desiredAccuracy, long timeoutMillis)
	{
		// Set the timeout.
		sendEmptyMessageAtTime(TIMEOUT_MESSAGE_CODE, timeoutMillis);
		
		// Get the location manager.
		locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		
		// Set the location provider criteria.
		Criteria criteria = new Criteria();
		criteria.setCostAllowed(false);
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		
		// Get the best location provider.
		String locationProviderName = locationManager.getBestProvider(criteria, true);
		
		// Request location updates.
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.0f, this);
	}
	
	public void cancel()
	{
		// Cancel the timeout.
		removeMessages(TIMEOUT_MESSAGE_CODE);
		
		// Cancel the location listening.
		locationManager.removeUpdates(this);
	}

	@Override
	public void handleMessage(Message message)
	{
		// Handle the timeout.
		
	}
	
	@Override
	public void onLocationChanged(Location location) 
	{
		Logger.d(Double.toString(location.getLatitude()));
	}

	@Override
	public void onProviderDisabled(String provider) 
	{

	}

	@Override
	public void onProviderEnabled(String provider) 
	{
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) 
	{
		
	}
	
	
}
