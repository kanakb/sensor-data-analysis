package com.senstastic;

import android.util.Log;

public class Logger 
{
	public static String LOG_TAG = "Senstastic";
	public static String MESSAGE_SEPARATOR = "\n";
	public static int MAX_LENGTH = 100000;
	
	private static volatile String log = "";
	
	public static void d(String message)
	{
		appendMessage(message);
		Log.d(LOG_TAG, message);
	}
	
	public static void e(String message)
	{
		appendMessage(message);
		Log.e(LOG_TAG, message);
	}
	
	public static synchronized String getLog()
	{
		return log;
	}
	
	private static synchronized void appendMessage(String message)
	{
		log = message + MESSAGE_SEPARATOR + log;
		
		// Keep the log length in check.
		if (log.length() > MAX_LENGTH)
			log = log.substring(0, MAX_LENGTH);
	}
}
