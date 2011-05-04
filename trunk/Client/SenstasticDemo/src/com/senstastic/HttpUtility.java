package com.senstastic;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpUtility 
{
	public static String executeHttpRequest(String url, List<NameValuePair> postDataNameValuePairs)
	{
		try
		{
			// Set up the HTTP client and post request.
	    	HttpClient httpClient = new DefaultHttpClient();
	    	HttpPost httpPost = new HttpPost(url);
	        httpPost.setEntity(new UrlEncodedFormEntity(postDataNameValuePairs));
	
	        // Execute the post.
	        HttpResponse response = httpClient.execute(httpPost);
	        
	        // Return the response.
	        return getHttpResponseBody(response);
		}
		catch(Exception e)
		{
			Logger.e("Error executing HTTP request!");
			return null;
		}
	}
	
	private static String getHttpResponseBody(HttpResponse response)
	{
		try
		{
			HttpEntity httpEntity = response.getEntity();
			InputStream inputStream = httpEntity.getContent();
			Reader reader = new InputStreamReader(inputStream);
			StringBuilder stringBuilder = new StringBuilder();
			
			char[] buffer = new char[1024];
			int numBytesRead = 0;
			while((numBytesRead = reader.read(buffer)) != -1)
			{
				stringBuilder.append(buffer, 0, numBytesRead);
			}
			
			reader.close();
			return stringBuilder.toString();
		}
		catch(Exception e)
		{
			Logger.e("Error getting HTTP response body!");
			return null;
		}
	}
}
