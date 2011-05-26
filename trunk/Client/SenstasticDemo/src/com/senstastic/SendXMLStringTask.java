package com.senstastic;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

public class SendXMLStringTask extends AsyncTask<String, Void, Void> 
{
	protected Void doInBackground(String... xmlStrings) 
	{
	    try 
	    {
	    	// Get the XML string from the arguments list.
	    	String xmlString = xmlStrings[0];
	    	
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
	    	Logger.e("Error sending measurement!");
	    }
		
		return null;
	}
}
