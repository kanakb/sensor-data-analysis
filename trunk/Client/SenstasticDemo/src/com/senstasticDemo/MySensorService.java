package com.senstasticDemo;

import com.senstastic.SensorService;

public class MySensorService extends SensorService
{
	public int interval()
	{
		return 10;
	}
	
	protected void sense()
	{
		int j = 0;
		j++;
	}
}
