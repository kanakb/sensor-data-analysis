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
		
		public function Device()
		{
			
		}

		public static function get deviceId():String
		{
			var deviceId:String = readFile(DEVICE_ID_FILE_NAME);
			
			if (!deviceId)
			{
				deviceId = UIDUtil.createUID();
				writeFile(DEVICE_ID_FILE_NAME, deviceId);
			}
			
			return deviceId;
		}
		
		public static function get deviceKind():String
		{
			return Capabilities.os;
		}
		
		private static function writeFile(fileName:String, fileContents:String):void
		{
			var file:File = File.applicationStorageDirectory.resolvePath(fileName);
			
			var fileStream:FileStream = new FileStream();
			fileStream.open(file, FileMode.WRITE);
			fileStream.writeUTF(fileContents);
			fileStream.close();
		}
		
		private static function readFile(fileName:String):String
		{
			var file:File = File.applicationStorageDirectory.resolvePath(fileName);
			
			if (!file.exists)
				return null;
			
			var fileStream:FileStream = new FileStream();
			fileStream.open(file, FileMode.READ);
			var fileContents:String = fileStream.readUTF();
			fileStream.close();
			
			return fileContents;
		}
	}
}