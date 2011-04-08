package senstastic
{
	import flash.events.GeolocationEvent;
	import flash.events.TimerEvent;
	import flash.sensors.Geolocation;
	import flash.utils.Timer;

	public class GPS
	{
		public static var timeout:Number = 30;
		public static var desiredAccuracy:Number = 10;
		public static var minAcceptableAccuracy:Number = 100;
		public static var requestedUpdateInterval:Number = 100;
		
		private static var geolocation:Geolocation;
		private static var timer:Timer;
		private static var bestGeolocationEvent:GeolocationEvent;
		private static var callbacks:Array = new Array();
		
		public static function requestLocation(callback:Function):void
		{
			// Immediately perform the callback if Geolocation is not supported.
			if (!Geolocation.isSupported)
			{
				callback(null);
				Log.log("Geolocation is not supported.");
			}
				
			// Add the new request callback.
			callbacks.push(callback);
			
			startCapture();
		}
		
		private static function onGeolocationUpdate(event:GeolocationEvent):void
		{
			if (!bestGeolocationEvent || getCombinedAccuracy(event) < getCombinedAccuracy(bestGeolocationEvent))
				bestGeolocationEvent = event
			
			// If the location data has reached our desired accuracy, perform all of the request callbacks.
			if (beatsAccuracy(bestGeolocationEvent, desiredAccuracy))
				performCallbacks(bestGeolocationEvent);
				
			// If there are no more requests, stop capturing Geolocation data.
			if (callbacks.length <= 0)
				stopCapture();
		}
		
		private static function onTimeout(event:TimerEvent):void
		{
			stopCapture();
			
			// If the best location is accurate enough, perform the request callbacks.
			if (beatsAccuracy(bestGeolocationEvent, minAcceptableAccuracy))
			{
				performCallbacks(bestGeolocationEvent);
			}
			else
			{
				performCallbacks(null);
				Log.log("Geolocation timed out. An acceptable accuracy could not be achieved.");
			}
		}
		
		private static function performCallbacks(event:GeolocationEvent):void
		{
			for each (var callback:Function in callbacks)
			{
				callback(bestGeolocationEvent);
			}

			// Clear all of the request callbacks.
			callbacks = new Array();
		}
		
		private static function startCapture():void
		{
			if (!geolocation)
			{
				geolocation = new Geolocation();
				geolocation.setRequestedUpdateInterval(requestedUpdateInterval);
				geolocation.addEventListener(GeolocationEvent.UPDATE, onGeolocationUpdate);
				
				timer = new Timer(timeout * 1000, 1);
				timer.addEventListener(TimerEvent.TIMER_COMPLETE, onTimeout);
			}
		}
		
		private static function stopCapture():void
		{
			geolocation.removeEventListener(GeolocationEvent.UPDATE, onGeolocationUpdate);
			geolocation = null;

			timer.removeEventListener(TimerEvent.TIMER_COMPLETE, onTimeout);
			timer = null;
			
			bestGeolocationEvent = null;
		}
		
		private static function getCombinedAccuracy(event:GeolocationEvent):Number
		{
			return event.horizontalAccuracy + event.verticalAccuracy;
		}
		
		private static function beatsAccuracy(event:GeolocationEvent, accuracy:Number):Boolean
		{
			return event.verticalAccuracy <= accuracy && event.horizontalAccuracy <= accuracy;
		}
	}
}