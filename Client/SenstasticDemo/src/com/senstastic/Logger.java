package com.senstastic;

import android.util.Log;

public class Logger 
{
	public static String LOG_TAG = "Senstastic";
	public static String MESSAGE_SEPARATOR = "\n";
	public static int MAX_MESSAGES = 100;
	
	private static String log = "";
	
	public static String getLog()
	{
		return log;
	}
	
	public static void d(String message)
	{
		log += message + MESSAGE_SEPARATOR;
		Log.d(LOG_TAG, message);
	}
	
	public static void e(String message)
	{
		log += message + MESSAGE_SEPARATOR;
		Log.e(LOG_TAG, message);
	}
}
