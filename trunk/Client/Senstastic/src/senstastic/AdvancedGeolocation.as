package senstastic
{
	import flash.events.Event;
	import flash.events.GeolocationEvent;
	import flash.events.TimerEvent;
	import flash.sensors.Geolocation;
	import flash.utils.Timer;

	public class AdvancedGeolocation extends Geolocation
	{
		private static const REQUESTED_UPDATE_INTERVAL:Number = 100;
		
		private var _callback:Function;
		private var _desiredAccuracy:Number;
		private var _minimumAcceptableAccuracy:Number;
		private var _timeout:Number;
		private var _timer:Timer;
		private var _bestGeolocationEvent:GeolocationEvent;
		
		public function AdvancedGeolocation(callback:Function, desiredAccuracy:Number=100, minimumAcceptableAccuracy:Number=200, timeout:Number=10)
		{
			super();
			
			setRequestedUpdateInterval(REQUESTED_UPDATE_INTERVAL);
			
			_callback = callback;
			_desiredAccuracy = desiredAccuracy;
			_minimumAcceptableAccuracy = minimumAcceptableAccuracy;
			_timeout = timeout;
			
			if (!Geolocation.isSupported)
				return callbackFailure("Geolocation is not supported!");

			if (muted)
				return callbackFailure("Gelocation is muted!");
			
			_timer = new Timer(timeout * 1000, 1);
			_timer.addEventListener(TimerEvent.TIMER_COMPLETE, onTimeout);
			_timer.start();
			
			addEventListener(GeolocationEvent.UPDATE, onGeolocationUpdate);
		}
		
		private function onGeolocationUpdate(event:GeolocationEvent):void
		{	
			if (isMoreAccurate(event, _bestGeolocationEvent))
				_bestGeolocationEvent = event;
			
			if (meetsAccuracy(_bestGeolocationEvent, _desiredAccuracy))
				callbackSuccess("Geolocation met desired accuracy.");
		}
		
		private function onTimeout(event:TimerEvent):void
		{
			if (meetsAccuracy(_bestGeolocationEvent, _minimumAcceptableAccuracy))
				callbackSuccess("Geolocation met minimum acceptable accuracy.");
			else
				callbackFailure("Geolocation timed out and did not meet minimum acceptable accuracy!");
		}
		
		// Callback and Termination.
		
		private function callbackSuccess(logMessage:String=null):void
		{
			if (logMessage)
				Log.log(logMessage);
					
			deinit();
			_callback(_bestGeolocationEvent);
		}
		
		private function callbackFailure(logMessage:String=null):void
		{
			if (logMessage)
				Log.log(logMessage);
			
			deinit();
			_callback(null);
		}
		
		private function deinit():void
		{
			if (_timer)
			{	
				_timer.removeEventListener(TimerEvent.TIMER_COMPLETE, onTimeout);
				_timer = null;
			}
			
			removeEventListener(GeolocationEvent.UPDATE, onGeolocationUpdate);
		}
		
		// GeolocationEvent Comparison.
		
		private static function meetsAccuracy(event:GeolocationEvent, accuracy:Number):Boolean
		{
			return event && event.verticalAccuracy <= accuracy && event.horizontalAccuracy <= accuracy;
		}
		
		private static function isMoreAccurate(event1:GeolocationEvent, event2:GeolocationEvent):Boolean
		{	
			return !event2 || event1.verticalAccuracy + event1.horizontalAccuracy < event2.verticalAccuracy + event2.horizontalAccuracy;
		}
	}
}