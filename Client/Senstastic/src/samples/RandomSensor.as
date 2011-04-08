package samples
{
	import flash.events.EventDispatcher;
	import flash.events.IEventDispatcher;
	import flash.events.TimerEvent;
	import flash.utils.Timer;
	
	import senstastic.Measurement;
	import senstastic.SensorEvent;
	import senstastic.Senstastic;
	
	public class RandomSensor extends EventDispatcher
	{
		private var _sensorKind:String = "RandomSensor";
		private var _timer:Timer;
		
		public function RandomSensor()
		{
			super();
		
			Senstastic.addSensor(this);
			
			_timer = new Timer(2000);
			_timer.addEventListener(TimerEvent.TIMER, onTimerFired);
			_timer.start();
		}
		
		private function onTimerFired(event:TimerEvent):void
		{
			var randomNumber:int = Math.round(Math.random() * 100.0) as int
			Measurement.asyncCreate(_sensorKind, randomNumber, onMeasurementCreationComplete);
		}
		
		private function onMeasurementCreationComplete(measurement:Measurement):void
		{
			if (!measurement)
				return;
			
			dispatchEvent(new SensorEvent(SensorEvent.MEASUREMENT_CREATED, measurement));
		}
	}
}