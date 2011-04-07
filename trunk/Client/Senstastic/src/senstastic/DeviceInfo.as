package senstastic
{
	import flash.net.NetworkInterface;

	public class DeviceInfo
	{
		import flash.net.NetworkInfo;
		
		public function DeviceInfo()
		{
			
		}
		
		/**
		 * Taken from: http://www.adobe.com/devnet/air/flex/articles/retrieving_network_interfaces.html
		 **/
		public static function traceInterfaces():void
		{
			var results:Vector.<NetworkInterface> =
				NetworkInfo.networkInfo.findInterfaces();
			
			for (var i:int=0; i<results.length; i++)
			{
				var output:String = output
					+ "Name: " + results[i].name + "\n"
					+ "DisplayName: " + results[i].displayName + "\n"
					+ "MTU: " + results[i].mtu + "\n"
					+ "HardwareAddr: " + results[i].hardwareAddress + "\n"
					+ "Active: "  + results[i].active + "\n";
				
				
				for (var j:int=0; j<results[i].addresses.length; j++)
				{
					output = output
						+ "Addr: " + results[i].addresses[j].address + "\n"
						+ "Broadcast: " + results[i].addresses[j].broadcast + "\n"
						+ "PrefixLength: " + results[i].addresses[j].prefixLength + "\n"
						+ "IPVersion: " + results[i].addresses[j].ipVersion + "\n";
				}
				
				output = output + "\n";
				
				trace(output);
			}
		}
	}
}