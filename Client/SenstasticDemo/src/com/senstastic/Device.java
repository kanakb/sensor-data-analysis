package com.senstastic;

import java.util.UUID;

import android.content.Context;

public class Device 
{
	private static String DEVICE_DIRECTORY_NAME = "device";
	private static String DEVICE_ID_FILE_NAME = "id";
	
	public static String getDeviceId(Context context)
	{
		synchronized (FileUtility.class)
		{
			// Read any previously stored device id.
			if (FileUtility.fileExists(context, DEVICE_DIRECTORY_NAME, DEVICE_ID_FILE_NAME))
				return FileUtility.readFile(context, DEVICE_DIRECTORY_NAME, DEVICE_ID_FILE_NAME);
			
			// Create and store a new device id.
			String deviceId = UUID.randomUUID().toString();
			FileUtility.writeFile(context, DEVICE_DIRECTORY_NAME, DEVICE_ID_FILE_NAME, deviceId);
			return deviceId;
		}
	}
	
	public static String getDeviceKind()
	{
		// TODO: Implement;
		return "DeviceKind";
	}
}
