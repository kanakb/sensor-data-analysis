package com.senstastic;

import android.util.Log;

public class Logger 
{
	public static String LOG_TAG = "Senstastic";
	
	public static void v(String message)
	{
		Log.v(LOG_TAG, message);
	}
	
	public static void d(String message)
	{
		Log.d(LOG_TAG, message);
	}
	
	public static void i(String message)
	{
		Log.i(LOG_TAG, message);
	}
	
	public static void w(String message)
	{
		Log.w(LOG_TAG, message);
	}
	
	public static void e(String message)
	{
		Log.e(LOG_TAG, message);
	}
}
