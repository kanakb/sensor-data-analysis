package senstastic
{
	import flash.utils.ByteArray;
	import flash.xml.XMLDocument;
	import flash.xml.XMLNode;
	
	import mx.rpc.xml.SimpleXMLEncoder;
	import mx.utils.Base64Encoder;

	public class Measurement
	{
		public var deviceKind:String;
		public var deviceId:String;
		public var measurementTime:Number;
		public var latitude:Number;
		public var longitude:Number;
		public var sensorType:String;
		private var _sensorData:String;
		
		public function Measurement(sensorType:String, sensorData:*)
		{
			this.sensorType = sensorType;
			this.sensorData = sensorData;
		
			deviceKind = Device.deviceKind;
			deviceId = Device.deviceId;
			measurementTime = unixTime;
		}
		
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
	}
}