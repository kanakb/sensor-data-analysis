package senstastic
{
	import flash.events.Event;
	import flash.events.GeolocationEvent;
	import flash.filesystem.File;
	import flash.utils.ByteArray;
	import flash.xml.XMLDocument;
	import flash.xml.XMLNode;
	
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.http.HTTPService;
	import mx.rpc.xml.SimpleXMLEncoder;
	import mx.utils.Base64Encoder;
	import mx.utils.UIDUtil;

	public class Measurement
	{
		// Properties.
		
		private static const SAVED_MEASUREMENTS_DIRECTORY_NAME:String = "measurements/";
		private static const SAVED_MEASUREMENTS_DIRECTORY:File = File.applicationStorageDirectory.resolvePath(SAVED_MEASUREMENTS_DIRECTORY_NAME);
		private static var _destinationURL:String;
		
		public var id:String;
		public var time:Number;
		public var latitude:Number;
		public var longitude:Number;
		public var speed:Number;
		public var deviceKind:String;
		public var deviceId:String;
		public var sensorKind:String;
		private var _data:String;
		
		public function get data():String
		{
			return _data;
		}
		
		public function set data(value:*):void
		{
			// Encode binary data.
			if (value is ByteArray)
			{
				var encoder:Base64Encoder = new Base64Encoder();
				encoder.encodeBytes(value);
				_data = encoder.toString();
			}
			// Convert primitive to string.
			else
			{
				_data = value.toString();
			}
		}
		
		private function get file():File
		{
			return SAVED_MEASUREMENTS_DIRECTORY.resolvePath(id);
		}
		
		private static function get unixTime():Number
		{
			var date:Date = new Date();
			return date.time / 1000;
		}
		
		public static function init(destinationURL:String):void
		{
			_destinationURL = destinationURL;
			
			// TODO: Listen to network changes.
		}
		
		// Initialization.
		
		public static function create(sensorKind:String, data:*):void
		{
			// Create a new measurement, and initialize its values.
			var measurement:Measurement = new Measurement();
			measurement.id = UIDUtil.createUID();
			measurement.time = unixTime;
			measurement.deviceKind = Device.kind;
			measurement.deviceId = Device.id;
			measurement.sensorKind = sensorKind;
			measurement.data = data;
			
			// Request GPS location.
			GPS.requestLocation(measurement.onGPSCallback);
		}
		
		private function onGPSCallback(event:GeolocationEvent):void
		{
			// If a GPS location could not be acquired, forget this measurement.
			if (!event)
				return;
			
			// Finish initializing the new measurement.
			latitude = event.latitude;
			longitude = event.longitude;
			speed = event.speed;
			
			// Send or save the measurement.
			if (Device.isWifiAvailable)
				send();
			else
				save();
		}
		

		// Persistence.
		
		private function save():void
		{
			if (!file.exists)
				FileUtility.writeObject(file, this);
		}
		
		private function destroy():void
		{
			if (file.exists)
				FileUtility.destroyObject(file);
		}
		
		// Sending.

		private function send():void
		{
			if (!_destinationURL)
			{
				Log("Destination URL is not set! Measurement could not be sent.");
				save();
				return;
			}
			
			var service:HTTPService = new HTTPService();
			service.url = _destinationURL;
			service.method = "POST";
			service.send(this);
		}
		
		private function onSendSuccess(event:ResultEvent):void
		{
			destroy();
		}
		
		private function onSendFailure(event:FaultEvent):void
		{
			save();
		}
		
		// Pushing out saved measurements.
		
		private static function sendSavedMeasurements():void
		{
			var measurements:Array = fetchSavedMeasurements();
			
			for each (var measurement:Measurement in measurements)
			{
				measurement.send();
			}
		}
		
		private static function fetchSavedMeasurements():Array
		{
			var savedMeasurementsDirectory:File = File.applicationStorageDirectory.resolvePath(SAVED_MEASUREMENTS_DIRECTORY_NAME);
			var files:Array = savedMeasurementsDirectory.getDirectoryListing();
			var measurements:Array = new Array();
			
			for each (var file:File in files)
			{
				measurements.push(FileUtility.readObject(file));
			}
			
			return measurements;
		}
		
	}
}