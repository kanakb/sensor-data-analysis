package com.senstasticDemo;

import com.senstastic.SensorService;

public class MySensorService extends SensorService
{
	public int getInterval()
	{
		return 5;
	}
	
	protected void sense()
	{
		int j = 0;
		j++;
	}
}
