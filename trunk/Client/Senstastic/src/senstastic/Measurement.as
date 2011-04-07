package senstastic
{
	import flash.utils.ByteArray;
	import flash.xml.XMLDocument;
	import flash.xml.XMLNode;
	
	import mx.rpc.xml.SimpleXMLEncoder;
	import mx.utils.Base64Encoder;

	public class Measurement
	{
		public var deviceType:String;
		public var deviceId:String;
		public var measurementTime:int;
		public var latitude:Number;
		public var longitude:Number;
		public var sensorType:String;
		public var sensorData:String;
		
		public function set numericSensorData(value:Number):void
		{
			sensorData = value.toString();
		}
		
		public function set binarySensorData(value:ByteArray):void
		{
			var encoder:Base64Encoder = new Base64Encoder();
			encoder.encodeBytes(value);
			sensorData = encoder.toString();
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
		
		public function Measurement()
		{
			
		}
	}
}