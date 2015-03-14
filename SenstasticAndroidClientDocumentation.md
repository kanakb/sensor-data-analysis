# What is the Senstastic Android client? #
Written by Max Vujovic on June 2, 2011.

Senstastic is a lightweight framework for passive participatory sensing on Android devices. It allows you to schedule your code to run in the background at a specified interval on users' devices. Your code will most likely use the device's built-in sensors in some way to generate measurement data. Senstastic will automatically augment your measurement data with additional metadata, including a geographic location (a latitude, longitude pair), the time, and additional device information. Senstastic will save the measurement data to the device until a WiFi connection becomes available. The device will then push the measurement data over a WiFi connection to your Senstastic server or any other web service you make to receive the measurements.

The Senstastic client is designed to consume a minimum amount of power and send mostly anonymous measurements. Senstastic generates a random device ID that it sends attached to measurements. Unless someone knows a particular user has a particular Senstastic device ID, the measurement is essentially anonymous. The device ID is used so that users can look up statistics about themselves specifically, not just aggregate statistics.

Note that in this document, we will sometimes call the Senstastic client-side framework as simply "Senstastic". We will also define the Senstastic framework to be all classes under the package com.senstastic.**.**

Note that the Senstastic framework is not extensively tested; we provide no guarantees, and you should use it at your own risk. However, we did our best to deliver clear, stable code and we do think Senstastic can be extremely useful as a foundation for participatory sensing applications. Our code is also well commented, so go ahead and explore the open source goodness!

The Senstastic client was written by Max Vujovic. The Senstastic server-side was written by Kanak Biscuitwala. Both were written as research project in Spring 2011 under the guidance of Professor Giovanni Pau at UCLA.

If you have any questions about the Senstastic client, ask the guy who wrote it by emailing maxvujovic at gmail dot com.

# How do I get started? #

SVN checkout the SenstasticDemo Android project for a working example of a participatory sensing application called "Noise Mapping" built on the Senstastic framework. The easiest way to use the Senstastic client side is to understand the SenstasticDemo project and transform it to fit your needs.

# How does the SenstasticDemo begin to work? #

Let's pretend you wrote the SenstaticDemo, a sample application that uses the Senstatic framework. Let's explore how the SenstasticDemo begins its work.

  1. When the user's device boots up, the Android OS broadcasts a BOOT\_COMPLETED intent.
  1. Your BootCompletedReceiver class, defined in BootCompletedReceiver.java, receives the intent in its onReceive method.
  1. In the onReceive method, you start up the Senstastic framework. You made a method called initSenstastic() that does this.
  1. In initSenstastic(), you provide the Senstastic framework with the URL where you would like to send all your sensor measurements, called the endpoint URL. This URL usually points to a script on the Senstastic server. Then, in initSenstastic(), you schedule which of your custom SensorService(s) you would like Senstastic to schedule and periodically execute.
  1. Suppose the user just downloaded your application, and you would like Senstastic to start sensing as soon as the user opens the application and not wait until the next time the device boots up. Then, in your application's main activity, you should check if this is the first launch of the your Senstastic application, and if it is, call your initSenstastic() method. Senstastic makes this easy; check out the code snippets from the SenstasticDemo project below.

```

// BootCompletedReceiver.java
public class BootCompletedReceiver extends BroadcastReceiver 
{
	// This is called when the device boots up.
	public void onReceive(Context context, Intent intent)
	{
		SenstasticDemo.initSenstastic(context);
	}
}

// SenstasticDemo.java
public class SenstasticDemo extends Activity implements OnClickListener
{
	...
	
	// You wrote this function to start up the Senstatic framework, telling it where to send measurements and what custom SensorService(s) to schedule.
	public static void initSenstastic(Context context)
	{
		String senstasticEndpointUrl = context.getString(R.string.senstastic_endpoint_url);
		Senstastic.init(senstasticEndpointUrl);
		Senstastic.schedule(context, VolumeSensorService.class);
	}
	
	// This is called when the user starts your application.
	public void onCreate(Bundle savedInstanceState) 
	{
		...  
        
		// If the user has just downloaded the app and started it for the first time, start Senstastic here. Otherwise, Senstastic will be launched when the device boots up, and shouldn't be relaunched here when the user opens the app again.
		if (Senstastic.isFirstLaunch(this))
			initSenstastic(this);
	}
    
    ...
}

```

# What is a SensorService, and why should I make one? #

You need to make your own SensorService to get any sensing done. A SensorService is a short-lived service that you can schedule to run at periodic intervals. You can make your own SensorService(s) by creating a new class that extends SensorService.

You can see an real example of extending SensorService in the VolumeSensorService class in the SenstasticDemo project. When Senstastic periodically invokes the VolumeSensorService, it records a small audio sample, determines the volume of the audio sample, and then passes the volume measurement to the Senstastic framework. The Senstastic framework then attaches additional metadata to the measurement such as the device's current location, the current time, and a Senstastic device id (described later). The Senstastic framework will then push out the measurement and metadata to the Senstastic server immediately if the device has a WiFi connection. Otherwise, Senstastic will save the measurement and metadata to the device's persistent internal memory until WiFi becomes available.

Lets look at an even simpler example of extending SensorService:

```

public class SimpleSensorService extends SensorService
{	
	protected String getSensorKind()
	{
		return "SimpleSensor";
	}
	
	protected int getIntervalInSeconds()
	{
		return 3600;
	}
	
	protected void sense()
	{
		int measurement_data = 123;
		finishSensing(data);
	}
}

```

This is a fully functioning SensorService! Granted, it always measures "123". This example shows the three methods you will usually override.

  1. getSensorKind() should return the name of your sensor as you wish it to be called. The name of your sensor will be attached to measurements you send to the Senstastic server.
  1. getIntervalInSeconds() should return how frequently your SensorService should run. This example will run every hour, or 3,600 seconds.
  1. sense() is the entry point of your SensorService. When Senstastic tells your SensorService to run at your specified interval, execution begins here.

## How do I finish sensing? ##

When you are done sensing and have obtained your desired measurement, you must pass the measurement data to the Senstastic framework by calling finishSensing(data). This will attach metadata to your measurement, save it to persistent memory or send it to the server, and finally shut down your SensorService until it is invoked again later.

finishSensing(data) can take in a variety of data types. It accepts any Java Object or primitive. Java primitives will automatically be converted into their corresponding objects (e.g. int -> Integer) by the JRE. Any custom object you pass in should be able to be converted to a string representation via its toString() method. This method belongs to all Java Objects. If you pass in a byte array (byte[.md](.md)), the Senstastic framework will automatically Base64 encode your data for transport via XML to the Senstastic server (described later).

## Does my sensing code block? ##

No. Senstastic automatically runs your SensorService in its own thread, so any code you write in sense() will not block anything else in your application. If sense() returns, note that your SensorService and its thread will still be running until you call SensorService's finishSensing(data) method. It is imperitive that you call finishSensing(data) in order to save or send your measurement and stop the SensorService.

## Can I sense asynchronously? ##

Yes. The thread your SensorService runs in is a special kind of Android thread called a HandlerThread. This means the thread has a run loop and can support asynchronous operations. Thus, in sense(), you can start an asynchronous operation which finishes with a callback to a custom method you defined in SensorService. That custom method can then call finishSensing(data) to save or send your measurement and shut down the SensorService. Here's a pseudocode example using a fake class called Ping that pings hosts asynchronously to illustrate asynchronous sensing:

```

public class PingSensorService extends SensorService, PingReceiver
{	
	protected String getSensorKind()
	{
		return "SimpleSensor";
	}
	
	protected int getIntervalInSeconds()
	{
		return 3600;
	}
	
	protected void sense()
	{
		// pingHost operates asynchronously. When pingHost returns, it will call this SensorService's callback method.
		Ping.pingHost(this);
	}
	
	private void callback(int delayInMilliseconds)
	{
		finishSensing(delayInMilliseconds);
	}
}

```

A real example of a class that operates asynchronously (and thus requires a run loop to return its data to) is Android's LocationManager class. Check that out if you want more information, or look at the source code for the SensorService base class, which uses LocationManager (though another class called NetworkLocationRequest) to get the device's GPS coordinates.

## Why must I add my SensorService to the Android manifest file? ##

After creating a SensorService, you must remember to add it to the Android manifest file! Otherwise, the Android OS will not be able to start it up. Here is the line in AndroidManifest.xml that adds the VolumeSensorService from the SenstasticDemo project:

```
		<!-- Add your sensor services here. -->
		<service android:name="VolumeSensorService"></service>
```

It's small, but its absolutely necessary! Check out AnroidManifest.xml in SenstasticDemo to see exactly where to declare your SensorService if you're not sure.

# What does the Senstastic client send out to the Senstastic server? #

The Senstastic client sends each measurement via an HTTP POST request to the Senstastic server or whatever endpoint URL you specify. The HTTP POST request data has a single field named "xml". As you may suspect, Senstastic stuffs your measurement data and the metadata Senstastic generates into an XML string. Then it sends the XML string to the Senstastic server by putting it in the HTTP POST request in the "xml" POST data field. Here is an example of the XML string representing a measurement that the Senstastic client sends to the Senstastic server:

```
<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>
<measurement>
  <deviceId>684bb906-b39a-4882-b60f-e765ef6b7e0c</deviceId>
  <deviceKind>Nexus One</deviceKind>
  <sensorKind>volume</sensorKind>
  <time>1307064664961</time>
  <latitude>34.0699599</latitude>
  <longitude>-118.45186149999999</longitude>
  <data>2024</data>
</measurement>
```

Let's go over the XML tags of the measurement that Senstastic generates in detail.

## deviceId ##

Senstastic creates a random device ID string for a user's device on its first launch. This ID is not guaranteed to be unique across all devices, but it is extremely likely that each device that runs your app will generate a different ID. For simplicity, Senstastic does not currently negotiate a guaranteed unique device ID with the server. Senstastic generates a device ID instead of using the device's actual ID or a MAC address for added anonymity. If a user uninstalls and reinstalls your Senstastic-based application, Senstastic will create a new device ID for the new installation of the application.

## deviceKind ##

deviceKind is a string that describes the kind of device that generated the measurement. Senstastic asks the Android OS for the model name of the user's device such as  a "Nexus One".

## sensorKind ##

sensorKind is a string that identifies the kind of sensor that generated the measurement. You define the name of your sensor in your SensorService's getSensorKind() method.

## time ##
time is long integer that represents the UNIX GMT timestamp in milliseconds at the time the measurement was taken. Specifically, the time is taken when your SensorService calls finishSensing(data).

## latitude, longitude ##
latitude and longitude are floats that specify the GPS coordinates where the measurement was taken. Senstastic automatically figures this out after you pass it your measurement data via finishSensing(data).

GPS locations can be obtained in various ways. Currently, Senstastic only uses cell towers and WiFi connections to obtain GPS locations, which is particularly well suited for low power consumption in urban environments. By default, classes that extend SensorService use the NetworkLocationRequest class defined in the Senstastic framework to fetch the GPS location from cell towers and WiFi connections.

You can use different methods of fetching the GPS location, perhaps from satelites instead of cell towers, by creating your own classes like NetworkLocationRequest. If you intend to do this, your class should implement the LocationRequest interface, and your SensorService should override the getLocationRequest() method to return a new instance of your custom LocationRequest. Currently, the SensorService base class returns a new NetworkLocationRequest in its getLocationRequest() method. For more details, look at the code for NetworkLocationRequest, and for even more details, look at how the SensorService class initiates a NetworkLocationRequest when finishSensing(data) is called.

## data ##
Finally, data is the measurement data your SensorService created and passed to Senstastic by calling finishSensing(data). Senstastic represents numerical values as strings in the XML. If you passed a byte array representing binary data to finishSensing(data), Senstastic will automatically Base64 encode the binary data for safe insertion between the XML data tags.

# What should I know about modifying the Senstastic framework? #

Senstastic is a lightweight framework, and you are welcome to modify the framework itself to fit your specific needs. If you do plan to modify the framework, there are few important items and design descisions we made that we would to point out. These points are not as important if you plan on using the framework as is.

  1. Senstastic code uses the FileUtility wrapper class to access the file system. Senstastic creates a "measurements" directory for storing measurements that need to be sent later and a "device" directory to store the device ID Senstastic generates. Both directories are created on the device's internal storage.
  1. When any Senstastic code accesses the file system for reading or writing data like the device ID or a measurement, it should the usage of the FileUtility class from any other part of the Senstastic system. Senstastic does this by creating a Java synchronized block using the FileUtility's static class member any time it's manipulating the file system via FileUtility. Here is an example from Senstastic's DeviceInfo class of locking up the file system via the FileUtility class while Senstastic accesses it:

```
	public static String getSavedDeviceId(Context context)
	{
		synchronized (FileUtility.class)
		{
			// If a previously saved device id exists, read it from the file system and return it.
			if (FileUtility.fileExists(context, DEVICE_DIRECTORY_NAME, DEVICE_ID_FILE_NAME))
				return FileUtility.readFile(context, DEVICE_DIRECTORY_NAME, DEVICE_ID_FILE_NAME);
			// Otherwise, return null.
			else
				return null;
		}
	}
```

> # Desired Improvements #

> The following are improvements to the Senstastic client framework that we would like to see in future versions.

  * Placing an upper limit on the number of measurements that can be saved on the device's internal storage.
  * Sending out multiple measurements in a single HTTP POST request instead of sending an HTTP POST request per measurement.
  * Pulling the measurement save vs. send policy out of the Measurements class. The send when WiFi is connected policy should be swappable with other policies that can take into account other information like battery life.
  * Scheduling SensorServices to run at specific times or variable intervals.
  * Implementing a measurement taking policy. For example, do not take let a SensorService run if the device battery is less than 15%.

> # More Help #

> If you have any questions about the Senstastic client, ask the guy who wrote it by emailing maxvujovic at gmail dot com.