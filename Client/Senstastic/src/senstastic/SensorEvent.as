package senstastic
{
	import flash.events.Event;

	public class SensorEvent extends Event
	{
		public static const MEASUREMENT_CREATED:String = "measurementCreated";
		
		public var measurement:Measurement;
		
		public function SensorEvent(type:String, measurement:Measurement)
		{
			super(type);
		
			this.measurement = measurement;
		}
	}
}