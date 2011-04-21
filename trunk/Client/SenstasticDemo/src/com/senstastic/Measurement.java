package com.senstastic;

import android.content.Context;

public class Measurement 
{	
	private static String MEASUREMENTS_DIRECTORY_NAME = "measurements";
	
	public String deviceId;
	public String deviceKind;
	public String sensorKind;
	public long time;
	public double latitude;
	public double longitude;
	public Object data;
	
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
	
	public void save(Context context)
	{
		String fileName = sensorKind + "_" + time + ".xml";
		
		synchronized (FileUtility.class) 
		{
			FileUtility.writeFile(context, MEASUREMENTS_DIRECTORY_NAME, fileName, getXmlString());	
			Logger.d(FileUtility.readFile(context, MEASUREMENTS_DIRECTORY_NAME, fileName));
		}
	}
}
