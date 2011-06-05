package com.senstasticDemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BootCompletedReceiver extends BroadcastReceiver 
{
	public void onReceive(Context context, Intent intent)
	{
		// For debugging purposes, show a message that Senstastic is initializing when the phone boots up.
		Toast toast = Toast.makeText(context, "Senstastic Initializing...", 10);
		toast.show();
		
		// Initialize Senstastic.
		SenstasticDemo.initSenstastic(context);
	}
}
