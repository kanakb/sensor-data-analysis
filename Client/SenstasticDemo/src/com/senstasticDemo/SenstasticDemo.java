package com.senstasticDemo;

import com.senstastic.Senstastic;

import android.app.Activity;
import android.os.Bundle;

public class SenstasticDemo extends Activity
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Senstastic.schedule(this, MySensorService.class, 20);
    }
}