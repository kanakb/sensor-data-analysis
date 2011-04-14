package com.senstasticDemo;

import com.senstastic.SensorService;

import android.app.Activity;
import android.os.Bundle;

public class SenstasticDemo extends Activity
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        SensorService.scheduleSensorServiceSubclass(this, SensorService.class, 20);
     }
}