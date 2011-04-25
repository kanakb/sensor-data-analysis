package com.senstastic;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

public class Measurement 
{	
	public static String MEASUREMENTS_DIRECTORY_NAME = "measurements";
	
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
		
		this.deviceId = DeviceInfo.getDeviceId(context);
		this.deviceKind = DeviceInfo.getDeviceKind();
		this.sensorKind = sensorKind;
		this.time = (new Date()).getTime();
		this.data = data;
		
		sendOrSave();
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
	
	private String getFileName()
	{
		return sensorKind + "_" + time + ".xml";
	}
	
	private void sendOrSave()
	{
		if (!ConnectionInfo.isWifiAvailable(context) || !send())
			save();
	}	
	
	private boolean send()
	{
		return sendXMLString(getXmlString());
	}
	
	private void save()
	{
		synchronized (FileUtility.class) 
		{
			FileUtility.writeFile(context, MEASUREMENTS_DIRECTORY_NAME, getFileName(), getXmlString());	
			Logger.d(FileUtility.readFile(context, MEASUREMENTS_DIRECTORY_NAME, getFileName()));
		}
	}
	
	private static boolean sendXMLString(String xml)
	{
	    try 
	    {
	    	HttpClient httpClient = new DefaultHttpClient();
	    	HttpPost httpPost = new HttpPost(Senstastic.endpointURL);
	    	
	    	// Build the post data.
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	        nameValuePairs.add(new BasicNameValuePair("xml", xml));
	        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute the post.
	        HttpResponse response = httpClient.execute(httpPost);
	        
	        // TODO: Return true or false based on response.
	        return true;
	    } 
	    catch (Exception e)
	    {
	    	Logger.e("Error sending measurement!");
	    	return false;
	    }
	}
	
	// Dealing with saved measurements.
	
	public static void attemptToSendSavedMeasurements(Context context)
	{
		if (ConnectionInfo.isWifiAvailable(context))
			sendSavedMeasurements(context);
	}
	
	private static void sendSavedMeasurements(Context context)
	{
		synchronized (FileUtility.class)
		{
			File[] files = FileUtility.getDirectoryFiles(context, MEASUREMENTS_DIRECTORY_NAME);
			
			for (File file : files)
			{
				String xmlString = FileUtility.readFile(file);
				
				// Try to send the measurement and clean up its file if it is sent out correctly.
				if (sendXMLString(xmlString))
					FileUtility.deleteFile(file);
				// If sending one measurement failed, don't try to send more.
				else
					return;
			}
		}
	}
}
