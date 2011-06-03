package com.senstastic;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

public class Measurement 
{	
	/*
	 * This is the name of the directory created in the application storage directory
	 * that stores all measurements that have not been sent to the server yet.
	 */
	public static String MEASUREMENTS_DIRECTORY_NAME = "measurements";
	
	/*
	 * This is the application context in which the measurement object was created.
	 * It is used to fetch information about the device that the measurement object needs in its methods
	 * (e.g. the device id, wifi availability).
	 * This is not sent to the server nor saved to disk with the measurement.
	 */
	private Context context;
	
	/*
	 * These are the core measurement data fields.
	 * These are sent to the server or saved to disk with the measurement.
	 */
	private String deviceId;
	private String deviceKind;
	private String sensorKind;
	private long time;
	private double latitude;
	private double longitude;
	private Object data;
	
	/*
	 * This constructor creates a measurement object.
	 * Then, the caller should decide what to do with this measurement.
	 * Usually, the caller should call sendWithSaveFallback.
	 */
	public Measurement(Context context, String sensorKind, Object data, double latitude, double longitude)
	{
		this.context = context;
		
		this.deviceId = DeviceInfo.getDeviceId(context);
		this.deviceKind = DeviceInfo.getDeviceKind();
		this.sensorKind = sensorKind;
		this.time = (new Date()).getTime();
		this.latitude = latitude;
		this.longitude = longitude;
		this.data = data;
	}
	
	// TODO: Ask Kanak to support measurement tag.
	/*
	 * This method encodes the measurement as an XML string.
	 * This XML string may be saved in a file for later sending, or
	 * this xml string may be sent as POST data to the server.
	 */
	private String getXmlString()
	{
		XmlStringGenerator gen = new XmlStringGenerator();
		gen.begin("measurements");
			gen.startTag("measurement");
				gen.addTag("deviceId", deviceId);
				gen.addTag("deviceKind", deviceKind);
				gen.addTag("sensorKind", sensorKind);
				gen.addTag("time", time);
				gen.addTag("latitude", latitude);
				gen.addTag("longitude", longitude);
				gen.addTag("data", data);
			gen.endTag("measurement");
		return gen.end();
	}
	
	/*
	 * This method gets the file name for the measurement.
	 * If the measurement is saved in the measurements directory for later sending,
	 * it is saved under this file name.
	 */
	private String getFileName()
	{
		return sensorKind + "_" + time + ".xml";
	}
	
	/*
	 * This method attempts to send the measurement to the server,
	 * respecting the measurement sending policy.
	 * It saves the measurement to disk if the measurement sending policy requirements are currently not met.
	 */
	public void sendWithSaveFallback()
	{
		if (shouldSendMeasurements(context))
			send();
		else
			save();
	}
	
	/*
	 * This method attempts to send the measurement to the server,
	 * It does not respect the measurement sending policy.
	 */
	private void send()
	{
		sendXMLString(getXmlString());
	}
	
	/*
	 * This method saves the measurement to the measurements directory as an XML file .
	 * It will be sent to the server at a later time, whenever attemptToSendSavedMeasurements is called.
	 */
	private void save()
	{
		synchronized (FileUtility.class) 
		{
			String fileName = getFileName();
			String xmlString = getXmlString();
			
			FileUtility.writeFile(context, MEASUREMENTS_DIRECTORY_NAME, fileName, xmlString);
			Logger.d("Measurement written to file " + fileName + ": \n" + xmlString);
		}
	}
	
	/*
	 * This method sends an XML string to the server.
	 * It places the XML string in a POST data variable called "xml".
	 */
	private static void sendXMLString(String xmlString)
	{
	    try 
	    {
	    	// Build the POST data.
	        List<NameValuePair> postDataNameValuePairs = new ArrayList<NameValuePair>(1);
	        postDataNameValuePairs.add(new BasicNameValuePair("xml", xmlString));

	        // Execute the HTTP request.
	        Logger.d("Sending HTTP POST data to server: \n" + xmlString);
	        String responseBody = HttpUtility.executeHttpRequest(Senstastic.endpointURL, postDataNameValuePairs);
	        Logger.d("HTTP response body: " + responseBody);
	    } 
	    catch (Exception e)
	    {
	    	Logger.e(e.getMessage());
	    }
	}

	/*
	 * This method implements the measurement sending policy.
	 * The current policy is measurements should be sent
	 * when a wifi connection is available.
	 */
	private static boolean shouldSendMeasurements(Context context)
	{
		return ConnectionInfo.isWifiAvailable(context);
	}
	
	/*
	 * This method attempts to send all saved measurements to the server,
	 * while respecting the measurment sending policy.
	 */
	public static void sendSavedMeasurements(Context context)
	{
		// Return quickly if the measurement sending policy dictates that we should not send measurements now.
		if (!shouldSendMeasurements(context))
			return;
		
		synchronized (FileUtility.class)
		{		
			File[] files = FileUtility.getDirectoryFiles(context, MEASUREMENTS_DIRECTORY_NAME);
			
			if (files == null)
				return;
			
			for (File file : files)
			{
				// If something changed (e.g. wifi disconnected), stop sending any more measurements.
				if (!shouldSendMeasurements(context))
					return;
				
				// Get the saved measurement's XML string.
				String xmlString = FileUtility.readFile(file);
				
				// Send the saved measurement's XML string.
				sendXMLString(xmlString);
				
				// Delete the saved measurement whether or not sending succeeded.
				FileUtility.deleteFile(file);
			}
		}
	}
}
