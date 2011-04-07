package senstastic
{
	import flash.filesystem.File;
	import flash.utils.ByteArray;
	import flash.xml.XMLDocument;
	import flash.xml.XMLNode;
	
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
		public var sensorType:String;
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
				_sensorData = value.toString;
			}
		}
		
		public function get xmlString():String 
		{
			var qualifiedName:QName = new QName("measurement");
			var xmlDocument:XMLDocument = new XMLDocument();
			var simpleXMLEncoder:SimpleXMLEncoder = new SimpleXMLEncoder(xmlDocument);
			var xmlNode:XMLNode = simpleXMLEncoder.encodeValue(this, qualifiedName, xmlDocument);
			var xml:XML = new XML(xmlDocument.toString());
			var xmlString:String = xml.toXMLString();
			return xmlString;
		}
		
		private static function get unixTime():Number
		{
			var date:Date = new Date();
			return date.time / 1000;
		}
		
		public function Measurement(sensorType:String, sensorData:*)
		{
			this.sensorType = sensorType;
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
			FileUtility.writeObject(SAVED_MEASUREMENTS_DIRECTORY.resolvePath(measurementId), this);
		}

	}
}