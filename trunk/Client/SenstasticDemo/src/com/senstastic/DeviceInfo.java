package com.senstastic;

import java.util.UUID;

import android.content.Context;

public class DeviceInfo 
{
	private static String DEVICE_DIRECTORY_NAME = "device";
	private static String DEVICE_ID_FILE_NAME = "id";
	
	public static String getSavedDeviceId(Context context)
	{
		synchronized (FileUtility.class)
		{
			// If a previously saved device id exists, read it from the file system and return it.
			if (FileUtility.fileExists(context, DEVICE_DIRECTORY_NAME, DEVICE_ID_FILE_NAME))
				return FileUtility.readFile(context, DEVICE_DIRECTORY_NAME, DEVICE_ID_FILE_NAME);
			// Otherwise, return null.
			else
				return null;
		}
	}
	
	public static String getDeviceId(Context context)
	{
		// If a previously saved device id exists, return it;
		String savedDeviceId = getSavedDeviceId(context);
		if (savedDeviceId != null)
			return savedDeviceId;
		
		// If no device id exists, create and store a new device id.
		synchronized (FileUtility.class)
		{
			String newDeviceId = UUID.randomUUID().toString();
			FileUtility.writeFile(context, DEVICE_DIRECTORY_NAME, DEVICE_ID_FILE_NAME, newDeviceId);
			return newDeviceId;
		}
	}
	
	public static String getDeviceKind()
	{
		return android.os.Build.MODEL;
	}
}
