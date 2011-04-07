package senstastic
{
	import flash.events.Event;

	public class SensorEvent extends Event
	{
		public static const MEASUREMENT_TAKEN:String = "measurementTaken";
		
		public var measurement:Measurement;
		
		public function SensorEvent(measurement:Measurement)
		{
			super(MEASUREMENT_TAKEN);
		
			this.measurement = measurement;
		}
	}
}