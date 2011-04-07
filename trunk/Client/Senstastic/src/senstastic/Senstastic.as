package senstastic
{
	import flash.events.IEventDispatcher;
	
	import mx.rpc.http.HTTPService;

	public class Senstastic
	{
		public static var measurementDestinationURL:String;
		
		public static function addSensor(sensor:IEventDispatcher):void
		{
			sensor.addEventListener(SensorEvent.MEASUREMENT_TAKEN, onMeasurementTaken);
		}
		
		public static function removeSensor(sensor:IEventDispatcher):void
		{
			sensor.removeEventListener(SensorEvent.MEASUREMENT_TAKEN, onMeasurementTaken);
		}
		
		private static function onMeasurementTaken(event:SensorEvent):void
		{
			if (!measurementDestinationURL)
				throw new Error("The measurement destination must be set!");
			
			var measurement:Measurement = event.measurement;
			measurement.send(measurementDestinationURL);
		}
	}
}