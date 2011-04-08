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
		
		public var id:String;
		public var time:Number;
		public var latitude:Number;
		public var longitude:Number;
		public var speed:Number;
		public var deviceKind:String;
		public var deviceId:String;
		public var sensorKind:String;
		private var _sensorData:String;
		private var _creationCompleteCallback:Function;
		
		public function get sensorData():String
		{
			return _sensorData;
		}
		
		public function set sensorData(value:*):void
		{
			// Encode binary data.
			if (value is ByteArray)
			{
				var encoder:Base64Encoder = new Base64Encoder();
				encoder.encodeBytes(value);
				_sensorData = encoder.toString();
			}
			// Convert primitive to string.
			else
			{
				_sensorData = value.toString();
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
		
		// Initialization.
		
		public static function asyncCreate(sensorKind:String, sensorData:*, creationCompleteCallback:Function):void
		{
			// Create a new measurement, and initialize its values.
			var measurement:Measurement = new Measurement();
			measurement.id = UIDUtil.createUID();
			measurement.time = unixTime;
			measurement.deviceKind = Device.kind;
			measurement.deviceId = Device.id;
			measurement.sensorKind = sensorKind;
			measurement.sensorData = sensorData;
			measurement._creationCompleteCallback = creationCompleteCallback;
			
			// Request GPS location.
			GPS.requestLocation(measurement.onGPSCallback);
		}
		
		public function onGPSCallback(event:GeolocationEvent):void
		{
			// If a GPS location could not be acquired, callback with null.
			if (!event)
			{
				_creationCompleteCallback(null);
				return;
			}
			
			latitude = event.latitude;
			longitude = event.longitude;
			speed = event.speed;
			
			_creationCompleteCallback(this);
		}
		
		public static function fetchSavedMeasurements():Array
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
		
		// Persistence.
		
		public function save():void
		{
			if (!file)
				FileUtility.writeObject(file, this);
		}
		
		public function destroy():void
		{
			FileUtility.destroyObject(file);
		}
		
		// Sending.

		public function send(url:String):void
		{
			var service:HTTPService = new HTTPService();
			service.url = url;
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
	}
}