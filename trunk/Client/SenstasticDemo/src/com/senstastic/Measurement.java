package com.senstastic;

import java.util.Date;

import android.content.Context;

public class Measurement 
{	
	private Context context;
	
	private String sensorKind;
	private long time;
	private float latitude;
	private float longitude;
	private float speed;
	private String deviceId;
	private String deviceKind;
	private Object data;
	
	public Measurement(Context context, String sensorKind, Object data)
	{	
		this.context = context;
		
		this.sensorKind = sensorKind;
		time = (new Date()).getTime();
		this.data = data;
		latitude = 0;
		longitude = 0;
		speed = 0;
		deviceId = Device.getDeviceId(context);
		deviceKind = "";
	}
	
	private String getXmlString()
	{
		XMLStringGenerator gen = new XMLStringGenerator();
		gen.begin("measurement");
		gen.addTag("sensorKind", sensorKind);
		gen.addTag("time", time);
		gen.addTag("latitude", latitude);
		gen.addTag("longitude", longitude);
		gen.addTag("speed", speed);
		gen.addTag("deviceId", deviceId);
		gen.addTag("deviceKind", deviceKind);
		gen.addTag("data", data);
		return gen.end();
	}
	
	private void save()
	{
		String fileName = sensorKind + "_" + time + ".xml";
		
		synchronized (FileUtility.class) 
		{
			FileUtility.writeFile(context, "measurements", fileName, getXmlString());	
		}
		
		Logger.d(FileUtility.readFile(context, "measurements", fileName));
	}
	
	public static void generate(Context context, String sensorKind, Object data)
	{
		Measurement measurement = new Measurement(context, sensorKind, data);
		
		measurement.save();
	}
}
