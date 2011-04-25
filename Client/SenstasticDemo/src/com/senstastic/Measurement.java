package com.senstastic;

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
		
		// Send the measurement or save it.
		if (ConnectionInfo.isWifiAvailable(context))
			send();
		else
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
	
	private String getFileName()
	{
		return sensorKind + "_" + time + ".xml";
	}
	
	private void save()
	{
		synchronized (FileUtility.class) 
		{
			FileUtility.writeFile(context, MEASUREMENTS_DIRECTORY_NAME, getFileName(), getXmlString());	
			Logger.d(FileUtility.readFile(context, MEASUREMENTS_DIRECTORY_NAME, getFileName()));
		}
	}
	
	private void delete()
	{
		synchronized (FileUtility.class)
		{
			FileUtility.deleteFile(context, MEASUREMENTS_DIRECTORY_NAME, getFileName());
		}
	}
	
	private void send()
	{
	    try 
	    {
	    	HttpClient httpClient = new DefaultHttpClient();
	    	HttpPost httpPost = new HttpPost(Senstastic.endpointURL);
	    	
	    	// Build the post data.
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	        nameValuePairs.add(new BasicNameValuePair("xml", getXmlString()));
	        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute the post.
	        HttpResponse response = httpClient.execute(httpPost);
	        
	        // TODO: Delete measurement on successful response.
	    } 
	    catch (Exception e)
	    {
	    	Logger.e("Error sending measurement!");
	    }
	}
}
