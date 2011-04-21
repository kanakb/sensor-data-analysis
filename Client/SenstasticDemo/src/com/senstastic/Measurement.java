package com.senstastic;

import android.content.Context;

public class Measurement 
{	
	private static String MEASUREMENTS_DIRECTORY_NAME = "measurements";
	
	private Context context;
	
	private String deviceId;
	private String deviceKind;
	private String sensorKind;
	private long time;
	private float latitude;
	private float longitude;
	private Object data;
	
	public Measurement(Context context, String sensorKind, long time, float latitude, float longitude, Object data)
	{	
		this.context = context;
		
		this.deviceId = Device.getDeviceId(context);
		this.deviceKind = "";
		this.sensorKind = sensorKind;
		this.time = time;
		this.latitude = latitude;
		this.longitude = longitude;
		this.data = data;
		
		// TODO: Change.
		save();
	}
	
	private String getXmlString()
	{
		XMLStringGenerator gen = new XMLStringGenerator();
		gen.begin("measurement");
		gen.addTag("deviceId", deviceId);
		gen.addTag("deviceKind", deviceKind);
		gen.addTag("sensorKind", sensorKind);
		gen.addTag("time", time);
		gen.addTag("latitude", latitude);
		gen.addTag("longitude", longitude);
		gen.addTag("data", data);
		return gen.end();
	}
	
	private void save()
	{
		String fileName = sensorKind + "_" + time + ".xml";
		
		synchronized (FileUtility.class) 
		{
			FileUtility.writeFile(context, MEASUREMENTS_DIRECTORY_NAME, fileName, getXmlString());	
			Logger.d(FileUtility.readFile(context, MEASUREMENTS_DIRECTORY_NAME, fileName));
		}
	}
}
