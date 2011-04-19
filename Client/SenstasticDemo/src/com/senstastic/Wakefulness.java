package com.senstastic;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class Wakefulness 
{
	private static final String WAKE_LOCK_NAME = "Wakefulness";
	private static WakeLock wakeLock;
	
	public static void acquire(Context context)
	{
		getWakeLock(context).acquire();
	}
	
	public static void release(Context context)
	{
		getWakeLock(context).release();
	}
	
	private static synchronized WakeLock getWakeLock(Context context) 
	{
		// Create the wake lock if necessary.
		if (wakeLock == null)
		{
			PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);

			wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_NAME);
			wakeLock.setReferenceCounted(true);
		}

		return wakeLock;
	}
}
