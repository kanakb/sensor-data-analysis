package com.senstasticDemo;

import com.senstastic.Sensor;

public class RandomSensor implements Sensor
{
	public int getInterval()
	{
		return 10;
	}
	
	public void sense()
	{
		int i = 0;
		i++;
	}
}
