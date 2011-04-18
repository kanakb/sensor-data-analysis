package com.senstastic;

import java.io.FileOutputStream;
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
		deviceId = "";
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
		try
		{
			String filePath = sensorKind + time + ".xml";

			FileOutputStream fileOutputStream = context.openFileOutput(filePath, Context.MODE_PRIVATE);
			fileOutputStream.write(getXmlString().getBytes());
			fileOutputStream.close();
		}
		catch(Exception e)
		{
			System.err.println("Could not save measurement!");
			return;
		}
	}
	
	public static void generate(Context context, String sensorKind, Object data)
	{
		Measurement measurement = new Measurement(context, sensorKind, data);
		
		measurement.save();
	}
}
