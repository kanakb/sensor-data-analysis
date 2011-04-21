package com.senstastic;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class NetworkLocationRequest extends Handler implements LocationRequest, LocationListener 
{
	private static final int TIMEOUT_MESSAGE_CODE = 0;
	
	LocationManager locationManager;
	LocationReceiver locationReceiver;
	
	public NetworkLocationRequest(Context context, LocationReceiver locationReceiver, long timeoutMillis)
	{
		// Set the location receiver.
		this.locationReceiver = locationReceiver;
		
		// Set the timeout.
		sendEmptyMessageDelayed(TIMEOUT_MESSAGE_CODE, timeoutMillis);
		
		// Get the location manager.
		locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		
		// Request location updates from wifi or cell towers.
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0.0f, this);
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
		cancel();
		locationReceiver.onLocationReceived(null);
	}
	
	@Override
	public void onLocationChanged(Location location) 
	{
		cancel();
		locationReceiver.onLocationReceived(location);
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
