package com.senstasticDemo;

import com.senstastic.Senstastic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver 
{
	public void onReceive(Context context, Intent intent)
	{
        String senstasticEndpointUrl = context.getString(R.string.senstastic_endpoint_url);
        Senstastic.init(senstasticEndpointUrl);
        Senstastic.schedule(context, VolumeSensorService.class);
	}
}
