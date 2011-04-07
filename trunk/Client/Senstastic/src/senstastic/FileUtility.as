package senstastic
{
	import flash.filesystem.File;
	import flash.filesystem.FileMode;
	import flash.filesystem.FileStream;

	public class FileUtility
	{
		public function FileUtility()
		{
			
		}
		
		public static function writeObject(file:File, object:*):void
		{
			var fileStream:FileStream = new FileStream();
			fileStream.open(file, FileMode.WRITE);
			fileStream.writeObject(object);
			fileStream.close();
		}
		
		public static function readObject(file:File):*
		{
			if (!file.exists)
				return null;
			
			var fileStream:FileStream = new FileStream();
			fileStream.open(file, FileMode.READ);
			var object:String = fileStream.readObject();
			fileStream.close();
			
			return object;
		}
	}
}