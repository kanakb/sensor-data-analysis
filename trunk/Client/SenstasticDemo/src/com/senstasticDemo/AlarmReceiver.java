package com.senstasticDemo;


import com.senstastic.WakefulIntentService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver
{
	public void onReceive(Context context, Intent intent) 
	{
		WakefulIntentService.sendWakefulWork(context, SensorService.class);
	}
}
