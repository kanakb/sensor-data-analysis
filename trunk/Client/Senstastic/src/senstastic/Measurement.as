package senstastic
{
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
		private static const SAVED_MEASUREMENTS_DIRECTORY_NAME:String = "measurements/";
		private static const SAVED_MEASUREMENTS_DIRECTORY:File = File.applicationStorageDirectory.resolvePath(SAVED_MEASUREMENTS_DIRECTORY_NAME);
		
		public var measurementId:String;
		public var deviceKind:String;
		public var deviceId:String;
		public var measurementTime:Number;
		public var latitude:Number;
		public var longitude:Number;
		public var sensorKind:String;
		private var _sensorData:String;
		
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
			return SAVED_MEASUREMENTS_DIRECTORY.resolvePath(measurementId);
		}
		
		private static function get unixTime():Number
		{
			var date:Date = new Date();
			return date.time / 1000;
		}
		
		public function Measurement(sensorKind:String, sensorData:*)
		{
			this.sensorKind = sensorKind;
			this.sensorData = sensorData;
			
			measurementId = UIDUtil.createUID();
			deviceKind = Device.deviceKind;
			deviceId = Device.deviceId;
			measurementTime = unixTime;
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
		
		public function save():void
		{
			FileUtility.writeObject(file, this);
		}
		
		public function destroy():void
		{
			FileUtility.destroyObject(file);
		}

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
			// Try again later.
		}
	}
}