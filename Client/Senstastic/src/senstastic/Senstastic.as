package senstastic
{
	import flash.events.IEventDispatcher;
	
	import mx.rpc.http.HTTPService;

	public class Senstastic
	{
		public static var measurementDestinationURL:String;
		
		public static function get shouldSendMeasurements():Boolean
		{
			// TODO: Implement.
			return true;
		}
		
		public static function addSensor(sensor:IEventDispatcher):void
		{
			sensor.addEventListener(SensorEvent.MEASUREMENT_CREATED, onMeasurementTaken);
		}
		
		public static function removeSensor(sensor:IEventDispatcher):void
		{
			sensor.removeEventListener(SensorEvent.MEASUREMENT_CREATED, onMeasurementTaken);
		}
		
		private static function onMeasurementTaken(event:SensorEvent):void
		{
			if (!measurementDestinationURL)
				throw new Error("The measurement destination must be set!");
			
			var measurement:Measurement = event.measurement;
			
			if (shouldSendMeasurements)	
				measurement.send(measurementDestinationURL);
			else
				measurement.save();
		}
	}
}