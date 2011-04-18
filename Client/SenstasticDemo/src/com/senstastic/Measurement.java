package com.senstastic;

import java.io.FileOutputStream;
import java.util.Date;

import android.content.Context;

public class Measurement 
{	
	private static String SAVED_MEASUREMENTS_DIRECTORY = "measurements/";
	
	private Context context;
	
	private int id;
	private long time;
	private float latitude;
	private float longitude;
	private float speed;
	private String deviceId;
	private String deviceKind;
	private String sensorKind;
	private String data;
	
	public Measurement(Context context, String sensorKind, Object data)
	{
		this.context = context;
		
		id = 0;
		
		Date date = new Date();
		time = date.getTime();
		
		latitude = 0;
		longitude = 0;
		speed = 0;
		
		deviceId = "";
		deviceKind = "";
		
		this.sensorKind = sensorKind;
		this.data = "";
	}
	
	private String getXmlString()
	{
		XMLStringGenerator gen = new XMLStringGenerator();
		gen.begin("measurement");
		gen.addTag("id", id);
		gen.addTag("time", time);
		gen.addTag("latitude", latitude);
		gen.addTag("longitude", longitude);
		gen.addTag("speed", speed);
		gen.addTag("deviceId", deviceId);
		gen.addTag("deviceKind", deviceKind);
		gen.addTag("sensorKind", sensorKind);
		gen.addTag("data", data);
		return gen.end();
	}
	
	private void save()
	{
		try
		{
			String filePath = Integer.toString(id) + ".xml";

			FileOutputStream fileOutputStream = context.openFileOutput(filePath, Context.MODE_PRIVATE);
			String s = getXmlString();
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
