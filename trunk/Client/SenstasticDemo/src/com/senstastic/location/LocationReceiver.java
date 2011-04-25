package com.senstastic.location;

import android.location.Location;

public interface LocationReceiver 
{
	public void onLocationReceived(Location location);
}
