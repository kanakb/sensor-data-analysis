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
		private var _sensorKind:String = "random sensor";
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
			var measurement:Measurement = new Measurement(_sensorKind, Math.round(Math.random() * 100) as int);
			dispatchEvent(new SensorEvent(measurement));
		}
	}
}