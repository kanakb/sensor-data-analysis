package com.senstasticDemo;

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
