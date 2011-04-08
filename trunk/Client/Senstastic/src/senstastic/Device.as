package senstastic
{
	import flash.filesystem.File;
	import flash.filesystem.FileMode;
	import flash.filesystem.FileStream;
	import flash.net.NetworkInterface;
	import flash.system.Capabilities;
	
	import mx.utils.UIDUtil;

	public class Device
	{
		private static const DEVICE_ID_FILE_NAME:String = "deviceId";
		private static const DEVICE_ID_FILE:File = File.applicationStorageDirectory.resolvePath(DEVICE_ID_FILE_NAME);
		
		public function Device()
		{
			
		}

		public static function get id():String
		{
			var deviceId:String = FileUtility.readObject(DEVICE_ID_FILE) as String;
			
			if (!deviceId)
			{
				deviceId = UIDUtil.createUID();
				FileUtility.writeObject(DEVICE_ID_FILE, deviceId);
			}
			
			return deviceId;
		}
		
		public static function get kind():String
		{
			return Capabilities.os;
		}
		

	}
}