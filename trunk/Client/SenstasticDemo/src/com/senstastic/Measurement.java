package com.senstastic;

import java.util.Date;

import android.content.Context;

public class Measurement 
{	
	private static String MEASUREMENTS_DIRECTORY_NAME = "measurements";
	
	private Context context;
	
	private String deviceId;
	private String deviceKind;
	private String sensorKind;
	private long time;
	private double latitude;
	private double longitude;
	private Object data;
	
	public Measurement(Context context, String sensorKind, Object data, double latitude, double longitude)
	{
		this.context = context;
		
		this.deviceId = Device.getDeviceId(context);
		this.deviceKind = Device.getDeviceKind();
		this.sensorKind = sensorKind;
		this.time = (new Date()).getTime();
		this.data = data;
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
	
	public void save()
	{
		String fileName = sensorKind + "_" + time + ".xml";
		
		synchronized (FileUtility.class) 
		{
			FileUtility.writeFile(context, MEASUREMENTS_DIRECTORY_NAME, fileName, getXmlString());	
			Logger.d(FileUtility.readFile(context, MEASUREMENTS_DIRECTORY_NAME, fileName));
		}
	}
}
